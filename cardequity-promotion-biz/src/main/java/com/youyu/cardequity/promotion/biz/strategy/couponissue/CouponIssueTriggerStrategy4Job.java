package com.youyu.cardequity.promotion.biz.strategy.couponissue;

import com.youyu.cardequity.common.base.annotation.StatusAndStrategyNum;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StatusAndStrategyNum(superClass = CouponIssueTriggerStrategy.class, number = "1", describe = "优惠券发放采用定时任务触发解决方案")
public class CouponIssueTriggerStrategy4Job extends CouponIssueTriggerStrategy {

    @Override
    protected void doIssueTask(CouponIssueEntity couponIssue) {
        // TODO: 2019/4/24
        System.out.println("触发逻辑...");
    }

}
