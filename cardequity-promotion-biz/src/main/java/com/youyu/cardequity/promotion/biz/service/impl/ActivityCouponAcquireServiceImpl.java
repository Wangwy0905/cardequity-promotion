package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.distributed.DistributedLockHandler;
import com.youyu.cardequity.common.base.uidgenerator.UidGenerator;
import com.youyu.cardequity.common.base.util.LocalDateUtils;
import com.youyu.cardequity.common.spring.service.RabbitConsumerService;
import com.youyu.cardequity.promotion.biz.dal.dao.*;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueHistoryEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponQuotaRuleEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.biz.enums.ProductCouponGetTypeEnum;
import com.youyu.cardequity.promotion.biz.enums.ProductCouponStatusEnum;
import com.youyu.cardequity.promotion.biz.service.ClientCouponService;
import com.youyu.cardequity.promotion.dto.message.ActivityCouponAcquireDto;
import com.youyu.cardequity.promotion.enums.CouponIssueResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;
import static com.youyu.cardequity.common.base.util.DateUtil.*;
import static com.youyu.cardequity.common.base.util.EnumUtil.getCardequityEnum;
import static com.youyu.cardequity.common.base.util.StringUtil.eq;
import static com.youyu.cardequity.promotion.biz.constant.RedissonKeyConstant.CARDEQUITY_ACTIVITY_COUPON_ACTIVITY_CLIENT_COUPON;
import static com.youyu.cardequity.promotion.enums.CouponIssueResultEnum.ISSUED_FAILED;
import static com.youyu.cardequity.promotion.enums.CouponIssueResultEnum.ISSUED_SUCCESSED;
import static com.youyu.cardequity.promotion.enums.CouponIssueStatusEnum.*;
import static com.youyu.cardequity.promotion.enums.CouponIssueVisibleEnum.INVISIBLE;
import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 活动发放用户优惠券Service 实现
 */
@Slf4j
@Service
public class ActivityCouponAcquireServiceImpl implements RabbitConsumerService {

    @Autowired
    private CouponIssueMapper couponIssueMapper;
    @Autowired
    private ProductCouponMapper productCouponMapper;
    @Autowired
    private CouponQuotaRuleMapper couponQuotaRuleMapper;
    @Autowired
    private CouponIssueHistoryMapper couponIssueHistoryMapper;
    @Autowired
    private ClientCouponMapper clientCouponMapper;

    @Autowired
    private ClientCouponService clientCouponService;

    @Autowired
    private UidGenerator uidGenerator;
    @Autowired
    private DistributedLockHandler distributedLockHandler;

    @Override
    @Transactional
    public void consumerMessage(String jsonMessage, String queueFlag) {
        log.info("活动发放优惠券发送json消息:[{}]", jsonMessage);
        ActivityCouponAcquireDto activityCouponAcquire = parseObject(jsonMessage, ActivityCouponAcquireDto.class);
        CouponIssueEntity couponIssueEntity = couponIssueMapper.getCouponIssueByActivityIdCouponId(activityCouponAcquire.getActivityId(), activityCouponAcquire.getCouponId());
        if (isNull(couponIssueEntity)) {
            return;
        }

        ProductCouponEntity productCouponEntity = productCouponMapper.selectByPrimaryKey(couponIssueEntity.getCouponId());
        boolean issueFlag = canIssueActivityCoupon(couponIssueEntity, productCouponEntity);

        String lockKey = format(CARDEQUITY_ACTIVITY_COUPON_ACTIVITY_CLIENT_COUPON, activityCouponAcquire.getActivityId(), activityCouponAcquire.getClientId(), activityCouponAcquire.getCouponId()).intern();
        distributedLockHandler.tryLock(lockKey, () -> {
            doIssueStatus(couponIssueEntity);
            doCreateIssueCoupon(activityCouponAcquire, couponIssueEntity, productCouponEntity, issueFlag);
            return null;
        });
    }

    /**
     * 修改优惠券发放状态
     *
     * @param couponIssueEntity
     */
    private void doIssueStatus(CouponIssueEntity couponIssueEntity) {
        String issueStatus = couponIssueEntity.getIssueStatus();
        if (eq(issueStatus, NOT_ISSUE.getCode())) {
            couponIssueEntity.setIssueStatus(ISSUING.getCode());
            couponIssueMapper.updateByPrimaryKeySelective(couponIssueEntity);
        }
    }

    /**
     * 创建优惠券发放
     *
     * @param activityCouponAcquire
     * @param couponIssueEntity
     * @param productCouponEntity
     * @param issueFlag
     */
    private void doCreateIssueCoupon(ActivityCouponAcquireDto activityCouponAcquire, CouponIssueEntity couponIssueEntity, ProductCouponEntity productCouponEntity, boolean issueFlag) {
        String couponIssueHistoryId = uidGenerator.getUID2();
        if (issueFlag) {
            doIssueCouponSuccess(activityCouponAcquire, couponIssueEntity, productCouponEntity, couponIssueHistoryId);
            return;
        }
        doIssueCouponFail(activityCouponAcquire, couponIssueEntity, couponIssueHistoryId);
    }

    /**
     * 发放成功
     *
     * @param activityCouponAcquire
     * @param couponIssueEntity
     * @param productCouponEntity
     * @param couponIssueHistoryId
     */
    private void doIssueCouponSuccess(ActivityCouponAcquireDto activityCouponAcquire, CouponIssueEntity couponIssueEntity, ProductCouponEntity productCouponEntity, String couponIssueHistoryId) {
        createCouponIssueHistoryEntity(activityCouponAcquire, couponIssueEntity, ISSUED_SUCCESSED, couponIssueHistoryId);

        List<CouponIssueHistoryEntity> issueSuccessedHistoryList = getIssueSuccessedHistoryList(activityCouponAcquire, couponIssueHistoryId);
        log.info("插入活动发放客户优惠券数据:[{}]", toJSONString(issueSuccessedHistoryList));
        clientCouponService.insertClientCoupon(issueSuccessedHistoryList, productCouponEntity, couponIssueEntity.getCouponIssueId());
    }

    /**
     * 创建用户发放的集合
     *
     * @param activityCouponAcquire
     * @param couponIssueHistoryId
     * @return
     */
    private List<CouponIssueHistoryEntity> getIssueSuccessedHistoryList(ActivityCouponAcquireDto activityCouponAcquire, String couponIssueHistoryId) {
        List<CouponIssueHistoryEntity> couponIssueHistoryEntities = new ArrayList<>();
        CouponIssueHistoryEntity couponIssueHistoryEntity = new CouponIssueHistoryEntity();
        couponIssueHistoryEntity.setClientId(activityCouponAcquire.getClientId());
        couponIssueHistoryEntity.setCouponIssueHistoryId(couponIssueHistoryId);
        couponIssueHistoryEntity.setIssueResult(ISSUED_SUCCESSED.getCode());
        couponIssueHistoryEntities.add(couponIssueHistoryEntity);
        return couponIssueHistoryEntities;
    }

    /**
     * 发放失败
     *
     * @param activityCouponAcquire
     * @param couponIssueEntity
     * @param couponIssueHistoryId
     */
    private void doIssueCouponFail(ActivityCouponAcquireDto activityCouponAcquire, CouponIssueEntity couponIssueEntity, String couponIssueHistoryId) {
        createCouponIssueHistoryEntity(activityCouponAcquire, couponIssueEntity, ISSUED_FAILED, couponIssueHistoryId);
    }

    /**
     * 创建优惠券发放历史且入库
     *
     * @param activityCouponAcquire
     * @param couponIssueEntity
     * @param couponIssueResultEnum
     * @param couponIssueHistoryId
     * @return
     */
    private void createCouponIssueHistoryEntity(ActivityCouponAcquireDto activityCouponAcquire, CouponIssueEntity couponIssueEntity, CouponIssueResultEnum couponIssueResultEnum, String couponIssueHistoryId) {
        CouponIssueHistoryEntity couponIssueHistoryEntity = new CouponIssueHistoryEntity();
        couponIssueHistoryEntity.setCouponIssueHistoryId(couponIssueHistoryId);
        couponIssueHistoryEntity.setCouponIssueId(couponIssueEntity.getCouponIssueId());
        couponIssueHistoryEntity.setClientId(activityCouponAcquire.getClientId());
        couponIssueHistoryEntity.setIssueResult(couponIssueResultEnum.getCode());
        couponIssueHistoryMapper.insertSelective(couponIssueHistoryEntity);
    }

    /**
     * 检查优惠券发放是否合法
     *
     * @param couponIssueEntity
     * @param productCouponEntity
     */
    private boolean canIssueActivityCoupon(CouponIssueEntity couponIssueEntity, ProductCouponEntity productCouponEntity) {
        Date issueTime = string2Date(couponIssueEntity.getIssueTime(), YYYY_MM_DD_HH_MM);

        if (now().before(issueTime)) {
            return false;
        }

        LocalDateTime issueTimeDateTime = LocalDateUtils.date2LocalDateTime(issueTime);
        if (issueTimeDateTime.isAfter(productCouponEntity.getAllowUseEndDate())) {
            return false;
        }

        ProductCouponStatusEnum productCouponStatusEnum = getCardequityEnum(ProductCouponStatusEnum.class, productCouponEntity.getStatus());
        if (!productCouponStatusEnum.isVisible()) {
            return false;
        }

        ProductCouponGetTypeEnum productCouponGetTypeEnum = getCardequityEnum(ProductCouponGetTypeEnum.class, productCouponEntity.getGetType());
        if (productCouponGetTypeEnum.isUserGet()) {
            return false;
        }

        CouponQuotaRuleEntity couponQuotaRule = couponQuotaRuleMapper.selectByPrimaryKey(couponIssueEntity.getCouponId());
        Integer usedQuantity = clientCouponMapper.getCountByCouponId(couponIssueEntity.getCouponId());
        int issueQuantity = couponQuotaRule.getMaxCount() - usedQuantity;
        if (nonNull(issueQuantity) && issueQuantity <= 0) {
            return false;
        }

        if (eq(couponIssueEntity.getIssueStatus(), ISSUED.getCode())) {
            return false;
        }

        if (eq(couponIssueEntity.getIsVisible(), INVISIBLE.getCode())) {
            return false;
        }

        return true;
    }
}
