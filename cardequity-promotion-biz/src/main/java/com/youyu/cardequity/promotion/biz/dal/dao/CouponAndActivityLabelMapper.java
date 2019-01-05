package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.CouponAndActivityLabelEntity;
import com.youyu.cardequity.promotion.vo.req.BaseQryLabelReq;
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
public interface  CouponAndActivityLabelMapper extends YyMapper<CouponAndActivityLabelEntity>  {
    /**
     * 查询指定标签
     * @param id
     * @return
     */
    CouponAndActivityLabelEntity findLabelById(@Param("id") String id);

    /**
     * 通用查询
     * @param qryInfo
     * @return
     */
    List<CouponAndActivityLabelEntity> findLabelByCommon(@Param("qryInfo") BaseQryLabelReq qryInfo);
}
