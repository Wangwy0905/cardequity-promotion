package com.youyu.cardequity.promotion.biz.controller;

import com.youyu.cardequity.promotion.api.ClientTakeInCouponApi;
import com.youyu.cardequity.promotion.biz.service.ClientTakeInCouponService;
import com.youyu.cardequity.promotion.vo.req.GetUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.req.PromotionDealReq;
import com.youyu.cardequity.promotion.vo.rsp.OrderCouponAndActivityRsp;

import com.youyu.common.api.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by caiyi on 2019/1/4.
 */
public class ClientTakeInCouponController implements ClientTakeInCouponApi {

    @Autowired
    private ClientTakeInCouponService clientTakeInCouponService;

    /**
     * 在订单时候使用活动及优惠券详情:处理优惠券记录，记录使用痕迹
     * @param req
     * @return
     */
    @Override
    @PostMapping(path = "/orderCouponAndActivityDeal")
    public Result<OrderCouponAndActivityRsp> orderCouponAndActivityDeal(@RequestBody PromotionDealReq req){
        return Result.ok(clientTakeInCouponService.orderCouponAndActivityDeal(req));
    }

    /**
     * 在订单预生成时候使用活动及优惠券详情
     * @param req
     * @return
     */
    @Override
    @PostMapping(path = "/preOrderCouponAndActivityDeal")
    public Result<OrderCouponAndActivityRsp> preOrderCouponAndActivityDeal(@RequestBody GetUseEnableCouponReq req){
        return Result.ok(clientTakeInCouponService.preOrderCouponAndActivityDeal(req));

    }
}
