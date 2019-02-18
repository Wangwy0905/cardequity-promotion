package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ClientCouponApi;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.other.ObtainCouponViewDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.FindCouponListByOrderDetailRsp;
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
    @ApiOperation(value = "【App】获取客户已领取的券")
    @PostMapping(path = "/findClientCoupon")
    public Result<List<ObtainCouponViewDto>> findClientCoupon(@RequestBody QryComonClientCouponReq req) {
        return  Result.ok(clientCouponService.findClientCoupon(req));
    }

    @Override
    @ApiOperation(value = "【App】客户领取优惠券")
    @PostMapping(path = "/obtainCoupon")
    public Result<CommonBoolDto<ObtainCouponViewDto>> obtainCoupon(@RequestBody ClientObtainCouponReq req) {

        return Result.ok(clientCouponService.obtainCoupon(req));
    }

    @Override
    @ApiOperation(value = "【App-已过期】在“不校验使用门槛”前提计算可领取优惠券")
    @PostMapping(path = "/findEnableUseCoupon")
    public Result<List<ObtainCouponViewDto>> findEnableUseCoupon(@RequestBody GetUseEnableCouponReq req) {
        return Result.ok(clientCouponService.findEnableUseCoupon(req));
    }

    @Override
    @ApiOperation(value = "【App】计算订单“预”生成时优惠券使用情况")
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
    @ApiOperation(value = "【App】计算订单生成时优惠券使用情况，并变动其状态和使用记录")
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
    @ApiOperation(value = "【App】获取客户当前有效期内满足指定商品条件的券")
    @PostMapping(path = "/findValidClientCouponForProduct")
    @Override
    public Result<List<ObtainCouponViewDto>> findValidClientCouponForProduct(@RequestBody BaseClientProductReq req){
        return Result.ok(clientCouponService.findValidClientCouponForProduct(req));
    }


    /**
     * 【App】根据订单计算优惠券状态（可用，不可用）情况
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "【App】根据订单计算优惠券状态（可用，不可用）情况")
    @PostMapping(path = "/findCouponListByOrderDetail")
    public Result<FindCouponListByOrderDetailRsp> findCouponListByOrderDetail(@RequestBody OrderUseEnableCouponReq req){
        return Result.ok(clientCouponService.findCouponListByOrderDetail(req));

    }

    @Override
    @ApiOperation(value = "【App】查询客户领取券统计数量")
    @PostMapping(path = "/findClientCouponNum")
    public Result<Integer> findClientCouponNum(@RequestBody QryComonClientCouponReq req){
        return Result.ok(clientCouponService.findClientCouponNum(req));
    }
}
