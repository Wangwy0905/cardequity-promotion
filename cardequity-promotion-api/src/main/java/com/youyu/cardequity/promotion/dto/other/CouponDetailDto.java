package com.youyu.cardequity.promotion.dto.other;

import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.cardequity.promotion.dto.*;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.*;
import com.youyu.cardequity.promotion.vo.req.BaseProductReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券详情
 */
@Getter
@Setter
public class CouponDetailDto {
    @ApiModelProperty(value = "优惠券基本信息", required = true)
    private ProductCouponDto productCouponDto;

    @ApiModelProperty(value = "优惠券涉及的商品")
    private List<BaseProductReq> productList;

    @ApiModelProperty(value = "领取或使用规则")
    private List<CouponGetOrUseFreqRuleDto> freqRuleList;

    @ApiModelProperty(value = "额度规则")
    private CouponQuotaRuleDto quotaRule;

    @ApiModelProperty(value = "子券信息")
    private List<CouponStageRuleDto> stageList;

    @ApiModelProperty(value = "操作者：用于更新产生者或更新者,一般存IP地址")
    private String operator;

    public CouponViewDto switchToView() {
        CouponViewDto dto = new CouponViewDto();
        if (productCouponDto != null) {
            BeanUtils.copyProperties(productCouponDto, dto);
            dto.setTargetFlag(CommonDict.FRONDEND_ALL.getCode());

            //转义领取对象
            if (productCouponDto.getClientTypeSet() != null) {
                //会员专属
                if (productCouponDto.getClientTypeSet().equals(ClientType.MEMBER.getDictValue())) {
                    dto.setTargetFlag(CommonDict.FRONDEND_MEMBER.getCode());
                } else {
                    //注册用户
                    if (UsedStage.Register.equals(productCouponDto.getGetStage())) {
                        dto.setTargetFlag(CommonDict.FRONDEND_NEW.getCode());
                    }
                }
            }

            //转义优惠券类型
            dto.setCouponViewType("0");
            if (CouponType.TRANSFERFARE.getDictValue().equals(productCouponDto.getCouponType()) ||
                    CouponType.FREETRANSFERFARE.getDictValue().equals(productCouponDto.getCouponType()))
                dto.setCouponViewType("1");

        }

        if (quotaRule != null) {
            dto.setMaxCount(quotaRule.getMaxCount());
        }

        if (stageList != null && stageList.size() > 0) {
            for (CouponStageRuleDto stage : stageList) {
                if (TriggerByType.NUMBER.getDictValue().equals(stage.getTriggerByType())) {
                    dto.setConditionFund(stage.getBeginValue());
                } else {
                    dto.setConditionCount(stage.getBeginValue());
                }
                dto.setProfitValue(stage.getCouponValue());
                //如果是等阶的消费券，需要转换计算每人最大优惠额
                if (CouponStrategyType.equalstage.getDictValue().equals(productCouponDto.getCouponStrategyType()) &&
                        stage.getEndValue()!=null && stage.getEndValue().compareTo(BigDecimal.ZERO)>0 && stage.getEndValue().compareTo(CommonConstant.IGNOREVALUE)<0 &&
                        stage.getBeginValue()!=null && stage.getBeginValue().compareTo(BigDecimal.ZERO)>0) {
                    dto.setPerProfitTopValue(stage.getEndValue().divide(stage.getBeginValue()).multiply(stage.getCouponValue()));
                }

                dto.setPerProfitTopValue(stage.getEndValue());
                dto.setStageId(stage.getUuid());
                break;//首期只有一个阶梯的数据
            }

        }
        if (freqRuleList != null && freqRuleList.size() > 0){
            for (CouponGetOrUseFreqRuleDto freq : freqRuleList) {
              if (OpCouponType.USERULE.getDictValue().equals(freq.getOpCouponType()))
                  continue;
                dto.setFreqId(freq.getUuid());
               dto.setUnit(freq.getUnit());
               dto.setAllowCount(freq.getAllowCount());
               dto.setPersonTotalNum(freq.getPersonTotalNum());
            }
        }

        dto.setProductList(productList);
        return dto;
    }

}
