package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.biz.dal.entity.ClientTakeInCouponEntity;
import com.youyu.cardequity.promotion.dto.ClientTakeInCouponDto;
import com.youyu.cardequity.promotion.dto.CommonBoolDto;
import com.youyu.cardequity.promotion.vo.req.BaseOrderInPromotionReq;
import com.youyu.cardequity.promotion.vo.req.GetUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.req.PromotionDealReq;
import com.youyu.cardequity.promotion.vo.rsp.OrderCouponAndActivityRsp;
import com.youyu.common.service.IService;

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

    /**
     * 【内部服务】取消订单预使用活动及优惠券详情
     * 考虑了幂等性
     * @param req
     * @return
     */
    CommonBoolDto cancelOrderCouponAndActivityDeal(BaseOrderInPromotionReq req);


}




