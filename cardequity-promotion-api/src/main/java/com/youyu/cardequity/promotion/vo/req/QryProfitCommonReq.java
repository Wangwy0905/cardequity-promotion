package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by caiyi on 2018/12/10.
 */
@Getter
@Setter
public class QryProfitCommonReq {
    @ApiModelProperty(value = "客户编号:")
    private String clinetId;

    @ApiModelProperty(value = "客户类型:如果传入只有clientid需要在服务层补全")
    private String clinetType;

    @ApiModelProperty(value = "商品id")
    private String productId;

    @ApiModelProperty(value = "商品组id:如果传入只有productid需要在服务层补全")
    private String groupId;

    @ApiModelProperty(value = "委托方式:见数据字典100002：0-web 1-ios 2-安卓 3-后台 4-对外接口处理")
    private String entrustWay;
}
