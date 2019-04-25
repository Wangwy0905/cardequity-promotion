package com.youyu.cardequity.promotion.biz.dal.entity;

import com.youyu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Auther: zjm
 * @Date: 2019-04-25
 * @Description:
 */
@Getter
@Setter
@Table(name = "TB_COUPON_ISSUE")
public class CouponIssueJobEntity extends BaseEntity<String> {
    /**
     * 优惠券发放任务的触发器ID
     */
    @Id
    @Column(name = "COUPON_ISSUE_JOB_ID")
    private String couponIssueJobId;

    /**
     * 优惠券发放任务列表ID
     */
    @Column(name = "COUPON_ISSUE_ID")
    private String couponIssueId;

    @Override
    public String getId() {
        return couponIssueJobId;
    }

    @Override
    public void setId(String id) {
        this.couponIssueJobId = id;
    }
}
