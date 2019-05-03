package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueEntity;
import com.youyu.cardequity.promotion.dto.req.CouponIssueDetailReq;
import com.youyu.cardequity.promotion.dto.req.CouponIssueQueryReq;
import com.youyu.common.mapper.YyMapper;

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

    /**
     * 根据couponIssueDetailReq查询优惠券发放明细
     *
     * @param couponIssueDetailReq
     * @return
     */
    CouponIssueEntity getCouponIssueDetail(CouponIssueDetailReq couponIssueDetailReq);

}
