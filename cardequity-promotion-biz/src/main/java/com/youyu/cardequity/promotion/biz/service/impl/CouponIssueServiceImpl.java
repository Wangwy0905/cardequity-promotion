package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.uidgenerator.UidGenerator;
import com.youyu.cardequity.common.base.util.LocalDateUtils;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponIssueMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.ProductCouponMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.biz.enums.ProductCouponGetTypeEnum;
import com.youyu.cardequity.promotion.biz.enums.ProductCouponStatusEnum;
import com.youyu.cardequity.promotion.biz.service.CouponIssueService;
import com.youyu.cardequity.promotion.biz.strategy.couponissue.CouponIssueTriggerStrategy;
import com.youyu.cardequity.promotion.dto.req.CouponIssueReqDto;
import com.youyu.common.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

import static com.youyu.cardequity.common.base.bean.CustomHandler.getBeanInstance;
import static com.youyu.cardequity.common.base.util.DateUtil.*;
import static com.youyu.cardequity.common.base.util.EnumUtil.getCardequityEnum;
import static com.youyu.cardequity.promotion.enums.CouponIssueStatusEnum.NOT_ISSUE;
import static com.youyu.cardequity.promotion.enums.CouponIssueTriggerTypeEnum.DELAY_JOB_TRIGGER_TYPE;
import static com.youyu.cardequity.promotion.enums.CouponIssueVisibleEnum.INVISIBLE;
import static com.youyu.cardequity.promotion.enums.ResultCode.*;

@Service
public class CouponIssueServiceImpl implements CouponIssueService {

    @Autowired
    private CouponIssueMapper couponIssueMapper;
    @Autowired
    private ProductCouponMapper productCouponMapper;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    @Transactional
    public void createIssue(CouponIssueReqDto couponIssueReq) {
        checkCoupon(couponIssueReq);
        CouponIssueEntity couponIssue = createCouponIssueEntity(couponIssueReq);
        triggerIssueTask(couponIssue);

        couponIssueMapper.insertSelective(couponIssue);
    }

    /**
     * 发放优惠券规则检验
     *
     * @param couponIssueReq
     */
    private void checkCoupon(CouponIssueReqDto couponIssueReq) {
        String couponId = couponIssueReq.getCouponId();
        ProductCouponEntity productCoupon = productCouponMapper.selectByPrimaryKey(couponId);

        ProductCouponStatusEnum productCouponStatusEnum = getCardequityEnum(ProductCouponStatusEnum.class, productCoupon.getStatus());
        if (!productCouponStatusEnum.isVisible()) {
            throw new BizException(INVISIBLE_COUPON_CANNOT_BE_ISSUED);
        }

        LocalDateTime nowTime = LocalDateTime.now();
        boolean isValid = nowTime.isAfter(productCoupon.getAllowUseBeginDate()) && nowTime.isBefore(productCoupon.getAllowUseEndDate());
        if (!isValid) {
            throw new BizException(COUPON_HAS_EXPIRED);
        }

        ProductCouponGetTypeEnum productCouponGetTypeEnum = getCardequityEnum(ProductCouponGetTypeEnum.class, productCoupon.getGetType());
        if (productCouponGetTypeEnum.isHanld()) {
            throw new BizException(MANUAL_COUPON_CANNOT_BE_ISSUED);
        }

        Date issueTime = string2Date(couponIssueReq.getIssueTime(), YYYY_MM_DD_HH_MM);
        Date now = now();
        if (now.after(issueTime)) {
            throw new BizException(ISSUE_TIME_MUST_GREATER_CURRENT_TIME);
        }

        LocalDateTime nowLocalDateTime = LocalDateUtils.date2LocalDateTime(now);
        if (nowLocalDateTime.isAfter(productCoupon.getAllowUseEndDate())) {
            throw new BizException(COUPON_END_DATE_MUST_GREATER_CURRENT_DATE);
        }
    }

    /**
     * 创建发放优惠券对象
     *
     * @param couponIssueReq
     * @return
     */
    private CouponIssueEntity createCouponIssueEntity(CouponIssueReqDto couponIssueReq) {
        CouponIssueEntity couponIssueEntity = new CouponIssueEntity();
        couponIssueEntity.setCouponIssueId(uidGenerator.getUID2());
        couponIssueEntity.setCouponId(couponIssueReq.getCouponId());
        couponIssueEntity.setCouponName(couponIssueReq.getCouponName());
        couponIssueEntity.setIssueTime(couponIssueReq.getIssueTime());
        couponIssueEntity.setIssueType(couponIssueReq.getIssueType());
        couponIssueEntity.setIsVisible(INVISIBLE.getCode());
        couponIssueEntity.setIssueStatus(NOT_ISSUE.getCode());
        couponIssueEntity.setTriggerType(DELAY_JOB_TRIGGER_TYPE.getCode());
        couponIssueEntity.setIssueIds(couponIssueReq.getIssueIds());
        return couponIssueEntity;
    }

    /**
     * 执行触发优惠券任务
     *
     * @param couponIssue
     */
    private void triggerIssueTask(CouponIssueEntity couponIssue) {
        String triggerType = couponIssue.getTriggerType();
        CouponIssueTriggerStrategy couponIssueTriggerStrategy = getBeanInstance(CouponIssueTriggerStrategy.class, triggerType);
        couponIssueTriggerStrategy.issueTask(couponIssue);
    }

}
