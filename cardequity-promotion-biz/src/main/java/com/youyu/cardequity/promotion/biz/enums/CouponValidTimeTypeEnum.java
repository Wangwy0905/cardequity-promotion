package com.youyu.cardequity.promotion.biz.enums;

import com.youyu.cardequity.common.base.enums.CardequityEnum;
import com.youyu.cardequity.promotion.biz.dal.entity.ClientCouponEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.dto.req.AddCouponReq2;
import com.youyu.cardequity.promotion.dto.req.EditCouponReq2;
import com.youyu.common.exception.BizException;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.youyu.cardequity.common.base.util.DateUtil.*;
import static com.youyu.cardequity.common.base.util.LocalDateUtils.date2LocalDateTime;
import static com.youyu.cardequity.common.base.util.StringUtil.eq;
import static com.youyu.cardequity.promotion.enums.ResultCode.COLLECTION_TIME_SETTING_NOT_REASONABLE;
import static com.youyu.cardequity.promotion.enums.ResultCode.COUPON_START_TIME_GREATER_EQ_CURRENT_TIME;
import static com.youyu.cardequity.promotion.enums.dict.CouponGetType.AUTO;
import static org.apache.commons.lang3.time.DateUtils.addMonths;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月13日 10:00:00
 * @work 优惠券有效时间类型枚举
 */
@Getter
public enum CouponValidTimeTypeEnum implements CardequityEnum {

    BY_DATE("0", "按日期") {
        @Override
        public void doCalcValidDate4Create(ProductCouponEntity productCouponEntity, AddCouponReq2 addCouponReq2) {
            productCouponEntity.setAllowUseBeginDate(addCouponReq2.getAllowUseBeginDate());
            productCouponEntity.setAllowUseEndDate(addCouponReq2.getAllowUseEndDate());
            if (eq(AUTO.getDictValue(), addCouponReq2.getGetType())) {
                return;
            }

            boolean validFlag = !addCouponReq2.getAllowGetBeginDate().isAfter(addCouponReq2.getAllowUseBeginDate()) && !addCouponReq2.getAllowGetEndDate().isAfter(addCouponReq2.getAllowUseEndDate());
            if (!validFlag) {
                throw new BizException(COLLECTION_TIME_SETTING_NOT_REASONABLE);
            }

            productCouponEntity.setAllowGetBeginDate(addCouponReq2.getAllowGetBeginDate());
            productCouponEntity.setAllowGetEndDate(addCouponReq2.getAllowGetEndDate());
        }

        @Override
        public void calcValidDate4Edit(EditCouponReq2 editCouponReq2, ProductCouponEntity existProductCouponEntity, ProductCouponEntity productCouponEntity) {
            LocalDateTime allowUseBeginDate = existProductCouponEntity.getAllowUseBeginDate();
            LocalDateTime allowUseEndDate = existProductCouponEntity.getAllowUseEndDate();
            if (eq(AUTO.getDictValue(), existProductCouponEntity.getGetType())) {
                return;
            }

            boolean validFlag = !editCouponReq2.getAllowGetBeginDate().isAfter(allowUseBeginDate) && !allowUseEndDate.isAfter(allowUseEndDate);
            if (!validFlag) {
                throw new BizException(COLLECTION_TIME_SETTING_NOT_REASONABLE);
            }

            productCouponEntity.setAllowGetBeginDate(editCouponReq2.getAllowGetBeginDate());
            productCouponEntity.setAllowGetEndDate(editCouponReq2.getAllowGetEndDate());
        }

        @Override
        public void calcClientCouponValidTime(ClientCouponEntity clientCouponEntity, ProductCouponEntity couponEntity) {
            clientCouponEntity.setValidStartDate(couponEntity.getAllowUseBeginDate());
            clientCouponEntity.setValidEndDate(couponEntity.getAllowUseEndDate());
        }

        @Override
        public String getCouponStatus(ProductCouponEntity productCouponEntity) {
            LocalDate nowLocalDate = LocalDate.now();

            LocalDate allowUseBeginDate = productCouponEntity.getAllowUseBeginDate().toLocalDate();
            if (nowLocalDate.isBefore(allowUseBeginDate)) {
                return "未开始";
            }

            LocalDate allowUseEndDate = productCouponEntity.getAllowUseEndDate().toLocalDate();
            if (nowLocalDate.isAfter(allowUseEndDate)) {
                return "已过期";
            }

            return "有效中";
        }
    },
    BY_DAY("1", "按天数") {
        @Override
        public void doCalcValidDate4Create(ProductCouponEntity productCouponEntity, AddCouponReq2 addCouponReq2) {
            productCouponEntity.setValIdTerm(addCouponReq2.getValidTerm());
            super.doCalcValidDate4Create(productCouponEntity, addCouponReq2);
        }

        @Override
        public void calcClientCouponValidTime(ClientCouponEntity clientCouponEntity, ProductCouponEntity couponEntity) {
            LocalDateTime now = LocalDateTime.now();
            Integer valIdTerm = couponEntity.getValIdTerm();
            clientCouponEntity.setValidStartDate(now);
            clientCouponEntity.setValidEndDate(now.plusDays(valIdTerm));
        }
    },
    CURRENT_MONTH_VALID("2", "当月有效") {
        @Override
        public void calcClientCouponValidTime(ClientCouponEntity clientCouponEntity, ProductCouponEntity couponEntity) {
            LocalDateTime now = LocalDateTime.now();
            clientCouponEntity.setValidStartDate(now);

            LocalDateTime endDateTime = date2LocalDateTime(string2Date(date2String(addMonths(now(), 1), "yyyy-MM") + "-01 00:00:00"));
            clientCouponEntity.setValidEndDate(endDateTime);
        }
    };

    private String code;

    private String msg;

    CouponValidTimeTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 根据优惠券有效日期类型计算日期
     *
     * @param productCouponEntity
     * @param addCouponReq2
     */
    public void calcValidDate4Create(ProductCouponEntity productCouponEntity, AddCouponReq2 addCouponReq2) {
        productCouponEntity.setValidTimeType(addCouponReq2.getValidTimeType());
        doCalcValidDate4Create(productCouponEntity, addCouponReq2);
    }

    /**
     * 添加:计算优惠券领取有效天数
     *
     * @param productCouponEntity
     * @param addCouponReq2
     */
    protected void doCalcValidDate4Create(ProductCouponEntity productCouponEntity, AddCouponReq2 addCouponReq2) {
        if (eq(AUTO.getDictValue(), addCouponReq2.getGetType())) {
            return;
        }
        checkValidDate(addCouponReq2.getAllowGetBeginDate().toLocalDate());
        productCouponEntity.setAllowGetBeginDate(addCouponReq2.getAllowGetBeginDate());
        productCouponEntity.setAllowGetEndDate(addCouponReq2.getAllowGetEndDate());
    }

    /**
     * 编辑:计算优惠券领取有效天数
     *
     * @param editCouponReq2
     * @param existProductCouponEntity
     * @param productCouponEntity
     */
    public void calcValidDate4Edit(EditCouponReq2 editCouponReq2, ProductCouponEntity existProductCouponEntity, ProductCouponEntity productCouponEntity) {
        if (eq(AUTO.getDictValue(), existProductCouponEntity.getGetType())) {
            return;
        }
        checkValidDate(editCouponReq2.getAllowGetBeginDate().toLocalDate());

        productCouponEntity.setAllowGetBeginDate(editCouponReq2.getAllowGetBeginDate());
        productCouponEntity.setAllowGetEndDate(editCouponReq2.getAllowGetEndDate());
    }

    /**
     * 检测开始领取时间是否大于等于当前时间
     *
     * @param allowGetBeginDate
     */
    private void checkValidDate(LocalDate allowGetBeginDate) {
        LocalDate nowLocalDate = LocalDate.now();
        if (allowGetBeginDate.isBefore(nowLocalDate)) {
            throw new BizException(COUPON_START_TIME_GREATER_EQ_CURRENT_TIME);
        }
    }

    /**
     * 计算客户优惠券有效时间区间
     *
     * @param clientCouponEntity
     * @param couponEntity
     */
    public abstract void calcClientCouponValidTime(ClientCouponEntity clientCouponEntity, ProductCouponEntity couponEntity);

    /**
     * 获取优惠券状态
     *
     * @param productCouponEntity
     * @return
     */
    public String getCouponStatus(ProductCouponEntity productCouponEntity) {
        return "——";
    }
}
