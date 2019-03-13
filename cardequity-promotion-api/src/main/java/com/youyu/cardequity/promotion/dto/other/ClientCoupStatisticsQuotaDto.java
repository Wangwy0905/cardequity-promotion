package com.youyu.cardequity.promotion.dto.other;

import com.youyu.cardequity.promotion.enums.CommonDict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 优惠使用情况统计数据结构
 */
@Data
public class ClientCoupStatisticsQuotaDto {

    public ClientCoupStatisticsQuotaDto(){
        clientPerDateAmount=BigDecimal.ZERO;
        clientAmount=BigDecimal.ZERO;
        clientPerDateCount=BigDecimal.ZERO;
        clientCount=BigDecimal.ZERO;
        statisticsFlag= CommonDict.IF_NO.getCode();
    }
    @ApiModelProperty(value = "指定统计的客户")
    private String clientId;

    @ApiModelProperty(value = "指定统计的券")
    private String couponId;

    @ApiModelProperty(value = "统计的客户当日使用优惠券额度：")
    private BigDecimal clientPerDateAmount;

    @ApiModelProperty(value = "客户优惠券使用总额度")
    private BigDecimal clientAmount;

    @ApiModelProperty(value = "客户当日使用优惠券数量")
    private BigDecimal clientPerDateCount;

    @ApiModelProperty(value = "客户优惠券使用总数量")
    private BigDecimal clientCount;

    @ApiModelProperty(value = "是否已统计")
    private String statisticsFlag;
}
