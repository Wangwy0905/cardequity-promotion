package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.ClientCouponEntity;
import com.youyu.cardequity.promotion.dto.other.ClientCoupStatisticsQuotaDto;
import com.youyu.cardequity.promotion.dto.other.ShortClientCouponDto;
import com.youyu.cardequity.promotion.vo.req.BaseOrderInPromotionReq;
import com.youyu.cardequity.promotion.vo.rsp.FindClientCouponNumReq;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 * 开发日志
 * V1.1-V1 1004258-徐长焕-20181213 修改代码，领取优惠券功能开发
 * V1.0-V1 1004247-徐长焕-20181207 新增代码，获取已领取优惠券功能开发
 */
public interface ClientCouponMapper extends YyMapper<ClientCouponEntity> {

    /**
     * 获取客户已领取的券含：已使用(status=1和2)，未使用（status=0且有效期内），已过期（status=0且未在有效期内）
     * @Param clientId:指定客户号，必填
     * @return 返回已领取的券
     * 开发日志
     * 1004247-徐长焕-20181207 新增
     */
    List<ClientCouponEntity> findClientCoupon(@Param("clientId") String clientId,
                                              @Param("obtainState") String obtainState);

    /**
     * 获取客户已领取指定券
     * @Param clientId 指定客户号，必填
     * @Param couponId 指定的券id
     * @return 返回已领取的券
     * 开发日志
     * 1004247-徐长焕-20181207 新增
     */
    List<ClientCouponEntity> findClientCouponByCouponId(@Param("clientId") String clientId,
                                                        @Param("couponId") String couponId);

    /**
     * 获取客户已领取指定券
     * @Param clientId 指定客户号，必填
     * @Param couponId 指定的券id
     * @return 返回已领取的券
     * 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    List<ClientCouponEntity> findClientCouponByCommon(@Param("id") String id,
                                                      @Param("clientId") String clientId,
                                                      @Param("couponId") String couponId,
                                                      @Param("stageId") String stageId);

    /**
     * 统计领取情况
     * @Param clientId 指定客户号，必填
     * @Param couponId 指定的券id
     * @return 返回统计信息
     * 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    ClientCoupStatisticsQuotaDto statisticsCouponByCommon(@Param("id") String id,
                                                                @Param("clientId") String clientId,
                                                                @Param("couponId") String couponId,
                                                                @Param("stageId") String stageId);

    /**
     * 获取客户已领取有效优惠券：未使用（status=0且有效期内）
     * @Param clientId:指定客户号，必填
     * @return 返回已领取的券
     * 开发日志
     * 1004246-徐长焕-20181213 新增
     */
    List<ClientCouponEntity> findClientValidCoupon(@Param("clientId") String clientId);




    /**
     * 获取客户已领的折扣券
     * @param clientId
     * @return
     */
    List<ClientCouponEntity> findClientValidDiscountCoupon(@Param("clientId") String clientId,@Param("couponLevel") String couponLevel);

    /**
     * 获取客户已领的普通券：红包、一般优惠券
     * @param clientId
     * @return
     */
    List<ClientCouponEntity> findClientValidCommonCoupon(@Param("clientId") String clientId,@Param("couponLevel") String couponLevel);

    /**
     * 获取客户已领的免邮券
     * @param clientId
     * @return
     */
    List<ClientCouponEntity> findClientValidFreePostageCoupon(@Param("clientId") String clientId,@Param("couponLevel") String couponLevel);

    /**
     * 获取客户已领的运费券
     * @param clientId
     * @return
     */
    List<ClientCouponEntity> findClientValidMimusPostageCoupon(@Param("clientId") String clientId,@Param("couponLevel") String couponLevel);

    /**
     * 获取客户已领的折扣券
     * @param clientId
     * @return
     */
    List<ClientCouponEntity> findClientCouponByIds(@Param("clientId") String clientId,@Param("shortClientCouponList") List<ShortClientCouponDto> shortClientCouponList);

    /**
     * 获取客户已领的折扣券
     * @param uuid
     * @return
     */
    ClientCouponEntity findClientCouponById(@Param("uuid") String uuid);

    /**
     * 根据订单信息更新恢复状态
     * @param orderinfo
     * @return
     */
    int modRecoverByOrderinfo(@Param("orderinfo") BaseOrderInPromotionReq orderinfo);

    /**
     * 获取客户已领取有效优惠券：未使用（status=0且有效期内）
     * @Param clientId:指定客户号，必填
     * @return 返回已领取的券
     * 开发日志
     * 1004246-徐长焕-20181213 新增
     */
    List<ClientCouponEntity> findClientValidCouponByProduct(@Param("clientId") String clientId,
                                                            @Param("productId") String productId,
                                                            @Param("skuId") String skuId);

    /**
     * 查询有效的已领取优惠券数量
     * @return
     */
    int findAllClientValidCouponCount(@Param("idList") List<String> idList);

    /**
     * 获取当月领取的券
     * @param clientId
     * @param obtainState
     * @return
     */
    List<ClientCouponEntity> findCurrMonthObtainCoupon(@Param("clientId") String clientId,
                                                       @Param("obtainState") String obtainState);

    /**
     * 获取客户已领取的券含：已使用(status=1和2)，未使用（status=0且有效期内），已过期（status=0且未在有效期内）
     * @Param clientId:指定客户号，必填
     * @return 返回已领取的券数量
     */
    FindClientCouponNumReq findClientCouponNnm(@Param("clientId") String clientId,
                                               @Param("obtainState") String obtainState);

    /**
     * 根据客户更新领取券新领标识
     * @param clientId
     * @return
     */
    int modClientCouponNewFlag(@Param("clientId") String clientId);
}




