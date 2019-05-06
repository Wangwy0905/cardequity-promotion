package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueEntity;
import com.youyu.cardequity.promotion.dto.CouponIssueCompensateDto;
import com.youyu.cardequity.promotion.dto.req.CouponIssueDetailReq;
import com.youyu.cardequity.promotion.dto.req.CouponIssueQueryReq;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 优惠券发放Mapper
 */
public interface CouponIssueMapper extends YyMapper<CouponIssueEntity> {

    /**
     * 根据couponIssueQueryReq查询优惠券发放
     *
     * @param couponIssueQueryReq
     * @return
     */
    List<CouponIssueEntity> getCouponIssueQuery(CouponIssueQueryReq couponIssueQueryReq);


    List<CouponIssueEntity> getCouponIssueCompensate(CouponIssueCompensateDto couponIssueCompensateDto);

    /**
     * 根据couponIssueDetailReq查询优惠券发放明细
     *
     * @param couponIssueDetailReq
     * @return
     */
    CouponIssueEntity getCouponIssueDetail(CouponIssueDetailReq couponIssueDetailReq);

    /**
     * 根据活动id和优惠券id查询优惠券发放
     * 注:默认目标类型是活动id
     *
     * @param activityId
     * @param couponId
     * @return
     */
    CouponIssueEntity getCouponIssueByActivityIdCouponId(@Param("activityId") String activityId, @Param("couponId") String couponId);
}
