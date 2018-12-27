package com.youyu.cardequity.promotion.biz.strategy.activity;

import com.youyu.cardequity.promotion.biz.dal.entity.ActivityProfitEntity;
import com.youyu.cardequity.promotion.dto.OrderProductDetailDto;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;

import java.util.List;

/**
 * Created by caiyi on 2018/12/26.
 */
public abstract class ActivityStrategy {

    public abstract UseActivityRsp applyActivity(ActivityProfitEntity item, List<OrderProductDetailDto> productList);
}
