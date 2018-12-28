package com.youyu.cardequity.promotion.api;

import com.youyu.cardequity.promotion.dto.CouponRefProductDto;
import com.youyu.cardequity.promotion.vo.req.BaseCouponRefProductReq;
import com.youyu.cardequity.promotion.vo.req.BaseCouponReq;
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
@RequestMapping(path = "/couponRefProductApi")
public interface CouponRefProductApi {

    /**
     * 添加优惠券关联商品
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "添加优惠券关联商品")
    @PostMapping(path = "/addProductRefCoupon")
    Result<List<CouponRefProductDto>> addProductRefCoupon(@RequestBody BaseCouponRefProductReq req);

    /**
     * 查询优惠券关联的商品列表
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "查询优惠券关联的商品列表")
    @PostMapping(path = "/findJoinProductByCoupon")
    Result<List<CouponRefProductDto>> findJoinProductByCoupon(@RequestBody BaseCouponReq req);
}
