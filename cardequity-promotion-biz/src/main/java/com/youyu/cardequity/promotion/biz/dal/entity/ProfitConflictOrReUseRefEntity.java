package com.youyu.cardequity.promotion.biz.dal.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "TB_PROFIT_CONFLICT_OR_RE_USE_REF")
public class ProfitConflictOrReUseRefEntity extends com.youyu.common.entity.BaseEntity<String> {
    /**
     * 编号:
     */
    @Id
    @Column(name = "UUID")
    private String uuid;

    /**
     * 对象类型:0-优惠券 1-活动
     */
    @Column(name = "OBJ_TYPE")
    private String objType;

    /**
     * 主编号:活动或优惠券编号
     */
    @Column(name = "OBJ_ID")
    private String objId;

    /**
     * 对方对象类型:0-优惠券 1-活动
     */
    @Column(name = "TARGET_OBJ_TYPE")
    private String targetObjType;

    /**
     * 对方编号:活动或优惠券编号
     */
    @Column(name = "TARGET_OBJ_ID")
    private String targetObjId;

    /**
     * 对应关系:0-叠加(配置白名单) 1-冲突(配置黑名单)
     */
    @Column(name = "REF_TYPE")
    private String refType;

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