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
    PARAM_ERROR("0001", "参数校验错误,备注:{0}"),
    COUPON_NOT_ALLOW_CLIENTTYPE("0002", "该客户类型不能使用此券:客户类型值{0}"),
    COUPON_NOT_ALLOW_PRODUCT("0003", "该商品不能使用此券：商品编号{0},商品子编号{1},优惠券编号{2},子券编号{3}"),
    COUPON_NOT_ALLOW_ENTRUSTWAY("0004", "该渠道及方式不能使用此券,ENTRUSTWAY={0}"),
    COUPON_NOT_ALLOW_DATE("0005", "该期间不能领取或使用该优惠券,备注：开始时间{0},结束时间{1}"),
    COUPON_FAIL_PERACCANDDATEQUOTA("0006", "超过该优惠每人每日可领取优惠金额,每人每日可领优惠金额{0},该客户当日已领优惠金额{1},客户编号{2}"),
    COUPON_FAIL_PERACCQUOTA("0007", "超过该优惠每人可领取优惠金额,每人可领优惠金额{0},该客户已领优惠金额{1},客户编号{2}"),
    COUPON_FAIL_PERDATEQUOTA("0008", "超过该优惠每日可领取优惠金额,每日可领优惠金额{0},当日已领优惠金额{1},优惠编号{2}"),
    COUPON_FAIL_QUOTA("0009", "超过该优惠所有客户可领取优惠总金额,可领优惠金额{0},已领优惠金额{1},优惠编号{2}"),
    COUPON_FAIL_COUNT_QUOTA("0010", "超过该优惠所有客户可领取优惠总数量,可领优惠数量{0},已领优惠数量{1},优惠编号{2}"),
    COUPON_FAIL_OP_FREQ("0011", "超过该优惠券领取或使用频率"),
    COUPON_FAIL_OBTAIN("0012", "优惠券领取失败,备注：{0}"),
    ACTIVE_NOT_EXIST("0013", "活动不存在,备注：{0}"),
    COUPON_NOT_EXISTS("0014", "该优惠券信息不存在,备注：{0}"),
    COUPON_USE_CONFLICT("0015", "优惠券使用冲突"),
    COUPON_FAIL_USE("0016", "优惠券领取失败,备注：{0}"),
    ACTIVITY_NOT_ALLOW_CLIENTTYPE("0017", "该客户类型不能参加此活动,CLIENTTYPE={0}"),
    ACTIVITY_NOT_ALLOW_ENTRUSTWAY("0018", "该渠道不能参加此活动,ENTRUSTWAY={0}"),
    ACTIVITY_NOT_ALLOW_PAYTYPE("0019", "该支付类型不能参加此活动,PAYTYPE={0}"),
    ACTIVITY_NOT_ALLOW_BANKCODE("0020", "该银行卡不能参加此活动,BANKCODE={0}"),
    ACTIVITY_NOT_ALLOW_DATE("0021", "该期间不能不能参加此活动,开始时间{0},结束时间{1}"),
    COUPON_NOT_ALLOW_BANKCODE("0022", "该银行不能使用此券,BANKCODE={0}"),
    COUPON_NOT_ALLOW_PAYTYPE("0023", "该支付方式不能使用此券,PAYTYPE={0}"),
    COUPON_FAIL_COUNT_PERDATEQUOTA("0024", "超过该优惠所有客户当日可领取优惠总数量,券当日可领优惠数量{0},券当日已领优惠数量{1},优惠编号{2}"),
    COUPON_FAIL_COUNT_PERACCANDDATEQUOTA("0025", "超过该优惠每日可领取优惠金额,每日可领优惠金额{0},当日已领优惠金额{1},优惠编号{2}"),
    COUPON_FAIL_COUNT_PERACCQUOTA("0026", "超过该优惠每人可领取数量,可领优惠数量{0},已领优惠数量{1},优惠编号{2}"),

    //后台发放优惠券
    INVISIBLE_COUPON_CANNOT_BE_ISSUED("0027", "已下架的优惠券不能发放!"),
    COUPON_HAS_EXPIRED("0028", "优惠券已过期!"),
    MANUAL_COUPON_CANNOT_BE_ISSUED("0029", "手动领取方式优惠券不能发放!"),
    ISSUE_TIME_MUST_GREATER_CURRENT_TIME("0030", "发放时间要大于当前时间!"),
    COUPON_END_DATE_MUST_GREATER_CURRENT_DATE("0031", "优惠券结束日要大于当前日期!"),
    COUPON_ISSUE_QUANTITY_CANNOT_LESS_ZERO("0032", "优惠券发行数量不能小于等于0!"),
    INVISIBLE_COUPON_ISSUE_TASK_CANNOT_BE_ISSUED("0033", "优惠券发放任务已经下架，不能实施优惠券发放"),
    COUPON_NOT_ISSUE_STATUS_CAN_DELETE("0034", "未发放状态的优惠券发放才可以删除!"),
    COUPON_ISSUE_TIMEOUT_INVALID_OPERATION("0035", "优惠券发放超时操作无效!"),

    COUPON_ISSUE_NO_CAPACITY_CANNOT_BE_ISSUED("0036","券的额度规则为空或者券库存剩余容量为0，此时不能发券");


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
        return CodeConstant.CODE_PREFIX + CodeConstant.PROMOTION_CENTER + code;
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
