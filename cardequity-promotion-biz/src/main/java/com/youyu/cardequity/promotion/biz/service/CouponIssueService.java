package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.dto.req.CouponIssueMsgDetailsReq;
import com.youyu.cardequity.promotion.dto.req.CouponIssueReq;

public interface CouponIssueService {

    /**
     * 创建发放优惠券
     *
     * @param couponIssueReq
     * @return
     */
    void createIssue(CouponIssueReq couponIssueReq);

    /**
     * 触发器到点正式执行发券操作
     */
    void processIssue(CouponIssueMsgDetailsReq couponIssueMsgDetailsReq);
}
