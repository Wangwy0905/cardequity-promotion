package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.distributed.DistributedLockHandler;
import com.youyu.cardequity.common.base.uidgenerator.UidGenerator;
import com.youyu.cardequity.common.base.util.LocalDateUtils;
import com.youyu.cardequity.common.spring.service.RabbitConsumerService;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponIssueHistoryMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponIssueMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponQuotaRuleMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.ProductCouponMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponIssueHistoryEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponQuotaRuleEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.biz.enums.ProductCouponGetTypeEnum;
import com.youyu.cardequity.promotion.biz.enums.ProductCouponStatusEnum;
import com.youyu.cardequity.promotion.biz.service.ClientCouponService;
import com.youyu.cardequity.promotion.dto.message.ActivityCouponAcquireDto;
import com.youyu.cardequity.promotion.dto.req.UserInfo4CouponIssueDto;
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
import static com.youyu.cardequity.common.base.util.DateUtil.*;
import static com.youyu.cardequity.common.base.util.EnumUtil.getCardequityEnum;
import static com.youyu.cardequity.promotion.biz.constant.RedissonKeyConstant.CARDEQUITY_ACTIVITY_COUPON_ACTIVITY_CLIENT_COUPON;
import static com.youyu.cardequity.promotion.enums.CouponIssueResultEnum.ISSUED_FAILED;
import static com.youyu.cardequity.promotion.enums.CouponIssueResultEnum.ISSUED_SUCCESSED;
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
            doCreateIssueCoupon(activityCouponAcquire, couponIssueEntity, productCouponEntity, issueFlag);
            return null;
        });
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
        if (issueFlag) {
            doIssueCouponSuccess(activityCouponAcquire, couponIssueEntity, productCouponEntity);
            return;
        }
        doIssueCouponFail(activityCouponAcquire, couponIssueEntity);
    }

    /**
     * 发放成功
     *
     * @param activityCouponAcquire
     * @param couponIssueEntity
     * @param productCouponEntity
     */
    private void doIssueCouponSuccess(ActivityCouponAcquireDto activityCouponAcquire, CouponIssueEntity couponIssueEntity, ProductCouponEntity productCouponEntity) {
        createCouponIssueHistoryEntity(activityCouponAcquire, couponIssueEntity, ISSUED_SUCCESSED);

        List<UserInfo4CouponIssueDto> issueUserList = getIssueUserList(activityCouponAcquire);
        clientCouponService.insertClientCoupon(issueUserList, productCouponEntity, couponIssueEntity.getCouponIssueId());
    }

    /**
     * 创建用户发放的集合
     *
     * @param activityCouponAcquire
     * @return
     */
    private List<UserInfo4CouponIssueDto> getIssueUserList(ActivityCouponAcquireDto activityCouponAcquire) {
        List<UserInfo4CouponIssueDto> userInfo4CouponIssues = new ArrayList<>();

        UserInfo4CouponIssueDto userInfo4CouponIssueDto = new UserInfo4CouponIssueDto();
        userInfo4CouponIssueDto.setClientId(activityCouponAcquire.getClientId());
        userInfo4CouponIssues.add(userInfo4CouponIssueDto);
        return userInfo4CouponIssues;
    }

    /**
     * 发放失败
     *
     * @param activityCouponAcquire
     * @param couponIssueEntity
     */
    private void doIssueCouponFail(ActivityCouponAcquireDto activityCouponAcquire, CouponIssueEntity couponIssueEntity) {
        createCouponIssueHistoryEntity(activityCouponAcquire, couponIssueEntity, ISSUED_FAILED);
    }

    /**
     * 创建优惠券发放历史且入库
     *
     * @param activityCouponAcquire
     * @param couponIssueEntity
     * @param couponIssueResultEnum
     * @return
     */
    private void createCouponIssueHistoryEntity(ActivityCouponAcquireDto activityCouponAcquire, CouponIssueEntity couponIssueEntity, CouponIssueResultEnum couponIssueResultEnum) {
        CouponIssueHistoryEntity couponIssueHistoryEntity = new CouponIssueHistoryEntity();
        couponIssueHistoryEntity.setCouponIssueHistoryId(uidGenerator.getUID2());
        couponIssueHistoryEntity.setCouponIssueId(couponIssueEntity.getCouponIssueId());
        couponIssueHistoryEntity.setClientId(activityCouponAcquire.getClientId());
        couponIssueHistoryEntity.setIssueResult(couponIssueResultEnum.getCode());
        //todo
        couponIssueHistoryEntity.setSequenceNumber("");
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

        LocalDateTime nowLocalDateTime = LocalDateUtils.date2LocalDateTime(issueTime);
        if (nowLocalDateTime.isAfter(productCouponEntity.getAllowUseEndDate())) {
            return false;
        }

        ProductCouponStatusEnum productCouponStatusEnum = getCardequityEnum(ProductCouponStatusEnum.class, productCouponEntity.getStatus());
        if (!productCouponStatusEnum.isVisible()) {
            return false;
        }

        LocalDateTime nowTime = LocalDateTime.now();
        boolean isValid = nowTime.isAfter(productCouponEntity.getAllowUseBeginDate()) && nowTime.isBefore(productCouponEntity.getAllowUseEndDate());
        if (!isValid) {
            return false;
        }

        ProductCouponGetTypeEnum productCouponGetTypeEnum = getCardequityEnum(ProductCouponGetTypeEnum.class, productCouponEntity.getGetType());
        if (productCouponGetTypeEnum.isHanld()) {
            return false;
        }

        CouponQuotaRuleEntity couponQuotaRule = couponQuotaRuleMapper.selectByPrimaryKey(couponIssueEntity.getCouponId());
        Integer issueQuantity = couponQuotaRule.getMaxCount();
        if (nonNull(issueQuantity) && issueQuantity <= 0) {
            return false;
        }

        return true;
    }
}
