package com.youyu.cardequity.promotion.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @Auther: zjm
 * @Date: 2019-05-06
 * @Description: 用于去查询未正常发放的couponIssue记录，以便定时任务完成补偿发券操作
 */
@Getter
@Setter
public class CouponIssueCompensateDto {
    /**
     * 目标对象类型 1:用户id 2:活动id
     */

    private String targetType;


    /**
     * 上下架 0-上架; 1-下架
     */
    private String isVisible;


    //todo
    /**
     * 发放状态：
     */
    private String issueStatus;

    /**
     * 用于扫描时间早于此时间的记录
     */
    private LocalDateTime dateLine;


}
