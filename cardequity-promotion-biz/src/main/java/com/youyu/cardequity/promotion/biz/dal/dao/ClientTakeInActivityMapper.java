package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.ClientTakeInActivityEntity;
import com.youyu.cardequity.promotion.dto.other.ClientCoupStatisticsQuotaDto;
import com.youyu.cardequity.promotion.vo.req.BaseOrderInPromotionReq;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
public interface ClientTakeInActivityMapper extends YyMapper<ClientTakeInActivityEntity> {

    /**
     * 统计领取情况
     * @Param clientId 指定客户号，非必填
     * @Param couponId 指定的券id
     * @return 返回统计信息
     * 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    ClientCoupStatisticsQuotaDto statisticsCouponByCommon(@Param("clientId") String clientId,
                                                          @Param("activityId") String activityId,
                                                          @Param("stageId") String stageId);


    /**
     * 根据订单信息逻辑删除使用记录
     * @param orderinfo
     * @return
     */
    int modRecoverByOrderinfo(BaseOrderInPromotionReq orderinfo);
}




