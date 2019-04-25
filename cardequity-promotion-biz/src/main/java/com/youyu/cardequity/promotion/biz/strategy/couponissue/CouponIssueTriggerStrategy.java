package com.youyu.cardequity.promotion.biz.strategy.couponissue;

import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueEntity;

public abstract class CouponIssueTriggerStrategy {


    public void issueTask(CouponIssueEntity couponIssue) {
        checkCouponIssue(couponIssue);
        doIssueTask(couponIssue);
    }

    private void checkCouponIssue(CouponIssueEntity couponIssue) {

    }

    protected abstract void doIssueTask(CouponIssueEntity couponIssue);
}
