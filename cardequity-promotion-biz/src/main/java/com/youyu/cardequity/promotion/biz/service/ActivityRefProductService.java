package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.biz.dal.entity.ActivityRefProductEntity;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.dto.ActivityRefProductDto;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.vo.req.BaseActivityReq;
import com.youyu.cardequity.promotion.vo.req.BaseProductReq;
import com.youyu.common.service.IService;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-20
 */
public interface ActivityRefProductService extends IService<ActivityRefProductDto, ActivityRefProductEntity> {

    /**
     * 指定活动的商品配置范围和其他活动是否冲突
     * @param req
     * @param activity 活动：校验冲突的两个活动有效时间是否重叠
     * @return
     */
     CommonBoolDto<List<ActivityRefProductEntity>> checkProductReUse(List<BaseProductReq> req, ActivityProfitDto activity);

    /**
     * 查询已经配置了活动的商品
     * @return
     */
    List<ActivityRefProductDto> findAllProductInValidActivity(BaseActivityReq req);
}




