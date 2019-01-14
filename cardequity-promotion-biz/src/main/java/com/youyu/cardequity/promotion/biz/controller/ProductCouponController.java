package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ProductCouponApi;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.other.CouponDetailDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.CouponPageQryRsp;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
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
    public Result<CouponPageQryRsp> findCouponListByCommon(@RequestBody BaseQryCouponReq req){
        return Result.ok(productCouponService.findCouponListByCommon(req));

    }

    /**
     * 模糊查询所有优惠券列表
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "模糊查询所有优惠券列表")
    @PostMapping(path = "/findCouponList")
    public Result<CouponPageQryRsp> findCouponList(@RequestBody BaseQryCouponReq req){
        return Result.ok(productCouponService.findCouponList(req));

    }

    /**
     * 查询指定优惠券详情
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "查询指定优惠券详情")
    @PostMapping(path = "/findCouponById")
    public Result<CouponDetailDto> findCouponById(@RequestBody BaseCouponReq req){
        return Result.ok(productCouponService.findCouponById(req));
    }


    /**
     * 查询优惠汇总信息
     *
     * @param req 普通优惠活动请求体
     * @return 优惠汇总列表
     */
    @Override
    @ApiOperation(value = "查询优惠汇总信息")
    @PostMapping(path = "/findGatherCouponByCommon")
    public Result<List<GatherInfoRsp>> findGatherCouponByCommon(@RequestBody BaseQryCouponReq req){
        return Result.ok(productCouponService.findGatherCouponByCommon(req));
    }


    /**
     * 查看指定优惠券id集合对应优惠券列表
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "查看指定优惠券id集合对应优惠券列表")
    @PostMapping(path = "/findCouponListByIds")
    public Result<List<CouponDetailDto>> findCouponListByIds(@RequestBody List<String> req){
        return Result.ok(productCouponService.findCouponListByIds(req));

    }
}
