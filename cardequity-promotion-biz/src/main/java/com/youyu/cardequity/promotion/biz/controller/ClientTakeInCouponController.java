package com.youyu.cardequity.promotion.biz.controller;

import com.youyu.cardequity.promotion.api.ClientTakeInCouponApi;
import com.youyu.cardequity.promotion.biz.service.ClientTakeInCouponService;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.vo.req.BaseOrderInPromotionReq;
import com.youyu.cardequity.promotion.vo.req.GetUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.req.OrderUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.req.PromotionDealReq;
import com.youyu.cardequity.promotion.vo.rsp.FindCouponListByOrderDetailRsp;
import com.youyu.cardequity.promotion.vo.rsp.OrderCouponAndActivityRsp;

import com.youyu.common.api.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/clientTakeInCoupon")
public class ClientTakeInCouponController implements ClientTakeInCouponApi {

    @Autowired
    private ClientTakeInCouponService clientTakeInCouponService;

    /**
     * 【内部】在订单时候使用活动及优惠券详情:处理优惠券记录，记录使用痕迹
     * @param req 订单编号、订单使用活动及优惠券详情
     * @return 订单使用活动及优惠券详情
     */
    @Override
    @ApiOperation(value = "【内部】在订单时候使用活动及优惠券详情:处理优惠券记录，记录使用痕迹")
    @PostMapping(path = "/orderCouponAndActivityDeal")
    public Result<OrderCouponAndActivityRsp> orderCouponAndActivityDeal(@RequestBody PromotionDealReq req){
        return Result.ok(clientTakeInCouponService.orderCouponAndActivityDeal(req));
    }

    /**
     * 【内部】在订单预生成时候使用活动及优惠券详情
     * @param req 订单详情
     * @return 订单使用活动及优惠券详情
     */
    @Override
    @ApiOperation(value = "【内部】在订单预生成时候使用活动及优惠券详情")
    @PostMapping(path = "/preOrderCouponAndActivityDeal")
    public Result<OrderCouponAndActivityRsp> preOrderCouponAndActivityDeal(@RequestBody GetUseEnableCouponReq req){
        return Result.ok(clientTakeInCouponService.preOrderCouponAndActivityDeal(req));

    }

    /**
     * 【内部】取消订单预使用活动及优惠券详情
     * 考虑了幂等性
     * @param req 订单基本信息
     * @return 成功与否
     */
    @Override
    @ApiOperation(value = "【内部】取消订单预使用活动及优惠券详情")
    @PostMapping(path = "/cancelOrderCouponAndActivityDeal")
    public Result<CommonBoolDto> cancelOrderCouponAndActivityDeal(@RequestBody BaseOrderInPromotionReq req){
        return Result.ok(clientTakeInCouponService.cancelOrderCouponAndActivityDeal(req));

    }

    /**
     * 订单适用的“所有”优惠券
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "【内部】订单适用的“所有”优惠券:没有限定使用张数情况下")
    @PostMapping(path = "/OrderDetailApplyAllCouponList")
    public Result<FindCouponListByOrderDetailRsp> OrderDetailApplyAllCouponList(@RequestBody OrderUseEnableCouponReq req){
        return Result.ok(clientTakeInCouponService.OrderDetailApplyAllCouponList(req));

    }

}
