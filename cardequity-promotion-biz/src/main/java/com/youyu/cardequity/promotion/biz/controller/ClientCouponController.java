package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ClientCouponApi;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.UseCouponRsp;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.ClientCouponDto;
import com.youyu.cardequity.promotion.biz.service.ClientCouponService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
@RestController
@RequestMapping(path = "/clientCoupon")
public class ClientCouponController implements ClientCouponApi {

    @Autowired
    private ClientCouponService clientCouponService;


    @Override
    @ApiOperation(value = "查询已经领取的优惠券")
    @PostMapping(path = "/findClientCoupon")
    public Result<List<ClientCouponDto>> findClientCoupon(@RequestBody BaseClientReq req) {
        return  Result.ok(clientCouponService.findClientCoupon(req));
    }

    @Override
    @ApiOperation(value = "领取优惠券")
    @PostMapping(path = "/obtainCoupon")
    public Result<CommonBoolDto> obtainCoupon(@RequestBody ClientObtainCouponReq req) {

        return Result.ok(clientCouponService.obtainCoupon(req));
    }

    @Override
    @ApiOperation(value = "查询可领取优惠券")
    @PostMapping(path = "/findEnableUseCoupon")
    public Result<List<ClientCouponDto>> findEnableUseCoupon(@RequestBody GetUseEnableCouponReq req) {
        return Result.ok(clientCouponService.findEnableUseCoupon(req));
    }

    @Override
    @ApiOperation(value = "订单预生成时优惠券使用情况")
    @PostMapping(path = "/combCouponRefProductDeal")
    public Result<List<UseCouponRsp>> combCouponRefProductDeal(@RequestBody GetUseEnableCouponReq req){
        return Result.ok(clientCouponService.combCouponRefProductDeal(req));
    }

    /**
     * 根据指定的优惠券进行校验其适用情况，并变动其状态和使用记录
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "订单生成时优惠券使用情况，并变动其状态和使用记录")
    @PostMapping(path = "/combCouponRefProductAndUse")
    public Result<List<UseCouponRsp>> combCouponRefProductAndUse(@RequestBody GetUseEnableCouponReq req){
        return Result.ok(clientCouponService.combCouponRefProductAndUse(req));
    }

    /**
     * 获取客户当前有效的券
     *
     * @param req 客户及商品信息
     * @return 返回已领取的券
     * 开发日志
     */
    @ApiOperation(value = "获取客户当前有效的券")
    @PostMapping(path = "/findValidClientCouponForProduct")
    @Override
    public Result<List<ClientCouponDto>> findValidClientCouponForProduct(@RequestBody BaseClientProductReq req){
        return Result.ok(clientCouponService.findValidClientCouponForProduct(req));
    }



}
