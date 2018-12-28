package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ClientCouponApi;
import com.youyu.cardequity.promotion.dto.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.ProductCouponDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.CouponDefineRsp;
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
    @PostMapping(path = "/findClientCoupon")
    public Result<List<ClientCouponDto>> findClientCoupon(@RequestBody BaseClientReq req) {
        return  Result.ok(clientCouponService.findClientCoupon(req));
    }

    @Override
    @PostMapping(path = "/obtainCoupon")
    public Result<CommonBoolDto> obtainCoupon(@RequestBody ClientObtainCouponReq req) {

        return Result.ok(clientCouponService.obtainCoupon(req));
    }

    @Override
    @PostMapping(path = "/findEnableUseCoupon")
    public Result<List<ClientCouponDto>> findEnableUseCoupon(@RequestBody GetUseEnableCouponReq req) {
        return Result.ok(clientCouponService.findEnableUseCoupon(req));
    }

    @Override
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
    @PostMapping(path = "/combCouponRefProductAndUse")
    public Result<List<UseCouponRsp>> combCouponRefProductAndUse(@RequestBody GetUseEnableCouponReq req){
        return Result.ok(clientCouponService.combCouponRefProductAndUse(req));
    }

    /**
     * 添加优惠券
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "添加优惠券(暂未实现)：添加基本信息、领取频率、使用门槛、关联商品等")
    @PostMapping(path = "/addCoupon")
    public Result<List<ProductCouponDto>> addCoupon(@RequestBody CouponDetailReq req){
        return null;
    }


    /**
     * 编辑优惠券
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "编辑优惠券(暂未实现)：编辑基本信息、领取频率、使用门槛、关联商品等")
    @PostMapping(path = "/editCoupon")
    public Result<List<ProductCouponDto>> editCoupon(@RequestBody CouponDetailReq req){
        return null;
    }

    /**
     * 查看商品对应优惠券列表
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "查看商品对应优惠券列表(暂未实现)")
    @PostMapping(path = "/findCouponListByProduct")
    public Result<List<CouponDefineRsp>> findCouponListByProduct(@RequestBody BaseProductReq req){
        return null;
    }

    /**
     * 查询所有优惠券列表
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "查询所有优惠券列表")
    @PostMapping(path = "/findCouponListByCommon")
    public Result<List<CouponDefineRsp>> findCouponListByCommon(@RequestBody BaseCouponReq req){
        return null;
    }
}
