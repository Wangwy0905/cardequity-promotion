package com.youyu.cardequity.promotion.biz.strategy.couponissue;

import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueEntity;

public abstract class CouponIssueTriggerStrategy {


    public void issueTask(CouponIssueEntity couponIssue) {
        //todo
//        checkCouponIssue(couponIssue);
        issueTaskPreProcess(couponIssue);

        //todo 预留拓展空间
//        doIssueTask(couponIssue);
    }

    private void checkCouponIssue(CouponIssueEntity couponIssue) {

    }

    protected abstract void doIssueTask(CouponIssueEntity couponIssue);

    /**
     * 执行发放任务的前置处理
     *
     * @param couponIssueEntity
     */
    protected abstract void issueTaskPreProcess(CouponIssueEntity couponIssueEntity);
}
