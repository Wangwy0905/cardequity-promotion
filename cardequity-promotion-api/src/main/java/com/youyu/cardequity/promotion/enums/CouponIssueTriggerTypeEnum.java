package com.youyu.cardequity.promotion.enums;

import com.youyu.cardequity.common.base.enums.CardequityEnum;
import lombok.Getter;

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
