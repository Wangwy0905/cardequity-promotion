package com.youyu.cardequity.promotion.vo.req;

import com.youyu.cardequity.promotion.dto.OrderProductDetailDto;
import com.youyu.cardequity.promotion.dto.ShortClientCouponDto;
import com.youyu.cardequity.promotion.dto.ShortCouponDetailDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by caiyi on 2018/12/14.
 */
@Getter
@Setter
public class GetUseEnableCouponReq {
    @ApiModelProperty(value = "客户编号:必填")
    private String clinetId;

    @ApiModelProperty(value = "客户类型:冗余，服务层调用时传入空，需要调用用户中心进行查询该字段")
    private String clinetType;

    @ApiModelProperty(value = "委托方式:验证该渠道操作方式是否可以领取")
    private String entrustWay;

    @ApiModelProperty(value = "银行代码:传入用于校验是否该银行卡可用")
    private String bankCode;

    @ApiModelProperty(value = "支付类型:传入用于校验是否该支付类型可用")
    private String payType;

    @ApiModelProperty(value = "活动编号:关联的活动编号")
    private String activityId;

    @ApiModelProperty(value = "本次订单未优惠前运费")
    private BigDecimal transferFare;

    @ApiModelProperty(value = "指定使用的优惠券，必须是已领用的：每个级别（全局券和非全局）只能指定一个")
    private List<ShortClientCouponDto> obtainCouponList;

    @ApiModelProperty(value = "相关商品明细，用于判断是否满足使用阶梯门槛")
    private List<OrderProductDetailDto> productList;
}
