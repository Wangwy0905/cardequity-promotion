
package com.youyu.cardequity.promotion.dto.req;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class UserInfo4CouponIssueDto implements Serializable {
    private static final long serialVersionUID = -8243034458678761124L;

    @ApiModelProperty("用户ID")
    private String clientId;

    @ApiModelProperty("用户类型:10-普通用户 11-会员用户 12-赠送会员用户")
    private String userType;

    @ApiModelProperty("是否是新用户")
    private String newUserFlag;


}
