package com.youyu.cardequity.promotion.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.youyu.cardequity.common.base.util.BeanPropertiesUtils;
import com.youyu.cardequity.common.base.util.LocalDateUtils;
import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.cardequity.promotion.dto.other.CouponDetailDto;
import com.youyu.cardequity.promotion.enums.dict.CouponUseStatus;
import com.youyu.cardequity.promotion.enums.dict.UsedStage;
import com.youyu.common.dto.IBaseDto;
import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;

import static com.youyu.cardequity.common.base.util.LocalDateUtils.*;
import static com.youyu.cardequity.common.base.util.LocalDateUtils.string2LocalDateTime;
import static java.time.LocalDate.*;


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

    @ApiModelProperty(value = "优惠策略类型:0-折扣券 1-满减现金 2-无门槛现金券  3-随机  4-每满减现金")
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

    @ApiModelProperty(value = "获取方式:0-自动 1-收到 2-平台指定发放")
    private String getType;

    @ApiModelProperty(value = "优惠金额:")
    private BigDecimal couponAmout;

    @ApiModelProperty(value = "有效起始日:到时分秒")
    private LocalDateTime validStartDate;

    @ApiModelProperty(value = "有效结束日:到时分秒")
    private LocalDateTime validEndDate;

    @ApiModelProperty(value = "状态:0-正常 1-使用中 2-已使用")
    private String status;

    @ApiModelProperty(value = "关联订单号:多次恢复可能涉及多个订单号时填最近一次订单号")
    private String joinOrderId;

    @ApiModelProperty(value = "备注:")
    private String remark;

    @ApiModelProperty(value = "使用日")
    private LocalDate businDate;

    @ApiModelProperty(value = "级别：0-小鱼券 1-大鱼券")
    private String couponLevel;

    @ApiModelProperty(value = "门槛触发类型:0-按买入金额 1-按买入数量")
    private String triggerByType;

    @ApiModelProperty(value = "值起始（含）")
    private BigDecimal beginValue;

    @ApiModelProperty(value = "结束值")
    private BigDecimal endValue;

    @ApiModelProperty(value = "适用商品类型:0-自动义商品范围 1-全部")
    private String applyProductFlag;

    @ApiModelProperty(value = "新领标识:0-否 1-是")
    private String newFlag;

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void setId(String id) {
        this.uuid = id;
    }

    public CouponDetailDto switchSimpleMol(){
        ProductCouponDto productCouponDto=new ProductCouponDto();
        BeanPropertiesUtils.copyProperties(this,productCouponDto);
        productCouponDto.setUuid(couponId);
        productCouponDto.setProfitValue(couponAmout);
        productCouponDto.setAllowUseBeginDate(validStartDate);
        productCouponDto.setAllowUseEndDate(validEndDate);
        productCouponDto.setAllowGetBeginDate(validStartDate);
        productCouponDto.setAllowGetEndDate(validEndDate);
        productCouponDto.setGetStage(UsedStage.Other.getDictValue());

        productCouponDto.setLabelDto(new CouponAndActivityLabelDto());
        productCouponDto.getLabelDto().setId(couponLable);

        List<CouponStageRuleDto> list=new ArrayList<>();
        CouponStageRuleDto stage=new CouponStageRuleDto();
        BeanPropertiesUtils.copyProperties(this,stage);
        stage.setCouponValue(couponAmout);
        stage.setUuid(stageId);
        list.add(stage);

        CouponDetailDto result=new CouponDetailDto();
        result.setProductCouponDto(productCouponDto);
        result.setStageList(list);
        return result;
    }

    /**
     * 获取领取状态
     * @return
     */
    public String refreshObtainState(){
         //领取了但是是无效的
        if (CouponUseStatus.USED.getDictValue().equals(status) ||
                CouponUseStatus.USING.getDictValue().equals(status)) {
            return CommonConstant.OBTAIN_STATE_USE;
        } else if (validEndDate.compareTo(timestamp2LocalDateTime(localDate2TimeStamp(now()))) < 0 ) {
            return  CommonConstant.OBTAIN_STATE_OVERDUE;
        }else if (validStartDate.compareTo(timestamp2LocalDateTime(localDate2TimeStamp(now()))) > 0 ){
            return CommonConstant.OBTAIN_STATE_UNSTART;
        }

        return CommonConstant.OBTAIN_STATE_YES;
    }

}

