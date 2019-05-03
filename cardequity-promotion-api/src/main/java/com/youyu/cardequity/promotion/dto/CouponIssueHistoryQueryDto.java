package com.youyu.cardequity.promotion.dto;

import com.youyu.cardequity.promotion.dto.req.CouponIssueHistoryQueryReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @Auther: zjm
 * @Date: 2019-04-30
 * @Description:
 */
@Setter
@Getter
public class CouponIssueHistoryQueryDto extends CouponIssueHistoryQueryReq {
    /**
     * ：发放状态 1-发放成功；2-发放失败或未发放
     */
    private String issueResult;

    /**
     * 优惠券使用状态：0-正常 1-使用中 2-已使用
     */
    private String clientCouponUseStatus;

    /**
     * 是否过期：0-未过期；1-过期
     */
    private String couponInValid;

}
