package com.youyu.cardequity.promotion.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 优惠券发放编辑响应Rsp
 */
@Setter
@Getter
@ApiModel("优惠券发放编辑响应Rsp")
public class CouponIssueEditRsp implements Serializable {

    private static final long serialVersionUID = -3909016219030551966L;

    @ApiModelProperty("优惠券发放时间")
    private String issueTime;

    @ApiModelProperty("优惠券发放时间修改标志 true:已修改 false:未修改")
    private Boolean issueTimeModifyFlag;
}
