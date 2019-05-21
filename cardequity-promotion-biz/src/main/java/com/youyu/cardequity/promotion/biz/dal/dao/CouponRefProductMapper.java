package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.CouponRefProductEntity;
import com.youyu.cardequity.promotion.dto.req.CouponRefProductQueryReq;
import com.youyu.cardequity.promotion.dto.rsp.CouponRefProductQueryRsp;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-20
 */
public interface CouponRefProductMapper extends YyMapper<CouponRefProductEntity> {
    /**
     * 查询指定券和产品是否适用
     * @param couponId 必填
     * @param productId 必填
     * @return
     */
    CouponRefProductEntity findByBothId(@Param("couponId") String couponId,
                                            @Param("productId") String productId );

    /**
     * 查询指定券或产品适用列表
     * @param couponId
     * @param productId
     * @return
     */
    List<CouponRefProductEntity> findListByCommon(@Param("couponId") String couponId,
                                                  @Param("productId") String productId );

    /**
     * 通过优惠券id删除对应适用商品配置
     * @param couponId
     * @return
     */
    int deleteByCouponId(@Param("couponId") String couponId);

    /**
     * 查询指定券和产品是否适用
     * @param list 必填
     * @return
     */
    List<CouponRefProductEntity> findByCouponIds(@Param("list") List<String> list);

    /**
     * 查询优惠券关联商品
     *
     * @param couponRefProductQueryReq
     * @return
     */
    List<CouponRefProductQueryRsp> getCouponRefProductQuery(@Param("couponRefProductQueryReq") CouponRefProductQueryReq couponRefProductQueryReq);
}




