package com.youyu.cardequity.promotion.dto.other;

import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.cardequity.promotion.dto.*;
import com.youyu.cardequity.promotion.dto.other.CouponDetailDto;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.*;
import com.youyu.cardequity.promotion.vo.req.BaseProductReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 优惠券详情视图
 */
@Data
public class CouponViewDto {

    @ApiModelProperty(value = "优惠券编号:")
    private String uuid;

    @ApiModelProperty(value = "领取对象：0-全部用户 1-新用户 2-会员")
    private String targetFlag;

    @ApiModelProperty(value = "级别：0-自动义 1-全局")
    private String couponLevel;

    @ApiModelProperty(value = "类型:0-消费券 1-运费券")
    private String couponViewType;

    @ApiModelProperty(value = "优惠名称:")
    private String couponName;

    @ApiModelProperty(value = "门槛短描:如满3件减20")
    private String couponShortDesc;

    @ApiModelProperty(value = "使用说明")
    private String couponDesc;

    @ApiModelProperty(value = "券数量")
    private Integer maxCount;

    @ApiModelProperty(value = "优惠值:如果是阶梯或随机的填0，存折扣、金额")
    private BigDecimal profitValue;

    @ApiModelProperty(value = "门槛id:前台不展示")
    private String stageId;

    @ApiModelProperty(value = "使用金额门槛")
    private BigDecimal conditionValue;

    @ApiModelProperty(value = "每张券最大优惠金额")
    private BigDecimal perProfitTopValue;

    @ApiModelProperty(value = "优惠开始日:")
    private LocalDateTime allowUseBeginDate;

    @ApiModelProperty(value = "优惠结束日:")
    private LocalDateTime allowUseEndDate;

    @ApiModelProperty(value = "有效期限:以天为单位")
    private Integer valIdTerm;

    @ApiModelProperty(value = "领取开始日:到分秒级别")
    private LocalDateTime allowGetBeginDate;

    @ApiModelProperty(value = "领取结束日:")
    private LocalDateTime allowGetEndDate;

    @ApiModelProperty(value = "频率id:前台不展示")
    private String freqId;

    @ApiModelProperty(value = "状态:0-下架 1-上架")
    private String status;

    @ApiModelProperty(value = "频率周期类型:0-天 1-周 2-月 3-年 a-有效期内")
    private String unit;

    @ApiModelProperty(value = "客户获取总数/客户每次使用数")
    private Integer personTotalNum;

    @ApiModelProperty(value = "周期内允许此时:")
    private Integer allowCount;

    @ApiModelProperty(value = "适用商品类型:0-自动义商品范围 1-全部")
    private String applyProductFlag;

    @ApiModelProperty(value = "门槛触发类型:0-按买入金额 1-按买入数量")
    private String triggerByType;

    @ApiModelProperty(value = "优惠券涉及的商品")
    private List<BaseProductReq> productList;

    @ApiModelProperty(value = "优惠标签:标签：满返券、促销等")
    private CouponAndActivityLabelDto labelDto ;

    public CouponDetailDto switchToModel() {
        CouponDetailDto dto = new CouponDetailDto();

        //********主体信息拷贝*********
        ProductCouponDto couponDto = new ProductCouponDto();
        dto.setProductCouponDto(couponDto);
        BeanUtils.copyProperties(this, couponDto);
        couponDto.setLabelDto(labelDto);
        //couponDto.getLabelDto().setId(this.getCouponLable());

        //********适用对象转义*********
        couponDto.setClientTypeSet(CommonConstant.WILDCARD);
        //默认不是新注册客户特有
        couponDto.setGetStage(UsedStage.Other.getDictValue());
        //会员专属
        if (CommonDict.FRONDEND_MEMBER.getCode().equals(targetFlag)) {
            couponDto.setClientTypeSet(ClientType.MEMBER.getDictValue());
            //新手注册专属
        } else if (CommonDict.FRONDEND_NEW.getCode().equals(targetFlag)) {
            couponDto.setClientTypeSet(CommonConstant.WILDCARD);
            couponDto.setGetStage(UsedStage.Register.getDictValue());
        }

        //*******券类型转义**********
        //默认没有门槛
        couponDto.setCouponStrategyType(CouponStrategyType.stage.getDictValue());
        couponDto.setCouponType(CouponType.COUPON.getDictValue());
        if ("1".equals(couponViewType)) {
            couponDto.setCouponType(CouponType.TRANSFERFARE.getDictValue());
            if (productList==null || productList.isEmpty())
                couponDto.setCouponLevel(CouponActivityLevel.GLOBAL.getDictValue());
        }

        //*******门槛转义**********
        if (conditionValue != null && conditionValue.compareTo(BigDecimal.ZERO) < 0)
            conditionValue = BigDecimal.ZERO;
        if (conditionValue.compareTo(BigDecimal.ZERO) > 0) {
            couponDto.setCouponStrategyType(CouponStrategyType.stage.getDictValue());
            List<CouponStageRuleDto> stageList=new ArrayList<>();
            CouponStageRuleDto stage=new CouponStageRuleDto();
            stage.setCouponId(uuid);
            stage.setUuid(stageId);
            stage.setCouponShortDesc(this.couponShortDesc);
            stage.setBeginValue(conditionValue);
            stage.setCouponValue(profitValue);

            if (perProfitTopValue!=null &&
                    perProfitTopValue.compareTo(BigDecimal.ZERO)>0 &&
                    perProfitTopValue.compareTo(CommonConstant.IGNOREVALUE)<0 &&
                    perProfitTopValue.compareTo(stage.getCouponValue())>0) {
                couponDto.setCouponStrategyType(CouponStrategyType.equalstage.getDictValue());
                stage.setEndValue(perProfitTopValue.divide(profitValue).multiply(conditionValue));
            }
            stageList.add(stage);
            dto.setStageList(stageList);
        }

        //*******额度转义**********
        if (maxCount!=null && maxCount.intValue()>=0){
            CouponQuotaRuleDto quotaRuleDto=new CouponQuotaRuleDto();
            quotaRuleDto.setCouponId(uuid);
            quotaRuleDto.setMaxCount(maxCount);
            dto.setQuotaRule(quotaRuleDto);
        }

        //*******频率转义**********
        if (allowCount!=null && allowCount.intValue()>=0){
            List<CouponGetOrUseFreqRuleDto> freqRuleList=new ArrayList<>();
            CouponGetOrUseFreqRuleDto freqRuleDto = new CouponGetOrUseFreqRuleDto();
            freqRuleDto.setUuid(freqId);
            freqRuleDto.setCouponId(uuid);
            freqRuleDto.setStageId(stageId);
            freqRuleDto.setOpCouponType(OpCouponType.GETRULE.getDictValue());
            freqRuleDto.setAllowCount(allowCount);
            freqRuleDto.setPersonTotalNum(personTotalNum);
            freqRuleDto.setUnit(unit);
            freqRuleDto.setValue(1);
            freqRuleList.add(freqRuleDto);
            dto.setFreqRuleList(freqRuleList);
        }

        dto.setProductList(productList);

        return dto;
    }
}
