package com.youyu.cardequity.promotion.biz.strategy.couponissue;

import com.youyu.cardequity.common.base.annotation.StatusAndStrategyNum;
import com.youyu.cardequity.common.base.uidgenerator.UidGenerator;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponIssueJobMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueJobEntity;
import com.youyu.cardequity.promotion.enums.CouponIssueTriggerTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 优惠券延迟发放策略，其number为1，详细发放策略信息见 {@link CouponIssueTriggerTypeEnum}
 */
@Slf4j
@Component
@StatusAndStrategyNum(superClass = CouponIssueTriggerStrategy.class, number = "1", describe = "优惠券发放采用定时任务触发解决方案")
public class CouponIssueTriggerStrategy4Job extends CouponIssueTriggerStrategy {

    @Autowired
    private CouponIssueJobMapper couponIssueJobMapper;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    protected void doIssueTask(CouponIssueEntity couponIssue) {


    }

    protected  void issueTaskPreProcess(CouponIssueEntity couponIssue){
        // TODO: check
        log.debug("发放任务前置处理insert couponIssueJob中");
        CouponIssueJobEntity couponIssueJobEntity = couponIssueJobEntityMapping(couponIssue);
        couponIssueJobEntity.setCouponIssueJobId(uidGenerator.getUID2());
        couponIssueJobMapper.insertSelective(couponIssueJobEntity);

    }



    private CouponIssueJobEntity couponIssueJobEntityMapping(CouponIssueEntity couponIssue) {
        CouponIssueJobEntity couponIssueJobEntity = new CouponIssueJobEntity();
        couponIssueJobEntity.setCouponIssueId(couponIssue.getCouponIssueId());

        return couponIssueJobEntity;
    }

}
