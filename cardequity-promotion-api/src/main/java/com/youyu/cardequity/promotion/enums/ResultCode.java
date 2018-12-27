package com.youyu.cardequity.promotion.enums;

import com.youyu.cardequity.common.base.api.CardequityIBaseResultCode;
import com.youyu.cardequity.common.base.constant.CodeConstant;
import com.youyu.common.api.IBaseResultCode;
import com.youyu.common.constant.ResultCodeConstant;
import org.springframework.util.ObjectUtils;

/**
 * @Auther: 徐长焕
 * @Date: 2018/12/10 10:29
 * @Description:
 */
public enum ResultCode implements CardequityIBaseResultCode {

    SUCCESS(ResultCodeConstant.SUCCESS, "success") {
        @Override
        public String getCode() {
            return ResultCodeConstant.SUCCESS;
        }
    },
    SYSTEM_ERROR(ResultCodeConstant.SYSTEM_ERROR, "系统错误") {
        @Override
        public String getCode() {
            return ResultCodeConstant.SYSTEM_ERROR;
        }
    },

    NET_ERROR("0000", "网络超时请稍后重试"),
    PARAM_ERROR("0001", "参数校验错误"),
    COUPON_NOT_ALLOW_CLIENTTYPE("0002", "该客户类型不能使用此券"),
    COUPON_NOT_ALLOW_PRODUCT("0003", "该商品不能使用此券"),
    COUPON_NOT_ALLOW_ENTRUSTWAY("0004", "该渠道不能使用此券"),
    COUPON_NOT_ALLOW_DATE("0005", "该期间不能领取或使用该优惠券"),
    COUPON_FAIL_PERACCANDDATEQUOTA("0006", "超过该优惠券每人每日可领取优惠金额"),
    COUPON_FAIL_PERACCQUOTA("0007", "超过该优惠券每人可领取优惠金额"),
    COUPON_FAIL_PERDATEQUOTA("0008", "超过该优惠券每日可领取优惠金额"),
    COUPON_FAIL_QUOTA("0009", "超过该优惠券所有客户可领取优惠总金额"),
    COUPON_FAIL_COUNT_QUOTA("0010", "超过该优惠券所有客户可领取优惠总数量"),
    COUPON_FAIL_OP_FREQ("0011", "超过该优惠券领取或使用频率"),
    COUPON_FAIL_OBTAIN("0012", "优惠券领取失败"),
    ACTIVE_NOT_EXIST("0013", "活动不存在"),
    COUPON_NOT_EXISTS("0014", "该优惠券信息不存在"),
    COUPON_USE_CONFLICT("0015", "优惠券使用冲突"),
    COUPON_FAIL_USE("0016", "优惠券领取失败"),
    ACTIVITY_NOT_ALLOW_CLIENTTYPE("0017", "该客户类型不能参加此活动"),
    ACTIVITY_NOT_ALLOW_ENTRUSTWAY("0018", "该渠道不能参加此活动"),
    ACTIVITY_NOT_ALLOW_PAYTYPE("0019", "该支付类型不能参加此活动"),
    ACTIVITY_NOT_ALLOW_BANKCODE("0020", "该银行卡不能参加此活动"),
    ACTIVITY_NOT_ALLOW_DATE("0021", "该期间不能不能参加此活动"),
    ;

    /**
     * 返回错误码
     **/
    private String code;

    /**
     * 返回错误信息
     **/
    private String desc;


    ResultCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return CodeConstant.CODE_PREFIX+CodeConstant.PROMOTION_CENTER+code;
    }


    @Override
    public String getDesc() {
        return desc;
    }

    public static IBaseResultCode getByCode(String code) {
        if (ObjectUtils.isEmpty(code)) {
            return null;
        }

        for (IBaseResultCode e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
