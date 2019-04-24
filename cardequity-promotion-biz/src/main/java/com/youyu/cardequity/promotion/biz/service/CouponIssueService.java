package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.dto.req.CouponIssueReqDto;

public interface CouponIssueService {

    /**
     * 创建发放优惠券
     *
     * @param couponIssueReqDto
     * @return
     */
    void createIssue(CouponIssueReqDto couponIssueReqDto);
}
