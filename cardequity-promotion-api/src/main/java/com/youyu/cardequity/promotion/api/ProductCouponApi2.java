package com.youyu.cardequity.promotion.api;

import com.youyu.cardequity.promotion.dto.req.*;
import com.youyu.cardequity.promotion.dto.rsp.CouponRefProductQueryRsp;
import com.youyu.cardequity.promotion.dto.rsp.ProductCouponGetStatisticsRsp;
import com.youyu.cardequity.promotion.dto.rsp.ProductCouponQueryRsp;
import com.youyu.cardequity.promotion.dto.rsp.ProductCouponViewRsp;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月20日 10:00:00
 * @work 优惠券商品信息管理Api
 */
@Api(tags = "优惠券信息管理")
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/productCoupon2")
public interface ProductCouponApi2 {

    /**
     * 优惠券添加接口
     *
     * @param productCouponAddReq
     * @return
     */
    @ApiOperation(value = "优惠券添加")
    @PostMapping(path = "/add")
    Result add(@Valid @RequestBody ProductCouponAddReq productCouponAddReq);

    /**
     * 优惠券编辑接口
     *
     * @param productCouponEditReq
     * @return
     */
    @ApiOperation(value = "优惠券编辑")
    @PostMapping(path = "/edit")
    Result edit(@Valid @RequestBody ProductCouponEditReq productCouponEditReq);

    /**
     * 优惠券查看接口
     *
     * @param productCouponViewReq
     * @return
     */
    @ApiOperation(value = "优惠券查看")
    @PostMapping(path = "/view")
    Result<ProductCouponViewRsp> view(@Valid @RequestBody ProductCouponViewReq productCouponViewReq);

    /**
     * 优惠券上下架接口
     *
     * @param productCouponStatusReq
     * @return
     */
    @ApiOperation(value = "优惠券上下架")
    @PostMapping(path = "/setStatus")
    Result setStatus(@RequestBody ProductCouponStatusReq productCouponStatusReq);

    /**
     * 优惠券查询接口
     *
     * @param productCouponQueryReq
     * @return
     */
    @ApiOperation(value = "优惠券查询")
    @PostMapping(path = "/get")
    Result<PageData<ProductCouponQueryRsp>> get(@RequestBody ProductCouponQueryReq productCouponQueryReq);

    /**
     * 优惠券查询可领取对象统计
     *
     * @param productCouponGetStatisticsReq
     * @return
     */
    @ApiOperation(value = "优惠券查询可领取对象统计")
    @PostMapping(path = "/getStatisticsByClientType")
    Result<List<ProductCouponGetStatisticsRsp>> getStatisticsByClientType(@RequestBody ProductCouponGetStatisticsReq productCouponGetStatisticsReq);

    /**
     * 添加优惠券关联的商品
     *
     * @param couponRefProductReq
     * @return
     */
    @ApiOperation(value = "添加优惠券关联的商品")
    @PostMapping(path = "/addProduct")
    Result addProduct(@RequestBody CouponRefProductReq couponRefProductReq);

    /**
     * 添加优惠券关联的所有商品
     *
     * @param couponRefAllProductReq
     * @return
     */
    @ApiOperation(value = "添加优惠券关联的所有商品")
    @PostMapping(path = "/addAllProduct")
    Result addAllProduct(@RequestBody CouponRefAllProductReq couponRefAllProductReq);

    /**
     * 查询优惠券关联商品
     *
     * @param couponRefProductQueryReq
     * @return
     */
    @ApiOperation(value = "查询优惠券关联商品")
    @PostMapping(path = "/getCouponRefProductQuery")
    Result<PageData<CouponRefProductQueryRsp>> getCouponRefProductQuery(@RequestBody CouponRefProductQueryReq couponRefProductQueryReq);
}
