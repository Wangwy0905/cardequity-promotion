package com.youyu.cardequity.promotion.api;

import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.CouponRefProductDto;
import com.youyu.cardequity.promotion.vo.req.CouponRefAllProductReq;
import com.youyu.cardequity.promotion.vo.req.BaseCouponReq;
import com.youyu.cardequity.promotion.vo.req.BatchBaseProductReq;
import com.youyu.cardequity.promotion.vo.req.BatchRefProductReq;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 优惠券关联商品管理
 *
 * @author 徐长焕
 * @date 2018-12-27
 * 修改日志：
 * V1.0-V1 1004259-徐长焕-20181227 新增
 */
@Api(tags = "优惠券关联商品管理：优惠券关联商品相关操作")
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/couponRefProduct")
public interface CouponRefProductApi {

    /**
     * *********************************【后台接口】************************
     * 添加优惠券关联商品
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "【后台】添加优惠券关联商品")
    @PostMapping(path = "/addProductRefCoupon")
    Result<CommonBoolDto<Integer>> addProductRefCoupon(@RequestBody BatchRefProductReq req);

    /**
     * *********************************【后台接口】************************
     * 优惠券关联所有商品的添加操作
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "【后台】优惠券关联所有商品的添加操作")
    @PostMapping(path = "/addAllProductRefCoupon")
    Result<CommonBoolDto<Integer>> addAllProductRefCoupon(@RequestBody BatchRefProductReq req);


    /**
     * 查询商品的活动数量
     * @param req 商品基本信息
     * @return 活动数量列表
     */
    @ApiOperation(value = "【后台-有效期-上架】查询商品的活动数量")
    @PostMapping(path = "/findProductAboutCouponNum")
    Result<List<GatherInfoRsp>> findProductAboutCouponNum(@RequestBody BatchBaseProductReq req);


    /**
     * *********************************【通用接口】************************
     * 查询优惠券关联的商品列表
     *
     * @param req
     * @return
     */
    @ApiOperation( value = "【通用】查询优惠券关联的商品列表")
    @PostMapping(path = "/findJoinProductByCoupon")
    Result<List<CouponRefProductDto>> findJoinProductByCoupon(@RequestBody BaseCouponReq req);

}
