package com.youyu.cardequity.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by caiyi on 2018/12/12.
 */
@Getter
@Setter
public class ClientCoupStatisticsQuotaDto {

    public ClientCoupStatisticsQuotaDto(){
        clientPerDateAmount=BigDecimal.ZERO;
        clientAmount=BigDecimal.ZERO;
        clientPerDateCount=BigDecimal.ZERO;
        clientCount=BigDecimal.ZERO;
        statisticsFlag="0";
    }
    @ApiModelProperty(value = "指定统计的客户")
    private String clientId;

    @ApiModelProperty(value = "指定统计的券")
    private String couponId;

    @ApiModelProperty(value = "客户当日优惠券额度")
    private BigDecimal clientPerDateAmount;

    @ApiModelProperty(value = "客户优惠券总额度")
    private BigDecimal clientAmount;

    @ApiModelProperty(value = "客户当日优惠券数量")
    private BigDecimal clientPerDateCount;

    @ApiModelProperty(value = "客户优惠券总数量")
    private BigDecimal clientCount;

    @ApiModelProperty(value = "是否已统计")
    private String statisticsFlag;
}
