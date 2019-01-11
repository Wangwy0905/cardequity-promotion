package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询用户可领优惠券请求体
 */
@Data
public class QryProfitCommonReq {
    @ApiModelProperty(value = "客户编号:" , required = true)
    private String clinetId;

    @ApiModelProperty(value = "客户类型:如果传入只有clientid需要在服务层补全")
    private String clinetType;

    @ApiModelProperty(value = "商品id")
    private String productId;

    @ApiModelProperty(value = "是否新注册用户 0-否(默认) 1-是")
    private String newRegisterFlag;

    @ApiModelProperty(value = "委托方式:见数据字典100002：0-web 1-ios 2-安卓 3-后台 4-对外接口处理")
    private String entrustWay;

    @ApiModelProperty(value = "排除标志 0-不排除 1-排除额度、领取频率受限；默认为1")
    private String exclusionFlag;
}
