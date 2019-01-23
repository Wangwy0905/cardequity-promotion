package com.youyu.cardequity.promotion.vo.domain;

import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.cardequity.promotion.enums.CommonDict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by caiyi on 2018/12/29.
 */
@Getter
@Setter
public class QuotaIndexDiffInfo {
    public QuotaIndexDiffInfo(){
        clientDiffPerDateAmount= CommonConstant.IGNOREVALUE;
        clientDiffAmount= CommonConstant.IGNOREVALUE;
        clientDiffPerDateCount= CommonConstant.IGNOREVALUE;
        clientDiffCount= CommonConstant.IGNOREVALUE;
        minDiffCount= CommonConstant.IGNOREVALUE;
        minDiffAmount= CommonConstant.IGNOREVALUE;
        perMaxAmount= CommonConstant.IGNOREVALUE;
        perMaxCount= CommonConstant.IGNOREVALUE;
    }


    @ApiModelProperty(value = "指定统计的客户")
    private String clientId;

    @ApiModelProperty(value = "指定统计活动或优惠券编号")
    private String id;

    @ApiModelProperty(value = "每笔最大优惠额:每笔优惠池，主要多折扣券的总优惠金额做保护")
    private BigDecimal perMaxAmount;

    @ApiModelProperty(value = "每笔最大优惠数量")
    private BigDecimal perMaxCount;

    @ApiModelProperty(value = "客户当日优惠券额度")
    private BigDecimal clientDiffPerDateAmount;

    @ApiModelProperty(value = "客户优惠券总额度")
    private BigDecimal clientDiffAmount;

    @ApiModelProperty(value = "客户当日优惠券数量")
    private BigDecimal clientDiffPerDateCount;

    @ApiModelProperty(value = "客户优惠券总数量")
    private BigDecimal clientDiffCount;

    @ApiModelProperty(value = "二次汇总结果值：最终允许最大数量，以上数量之间取小而得")
    private BigDecimal minDiffCount;

    @ApiModelProperty(value = "二次汇总结果值：最终允许最大金额，以上金额之间取小而得")
    private BigDecimal minDiffAmount;




}
