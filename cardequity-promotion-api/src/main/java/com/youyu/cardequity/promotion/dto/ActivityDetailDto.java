package com.youyu.cardequity.promotion.dto;

/**
 * Created by caiyi on 2019/1/3.
 */

import com.youyu.cardequity.common.base.bean.BeanProperties;
import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
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
        if (activityProfit != null)
            BeanUtils.copyProperties(activityProfit, result);

        if (activityQuotaRule!=null){
            result.setMaxCount(activityQuotaRule.getMaxCount());
        }
        result.setStageList(stageList);
        result.setProductList(productList);
        return result;
    }
}
