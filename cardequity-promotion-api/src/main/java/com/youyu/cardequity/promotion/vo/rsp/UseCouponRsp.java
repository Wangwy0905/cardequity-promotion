package com.youyu.cardequity.promotion.vo.rsp;

import com.youyu.cardequity.promotion.dto.OrderProductDetailDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caiyi on 2018/12/14.
 */
@Getter
@Setter
public class UseCouponRsp {
    public UseCouponRsp(){
        productLsit=new ArrayList<>();
    }

    @ApiModelProperty(value = "编号:")
    private String uuid;

    @ApiModelProperty(value = "优惠券编号:")
    private String couponId;

    @ApiModelProperty(value = "阶梯编号:")
    private String stageId;

    @ApiModelProperty(value = "客户号:")
    private String clientId;

    @ApiModelProperty(value = "总优惠金额")
    private BigDecimal profitAmount;

    @ApiModelProperty(value = "叠加标志:0-不可叠加 1-可叠加 2-自定义（建议规则简单点）")
    private String reCouponFlag;

    @ApiModelProperty(value = "相关商品明细，关联该券使用的商品及数量")
    private List<OrderProductDetailDto> productLsit;
}
