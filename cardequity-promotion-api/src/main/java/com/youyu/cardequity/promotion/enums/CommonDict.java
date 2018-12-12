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
