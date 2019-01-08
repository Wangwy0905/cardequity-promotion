package com.youyu.cardequity.promotion.vo.rsp;

import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.dto.ActivityStageCouponDto;
import com.youyu.cardequity.promotion.dto.other.OrderProductDetailDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caiyi on 2018/12/18.
 */
@Getter
@Setter
public class UseActivityRsp {
    public UseActivityRsp(){
        profitAmount=BigDecimal.ZERO;
        productLsit=new ArrayList<>();
    }

    @ApiModelProperty(value = "活动")
    private ActivityProfitDto activity;

    @ApiModelProperty(value = "阶梯编号:")
    private ActivityStageCouponDto stage;

    @ApiModelProperty(value = "客户号:")
    private String clientId;

    @ApiModelProperty(value = "总优惠金额")
    private BigDecimal profitAmount;

    @ApiModelProperty(value = "叠加标志:0-不可叠加 1-可叠加 2-自定义（建议规则简单点）")
    private String reCouponFlag;

    @ApiModelProperty(value = "相关商品明细，关联该活动使用的商品及数量")
    private List<OrderProductDetailDto> productLsit;
}
