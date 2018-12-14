package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.ProfitConflictOrReUseRefEntity;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-13
 */
public interface ProfitConflictOrReUseRefMapper extends YyMapper<ProfitConflictOrReUseRefEntity> {

    //ProfitConflictOrReUseRefEntity findById(@Param("id") String id);

    List<ProfitConflictOrReUseRefEntity> findByMainId(@Param("activOrCouponFlag") String activOrCouponFlag,
                                                     @Param("activOrCouponId") String activOrCouponId);

}




