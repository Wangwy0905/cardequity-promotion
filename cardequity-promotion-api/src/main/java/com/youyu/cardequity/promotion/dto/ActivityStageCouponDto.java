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
public class ActivityStageCouponDto implements IBaseDto<String>{

    @ApiModelProperty(value = "编号:")
    private String uuid;

    @ApiModelProperty(value = "活动阶段短描:用于前端展示：如任选3件99元、满200减50，会覆盖ActivityProfit.ActivityShortDesc")
    private String activityShortDesc;

    @ApiModelProperty(value = "活动编号:")
    private String activityId;

    @ApiModelProperty(value = "门槛触发类型:0-按买入金额 1-按买入数量（应设置其中之一，如果第二件5折可在此设置）")
    private String triggerByType;

    @ApiModelProperty(value = "值起始（不含）:没有阶梯填(0,999999999]")
    private BigDecimal beginValue;

    @ApiModelProperty(value = "结束值（含）:最大值为999999999；空时也表示最大值")
    private BigDecimal endValue;

    @ApiModelProperty(value = "优惠值:使用时覆盖ActivityProfit中值起效")
    private BigDecimal profitValue;

    @ApiModelProperty(value = "是否有效:")
    private String isEnable;

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void setId(String id) {
        this.uuid = id;
    }
}

