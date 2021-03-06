package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by caiyi on 2019/1/2.
 */
@Data
public class BatchRefProductReq {
    @ApiModelProperty(value = "优惠id")
    private String id;

    @ApiModelProperty(value = "优惠券涉及的商品")
    private  List<BaseProductReq> productList;

    //改动
    @ApiModelProperty(value = "优惠券涉及的需要删除的商品")
    private  List<BaseProductReq> delProductList;

    @ApiModelProperty(value = "操作标识：0-全量 1-增量")
    private String operatFlag;

    @ApiModelProperty(value = "操作者：用于更新产生者或更新者，一般传网关获取的ip")
    private String operator;
}
