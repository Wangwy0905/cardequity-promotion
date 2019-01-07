package com.youyu.cardequity.promotion.dto;

import java.time.LocalDateTime;
import com.youyu.common.dto.IBaseDto;
import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;


/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-20
 */
@Data
@ApiModel
public class ActivityRefProductDto implements IBaseDto<String>{

    @ApiModelProperty(value = "活动编号:")
    private String uuid;

    @ApiModelProperty(value = "活动编号:")
    private String activityId;

    @ApiModelProperty(value = "商品id:")
    private String productId;


    @ApiModelProperty(value = "子商品id:")
    private String skuId;


    @ApiModelProperty(value = "备注:")
    private String remark;

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void setId(String id) {
        this.uuid = id;
    }
}

