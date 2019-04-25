package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.dto.req.CouponIssueReq;

public interface CouponIssueService {

    /**
     * 创建发放优惠券
     *
     * @param couponIssueReq
     * @return
     */
    void createIssue(CouponIssueReq couponIssueReq);
}
