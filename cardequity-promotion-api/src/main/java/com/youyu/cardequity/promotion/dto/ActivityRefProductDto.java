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

    @ApiModelProperty(value = "备注:")
    private String remark;

    @ApiModelProperty(value = "产生者:")
    private String createAuthor;

    @ApiModelProperty(value = "更新者:")
    private String updateAuthor;

    @ApiModelProperty(value = "更新时间:")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "产生时间:")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "是否有效:")
    private String isEnable;

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void setId(String id) {
        this.uuid = id;
    }
}

