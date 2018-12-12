package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.ActivityProfitEntity;
import com.youyu.cardequity.promotion.vo.req.QryProfitCommonReq;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
public interface ActivityProfitMapper extends YyMapper<ActivityProfitEntity> {

    /**
     * 获取可参与普通活动(非银行卡相关和会员相关)根据订单及商品属性
     * @param productId：商品编号
     * @return
     */
    List<ActivityProfitEntity> findEnableGetCommonActivity(@Param("productId") String productId,
                                                           @Param("groupId") String groupId,
                                                           @Param("entrustWay") String entrustWay);

    /**
     * 获取可参与普通活动(非银行卡相关和会员相关)根据订单及商品属性
     * @param productId：商品编号，为空表示查询不指定相关商品的活动
     * @return
     */
    List<ActivityProfitEntity> findEnableGetMemberActivity(@Param("productId") String productId,
                                                           @Param("groupId") String groupId,
                                                           @Param("entrustWay") String entrustWay,
                                                           @Param("clientType") String clientType);
}




