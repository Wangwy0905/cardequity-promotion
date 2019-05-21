package com.youyu.cardequity.promotion.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月20日 10:00:00
 * @work 优惠券查看rsp
 */
@ApiModel("优惠券查看rsp")
@Setter
@Getter
public class ProductCouponViewRsp implements Serializable {

    private static final long serialVersionUID = 3504166965446106479L;

    @ApiModelProperty(value = "优惠券id")
    private String productCouponId;

    @ApiModelProperty(value = "类型 1:消费券 2:运费券")
    private String couponType;

    @ApiModelProperty(value = "领取对象 *:全部用户 10:注册 11:会员")
    private String clientTypeSet;

    @ApiModelProperty(value = "级别 0:小鱼券 1:大鱼券")
    private String couponLevel;

    @ApiModelProperty(value = "优惠标签id标签:满返券、促销等")
    private String couponLabel;

    @ApiModelProperty(value = "状态 0:下架 1:上架")
    private String status;

    @ApiModelProperty(value = "优惠券名称")
    private String couponName;

    @ApiModelProperty(value = "门槛短描:如满3件减20")
    private String couponShortDesc;

    @ApiModelProperty(value = "使用说明")
    private String couponDesc;

    @ApiModelProperty(value = "券数量")
    private Integer maxCount;

    @ApiModelProperty(value = "优惠值:如果是阶梯或随机的填0，存折扣、金额")
    private BigDecimal profitValue;

    @ApiModelProperty(value = "使用金额门槛")
    private BigDecimal conditionValue;

    @ApiModelProperty(value = "每张券最大优惠金额")
    private BigDecimal perProfitTopValue;

    @ApiModelProperty(value = "有效时间类型 0:按日期 1:按天数 2:当月有效")
    private String validTimeType;

    @ApiModelProperty(value = "优惠开始日:格式yyyy-MM-dd HH:mm:ss")
    private LocalDateTime allowUseBeginDate;

    @ApiModelProperty(value = "优惠结束日:格式yyyy-MM-dd HH:mm:ss")
    private LocalDateTime allowUseEndDate;

    @ApiModelProperty(value = "有效期限:以天为单位")
    private Integer validTerm;

    @ApiModelProperty(value = "发放优惠券的方式 0:后台发放 1:用户领取")
    private String getType;

    @ApiModelProperty(value = "领取开始日:格式yyyy-MM-dd HH:mm:ss")
    private LocalDateTime allowGetBeginDate;

    @ApiModelProperty(value = "领取结束日:格式yyyy-MM-dd HH:mm:ss")
    private LocalDateTime allowGetEndDate;

    @ApiModelProperty(value = "频率周期类型 a:有效期内 0:每天 1:每周 2:每月 3:每年 ")
    private String unit;

    @ApiModelProperty(value = "周期限领")
    private Integer personTotalNum;

    @ApiModelProperty(value = "总限领")
    private Integer allowCount;
}
