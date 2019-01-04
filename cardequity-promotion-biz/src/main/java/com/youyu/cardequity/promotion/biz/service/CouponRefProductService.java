package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.biz.dal.entity.CouponRefProductEntity;
import com.youyu.cardequity.promotion.dto.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.CouponRefProductDto;
import com.youyu.cardequity.promotion.vo.req.BatchRefProductReq;
import com.youyu.common.service.IService;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-20
 */
public interface CouponRefProductService extends IService<CouponRefProductDto, CouponRefProductEntity> {
    /**
     * 配置优惠的适用商品范围
     * @param req
     * @return
     */
     CommonBoolDto batchAddCouponRefProduct(BatchRefProductReq req);
}




