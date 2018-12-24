package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.promotion.biz.dal.dao.ActivityStageCouponMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityStageCouponEntity;
import com.youyu.cardequity.promotion.biz.service.ActivityProfitService;
import com.youyu.cardequity.promotion.dto.ActivityStageCouponDto;
import com.youyu.cardequity.promotion.vo.req.GetUseActivityReq;
import com.youyu.cardequity.promotion.vo.req.QryProfitCommonReq;
import com.youyu.cardequity.promotion.vo.rsp.ActivityDefineRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import com.youyu.common.service.AbstractService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityProfitEntity;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.biz.dal.dao.ActivityProfitMapper;

import java.util.ArrayList;
import java.util.List;


/**
 *  代码生成器
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

    /**
     * 获取可参与的活动列表
     * @param req
     * @return
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    @Override
    public List<ActivityDefineRsp> findEnableGetActivity(QryProfitCommonReq req) {

        List<ActivityDefineRsp> rspList=new ArrayList<ActivityDefineRsp>();

        //获取普通活动列表
        List<ActivityProfitEntity> activityList=activityProfitMapper.findEnableGetCommonActivity(req.getProductId(),req.getEntrustWay());

        //将其使用门槛阶梯与活动主信息组装后返回
        rspList.addAll(combinationActivity(activityList,false));

        //获取会员活动列表
        List<ActivityProfitEntity> activityForMemberList=activityProfitMapper.findEnableGetMemberActivity(req.getProductId(),req.getEntrustWay(),req.getClinetType());
        //将会员活动使用门槛阶梯与活动主信息组装后返回
        rspList.addAll(combinationActivity(activityForMemberList,true));

        return rspList;
    }


    public List<UseActivityRsp> combActivityRefProductDeal(GetUseActivityReq req) {
        //定义返回结果
        List<UseActivityRsp> rsp = new ArrayList<>();
        //获取所有可以参与的活动：按初始条件，有效日期内
        //获取普通活动列表
        List<ActivityProfitEntity> activityList=null;//activityProfitMapper.findEnableGetCommonActivity()

        //空订单或者没有可参与活动直接返回

        //订单中商品先按单价排序，价格约高的优先参与优惠券活动（如任选3件99元等）

        //团购活动优先处理

        //循环活动进行计算优惠金额
        for (ActivityProfitEntity item:activityList){
            //校验基本信息：有效期的、商品属性、订单属性、支付属性

            //递归得到所有组合规则1：任选和打折活动选择最优惠金额；规则2：任选有效

            //所有任选都是互斥的


        }
        return rsp;
    }



    /**
     * 组装活动信息（活动主信息+阶梯信息)
     * @param activityList
     * @param isMember
     * @return
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    public List<ActivityDefineRsp> combinationActivity(List<ActivityProfitEntity> activityList,boolean isMember) {

        List<ActivityDefineRsp> rspList=new ArrayList<ActivityDefineRsp>();
        //循环获取其阶梯信息
        for (ActivityProfitEntity item:activityList){

            //转换为传出参数
            ActivityDefineRsp rsp=new ActivityDefineRsp();
            BeanUtils.copyProperties(item, rsp);

            rsp.setIsAboutMember(isMember?"1":"0");

            //获取指定活动的使用门槛阶梯
            List<ActivityStageCouponEntity> stageList=activityStageCouponMapper.findActivityProfitDetail(item.getId());
            if (stageList.size()>0){

                //数据转换：从实体类转换为Dto
                List<ActivityStageCouponDto> stageDtoList=new ArrayList<>();
                for (ActivityStageCouponEntity stage:stageList){
                    ActivityStageCouponDto dto=new ActivityStageCouponDto();
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




