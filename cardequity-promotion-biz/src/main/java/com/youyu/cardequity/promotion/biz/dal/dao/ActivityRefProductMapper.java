package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.ActivityRefProductEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponRefProductEntity;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-20
 */
public interface ActivityRefProductMapper extends YyMapper<ActivityRefProductEntity> {

    /**
     * 查询指定券和产品是否适用
     * @param couponId 必填
     * @param productId 必填
     * @return
     */
    ActivityRefProductEntity findByBothId(@Param("couponId") String couponId,
                                        @Param("productId") String productId );
}




