package com.youyu.cardequity.promotion.biz.dal.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by caiyi on 2018/12/28.
 */

@Getter
@Setter
@Table(name = "TB_COUPON_AND_ACTIVITY_LABEL")
public class CouponAndActivityLabelEntity  extends com.youyu.common.entity.BaseEntity<String> {

    /**
     * 标签编号:
     */
    @Id
    @Column(name = "UUID")
    private String uuid;


    /**
     * 标签名称:
     */
    @Column(name = "LABEL_NAME")
    private String labelName;

    /**
     * 标签适用类型:0-优惠券 1-活动
     */
    @Column(name = "LABEL_TYPE")
    private String labelype;


    /**
     * 主题颜色
     */
    @Column(name = "THEME_COLOUR")
    private String themeColour;

    /**
     * 背景颜色
     */
    @Column(name = "BACKGROUND_COLOUR")
    private String backgroundColour;

    /**
     * 描边色
     */
    @Column(name = "FRAME_COLOUR")
    private String frameColour;

    /**
     * 备注
     */
    @Column(name = "REMARK")
    private String remark;
    /**
     * 是否有效:
     */
    @Column(name = "IS_ENABLE")
    private String isEnable;

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void setId(String uuid) {
        this.uuid = uuid;
    }
}
