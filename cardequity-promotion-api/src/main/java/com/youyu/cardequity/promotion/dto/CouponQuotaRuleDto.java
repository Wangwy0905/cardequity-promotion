package com.youyu.cardequity.promotion.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class CouponQuotaRuleDto implements IBaseDto<String>{

    @ApiModelProperty(value = "优惠券编号:")
    private String couponId;

    @ApiModelProperty(value = "每笔最大优惠额:每笔优惠池，主要多折扣券的总优惠金额做保护")
    private BigDecimal perMaxAmount;

    @ApiModelProperty(value = "每客每天最大优惠额:每天每客优惠池")
    private BigDecimal perDateAndAccMaxAmount;

    @ApiModelProperty(value = "每天最大优惠额:每天优惠池")
    private BigDecimal perDateMaxAmount;

    @ApiModelProperty(value = "每客最大优惠额:每客优惠池")
    private BigDecimal personMaxAmount;

    @ApiModelProperty(value = "最大优惠金额(资金池数量):优惠券金额池，控制金额不能超出池子金额")
    private BigDecimal maxAmount;

    @ApiModelProperty(value = "最大发放数量(券池数量):优惠券数量池")
    private Integer maxCount;

    @ApiModelProperty(value = "备注:")
    private String remark;

    @Override
    public String getId() {
        return couponId;
    }

    @Override
    public void setId(String id) {
        this.couponId = id;
    }
}

