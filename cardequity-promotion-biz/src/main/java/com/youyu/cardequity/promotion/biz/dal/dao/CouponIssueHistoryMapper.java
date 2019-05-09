package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueHistoryDetailsEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueHistoryEntity;
import com.youyu.cardequity.promotion.dto.CouponIssueHistoryQueryDto;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: zjm
 * @Date: 2019-04-28
 * @Description:
 */
public interface CouponIssueHistoryMapper extends YyMapper<CouponIssueHistoryEntity> {

    List<CouponIssueHistoryDetailsEntity> getCouponIssueHistoryDetails(
            @Param("couponIssueHistoryQueryDto") CouponIssueHistoryQueryDto couponIssueHistoryQueryDto);
}
