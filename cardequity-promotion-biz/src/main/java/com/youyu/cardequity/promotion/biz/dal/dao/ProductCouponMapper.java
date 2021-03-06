package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.dto.req.MemberProductMaxCouponReq;
import com.youyu.cardequity.promotion.dto.req.ProductCouponQueryReq;
import com.youyu.cardequity.promotion.dto.rsp.MemberProductMaxCouponRsp;
import com.youyu.cardequity.promotion.dto.rsp.ProductCouponGetStatisticsRsp;
import com.youyu.cardequity.promotion.dto.rsp.ProductCouponQueryRsp;
import com.youyu.cardequity.promotion.dto.rsp.ProductCouponViewRsp;
import com.youyu.cardequity.promotion.vo.req.BaseCouponReq;
import com.youyu.cardequity.promotion.vo.req.BaseQryCouponReq;
import com.youyu.cardequity.promotion.vo.req.BatchBaseCouponReq;
import com.youyu.cardequity.promotion.vo.req.BatchBaseProductReq;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 * 开发日志
 * V1.0-V1 1004244-徐长焕-20181207 新建代码，获取客户可领取的券
 */
public interface ProductCouponMapper extends YyMapper<ProductCouponEntity> {

    /**
     * 【有效期内、上架的】通过常用参数查询可领取优惠券列表
     *
     * @param productId  产品ID
     * @param clientType 客户类型
     * @param entrustWay 委托方式
     * @return 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    List<ProductCouponEntity> findEnableGetCouponListByCommon(@Param("productId") String productId,
                                                              @Param("entrustWay") String entrustWay,
                                                              @Param("clientType") String clientType);


    /**
     * 【有效期内、上架的】通过常用参数查询可领取优惠券列表,不排除限额不满足的优惠券
     *
     * @param productId  产品ID
     * @param clientType 客户类型
     * @param entrustWay 委托方式
     * @return 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    List<ProductCouponEntity> findEnableGetCouponList(@Param("productId") String productId,
                                                      @Param("entrustWay") String entrustWay,
                                                      @Param("clientType") String clientType
    );

    /**
     * 根据指定券，获取到券的基本信息
     *
     * @param couponId 优惠券id
     * @return 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    ProductCouponEntity findProductCouponById(@Param("couponId") String couponId);


    /**
     * 查询指定的优惠券列表
     *
     * @param qry 通用信息
     * @return
     */
    List<ProductCouponEntity> findCouponListByIds(List<String> qry);


    /**
     * ******************************************后台功能********************************
     * 【后台】通用查询
     *
     * @param commonQry 通用信息
     * @return
     */
    List<ProductCouponEntity> findCouponListByCommon(@Param("commonQry") BaseQryCouponReq commonQry);

    /**
     * 【后台】批量逻辑删除
     *
     * @param list
     * @return
     */
    int logicDelByList(BatchBaseCouponReq list);

    /**
     * 【后台】逻辑删除
     *
     * @param baseCoupon
     * @return
     */
    int logicDelById(BaseCouponReq baseCoupon);


    /**
     * 【后台】模糊指定关键字通用查询
     *
     * @param commonQry 通用信息
     * @return
     */
    List<ProductCouponEntity> findCouponList(@Param("commonQry") BaseQryCouponReq commonQry);


    /**
     * 【后台】模糊指定关键字汇总统计查询，与findCouponList对应
     *
     * @param commonQry
     * @return
     */
    List<GatherInfoRsp> findGatherCouponList(@Param("commonQry") BaseQryCouponReq commonQry);

    /**
     * 【后台】查询【有效期内、上架的】商品相关优惠数量
     *
     * @param list
     * @return
     */
    List<GatherInfoRsp> findCouponNumByProducts(@Param("list") BatchBaseProductReq list);

    /**
     * 【后台】查询【有效期内、上架的】无产品限制的优惠券
     *
     * @return
     */
    List<ProductCouponEntity> findUnlimitedProductCoupon();


    /**
     * 【后台】查询商品相关的优惠券
     *
     * @return
     */
    List<ProductCouponEntity> findCouponListByProduct(@Param("status") String status,
                                                      @Param("couponStatus") String couponStatus,
                                                      @Param("productId") String productId,
                                                      @Param("skuId") String skuId,
                                                      @Param("couponType") String couponType,
                                                      @Param("obtainType") String obtainType);

    /**
     * 【后台】查询商品相关的优惠券
     *
     * @return
     */
    List<ProductCouponEntity> findInQuotaCouponListByProduct(@Param("status") String status,
                                                             @Param("couponStatus") String couponStatus,
                                                             @Param("productId") String productId,
                                                             @Param("skuId") String skuId,
                                                             @Param("couponType") String couponType);


    /**
     * 获取指定月可领取优惠券
     *
     * @param productId
     * @param entrustWay
     * @param clientType
     * @param monthNum
     * @return
     */
    List<ProductCouponEntity> findSpacifyMonthEnableGetCouponsByCommon(@Param("productId") String productId,
                                                                       @Param("entrustWay") String entrustWay,
                                                                       @Param("clientType") String clientType,
                                                                       @Param("monthNum") int monthNum,
                                                                       @Param("lastMonthDay") String lastMonthDay);

    /**
     * 根据请求参数获取最大优惠券金额
     *
     * @param productMaxCouponReq
     * @return
     */
    MemberProductMaxCouponRsp getMemberProductMaxCoupon(@Param("productMaxCouponReq") MemberProductMaxCouponReq productMaxCouponReq);

    /**
     * 优惠券查询
     *
     * @param productCouponQueryReq
     * @return
     */
    List<ProductCouponQueryRsp> getProductCouponQuery(@Param("productCouponQueryReq") ProductCouponQueryReq productCouponQueryReq);

    /**
     * 根据用户类型进行统计
     *
     * @return
     */
    List<ProductCouponGetStatisticsRsp> getStatisticsByClientType();

    /**
     * 查看优惠券详情
     *
     * @param productCouponId
     * @return
     */
    ProductCouponViewRsp getByProductCouponId(@Param("productCouponId") String productCouponId);
}




