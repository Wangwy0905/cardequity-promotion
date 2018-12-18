package com.youyu.cardequity.promotion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class ClientCouponDto implements IBaseDto<String>{

    @ApiModelProperty(value = "编号:")
    private String uuid;

    @ApiModelProperty(value = "优惠券编号:")
    private String couponId;

    @ApiModelProperty(value = "阶梯编号:")
    private String stageId;

    @ApiModelProperty(value = "优惠策略类型:0-折扣券(该表不会出现) 1-阶梯优惠券(满多少减多少)  2-定额优惠券（该券无阶梯优惠固定金额）")
    private String couponStrategyType;

    @ApiModelProperty(value = "类型:0-红包 1-优惠券 2-运费券")
    private String couponType;

    @ApiModelProperty(value = "优惠标签:标签：满返券、促销等")
    private String couponLable;

    @ApiModelProperty(value = "优惠短描:如满3件减20")
    private String couponShortDesc;

    @ApiModelProperty(value = "客户号:")
    private String clientId;

    @ApiModelProperty(value = "委托方式:通过什么方式获取")
    private String entrustWay;

    @ApiModelProperty(value = "优惠金额:")
    private BigDecimal couponAmout;

    @ApiModelProperty(value = "有效起始日:到时分秒")
    private LocalDate validStartDate;

    @ApiModelProperty(value = "有效结束日:到时分秒")
    private LocalDate validEndDate;

    @ApiModelProperty(value = "状态:0-正常 1-使用中 2-已使用")
    private String status;

    @ApiModelProperty(value = "关联订单号:多次恢复可能涉及多个订单号时填最近一次订单号")
    private String joinOrderId;

    @ApiModelProperty(value = "备注:")
    private String remark;

    @ApiModelProperty(value = "使用日")
    private LocalDate BusinDate;

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void setId(String id) {
        this.uuid = id;
    }
}

