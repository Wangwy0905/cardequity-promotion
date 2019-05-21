package com.youyu.cardequity.promotion.biz.constant;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work redission key常量类
 */
public final class RedissonKeyConstant {

    /**
     * 活动优惠券消费redission key前缀
     */
    public static final String CARDEQUITY_ACTIVITY_COUPON = "cardequity:activity:coupon:";
    /**
     * 活动优惠券消费活动id,客户id和优惠券id
     */
    public static final String CARDEQUITY_ACTIVITY_COUPON_ACTIVITY_CLIENT_COUPON = CARDEQUITY_ACTIVITY_COUPON + "activityId:{0}" + "clientId:{1}" + "couponId:{2}";

    /**
     * 商品优惠券redission key前缀
     */
    public static final String CARDEQUITY_PRODUCT_COUPON = "cardequity:product:coupon:";
    /**
     * 所有商品信息缓存map redission key前缀
     */
    public static final String CARDEQUITY_PRODUCT_COUPON_ALL_PRODUCT_CACHE_MAP = CARDEQUITY_PRODUCT_COUPON + "allProductInfoCacheMap";

    public static final String CARDEQUITY_COUPON = "cardequity:coupon:";
    public static final String CARDEQUITY_COUPON_COUPON = CARDEQUITY_COUPON + "couponId:{0}";
}
