package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.dto.ShortCouponDetailDto;
import com.youyu.cardequity.promotion.vo.req.BaseCouponReq;
import com.youyu.cardequity.promotion.vo.req.BaseQryCouponReq;
import com.youyu.cardequity.promotion.vo.req.BatchBaseCouponReq;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 * 开发日志
 * V1.0-V1 1004244-徐长焕-20181207 新建代码，获取客户可领取的券
 */
public interface ProductCouponMapper extends YyMapper<ProductCouponEntity> {

    /**
     * 通过常用参数查询可领取优惠券列表
     * @param productId 产品ID
     * @param clientType 客户类型
     * @param entrustWay 委托方式
     * @return
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    List<ProductCouponEntity> findEnableGetCouponListByCommon(@Param("productId") String productId,
                                                              @Param("entrustWay") String entrustWay,
                                                              @Param("clientType") String clientType);


    /**
     * 根据指定券，获取到券的基本信息
     * @param couponId 优惠券id
     * @return
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    ProductCouponEntity findProductCouponById(@Param("couponId") String couponId);


    /**
     * 通用查询
     * @param commonQry 通用信息
     * @return
     */
    List<ProductCouponEntity> findCouponListByCommon(@Param("commonQry") BaseQryCouponReq commonQry);

    /**
     * 批量逻辑删除
     * @param list
     * @return
     */
    int logicDelByList(@Param("list") BatchBaseCouponReq list);

    /**
     * 逻辑删除
     * @param baseCoupon
     * @return
     */
    int logicDelById(BaseCouponReq baseCoupon);

}




