package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.ActivityProfitEntity;
import com.youyu.cardequity.promotion.vo.req.BaseActivityReq;
import com.youyu.cardequity.promotion.vo.req.BaseQryActivityReq;
import com.youyu.cardequity.promotion.vo.req.BatchBaseActivityReq;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 * 开发日志
 * V1.0-V1 1004244-徐长焕-20181207 新建，实现可参与活动列表
 */
public interface ActivityProfitMapper extends YyMapper<ActivityProfitEntity> {

    /**
     * 获取可参与普通活动(非银行卡相关和会员相关)根据订单及商品属性
     * @param productId：商品编号
     * @return
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    List<ActivityProfitEntity> findEnableGetCommonActivity(@Param("productId") String productId,
                                                           @Param("entrustWay") String entrustWay);

    /**
     * 获取可参与普通活动(非银行卡相关和会员相关)根据订单及商品属性
     * @param productId：商品编号，为空表示查询不指定相关商品的活动
     * @return
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    List<ActivityProfitEntity> findEnableGetMemberActivity(@Param("productId") String productId,
                                                           @Param("entrustWay") String entrustWay,
                                                           @Param("clientType") String clientType);

    /**
     * 根据活动id查询活动详情
     * @param id
     * @return
     */
    ActivityProfitEntity findById(@Param("id") String id);

    /**
     * 通过商品id查询其查询特价活动信息
     * @param productId
     * @return
     */
    List<ActivityProfitEntity> findPriceActivityByProductId(@Param("productId") String productId,@Param("skuId") String skuId);

    /**
     * 逻辑删除
     * @param list
     * @return
     */
    int logicDelByIdList(@Param("list") BatchBaseActivityReq list);

    /**
     * 逻辑删除
     * @param baseActivity
     * @return
     */
    int logicDelById(@Param("baseActivity") BaseActivityReq baseActivity);

    /**
     * 通用查询
     * @param commonQry 通用信息
     * @return
     */
    List<ActivityProfitEntity> findActivityListByCommon(@Param("commonQry") BaseQryActivityReq commonQry);


    /**
     * 通过活动id列表查询活动基本信息
     * @param list
     * @return
     */
    List<ActivityProfitEntity> findActivityByIds(@Param("list") BatchBaseActivityReq list);

    /**
     * 汇总信息查询
     * @param commonQry
     * @return
     */
    List<GatherInfoRsp> findGatherActivityListByCommon(@Param("commonQry") BaseQryActivityReq commonQry);

    /**
     * 通过商品id查询其查询特价活动信息
     * @param productId
     * @return
     */
    List<ActivityProfitEntity> findActivityByProductId(@Param("productId") String productId,@Param("skuId") String skuId);
}




