package com.youyu.cardequity.promotion.dto.other;

/**
 * 活动详情
 */

import com.youyu.cardequity.common.base.util.StringUtil;
import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.dto.ActivityQuotaRuleDto;
import com.youyu.cardequity.promotion.dto.ActivityStageCouponDto;
import com.youyu.cardequity.promotion.enums.dict.ClientType;
import com.youyu.cardequity.promotion.vo.req.BaseProductReq;
import com.youyu.cardequity.promotion.vo.rsp.BasePriceActivityRsp;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ActivityDetailDto {
    @ApiModelProperty(value = "活动基本信息", required = true)
    private ActivityProfitDto activityProfit;

    @ApiModelProperty(value = "活动额度")
    private ActivityQuotaRuleDto activityQuotaRule;

    @ApiModelProperty(value = "活动门槛阶梯")
    private List<ActivityStageCouponDto> stageList;

    @ApiModelProperty(value = "优惠券涉及的商品")
    private List<BaseProductReq> productList;

    public ActivityViewDto switchToView() {
        ActivityViewDto result = new ActivityViewDto();
        if (activityProfit != null) {
            BeanUtils.copyProperties(activityProfit, result);
            //适配适用的银行卡指定额则为银行卡专属
            if (activityProfit.getBankCodeSet() != null &&
                    activityProfit.getBankCodeSet() != "" &&
                    activityProfit.getBankCodeSet() != "*") {
                result.setApplyType(CommonConstant.PROMOTION_APPLYTYPE_BANKCODE);
            }
            if (ClientType.MEMBER.getDictValue().equals(activityProfit.getClientTypeSet())) {
                result.setApplyType(CommonConstant.PROMOTION_APPLYTYPE_MEMBER);
            }
            result.setLabelDto(activityProfit.getLabelDto());

            //保护优惠值
            if (activityProfit.getProfitValue() != null && activityProfit.getProfitValue().compareTo(BigDecimal.ZERO) > 0) {
                if (stageList != null && !stageList.isEmpty()) {
                    activityProfit.setProfitValue(stageList.get(0).getProfitValue());
                }
            }
        }

        if (activityQuotaRule != null) {
            result.setMaxCount(activityQuotaRule.getMaxCount());
        }


        result.setStageList(stageList);
        result.setProductList(productList);
        return result;
    }

    public BasePriceActivityRsp switchDetailToBasePrice() {
        if (productList== null || productList.isEmpty() || productList.get(0) == null || StringUtil.isEmpty(productList.get(0).getSkuId()))
            return null;
        BasePriceActivityRsp priceItem = new BasePriceActivityRsp();
        priceItem.setId(activityProfit.getId());
        if (activityQuotaRule != null)
            priceItem.setMaxCount(activityQuotaRule.getMaxCount());
        priceItem.setPrice(activityProfit.getProfitValue());
        priceItem.setStatus(activityProfit.getStatus());
        priceItem.setProductId(productList.get(0).getProductId());
        priceItem.setSkuId(productList.get(0).getSkuId());
        return priceItem;
    }
}
