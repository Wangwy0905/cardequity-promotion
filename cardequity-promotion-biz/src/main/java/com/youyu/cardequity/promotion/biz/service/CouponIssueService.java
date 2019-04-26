package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.dto.req.CouponIssueReq;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 优惠券发放service
 */
public interface CouponIssueService {

    /**
     * 创建发放优惠券
     *
     * @param couponIssueReq
     * @return
     */
    void createIssue(CouponIssueReq couponIssueReq);
}
