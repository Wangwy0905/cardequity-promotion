package com.youyu.cardequity.promotion.biz.service.impl;

import com.github.pagehelper.PageInfo;
import com.youyu.cardequity.common.base.uidgenerator.UidGenerator;
import com.youyu.cardequity.common.base.util.LocalDateUtils;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponIssueMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponQuotaRuleMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.ProductCouponMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponQuotaRuleEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.biz.enums.ProductCouponGetTypeEnum;
import com.youyu.cardequity.promotion.biz.enums.ProductCouponStatusEnum;
import com.youyu.cardequity.promotion.biz.service.CouponIssueService;
import com.youyu.cardequity.promotion.dto.req.*;
import com.youyu.cardequity.promotion.dto.rsp.CouponIssueDetailRsp;
import com.youyu.cardequity.promotion.dto.rsp.CouponIssueQueryRsp;
import com.youyu.common.api.PageData;
import com.youyu.common.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static com.github.pagehelper.page.PageMethod.startPage;
import static com.youyu.cardequity.common.base.util.DateUtil.*;
import static com.youyu.cardequity.common.base.util.EnumUtil.getCardequityEnum;
import static com.youyu.cardequity.common.base.util.PaginationUtils.convert;
import static com.youyu.cardequity.common.base.util.StringUtil.eq;
import static com.youyu.cardequity.promotion.enums.CouponIssueStatusEnum.NOT_ISSUE;
import static com.youyu.cardequity.promotion.enums.CouponIssueTriggerTypeEnum.DELAY_JOB_TRIGGER_TYPE;
import static com.youyu.cardequity.promotion.enums.CouponIssueVisibleEnum.INVISIBLE;
import static com.youyu.cardequity.promotion.enums.ResultCode.*;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.split;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 优惠券发放service 实现
 */
@Service
public class CouponIssueServiceImpl implements CouponIssueService {

    @Autowired
    private CouponIssueMapper couponIssueMapper;
    @Autowired
    private ProductCouponMapper productCouponMapper;
    @Autowired
    private CouponQuotaRuleMapper couponQuotaRuleMapper;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    @Transactional
    public void createIssue(CouponIssueReq couponIssueReq) {
        String couponId = couponIssueReq.getCouponId();
        ProductCouponEntity productCoupon = productCouponMapper.selectByPrimaryKey(couponId);

        CouponIssueEntity couponIssue = createCouponIssueEntity(couponIssueReq, productCoupon);
        checkCoupon(couponIssue, productCoupon);

        couponIssueMapper.insertSelective(couponIssue);
    }

    @Override
    public PageData<CouponIssueQueryRsp> getCouponIssueQuery(CouponIssueQueryReq couponIssueQueryReq) {
        startPage(couponIssueQueryReq.getPageNo(), couponIssueQueryReq.getPageSize());

        List<CouponIssueEntity> couponIssueEntities = couponIssueMapper.getCouponIssueQuery(couponIssueQueryReq);
        PageInfo<CouponIssueEntity> pageInfo = new PageInfo<>(couponIssueEntities);

        return convert(pageInfo, CouponIssueQueryRsp.class);
    }

    @Override
    public CouponIssueDetailRsp getCouponIssueDetail(CouponIssueDetailReq couponIssueDetailReq) {
        CouponIssueEntity couponIssue = couponIssueMapper.getCouponIssueDetail(couponIssueDetailReq);
        ProductCouponEntity productCoupon = productCouponMapper.selectByPrimaryKey(couponIssue.getCouponId());
        return getCouponIssueDetailRsp(couponIssue, productCoupon);
    }

    @Override
    @Transactional
    public void delete(CouponIssueDeleteReq couponIssueDeleteReq) {
        List<String> couponIssueIds = couponIssueDeleteReq.getCouponIssueIds();

        doDelete(couponIssueIds);
    }

    @Override
    public void setVisible(CouponIssueVisibleReq couponIssueVisibleReq) {
        CouponIssueEntity couponIssue = new CouponIssueEntity();
        couponIssue.setCouponIssueId(couponIssueVisibleReq.getCouponIssueId());
        couponIssue.setIsVisible(couponIssueVisibleReq.getIsVisible());

        couponIssueMapper.updateByPrimaryKeySelective(couponIssue);
    }

    /**
     * @param couponIssue
     * @param productCoupon
     * @return
     */
    private CouponIssueDetailRsp getCouponIssueDetailRsp(CouponIssueEntity couponIssue, ProductCouponEntity productCoupon) {
        Date issueDate = string2Date(couponIssue.getIssueTime(), YYYY_MM_DD_HH_MM);

        CouponIssueDetailRsp couponIssueDetailRsp = new CouponIssueDetailRsp();
        couponIssueDetailRsp.setCouponId(couponIssue.getCouponId());
        couponIssueDetailRsp.setCouponName(couponIssue.getCouponName());
        couponIssueDetailRsp.setCouponType(productCoupon.getCouponType());
        couponIssueDetailRsp.setCouponStatus(productCoupon.getStatus());
        couponIssueDetailRsp.setIssueDate(date2String(issueDate, YYYY_MM_DD));
        couponIssueDetailRsp.setIssueTime(date2String(issueDate, HH_MM_SS));
        couponIssueDetailRsp.setObjectType(couponIssue.getObjectType());
        couponIssueDetailRsp.setIssueIds(asList(split(couponIssue.getIssueIds(), ",")));
        return couponIssueDetailRsp;
    }

    /**
     * 发放优惠券规则检验
     *
     * @param couponIssue
     * @param productCoupon
     */
    private void checkCoupon(CouponIssueEntity couponIssue, ProductCouponEntity productCoupon) {
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

        Date issueTime = string2Date(couponIssue.getIssueTime(), YYYY_MM_DD_HH_MM);
        Date now = now();
        if (now.after(issueTime)) {
            throw new BizException(ISSUE_TIME_MUST_GREATER_CURRENT_TIME);
        }

        LocalDateTime nowLocalDateTime = LocalDateUtils.date2LocalDateTime(issueTime);
        if (nowLocalDateTime.isAfter(productCoupon.getAllowUseEndDate())) {
            throw new BizException(COUPON_END_DATE_MUST_GREATER_CURRENT_DATE);
        }

        CouponQuotaRuleEntity couponQuotaRule = couponQuotaRuleMapper.selectByPrimaryKey(couponIssue.getCouponId());
        Integer issueQuantity = couponQuotaRule.getMaxCount();
        if (nonNull(issueQuantity) && issueQuantity <= 0) {
            throw new BizException(COUPON_ISSUE_QUANTITY_CANNOT_LESS_ZERO);
        }
    }

    /**
     * 创建发放优惠券对象
     *
     * @param couponIssueReq
     * @param productCoupon
     * @return
     */
    private CouponIssueEntity createCouponIssueEntity(CouponIssueReq couponIssueReq, ProductCouponEntity productCoupon) {
        CouponIssueEntity couponIssueEntity = new CouponIssueEntity();
        couponIssueEntity.setCouponIssueId(uidGenerator.getUID2());
        couponIssueEntity.setCouponId(couponIssueReq.getCouponId());
        couponIssueEntity.setCouponName(productCoupon.getCouponName());
        couponIssueEntity.setIssueTime(couponIssueReq.getIssueTime());
        couponIssueEntity.setObjectType(couponIssueReq.getObjectType());
        couponIssueEntity.setIsVisible(INVISIBLE.getCode());
        couponIssueEntity.setIssueStatus(NOT_ISSUE.getCode());
        couponIssueEntity.setTriggerType(DELAY_JOB_TRIGGER_TYPE.getCode());
        couponIssueEntity.setIssueIds(join(couponIssueReq.getIssueIds(), ","));
        return couponIssueEntity;
    }

    /**
     * 删除
     *
     * @param couponIssueIds
     */
    private void doDelete(List<String> couponIssueIds) {
        CouponIssueDetailReq couponIssueDetailReq = null;
        for (String couponIssueId : couponIssueIds) {
            couponIssueDetailReq = new CouponIssueDetailReq();
            couponIssueDetailReq.setCouponIssueId(couponIssueId);

            CouponIssueEntity couponIssue = couponIssueMapper.getCouponIssueDetail(couponIssueDetailReq);
            if (!eq(NOT_ISSUE.getCode(), couponIssue.getIssueStatus())) {
                throw new BizException(COUPON_NOT_ISSUE_STATUS_CAN_DELETE);
            }
            couponIssueMapper.deleteByPrimaryKey(couponIssueDetailReq.getCouponIssueId());
        }
    }

}
