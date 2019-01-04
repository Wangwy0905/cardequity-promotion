package com.youyu.cardequity.promotion.dto;

import java.math.BigDecimal;

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
public class CouponStageRuleDto implements IBaseDto<String>{

    @ApiModelProperty(value = "阶段编号:")
    private String uuid;

    @ApiModelProperty(value = "优惠券编号:")
    private String couponId;

    @ApiModelProperty(value = "优惠短描:如满3件减20，会覆盖ProductCoupon.CouponShortDesc")
    private String couponShortDesc;

    @ApiModelProperty(value = "门槛触发类型:0-按买入金额 1-按买入数量（应设置其中之一，如果第二件5折可在此设置）")
    private String triggerByType;

    @ApiModelProperty(value = "值起始（不含）:没有阶梯填(0,999999999]")
    private BigDecimal beginValue;

    @ApiModelProperty(value = "结束值（含）:最大值为999999999；")
    private BigDecimal endValue;

    @ApiModelProperty(value = "优惠值:默认值等于ProductGroupCoupon中值，使用时覆盖ProductGroupCoupon中值起效，取值范围为ProductGroupCoupon中值")
    private BigDecimal couponValue;

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void setId(String id) {
        this.uuid = id;
    }
}

