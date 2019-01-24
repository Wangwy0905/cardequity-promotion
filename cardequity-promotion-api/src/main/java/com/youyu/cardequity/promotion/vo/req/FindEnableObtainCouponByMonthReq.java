package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/1/24.
 */
@Data
public class FindEnableObtainCouponByMonthReq extends QryProfitCommonReq {
    @ApiModelProperty(value = "第月可领的券的指定月数")
    private int monthNum;
}
