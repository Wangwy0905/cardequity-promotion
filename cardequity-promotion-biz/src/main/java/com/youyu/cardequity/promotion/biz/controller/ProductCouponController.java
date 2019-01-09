package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ProductCouponApi;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.other.CouponDetailDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.biz.service.ProductCouponService;
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
@RequestMapping(path = "/productCoupon")
public class ProductCouponController implements ProductCouponApi {

    @Autowired
    private ProductCouponService productCouponService;

    /**
     * 获取可以领取的优惠券
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "获取可以领取的优惠券")
    @PostMapping(path = "/findEnableGetCoupon")
    public Result<List<CouponDetailDto>> findEnableGetCoupon(@RequestBody QryProfitCommonReq req) {
        List<CouponDetailDto> rspList = productCouponService.findEnableGetCoupon(req);
        return Result.ok(rspList);
    }

    /**
     * 添加优惠券
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "添加优惠券：添加基本信息、领取频率、使用门槛、关联商品等")
    @PostMapping(path = "/addCoupon")
    public Result<CommonBoolDto<CouponDetailDto> > addCoupon(@RequestBody CouponDetailDto req)
    {
        return Result.ok(productCouponService.addCoupon(req));
    }


    /**
     * 编辑优惠券
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "编辑优惠券：编辑基本信息、领取频率、使用门槛、关联商品等")
    @PostMapping(path = "/editCoupon")
    public Result<CommonBoolDto<CouponDetailDto>> editCoupon(@RequestBody CouponDetailDto req)
    {
        return Result.ok(productCouponService.editCoupon(req));
    }

    /**
     * 批量删除优惠券：逻辑删除基本信息、额度值、频率规则、门槛；物理删除商品对应关系
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "批量删除优惠券：商品对应关系、额度值、频率规则、门槛")
    @PostMapping(path = "/batchDelCoupon")
    public Result<CommonBoolDto<Integer>> batchDelCoupon(@RequestBody BatchBaseCouponReq req){
        return Result.ok(productCouponService.batchDelCoupon(req));
    }
    /**
     * 查看商品对应优惠券列表
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "查看商品对应优惠券列表")
    @PostMapping(path = "/findCouponListByProduct")
    public Result<List<CouponDetailDto>> findCouponListByProduct(@RequestBody BaseProductReq req){
        return Result.ok(productCouponService.findCouponListByProduct(req));
    }

    /**
     * 查询所有优惠券列表
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "查询所有优惠券列表")
    @PostMapping(path = "/findCouponListByCommon")
    public Result<PageData<CouponDetailDto>> findCouponListByCommon(@RequestBody BaseQryCouponReq req){
        return Result.ok(productCouponService.findCouponListByCommon(req));

    }
}
