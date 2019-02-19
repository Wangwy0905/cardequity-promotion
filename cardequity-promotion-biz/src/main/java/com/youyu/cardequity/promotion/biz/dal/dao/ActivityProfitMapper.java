package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.ActivityProfitEntity;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.dto.other.GroupProductDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.BasePriceActivityRsp;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 * 开发日志
 * V1.0-V1 1004244-徐长焕-20181207 新建，实现可参与活动列表
 */
public interface ActivityProfitMapper extends YyMapper<ActivityProfitEntity> {

    /**
     * 【App】获取【有效期内、上架的、有额度的】可参与普通活动(非银行卡相关和会员相关)根据订单及商品属性
     *
     * @param productId：商品编号
     * @return 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    List<ActivityProfitEntity> findEnableGetCommonActivity(@Param("productId") String productId,
                                                           @Param("clientType") String clientType,
                                                           @Param("entrustWay") String entrustWay);

    /**
     * 【App】获取【有效期内、上架的】可参与（粗粒度校验）普通活动(非银行卡相关和会员相关)根据订单及商品属性
     *
     * @param productId：商品编号
     * @return 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    List<ActivityProfitEntity> findEnableGetCommonFirstActivity(@Param("productId") String productId,
                                                                @Param("clientType") String clientType,
                                                                @Param("entrustWay") String entrustWay);


    /**
     * 【App】获取【有效期内、上架的、有额度的】可参与会员活动(非银行卡相关和会员相关)根据订单及商品属性
     *
     * @param productId：商品编号，为空表示查询不指定相关商品的活动
     * @return 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    List<ActivityProfitEntity> findEnableGetMemberActivity(@Param("productId") String productId,
                                                           @Param("entrustWay") String entrustWay,
                                                           @Param("clientType") String clientType);


    /**
     * 【App】获取【有效期内、上架的】可参与（粗粒度校验）会员活动(非银行卡相关和会员相关)根据订单及商品属性
     *
     * @param productId：商品编号，为空表示查询不指定相关商品的活动
     * @return 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    List<ActivityProfitEntity> findEnableGetMemberFirstActivity(@Param("productId") String productId,
                                                                @Param("entrustWay") String entrustWay,
                                                                @Param("clientType") String clientType);

    /**
     * 【App】【有效期内、上架的】查询商品的特价基础信息
     *
     * @param productId
     * @param skuId
     * @return
     */
    List<BasePriceActivityRsp> findBasePriceByProduct(@Param("productId") String productId, @Param("skuId") String skuId);

    /**
     * 【App】查询【有效期内、上架的、有剩余额度】特价活动：去除已经达到额度的活动
     *
     * @param productId
     * @param skuId
     * @return
     */
    List<ActivityProfitEntity> findValidPriceActivityByProduct(@Param("productId") String productId, @Param("skuId") String skuId);

    /**
     * 【后台+App】根据活动id查询活动详情
     *
     * @param id
     * @return
     */
    ActivityProfitEntity findById(@Param("id") String id);


    /**
     * 【后台+App】通过活动id列表查询活动基本信息
     *
     * @param list
     * @return
     */
    List<ActivityProfitEntity> findActivityByIds(@Param("list") BatchBaseActivityReq list);


    /**
     * 【后台+App】查询【有效期内、上架的】特价活动信息通过商品id
     *
     * @param productId
     * @return
     */
    List<ActivityProfitEntity> findActivityByProductId(@Param("productId") String productId,
                                                       @Param("skuId") String skuId,
                                                       @Param("termStatus") String termStatus,
                                                       @Param("status") String status);


    /**
     * **************************************************************************
     * 【后台】批量逻辑删除
     *
     * @param list
     * @return
     */
    int logicDelByIdList(BatchBaseActivityReq list);

    /**
     * 【后台】逻辑删除
     *
     * @param baseActivity
     * @return
     */
    int logicDelById(BaseActivityReq baseActivity);

    /**
     * 【后台】通过商品id查询其查询特价活动信息
     *
     * @param productId
     * @return
     */
    List<ActivityProfitEntity> findPriceActivityByProductId(@Param("productId") String productId, @Param("skuId") String skuId);

    /**
     * 【后台】通用查询
     *
     * @param commonQry 通用信息
     * @return
     */
    List<ActivityProfitEntity> findActivityListByCommon(@Param("commonQry") BaseQryActivityReq commonQry);

    /**
     * 【后台】汇总信息查询
     *
     * @param commonQry
     * @return
     */
    List<GatherInfoRsp> findGatherActivityListByCommon(BaseQryActivityReq commonQry);


    /**
     * 【后台】通用查询：支持按id、商品id、名称模糊指定
     *
     * @param commonQry 通用信息
     * @return
     */
    List<ActivityProfitEntity> findActivityList(BaseQryActivityReq commonQry);

    /**
     * 【后台】查询【有效期内、上架的】商品对应活动数量
     *
     * @param list
     * @return
     */
    List<GatherInfoRsp> findActivityNumByProducts(BatchBaseProductReq list);

    /**
     * 【后台】【有效期内、上架的】获取无产品限制的活动
     *
     * @return
     */
    List<ActivityProfitEntity> findUnlimitedProductActivity();

    /**
     * 查询最新设置特价活动的商品
     * @param req 查询参数
     * @return
     */
    List<GroupProductDto> findLeastPriceProductActivity(OperatQryReq req);

    /**
     * 【后台+App】查询【有效期内、上架的】特价活动信息通过商品id
     *
     * @param idList 商品id集合
     * @return
     */
    List<ActivityProfitEntity> findPriceActivityByProductIds(@Param("idList") List<String> idList,
                                                       @Param("termStatus") String termStatus,
                                                       @Param("status") String status);

    /**
     * 查询当前有效的特价活动最晚结束日期
     * @param req
     * @return
     */
    Date findValidPriceLastTime(OperatReq req);


}




