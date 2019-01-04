package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.CouponQuotaRuleEntity;
import com.youyu.common.mapper.YyMapper;
import feign.Param;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
public interface CouponQuotaRuleMapper extends YyMapper<CouponQuotaRuleEntity> {

    /**
     * 根据优惠券id获取额度规则
     * @param couponId
     * @return
     */
    CouponQuotaRuleEntity findCouponQuotaRuleById(@Param("couponId") String couponId );

    /**
     * 逻辑删除通过优惠id
     * @param couponId
     * @return
     */
    int logicDelByCouponId(@org.apache.ibatis.annotations.Param("couponId") String couponId);

}




