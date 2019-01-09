package com.youyu.cardequity.promotion.enums;

/**
 * 公用信息
 *
 * @Auther: 徐长焕
 * @Date: 2018/12/10 15:41
 * @Description:
 */
public enum CommonDict {

    IF_YES("1", "是"),
    IF_NO("0", "否"),

    /**
     * null的替换临时变量，用于字符串比较
     */
    NULLREPLACE("UNDEFINE","用于替换null值方便比较等操作"),

    WILDCARD("*","通配符"),

    /**
     * validflag变量，0-验证不通过，1-验证通过，2-需要继续验证
     */
    FAILVALID("0", "校验失败"),
    PASSVALID("1", "校验通过"),
    CONTINUEVALID("2", "需要继续校验"),


    /**
     * 前台领取对象：0-全部用户 1-新用户 2-会员
     */
    FRONDEND_ALL("0", "全部用户"),
    FRONDEND_NEW("1", "新用户"),
    FRONDEND_MEMBER("2", "会员"),

    ;

    /**
     * code
     **/
    private String code;

    /**
     * desc
     **/
    private String desc;


    CommonDict(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
