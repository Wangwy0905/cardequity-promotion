package com.youyu.cardequity.promotion.biz.strategy.couponissue;

import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueEntity;

import static com.youyu.cardequity.promotion.enums.CouponIssueStatusEnum.ISSUING;

public abstract class CouponIssueTriggerStrategy {


    public void issueTask(CouponIssueEntity couponIssue) {
        checkCouponIssue(couponIssue);
        doIssueTask(couponIssue);
        couponIssue.setIssueStatus(ISSUING.getCode());
    }

    private void checkCouponIssue(CouponIssueEntity couponIssue) {

    }

    protected abstract void doIssueTask(CouponIssueEntity couponIssue);
}
