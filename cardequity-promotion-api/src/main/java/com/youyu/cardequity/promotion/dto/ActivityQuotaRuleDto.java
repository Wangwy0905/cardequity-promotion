package com.youyu.cardequity.promotion.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.common.dto.IBaseDto;
import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;


/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
@Data
@ApiModel
public class ActivityQuotaRuleDto implements IBaseDto<String>{
    public ActivityQuotaRuleDto(){
        //空不填都代表不控制的最大值
        perMaxAmount= CommonConstant.IGNOREVALUE;
        perDateAndAccMaxAmount= CommonConstant.IGNOREVALUE;
        perDateMaxAmount= CommonConstant.IGNOREVALUE;
        personMaxAmount= CommonConstant.IGNOREVALUE;
        maxAmount= CommonConstant.IGNOREVALUE;
        perMaxCount= CommonConstant.IGNOREVALUE;
        perDateAndAccMaxCount= CommonConstant.IGNOREVALUE;
        perDateMaxCount= CommonConstant.IGNOREVALUE;
        personMaxCount= CommonConstant.IGNOREVALUE;
        maxCount= CommonConstant.IGNOREVALUE;
    }

    @ApiModelProperty(value = "活动编号:")
    private String activityId;

    @ApiModelProperty(value = "每笔最大优惠额:每笔优惠池，主要多折扣券的总优惠金额做保护")
    private BigDecimal perMaxAmount;

    @ApiModelProperty(value = "每客每天最大优惠额:每天每客优惠池")
    private BigDecimal perDateAndAccMaxAmount;

    @ApiModelProperty(value = "每天最大优惠额:每天优惠池")
    private BigDecimal perDateMaxAmount;

    @ApiModelProperty(value = "每客最大优惠额:每客优惠池")
    private BigDecimal personMaxAmount;

    @ApiModelProperty(value = "活动最大优惠金额(资金池数量):优惠券金额池，控制金额不能超出池子金额")
    private BigDecimal maxAmount;

    @ApiModelProperty(value = "每笔最大数量:如每单限购10件")
    private BigDecimal perMaxCount;

    @ApiModelProperty(value = "每客每天最大数量:如每人每天限量10件")
    private BigDecimal perDateAndAccMaxCount;

    @ApiModelProperty(value = "每天最大数量:如每天限量1000件")
    private BigDecimal perDateMaxCount;

    @ApiModelProperty(value = "每客最大数量:如每人限量10件")
    private BigDecimal personMaxCount;

    @ApiModelProperty(value = "活动最大数量:做活动的商品数量")
    private BigDecimal maxCount;

    @ApiModelProperty(value = "备注:")
    private String remark;

    @Override
    public String getId() {
        return activityId;
    }

    @Override
    public void setId(String id) {
        this.activityId = id;
    }
}

