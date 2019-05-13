package com.youyu.cardequity.promotion.biz.enums;

import com.youyu.cardequity.common.base.enums.CardequityEnum;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponGetOrUseFreqRuleEntity;
import com.youyu.cardequity.promotion.dto.req.AddCouponReq2;
import lombok.Getter;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月13日 10:00:00
 * @work 优惠券频率领取枚举
 */
@Getter
public enum CouponUnitEnum implements CardequityEnum {

    COLLECTION_TIME("a", "领取时间内"){
        @Override
        public void setPersonTotalNum(CouponGetOrUseFreqRuleEntity couponGetOrUseFreqRuleEntity, AddCouponReq2 addCouponReq2) {
            couponGetOrUseFreqRuleEntity.setPersonTotalNum(addCouponReq2.getAllowCount());
        }
    },
    EVERY_DAY("0", "每天"),
    EVERY_WEEK("1", "每周"),
    EVERY_MONTH("2", "每月"),
    EVERY_YEAR("3", "每年");

    private String code;

    private String msg;

    CouponUnitEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 设置客户获取总数/客户每次使用数
     *
     * @param couponGetOrUseFreqRuleEntity
     * @param addCouponReq2
     */
    public void setPersonTotalNum(CouponGetOrUseFreqRuleEntity couponGetOrUseFreqRuleEntity, AddCouponReq2 addCouponReq2) {
        couponGetOrUseFreqRuleEntity.setPersonTotalNum(addCouponReq2.getPersonTotalNum());
    }
}
