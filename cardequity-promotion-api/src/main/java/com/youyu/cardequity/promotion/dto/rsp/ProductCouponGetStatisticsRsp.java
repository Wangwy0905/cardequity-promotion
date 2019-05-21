package com.youyu.cardequity.promotion.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月20日 10:00:00
 * @work 优惠券领取统计rsp
 */
@ApiModel("优惠券领取统计rsp")
@Setter
@Getter
public class ProductCouponGetStatisticsRsp implements Serializable {

    private static final long serialVersionUID = -2999619168592846315L;

    @ApiModelProperty("领取对象")
    private String getObject;

    @ApiModelProperty("数量")
    private Integer quantity;
}
