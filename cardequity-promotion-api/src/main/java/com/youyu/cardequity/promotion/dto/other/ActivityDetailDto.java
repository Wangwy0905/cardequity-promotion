package com.youyu.cardequity.promotion.dto.other;

/**
 * Created by caiyi on 2019/1/3.
 */

import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.dto.ActivityQuotaRuleDto;
import com.youyu.cardequity.promotion.dto.ActivityStageCouponDto;
import com.youyu.cardequity.promotion.enums.dict.ClientType;
import com.youyu.cardequity.promotion.vo.req.BaseProductReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

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
            if (activityProfit.getBankCodeSet()!=null &&
                    activityProfit.getBankCodeSet()!="" &&
                    activityProfit.getBankCodeSet()!="*"){
                result.setApplyType(CommonConstant.PROMOTION_APPLYTYPE_BANKCODE);
            }
            if (ClientType.MEMBER.getDictValue().equals(activityProfit.getClientTypeSet())){
                result.setApplyType(CommonConstant.PROMOTION_APPLYTYPE_MEMBER);
            }
        }

        if (activityQuotaRule!=null){
            result.setMaxCount(activityQuotaRule.getMaxCount());
        }
        result.setStageList(stageList);
        result.setProductList(productList);
        return result;
    }
}
