package com.youyu.cardequity.promotion.dto;

import com.youyu.common.dto.IBaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by caiyi on 2018/12/28.
 */
@Data
@ApiModel
public class CouponAndActivityLabelDto implements IBaseDto<String> {
    @ApiModelProperty(value = "活动编号:")
    private String uuid;

    @ApiModelProperty(value = "标签名称:")
    private String labelName;

    @ApiModelProperty(value = "标签使用类型:0-优惠券 1-活动")
    private String labelType;


    @ApiModelProperty(value = "主题颜色:")
    private String themeColour;


    @ApiModelProperty(value = "背景色:")
    private String backgroundColour;

    @ApiModelProperty(value = "描边色:")
    private String frameColour;

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
