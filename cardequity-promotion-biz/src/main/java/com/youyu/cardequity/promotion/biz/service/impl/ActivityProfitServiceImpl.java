package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.bean.CustomHandler;
import com.youyu.cardequity.promotion.biz.dal.dao.ActivityRefProductMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.ActivityStageCouponMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.*;
import com.youyu.cardequity.promotion.biz.service.ActivityProfitService;
import com.youyu.cardequity.promotion.biz.strategy.activity.ActivityStrategy;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.*;
import com.youyu.cardequity.promotion.enums.dict.*;
import com.youyu.cardequity.promotion.vo.req.GetUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.req.QryProfitCommonReq;
import com.youyu.cardequity.promotion.vo.rsp.ActivityDefineRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import com.youyu.common.service.AbstractService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.youyu.cardequity.promotion.biz.dal.dao.ActivityProfitMapper;

import java.time.LocalDate;
import java.util.*;

import static com.youyu.cardequity.promotion.enums.ResultCode.*;


/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 * 开发日志
 * V1.0-V1 1004244-徐长焕-20181207 新建，实现可参与活动列表
 */
@Service
public class ActivityProfitServiceImpl extends AbstractService<String, ActivityProfitDto, ActivityProfitEntity, ActivityProfitMapper> implements ActivityProfitService {

    @Autowired
    private ActivityProfitMapper activityProfitMapper;

    @Autowired
    private ActivityStageCouponMapper activityStageCouponMapper;

    @Autowired
    private ActivityRefProductMapper activityRefProductMapper;

    /**
     * 获取可参与的活动列表
     *
     * @param req 查询优惠活动请求体
     * @return 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    @Override
    public List<ActivityDefineRsp> findEnableGetActivity(QryProfitCommonReq req) {

        List<ActivityDefineRsp> rspList = new ArrayList<>();

        //获取普通活动列表
        List<ActivityProfitEntity> activityList = activityProfitMapper.findEnableGetCommonActivity(req.getProductId(), req.getEntrustWay());

        //将其使用门槛阶梯与活动主信息组装后返回
        rspList.addAll(combinationActivity(activityList, false));

        //获取会员活动列表
        List<ActivityProfitEntity> activityForMemberList = activityProfitMapper.findEnableGetMemberActivity(req.getProductId(), req.getEntrustWay(), req.getClinetType());
        //将会员活动使用门槛阶梯与活动主信息组装后返回
        rspList.addAll(combinationActivity(activityForMemberList, true));

        return rspList;
    }

    @Override
    public List<UseActivityRsp> combActivityRefProductDeal(GetUseEnableCouponReq req) {
        //定义返回结果
        List<UseActivityRsp> rsps = new ArrayList<>();
        //获取所有可以参与的活动：按初始条件，有效日期内
        //获取普通活动列表
        List<ActivityProfitEntity> activityList = activityProfitMapper.findEnableGetCommonActivity("", req.getEntrustWay());
        //空订单或者没有可用活动直接返回
        if (req.getProductList() == null ||
                activityList == null ||
                req.getProductList().size() <= 0 ||
                activityList.size() <= 0) {
            return rsps;
        }

        //团购活动优先处理
        // TODO: 2018/12/25

        //循环活动进行计算优惠金额，优先顺序为：任选->折扣->满减
        for (ActivityProfitEntity item : activityList) {
            //校验基本信息：有效期的、商品属性、订单属性、支付属性
            checkActivityBase(item, req);

            //根据策略得到该活动是否满足门槛，返回满足活动适用信息
            String key = ActivityStrategy.class.getSimpleName() + item.getActivityCouponType();
            ActivityStrategy executor = (ActivityStrategy) CustomHandler.getBeanByName(key);
            UseActivityRsp rsp = executor.applyActivity(item, req.getProductList());
            if (rsp != null)
                rsps.add(rsp);
        }
        return rsps;
    }


    /**
     * 校验活动基本信息
     *
     * @param activity  活动实体
     * @param req 获取可用活动优惠券请求体
     * @return 返回是否校验成功
     */
    private CommonBoolDto checkActivityBase(ActivityProfitEntity activity,
                                            GetUseEnableCouponReq req) {
        CommonBoolDto dto = new CommonBoolDto(true);

        //a.客户属性校验
        // 客户类型是否允许领取
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(activity.getClientTypeSet(),
                req.getClinetType())) {

            dto.setSuccess(false);
            dto.setDesc(ACTIVITY_NOT_ALLOW_CLIENTTYPE.getFormatDesc(req.getClinetType()));
            return dto;
        }

        //b.商品属性校验，不校验其买入卖出门槛
        if (req.getProductList() != null && req.getProductList().size() > 0) {
            dto.setSuccess(false);
            for (OrderProductDetailDto item : req.getProductList()) {
                dto = checkRefProduct(activity, item.getProductId());
                if (dto.getSuccess()) {
                    break;
                }
            }
            if (!dto.getSuccess()) {
                dto.setDesc("该活动不适用本次选择的任何商品");
                return dto;
            }
        }

        //c.订单属性校验
        //该渠道信息是否允许领取
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(activity.getEntrustWaySet(),
                req.getEntrustWay())) {
            dto.setSuccess(false);
            dto.setDesc(ACTIVITY_NOT_ALLOW_ENTRUSTWAY.getFormatDesc(req.getEntrustWay()));
            return dto;
        }

        //d.支付属性校验
        //该银行卡是否允许领取该券
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(activity.getBankCodeSet(),
                req.getBankCode())) {

            dto.setSuccess(false);
            dto.setDesc(ACTIVITY_NOT_ALLOW_BANKCODE.getFormatDesc(req.getBankCode()));
            return dto;
        }

        //该支付类型是否允许领取该券
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(activity.getPayTypeSet(),
                req.getPayType())) {
            dto.setSuccess(false);
            dto.setDesc(ACTIVITY_NOT_ALLOW_PAYTYPE.getFormatDesc(req.getPayType()));
            return dto;
        }

        //是否在允許使用期間
        if ((activity.getAllowUseBeginDate() != null && activity.getAllowUseBeginDate().compareTo(LocalDate.now()) > 0) ||
                (activity.getAllowUseEndDate() != null && activity.getAllowUseEndDate().compareTo(LocalDate.now()) < 0)) {

            dto.setSuccess(false);
            dto.setDesc(ACTIVITY_NOT_ALLOW_DATE.getFormatDesc(activity.getAllowUseBeginDate(),activity.getAllowUseEndDate()));
            return dto;
        }
        return dto;
    }


    /**
     * 检查商品是否适用
     *
     * @param activity 活动实体
     * @param productId 商品编号
     * @return 返回是否校验成功
     */
    private CommonBoolDto checkRefProduct(ActivityProfitEntity activity, String productId) {
        CommonBoolDto dto = new CommonBoolDto(false);
        // ApplyProductFlag空值做保护
        if (!ApplyProductFlag.ALL.getDictValue().equals(activity.getApplyProductFlag())) {
            //该商品属性是否允许参与活动
            ActivityRefProductEntity entity = activityRefProductMapper.findByBothId(activity.getId(), productId);
            if (entity != null) {
                dto.setSuccess(true);
                return dto;
            }
        } else {
            dto.setSuccess(true);
            return dto;
        }
        return dto;
    }

    /**
     * 组装活动信息（活动主信息+阶梯信息)
     *
     * @param activityList 活动实体列表
     * @param isMember 是否会员活动
     * @return 返回活动信息及其门槛阶梯
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    private List<ActivityDefineRsp> combinationActivity(List<ActivityProfitEntity> activityList, boolean isMember) {

        List<ActivityDefineRsp> rspList = new ArrayList<>();
        //循环获取其阶梯信息
        for (ActivityProfitEntity item : activityList) {

            //转换为传出参数
            ActivityDefineRsp rsp = new ActivityDefineRsp();
            BeanUtils.copyProperties(item, rsp);

            rsp.setIsAboutMember(isMember ? "1" : "0");

            //获取指定活动的使用门槛阶梯
            List<ActivityStageCouponEntity> stageList = activityStageCouponMapper.findActivityProfitDetail(item.getId());
            if (stageList.size() > 0) {

                //数据转换：从实体类转换为Dto
                List<ActivityStageCouponDto> stageDtoList = new ArrayList<>();
                for (ActivityStageCouponEntity stage : stageList) {
                    ActivityStageCouponDto dto = new ActivityStageCouponDto();
                    BeanUtils.copyProperties(stage, dto);
                    stageDtoList.add(dto);
                }

                //BeanPropertiesConverter.copyPropertiesOfList无法对LocalDate数据进行拷贝
                //List<ActivityStageCouponDto> stageDtoList=BeanPropertiesConverter.copyPropertiesOfList(stageList, ActivityStageCouponDto.class);
                rsp.setActivityStageCouponDtoList(stageDtoList);
            }
            rspList.add(rsp);
        }
        return rspList;
    }

}




