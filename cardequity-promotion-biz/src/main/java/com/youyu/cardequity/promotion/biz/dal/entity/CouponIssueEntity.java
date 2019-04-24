package com.youyu.cardequity.promotion.biz.dal.entity;

import com.youyu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name = "TB_COUPON_ISSUE")
public class CouponIssueEntity extends BaseEntity<String> {

    @Id
    @Column(name = "COUPON_ISSUE_ID")
    private String couponIssueId;

    @Column(name = "COUPON_ID")
    private String couponId;

    @Column(name = "COUPON_NAME")
    private String couponName;

    @Column(name = "ISSUE_TYPE")
    private String issueType;

    @Column(name = "IS_VISIBLE")
    private String isVisible;

    @Column(name = "ISSUE_TIME")
    private String issueTime;

    @Column(name = "ISSUE_STATUS")
    private String issueStatus;

    @Column(name = "TRIGGER_TYPE")
    private String triggerType;

    @Column(name = "ISSUE_IDS")
    private List<String> issueIds = new ArrayList<>();

    public CouponIssueEntity() {
    }

    @Override
    public String getId() {
        return couponIssueId;
    }

    @Override
    public void setId(String couponIssueId) {
        this.couponIssueId = couponIssueId;
    }
}
