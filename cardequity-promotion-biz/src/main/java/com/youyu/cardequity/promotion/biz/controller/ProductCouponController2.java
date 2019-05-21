package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ProductCouponApi2;
import com.youyu.cardequity.promotion.biz.service.ProductCouponService2;
import com.youyu.cardequity.promotion.dto.req.*;
import com.youyu.cardequity.promotion.dto.rsp.CouponRefProductQueryRsp;
import com.youyu.cardequity.promotion.dto.rsp.ProductCouponGetStatisticsRsp;
import com.youyu.cardequity.promotion.dto.rsp.ProductCouponQueryRsp;
import com.youyu.cardequity.promotion.dto.rsp.ProductCouponViewRsp;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static com.youyu.common.api.Result.ok;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月20日 10:00:00
 * @work 优惠券商品信息管理Controller
 */
@RestController
@RequestMapping(path = "/productCoupon2")
public class ProductCouponController2 implements ProductCouponApi2 {

    @Autowired
    private ProductCouponService2 productCouponService2;

    @Override
    public Result add(@Valid @RequestBody ProductCouponAddReq productCouponAddReq) {
        productCouponService2.add(productCouponAddReq);
        return ok();
    }

    @Override
    public Result edit(@Valid @RequestBody ProductCouponEditReq productCouponEditReq) {
        productCouponService2.edit(productCouponEditReq);
        return ok();
    }

    @Override
    public Result<ProductCouponViewRsp> getDetail(@Valid @RequestBody  ProductCouponViewReq productCouponViewReq) {
        return ok(productCouponService2.getDetail(productCouponViewReq));
    }

    @Override
    public Result setStatus(@RequestBody ProductCouponStatusReq productCouponStatusReq) {
        productCouponService2.setStatus(productCouponStatusReq);
        return ok();
    }

    @Override
    public Result<PageData<ProductCouponQueryRsp>> get(@RequestBody ProductCouponQueryReq productCouponQueryReq) {
        return ok(productCouponService2.get(productCouponQueryReq));
    }

    @Override
    public Result<List<ProductCouponGetStatisticsRsp>> getStatisticsByClientType(@RequestBody ProductCouponGetStatisticsReq productCouponGetStatisticsReq) {
        return ok(productCouponService2.getStatisticsByClientType(productCouponGetStatisticsReq));
    }

    @Override
    public Result addProduct(@RequestBody CouponRefProductReq couponRefProductReq) {
        productCouponService2.addProduct(couponRefProductReq);
        return ok();
    }

    @Override
    public Result addAllProduct(@RequestBody CouponRefAllProductReq couponRefAllProductReq) {
        productCouponService2.addAllProduct(couponRefAllProductReq);
        return ok();
    }

    @Override
    public Result<PageData<CouponRefProductQueryRsp>> getCouponRefProductQuery(@RequestBody CouponRefProductQueryReq couponRefProductQueryReq) {
        return ok(productCouponService2.getCouponRefProductQuery(couponRefProductQueryReq));
    }

}
