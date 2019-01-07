package com.youyu.cardequity.promotion.dto.other;

import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.dto.ActivityQuotaRuleDto;
import com.youyu.cardequity.promotion.dto.ActivityStageCouponDto;
import com.youyu.cardequity.promotion.dto.other.ActivityDetailDto;
import com.youyu.cardequity.promotion.enums.dict.ClientType;
import com.youyu.cardequity.promotion.vo.req.BaseProductReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by caiyi on 2019/1/3.
 */
@Data
public class ActivityViewDto {

    @ApiModelProperty(value = "活动编号:")
    private String uuid;

    @ApiModelProperty(value = "0-限额任选 1-折扣 2-优惠价 3-现金立减 4-自动返券")
    private String activityCouponType;

    @ApiModelProperty(value = "优惠名称:")
    private String activityName;

    @ApiModelProperty(value = "门槛短描:如满3件减20")
    private String ActivityShortDesc;

    @ApiModelProperty(value = "优惠值:如果是阶梯或随机的填0，存折扣、金额")
    private BigDecimal profitValue;

    @ApiModelProperty(value = "优惠开始日:")
    private LocalDateTime allowUseBeginDate;

    @ApiModelProperty(value = "优惠结束日:")
    private LocalDateTime allowUseEndDate;

    @ApiModelProperty(value = "优惠商品数量:特价活动一般需要设置")
    private BigDecimal maxCount;

    @ApiModelProperty(value = "门槛阶梯信息:传入的activityShortDesc、endValue可以为空")
    private List<ActivityStageCouponDto> stageList;

    @ApiModelProperty(value = "优惠券涉及的商品")
    private List<BaseProductReq> productList;

    @ApiModelProperty(value = "适用类型:0-普通 1-会员专属 2-银行卡专属")
    private String applyType;

    public ActivityDetailDto switchToModel(){
        ActivityDetailDto result=new ActivityDetailDto();
        ActivityProfitDto dto = new ActivityProfitDto();
        BeanUtils.copyProperties(this,dto);
        if (CommonConstant.PROMOTION_APPLYTYPE_MEMBER.equals(applyType)){
            dto.setClientTypeSet(ClientType.MEMBER.getDictValue());
        }

        if (maxCount!=null && maxCount.compareTo(BigDecimal.ZERO)>0){
            ActivityQuotaRuleDto quotaRuleDto = new ActivityQuotaRuleDto();
            quotaRuleDto.setActivityId(uuid);
            quotaRuleDto.setMaxCount(maxCount);
        }

        result.setActivityProfit(dto);
        result.setStageList(stageList);
        result.setProductList(productList);
        return result;
    }

}
