package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.common.base.util.BeanPropertiesUtils;
import com.youyu.cardequity.common.base.util.StringUtil;
import com.youyu.cardequity.common.spring.service.BatchService;
import com.youyu.cardequity.promotion.biz.dal.dao.ActivityProfitMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityProfitEntity;
import com.youyu.cardequity.promotion.biz.service.ActivityRefProductService;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.ApplyProductFlag;
import com.youyu.cardequity.promotion.enums.dict.CouponStatus;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityRefProductEntity;
import com.youyu.cardequity.promotion.dto.ActivityRefProductDto;
import com.youyu.cardequity.promotion.biz.dal.dao.ActivityRefProductMapper;

import java.util.ArrayList;
import java.util.List;

import static com.youyu.cardequity.promotion.enums.ResultCode.NET_ERROR;
import static com.youyu.cardequity.promotion.enums.ResultCode.PARAM_ERROR;


/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-20
 */
@Service
public class ActivityRefProductServiceImpl extends AbstractService<String, ActivityRefProductDto, ActivityRefProductEntity, ActivityRefProductMapper> implements ActivityRefProductService {
    @Autowired
    private ActivityRefProductMapper activityRefProductMapper;

    @Autowired
    private ActivityProfitMapper activityProfitMapper;

    @Autowired
    private BatchService batchService;


    /**
     * 【后台】指定活动的商品配置范围和其他活动是否冲突,考虑分页的问题
     *
     * @param req      商品编号类别
     * @param activity 活动详情
     * @return 关联基础信息列表
     */
    @Override
    public CommonBoolDto<List<ActivityRefProductEntity>> checkProductReUse(List<BaseProductReq> req, ActivityProfitDto activity) {

        CommonBoolDto<List<ActivityRefProductEntity>> result = new CommonBoolDto<>(true);
        result.setCode(NET_ERROR.getCode());
        //下架活动无需校验
        if (activity != null && !CouponStatus.YES.getDictValue().equals(activity.getStatus())) {
            return result;
        }

        List<ActivityProfitEntity> unlimitedProductActivity = activityProfitMapper.findUnlimitedProductActivity();
        if (unlimitedProductActivity.size() > 0) {
            for (ActivityProfitEntity item : unlimitedProductActivity) {
                if (activity == null ||
                        !(item.getAllowUseBeginDate().compareTo(activity.getAllowUseEndDate()) > 0 || item.getAllowUseEndDate().compareTo(activity.getAllowUseBeginDate()) < 0)) {
                    result.setSuccess(false);
                    result.setDesc("存在商品已经参与了其他活动,该活动允许所有商品,其中冲突活动编号=" + unlimitedProductActivity.get(0).getId());
                    return result;
                }
            }
        }

        if (activity != null && ApplyProductFlag.ALL.getDictValue().equals(activity.getApplyProductFlag())) {
            BaseQryActivityReq innerreq = new BaseQryActivityReq();
            innerreq.setStatus("1");
            List<ActivityProfitEntity> activityListByCommon = activityProfitMapper.findActivityListByCommon(innerreq);
            if (activityListByCommon.size() > 1) {
                for (ActivityProfitEntity item : activityListByCommon) {
                    //如果是编辑活动时校验，会出现下面条件
                    if (item.getId().equals(activity.getId())) {
                        continue;
                    }
                    if (CouponStatus.YES.getDictValue().equals(item.getStatus())) {
                        if (!(item.getAllowUseBeginDate().compareTo(activity.getAllowUseEndDate()) > 0 || item.getAllowUseEndDate().compareTo(activity.getAllowUseBeginDate()) < 0)) {
                            result.setSuccess(false);
                            result.setDesc("该活动是允许所有商品参与，存在冲突,其中冲突活动编号=" + unlimitedProductActivity.get(0).getId());
                            return result;
                        }
                    }
                }
            }
        }

        if (req == null || req.isEmpty())
            return result;

        int perCount = 1000, index = 0;
        List<BaseProductReq> listTemp = null;
        do {
            if (req.size() > (index + perCount)) {
                listTemp = req.subList(index, index + perCount);// 分段处理
            } else {
                listTemp = req.subList(index, req.size());//获得[index, req.size())
            }

            List<GatherInfoRsp> gathers = activityRefProductMapper.findReProductBylist(listTemp, activity);

            if (!gathers.isEmpty()) {

                for (GatherInfoRsp item : gathers) {
                    String key = StringUtil.split(item.getGatherItem(), "|")[0];
                    for (BaseProductReq productReq : listTemp) {
                        if (key.equals(productReq.getProductId())) {
                            if (CommonUtils.isEmptyorNull(productReq.getSkuId()) ||
                                    item.getGatherItem().equals(key + "|" + productReq.getSkuId())) {
                                result.setSuccess(false);
                                result.setCode(PARAM_ERROR.getCode());
                                result.setDesc("存在商品已经参与了其他活动,其中商品id=" + productReq.getProductId() + "子商品id=" + productReq.getSkuId());
                                return result;
                            }
                        }
                    }

                }

            }
            index = index + perCount;
        } while (index <= req.size());

        return result;
    }

    /**
     * 查询除指定活动外已经配置了活动的商品
     *
     * @return 基础关联信息
     */
    @Override
    public List<ActivityRefProductDto> findAllProductInValidActivity(BaseActivityReq req) {
        List<ActivityRefProductEntity> entities = activityRefProductMapper.findByExcludeActivityId(req.getActivityId());
        return BeanPropertiesConverter.copyPropertiesOfList(entities, ActivityRefProductDto.class);
    }


    /**
     * 查询已经配置了活动的商品
     *
     * @return 商品编号
     */
    @Override
    public List<BaseProductReq> findProductInValidActivity(FindProductInValidActivityReq req) {
        return activityRefProductMapper.findProductInValidActivity(req.getStatus(), req.getActivityCouponType());
    }

    /**
     * 查询活动配置的商品
     *
     * @param req 活动基本信息
     * @return 商品基本信息
     */
    @Override
    public List<BaseProductReq> findActivityProducts(BaseActivityReq req) {
        if (req == null || CommonUtils.isEmptyorNull(req.getActivityId()))
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定活动"));

        List<ActivityRefProductEntity> refProductEntities = activityRefProductMapper.findByActivityId(req.getActivityId());
        return BeanPropertiesConverter.copyPropertiesOfList(refProductEntities, BaseProductReq.class);
    }

    /**
     * 配置活动的商品信息
     *
     * @param req 批量商品编号
     * @return 执行数量
     */
    @Override
    public CommonBoolDto<Integer> batchAddActivityRefProduct(BatchRefProductReq req) {
        if (req == null || CommonUtils.isEmptyorNull(req.getId()))
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定活动"));

        CommonBoolDto<Integer> result = new CommonBoolDto<>(true);
        result.setCode(NET_ERROR.getCode());
        ActivityProfitEntity profitEntity = activityProfitMapper.findById(req.getId());
        if (profitEntity == null) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("指定活动不存在，活动编号" + req.getId()));
        }

        List<String> activities = new ArrayList<>();
        activities.add(req.getId());
        //全量时才允许删除
        if ("1" != req.getOperatFlag())
            batchService.batchDispose(activities, ActivityRefProductMapper.class, "deleteByActivityId");
        if (req.getProductList() != null && !req.getProductList().isEmpty()) {

            CommonBoolDto<List<ActivityRefProductEntity>> boolDto = checkProductReUse(req.getProductList(), BeanPropertiesUtils.copyProperties(profitEntity, ActivityProfitDto.class));
            if (!boolDto.getSuccess()) {
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc(boolDto.getDesc()));
            }
            List<ActivityRefProductEntity> entities = new ArrayList<>();
            for (BaseProductReq item : req.getProductList()) {
                ActivityRefProductEntity entity = new ActivityRefProductEntity();
                entity.setCreateAuthor(req.getOperator());
                entity.setUpdateAuthor(req.getOperator());
                entity.setProductId(item.getProductId());
                entity.setActivityId(profitEntity.getId());
                entity.setId(CommonUtils.getUUID());
                entity.setIsEnable(CommonDict.IF_YES.getCode());
                entities.add(entity);
            }
            batchService.batchDispose(entities, ActivityRefProductMapper.class, "insert");
            result.setData(req.getProductList().size());
        }
        return result;

    }


    /**
     * 查询商品对应的活动数量
     *
     * @param req 商品列表
     * @return 商品对应活动数量
     */
    @Override
    public List<GatherInfoRsp> findProductAboutActivityNum(BatchBaseProductReq req) {
        if (req == null || req.getProductList() == null || req.getProductList().isEmpty())
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定商品"));

        List<GatherInfoRsp> firstresult = activityProfitMapper.findActivityNumByProducts(req);

        List<ActivityProfitEntity> entities = activityProfitMapper.findUnlimitedProductActivity();
        List<GatherInfoRsp> result = new ArrayList<>();
        if (!entities.isEmpty()) {

            for (BaseProductReq item : req.getProductList()) {
                boolean isExist = false;
                String key = item.getProductId() + (CommonUtils.isEmptyorNull(item.getSkuId()) ? "|EMPTY" : "|" + item.getSkuId());

                for (GatherInfoRsp gather : firstresult) {
                    if (key.equals(gather.getGatherItem())) {
                        gather.setGatherValue(gather.getGatherValue() + entities.size());
                        isExist = true;
                        result.add(gather);
                        firstresult.remove(gather);
                        break;
                    }
                }
                if (!isExist) {
                    GatherInfoRsp rsp = new GatherInfoRsp();
                    rsp.setGatherItem(key);
                    rsp.setGatherValue(entities.size());
                    result.add(rsp);
                }
            }
        } else {
            result = firstresult;
        }
        return result;
    }


    /**
     * 根据初始产品列表过滤出可以配置的产品
     *
     * @param req 商品列表及活动
     * @return 商品对应活动数量
     */
    @Override
    public List<BaseProductReq> findEnableCifgInProducts(BatchRefProductDetailReq req) {
        if (req == null || req.getProductList() == null || req.getProductList().isEmpty())
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定商品"));

        ActivityProfitDto dto=new ActivityProfitDto();
        dto.setId(req.getId());
        dto.setAllowUseEndDate(req.getAllowUseEndDate());
        dto.setAllowUseBeginDate(req.getAllowUseBeginDate());

        List<BaseProductReq> result = new ArrayList<>();
        List<BaseProductReq> data = activityRefProductMapper.findEnableCifgInProducts(req.getProductList(),dto);
        for (BaseProductReq item:req.getProductList()){
            result.add(item);
            for (BaseProductReq dataitem:data) {
                if (item.getProductId().equals(dataitem.getProductId()))
                    result.remove(item);
            }
        }
        return result;
    }
}




