package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.dto.req.*;
import com.youyu.cardequity.promotion.dto.rsp.CouponRefProductQueryRsp;
import com.youyu.cardequity.promotion.dto.rsp.ProductCouponGetStatisticsRsp;
import com.youyu.cardequity.promotion.dto.rsp.ProductCouponQueryRsp;
import com.youyu.cardequity.promotion.dto.rsp.ProductCouponViewRsp;
import com.youyu.common.api.PageData;

import java.util.List;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月20日 10:00:00
 * @work 优惠券商品信息管理Service
 */
public interface ProductCouponService2 {

    /**
     * 添加优惠券
     *
     * @param productCouponAddReq
     */
    void add(ProductCouponAddReq productCouponAddReq);

    /**
     * 编辑优惠券
     *
     * @param productCouponEditReq
     */
    void edit(ProductCouponEditReq productCouponEditReq);

    /**
     * 优惠券上下架
     *
     * @param productCouponStatusReq
     */
    void setStatus(ProductCouponStatusReq productCouponStatusReq);

    /**
     * 优惠券查询
     *
     * @param productCouponQueryReq
     * @return
     */
    PageData<ProductCouponQueryRsp> get(ProductCouponQueryReq productCouponQueryReq);

    /**
     * 优惠券查询可领取对象统计
     *
     * @param productCouponGetStatisticsReq
     * @return
     */
    List<ProductCouponGetStatisticsRsp> getStatisticsByClientType(ProductCouponGetStatisticsReq productCouponGetStatisticsReq);

    /**
     * 优惠券查看详情
     *
     * @param productCouponViewReq
     * @return
     */
    ProductCouponViewRsp view(ProductCouponViewReq productCouponViewReq);

    /**
     * 添加优惠券关联商品
     *
     * @param couponRefProductReq
     */
    void addProduct(CouponRefProductReq couponRefProductReq);

    /**
     * 添加优惠券关联所有商品
     *
     * @param couponRefAllProductReq
     */
    void addAllProduct(CouponRefAllProductReq couponRefAllProductReq);

    /**
     * 查询优惠券关联商品
     *
     * @param couponRefProductQueryReq
     * @return
     */
    PageData<CouponRefProductQueryRsp> getCouponRefProductQuery(CouponRefProductQueryReq couponRefProductQueryReq);
}
