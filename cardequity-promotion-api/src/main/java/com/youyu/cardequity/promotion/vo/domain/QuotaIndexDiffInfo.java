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
        clientMinDiffCount= CommonConstant.IGNOREVALUE;
        clientMinDiffAmount= CommonConstant.IGNOREVALUE;
    }
    @ApiModelProperty(value = "指定统计的客户")
    private String clientId;

    @ApiModelProperty(value = "指定统计的券")
    private String couponId;

    @ApiModelProperty(value = "客户当日优惠券额度")
    private BigDecimal clientDiffPerDateAmount;

    @ApiModelProperty(value = "客户优惠券总额度")
    private BigDecimal clientDiffAmount;

    @ApiModelProperty(value = "客户当日优惠券数量")
    private BigDecimal clientDiffPerDateCount;

    @ApiModelProperty(value = "客户优惠券总数量")
    private BigDecimal clientDiffCount;

    @ApiModelProperty(value = "客户允许最大数量")
    private BigDecimal clientMinDiffCount;

    @ApiModelProperty(value = "客户允许最大金额")
    private BigDecimal clientMinDiffAmount;


}
