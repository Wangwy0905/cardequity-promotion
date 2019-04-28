package com.youyu.cardequity.promotion.enums;

import com.youyu.cardequity.common.base.enums.CardequityEnum;
import lombok.Getter;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 优惠券发放触发枚举
 */
@Getter
public enum CouponIssueTriggerTypeEnum implements CardequityEnum {

    DELAY_JOB_TRIGGER_TYPE("1", "延期定时任务触发器");

    private String code;

    private String msg;

    CouponIssueTriggerTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
