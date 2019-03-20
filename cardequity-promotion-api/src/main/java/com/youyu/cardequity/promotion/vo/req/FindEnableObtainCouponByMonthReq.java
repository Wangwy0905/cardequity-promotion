package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/1/24.
 */
@Data
public class FindEnableObtainCouponByMonthReq extends QryProfitCommonReq {
    @ApiModelProperty(value = "第几月可领的券的指定月数")
    private int monthNum;

    @ApiModelProperty(value = "截止第几月或指定第几月标志 0-截止第几月 1-指定第几月")
    private int monthNumFlag;
}
