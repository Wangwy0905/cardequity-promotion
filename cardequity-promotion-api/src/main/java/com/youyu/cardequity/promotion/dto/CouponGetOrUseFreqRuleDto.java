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
 * @date 2018-12-07
 */
@Data
@ApiModel
public class CouponGetOrUseFreqRuleDto implements IBaseDto<String>{

    @ApiModelProperty(value = "编号:")
    private String uuid;

    @ApiModelProperty(value = "优惠券编号:")
    private String couponId;

    @ApiModelProperty(value = "操作方式:0-获取 1-使用")
    private String opCouponType;

    @ApiModelProperty(value = "优惠阶梯编号:")
    private String stageId;

    @ApiModelProperty(value = "频率单位:0-天 1-周 2-月 3-年")
    private String unit;

    @ApiModelProperty(value = "频率值:预留字段默认为1，暂不支持多周期频率")
    private Integer value;

    @ApiModelProperty(value = "允许次数:设置时注意每天使用数>=ProductCoupon每次允许使用数；特殊值0代表任何时刻都只有且可以有一张有效的券（即客户每时刻可以且只能有一张有效券），此时Unit值无效")
    private Integer allowCount;

    @ApiModelProperty(value = "客户获取总数/客户每次使用数:999999标识无限制")
    private Integer personTotalNum;

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

