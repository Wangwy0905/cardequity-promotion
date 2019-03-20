package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/1/23.
 */
@Data
public class QryComonClientCouponReq extends BaseClientReq {

    @ApiModelProperty(value = "领取状态：0-未领取(不会使用到) 1-已领取且有效 2-已使用 3-已过期 4-未开始 5-未过期未使用")
    private String obtainState;

}
