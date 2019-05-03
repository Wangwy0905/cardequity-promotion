package com.youyu.cardequity.promotion.biz.dal.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Auther: zjm
 * @Date: 2019-04-29
 * @Description:
 */
@Setter
@Getter
public class CouponIssueHistoryDetailsEntity {

    /**
     * 发放流水表主键ID
     */
    private String couponIssueHistoryId;

    /**
     * 发放券ID
     */
    private String couponIssueId;

    /**
     * 序号
     */
    private String sequenceNumber;


    /**
     * 用户账号ID
     */
    private String clientId;

    /**
     * 发放结果：1：成功；2：失败
     */
    private String issueResult;

    /**
     * 券的使用状态：状态:0-正常 1-使用中 2-已使用
     */
    private String couponUseStatus;

    /**
     * 使用时间：券状态为已使用才会有值
     */
    private String usedDate;

    /**
     * 订单编号
     */
    private String orderId;

    /**
     * 券有效期结束时间
     */
    private Date validEndDate;

}
