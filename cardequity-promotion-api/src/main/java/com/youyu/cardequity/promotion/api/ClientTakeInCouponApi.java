package com.youyu.cardequity.promotion.api;

import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.vo.req.BaseOrderInPromotionReq;
import com.youyu.cardequity.promotion.vo.req.GetUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.req.PromotionDealReq;
import com.youyu.cardequity.promotion.vo.rsp.OrderCouponAndActivityRsp;
import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 优惠使用情况
 *
 * @author 徐长焕
 * @date 2018-12-27
 * 修改日志：
 * V1.0-V1 1004259-徐长焕-20181227 新增
 */
@Api(tags = "优惠使用操作")
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/clientTakeInCoupon")
public interface ClientTakeInCouponApi {
    /**
     * *********************************【内部接口】************************
     * 【内部】在订单时候根据使用活动及优惠券详情处理优惠券记录，记录使用痕迹
     *
     * @param req 订单及其权益使用信息
     * @return
     */
    @ApiOperation(value = "【内部】在订单时候使用活动及优惠券详情:处理优惠券记录，记录使用痕迹")
    @PostMapping(path = "/orderCouponAndActivityDeal")
     Result<OrderCouponAndActivityRsp> orderCouponAndActivityDeal(@RequestBody  PromotionDealReq req);


    /**
     * 【内部】在订单预生成时候使用活动及优惠券详情
     * 考虑了幂等性
     * @param req
     * @return
     */
    @ApiOperation(value = "【内部】在订单预生成时候使用活动及优惠券详情")
    @PostMapping(path = "/preOrderCouponAndActivityDeal")
     Result<OrderCouponAndActivityRsp> preOrderCouponAndActivityDeal(@RequestBody GetUseEnableCouponReq  req);

    /**
     * 【内部】取消订单预使用活动及优惠券详情
     * 考虑了幂等性
     * @param req
     * @return
     */
    @ApiOperation(value = "【内部】取消订单预使用活动及优惠券详情")
    @PostMapping(path = "/cancelOrderCouponAndActivityDeal")
    Result<CommonBoolDto> cancelOrderCouponAndActivityDeal(@RequestBody BaseOrderInPromotionReq req);
}
