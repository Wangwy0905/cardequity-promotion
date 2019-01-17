package com.youyu.cardequity.promotion.dto.other;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by caiyi on 2019/1/17.
 */
@Data
public class ObtainCouponViewDto extends CouponViewDto{
    @ApiModelProperty(value = "领取状态：0-未领取 1-已领取且有效 2-曾经领取")
    private String obtainState;

    @ApiModelProperty(value = "领取编号")
    private String obtainId;

    @ApiModelProperty(value = "有效起始日:到时分秒")
    private LocalDateTime validStartDate;

    @ApiModelProperty(value = "有效结束日:到时分秒")
    private LocalDateTime validEndDate;
}
