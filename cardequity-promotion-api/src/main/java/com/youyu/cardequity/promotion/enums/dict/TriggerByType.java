package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/14.
 */
public enum TriggerByType {
        CAPITAL("0", "按金额触发"),
        NUMBER("1", "按数量触发"),

        ;

public static final String DICTID = "100179";
public static final String DICTNAME = "TriggerByType";
public static final String DICTKIND = "2";
public static final String DICTCOMMENT = "门槛触发类型";

private final String dictValue;
private final String dictComment;

    TriggerByType(String dictValue, String dictComment) {
        this.dictValue = dictValue;
        this.dictComment = dictComment;
        }

public String getDictValue() {
        return dictValue;
        }

public String getDictComment() {
        return dictComment;
        }
}
