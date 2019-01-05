package com.youyu.cardequity.promotion.api;

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
@RequestMapping(path = "/clientTakeInCouponApi")
public interface ClientTakeInCouponApi {
    /**
     * 在订单时候根据使用活动及优惠券详情处理优惠券记录，记录使用痕迹
     * @param req
     * @return
     */
    @ApiOperation(value = "在订单时候使用活动及优惠券详情:处理优惠券记录，记录使用痕迹")
    @PostMapping(path = "/orderCouponAndActivityDeal")
     Result<OrderCouponAndActivityRsp> orderCouponAndActivityDeal(@RequestBody  PromotionDealReq req);


    /**
     * 在订单预生成时候使用活动及优惠券详情
     * @param req
     * @return
     */
    @ApiOperation(value = "在订单预生成时候使用活动及优惠券详情")
    @PostMapping(path = "/preOrderCouponAndActivityDeal")
     Result<OrderCouponAndActivityRsp> preOrderCouponAndActivityDeal(@RequestBody GetUseEnableCouponReq  req);
}
