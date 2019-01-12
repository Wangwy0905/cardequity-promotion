package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.ActivityStageCouponEntity;
import com.youyu.cardequity.promotion.vo.req.BaseActivityReq;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
public interface ActivityStageCouponMapper extends YyMapper<ActivityStageCouponEntity> {

    /**
     * 1004259-徐长焕-20181210 新增
     * 功能：查询指定活动门槛阶梯详细信息
     * @param activityId 活动id
     * @return
     */
    List<ActivityStageCouponEntity> findActivityProfitDetail(@Param("activityId") String activityId);

    /**
     * 通过活动id逻辑删除
     * @param activityId
     * @return
     */
    int logicDelByActivityId(@Param("activityId") String activityId);

    /**
     * 通过活动id逻辑删除
     * @param baseActivity
     * @return
     */
    int logicDelByBaseActivity(BaseActivityReq baseActivity);
}




