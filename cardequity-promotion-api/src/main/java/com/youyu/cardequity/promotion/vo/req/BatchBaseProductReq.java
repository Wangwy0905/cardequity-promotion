package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caiyi on 2019/1/10.
 */
@Data
public class BatchBaseProductReq {
    public BatchBaseProductReq(){
        productList=new ArrayList<>();
    }

    @ApiModelProperty(value = "指定商品列表")
    private List<BaseProductReq> productList;

    @ApiModelProperty(value = "操作者：用于更新产生者或更新者，一般传网关获取的ip")
    private String operator;
}
