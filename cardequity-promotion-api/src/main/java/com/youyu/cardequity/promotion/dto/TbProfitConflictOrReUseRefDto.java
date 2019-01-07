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
 * @date 2018-12-13
 */
@Data
@ApiModel
public class TbProfitConflictOrReUseRefDto implements IBaseDto<String>{

    @ApiModelProperty(value = "编号:")
    private String uuid;

    @ApiModelProperty(value = "对象类型:0-优惠券 1-活动")
    private String objType;

    @ApiModelProperty(value = "主编号:活动或优惠券编号")
    private String objId;

    @ApiModelProperty(value = "对方对象类型:0-优惠券 1-活动")
    private String targetObjType;

    @ApiModelProperty(value = "对方编号:活动或优惠券编号")
    private String targetObjId;

    @ApiModelProperty(value = "对应关系:0-叠加(配置白名单) 1-冲突(配置黑名单)")
    private String refType;

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void setId(String id) {
        this.uuid = id;
    }
}

