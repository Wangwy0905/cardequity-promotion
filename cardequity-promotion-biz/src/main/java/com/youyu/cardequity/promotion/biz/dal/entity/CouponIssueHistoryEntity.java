package com.youyu.cardequity.promotion.biz.dal.entity;

import com.youyu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * @Auther: zjm
 * @Date: 2019-04-28
 * @Description:
 */

@Setter
@Getter
public class CouponIssueHistoryEntity extends BaseEntity<String> {
    @Id
    @Column(name = "COUPON_ISSUE_HISTORY_ID")
    private String couponIssueHistoryId;

    @Column(name = "COUPON_ISSUE_ID")
    private String couponIssueId;

    @Column(name = "CLIENT_ID")
    private String clientId;

    @Column(name = "SEQUENCE_NUMBER")
    private String sequenceNumber;

    @Column(name = "ISSUE_RESULT")
    private String issueResult;


    @Override
    public String getId() {
        return couponIssueHistoryId;
    }

    @Override
    public void setId(String couponIssueHistoryId) {
        this.couponIssueHistoryId = couponIssueHistoryId;
    }
}
