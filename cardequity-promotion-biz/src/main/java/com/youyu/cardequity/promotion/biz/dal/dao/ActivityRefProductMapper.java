package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.ActivityRefProductEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponRefProductEntity;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.vo.req.BaseActivityReq;
import com.youyu.cardequity.promotion.vo.req.BaseProductReq;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-20
 */
public interface ActivityRefProductMapper extends YyMapper<ActivityRefProductEntity> {

    /**
     * 查询指定券和产品是否适用
     * @param activityId 必填
     * @param productId 必填
     * @return
     */
    ActivityRefProductEntity findByBothId(@Param("activityId") String activityId,
                                        @Param("productId") String productId );

    /**
     * 查询指定券和产品是否适用
     * @param activityId 必填
     * @param productId 必填
     * @return
     */
    ActivityRefProductEntity findByActivityAndSkuId(@Param("activityId") String activityId,
                                          @Param("productId") String productId ,
                                      @Param("skuId") String skuId);

    /**
     * 查询商品在其他活动中配置信息：用于检查是否一个商品配置了两个活动
     * @param list
     * @param activity
     * @return
     */
    List<ActivityRefProductEntity> findReProductBylist(@Param("list") List<BaseProductReq> list,
                                                 @Param("activity") ActivityProfitDto activity);

    /**
     * 通过活动编号物理删除适用商品
     * @param activityId
     * @return
     */
    int deleteByActivityId(@Param("activityId") String activityId);

    /**
     * 通过活动信息物理删除适用商品
     * @param baseActivity
     * @return
     */
    int deleteByBaseActivity(BaseActivityReq baseActivity);

    /**
     * 通过活动编号获得配置的商品
     * @param activityId
     * @return
     */
    List<ActivityRefProductEntity> findByActivityId(@Param("activityId") String activityId);

    /**
     * 通过排除指定活动编号获得其他配置的商品
     * @param activityId
     * @return
     */
    List<ActivityRefProductEntity> findByExcludeActivityId(@Param("activityId") String activityId);

    /**
     * 通过活动编号获得配置的商品
     * @param activityCouponType
     * @return
     */
    List<BaseProductReq> findProductInValidActivity(@Param("status") String status,@Param("activityCouponType") String activityCouponType);

}




