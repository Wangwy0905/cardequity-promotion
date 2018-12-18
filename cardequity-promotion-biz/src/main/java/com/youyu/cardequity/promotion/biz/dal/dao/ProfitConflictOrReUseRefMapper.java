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

    /**
     * 根据主的优惠券或活动id查找冲突或可叠加的优惠券或活动
     * @param activeOrCouponType
     * @param activeOrCouponId
     * @param conflictFlag
     * @return
     */
    List<ProfitConflictOrReUseRefEntity> findBySpecifyId(@Param("activeOrCouponType") String activeOrCouponType,
                                                         @Param("activeOrCouponId") String activeOrCouponId,
                                                         @Param("conflictFlag") String conflictFlag);

    /**
     * 通过对应关系验证冲突或叠加配置
     * @param activeOrCouponType
     * @param activOrCouponId
     * @param targetActiveOrCouponType
     * @param targetActivOrCouponId
     * @param conflictFlag
     * @return
     */
    ProfitConflictOrReUseRefEntity findByBothId(@Param("activeOrCouponType") String activeOrCouponType,
                                                @Param("activOrCouponId") String activOrCouponId,
                                                @Param("targetActiveOrCouponType") String targetActiveOrCouponType,
                                                @Param("targetActivOrCouponId") String targetActivOrCouponId,
                                                @Param("conflictFlag") String conflictFlag);

}




