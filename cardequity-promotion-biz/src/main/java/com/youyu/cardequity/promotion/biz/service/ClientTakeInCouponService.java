package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.biz.dal.entity.ClientTakeInCouponEntity;
import com.youyu.cardequity.promotion.dto.ClientTakeInCouponDto;
import com.youyu.cardequity.promotion.vo.req.GetUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.req.PromotionDealReq;
import com.youyu.cardequity.promotion.vo.rsp.OrderCouponAndActivityRsp;
import com.youyu.common.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
public interface ClientTakeInCouponService extends IService<ClientTakeInCouponDto, ClientTakeInCouponEntity> {
    /**
     * 在订单时候使用活动及优惠券详情:处理优惠券记录，记录使用痕迹
     *
     * @param req
     * @return
     */
    OrderCouponAndActivityRsp orderCouponAndActivityDeal(PromotionDealReq req );

    /**
     * 在订单预生成时候使用活动及优惠券详情
     *
     * @param req
     * @return
     */
    OrderCouponAndActivityRsp preOrderCouponAndActivityDeal(GetUseEnableCouponReq req);
}




