package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.promotion.biz.dal.dao.ActivityStageCouponMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityStageCouponEntity;
import com.youyu.cardequity.promotion.biz.service.ActivityProfitService;
import com.youyu.cardequity.promotion.dto.ActivityStageCouponDto;
import com.youyu.cardequity.promotion.dto.CouponStageUseAndGetRuleDto;
import com.youyu.cardequity.promotion.vo.req.QryProfitCommonReq;
import com.youyu.cardequity.promotion.vo.rsp.ActivityDefineRsp;
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
 */
@Service
public class ActivityProfitServiceImpl extends AbstractService<String, ActivityProfitDto, ActivityProfitEntity, ActivityProfitMapper> implements ActivityProfitService {

    @Autowired
    private ActivityProfitMapper activityProfitMapper;

    @Autowired
    private ActivityStageCouponMapper activityStageCouponMapper;

    @Override
    public List<ActivityDefineRsp> findEnableGetActivity(QryProfitCommonReq req) {
        String isMemberFlag="0";
        //获取普通活动列表
        List<ActivityProfitEntity> activityList=activityProfitMapper.findEnableGetCommonActivity(req.getProductId(),req.getGroupId(),req.getEntrustWay());

        List<ActivityDefineRsp> rspList=new ArrayList<ActivityDefineRsp>();
        //将其使用门槛阶梯与活动主信息组装后返回
        rspList.addAll(combinationActivity(activityList,isMemberFlag));
        //获取会员活动列表
        List<ActivityProfitEntity> activityForMemberList=activityProfitMapper.findEnableGetMemberActivity(req.getProductId(),req.getGroupId(),req.getEntrustWay(),req.getClinetType());
        isMemberFlag="1";
        //将会员活动使用门槛阶梯与活动主信息组装后返回
        rspList.addAll(combinationActivity(activityForMemberList,isMemberFlag));

        return rspList;
    }

    public List<ActivityDefineRsp> combinationActivity(List<ActivityProfitEntity> activityList,String isMemberFlag) {

        List<ActivityDefineRsp> rspList=new ArrayList<ActivityDefineRsp>();
        //循环获取其阶梯信息
        for (ActivityProfitEntity item:activityList){
            //转换为传出参数
            ActivityDefineRsp rsp=new ActivityDefineRsp();
            BeanUtils.copyProperties(item, rsp);

            rsp.setIsAboutMember(isMemberFlag);

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




