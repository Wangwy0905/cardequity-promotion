package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.ClientTakeInCouponEntity;
import com.youyu.cardequity.promotion.vo.req.BaseOrderInPromotionReq;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
public interface ClientTakeInCouponMapper extends YyMapper<ClientTakeInCouponEntity> {

    int modRecoverByOrderinfo(@Param("orderinfo")BaseOrderInPromotionReq orderinfo);
}




