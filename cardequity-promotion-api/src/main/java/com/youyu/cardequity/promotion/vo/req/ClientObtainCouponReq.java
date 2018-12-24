package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 *  2018/12/12. 徐长焕  1004258 新增传入获取优惠券的request
 */
@Getter
@Setter
public class ClientObtainCouponReq {
    @ApiModelProperty(value = "客户编号:必填")
    private String clientId;

    @ApiModelProperty(value = "优惠券id：必填")
    private String couponId;

    @ApiModelProperty(value = "所属阶梯id：对有门槛的优惠券必填")
    private String stageId;

    @ApiModelProperty(value = "商品id：指定相关商品，为空不校验该券是否对应该商品可使用")
    private String productId;

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
}
