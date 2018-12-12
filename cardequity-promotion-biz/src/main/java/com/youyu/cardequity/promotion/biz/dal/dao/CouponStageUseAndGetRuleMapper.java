package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.CouponStageUseAndGetRuleEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.dto.ShortCouponDetailDto;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
public interface CouponStageUseAndGetRuleMapper extends YyMapper<CouponStageUseAndGetRuleEntity> {

    /**
     * 通过优惠券id查询优惠券详细阶梯信息列表
     * @param couponId 优惠券ID
     * @return
     */
    List<CouponStageUseAndGetRuleEntity> findStageByCouponId(@Param("couponId") String couponId);


    /**
     * 根据指定券的阶梯Id，获取到券的阶梯信息
     * @param stageId 优惠券阶梯id，必传
     * @param useFlag 值为1标识查询使用规则，值为0标识自动发放规则，为空标识查询全部
     * @param couponId 优惠券Id，为空标识不参与限制查询
     * @return
     */
    CouponStageUseAndGetRuleEntity findCouponStageById(@Param("couponId") String couponId,@Param("stageId") String stageId,@Param("useFlag") String useFlag);
}




