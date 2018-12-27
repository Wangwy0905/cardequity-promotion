package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.biz.dal.entity.ActivityProfitEntity;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.vo.req.GetUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.req.QryProfitCommonReq;
import com.youyu.cardequity.promotion.vo.rsp.ActivityDefineRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import com.youyu.common.service.IService;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
public interface ActivityProfitService extends IService<ActivityProfitDto, ActivityProfitEntity> {

    List<ActivityDefineRsp> findEnableGetActivity(QryProfitCommonReq req);

    List<UseActivityRsp> combActivityRefProductDeal(GetUseEnableCouponReq req);
}




