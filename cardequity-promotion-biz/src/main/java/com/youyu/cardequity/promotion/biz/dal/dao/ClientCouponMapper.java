package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.ClientCouponEntity;
import com.youyu.cardequity.promotion.dto.ClientCoupStatisticsQuotaDto;
import com.youyu.common.mapper.YyMapper;
import feign.Param;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
public interface ClientCouponMapper extends YyMapper<ClientCouponEntity> {

    /**
     * 获取客户已领取的券含：已使用(status=1和2)，未使用（status=0且有效期内），已过期（status=0且未在有效期内）
     * @Param clientId:指定客户号，必填
     * @return 返回已领取的券
     */
    List<ClientCouponEntity> findClientCoupon(@Param("clientId") String clientId);

    /**
     * 获取客户已领取指定券
     * @Param clientId 指定客户号，必填
     * @Param couponId 指定的券id
     * @return 返回已领取的券
     */
    List<ClientCouponEntity> findClientCouponByCouponId(@Param("clientId") String clientId,@Param("couponId") String couponId);

    /**
     * 获取客户已领取指定券
     * @Param clientId 指定客户号，必填
     * @Param couponId 指定的券id
     * @return 返回已领取的券
     */
    List<ClientCouponEntity> findClientCouponByCommon(@Param("id") String id,
                                                      @Param("clientId") String clientId,
                                                      @Param("couponId") String couponId,
                                                      @Param("stageId") String stageId);

    ClientCoupStatisticsQuotaDto statisticsCouponByCommon(@Param("id") String id,
                                                                @Param("clientId") String clientId,
                                                                @Param("couponId") String couponId,
                                                                @Param("stageId") String stageId);
}




