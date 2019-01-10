package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.biz.dal.entity.CouponAndActivityLabelEntity;
import com.youyu.cardequity.promotion.dto.CouponAndActivityLabelDto;
import com.youyu.cardequity.promotion.vo.req.BasePageQryLabelReq;
import com.youyu.cardequity.promotion.vo.req.BaseQryLabelReq;
import com.youyu.cardequity.promotion.vo.req.BatchBaseLabelReq;
import com.youyu.common.api.PageData;
import com.youyu.common.service.IService;

import java.util.List;

/**
 * Created by caiyi on 2019/1/5.
 */
public interface CouponAndActivityLabelService  extends IService<CouponAndActivityLabelDto, CouponAndActivityLabelEntity> {

    /**
     * 添加标签
     * @param req 标签详情
     * @return 处理后的标签详情
     */
    CouponAndActivityLabelDto add( CouponAndActivityLabelDto req);

    /**
     * 编辑标签
     * @param req 标签详情
     * @return 处理后的标签详情
     */
    CouponAndActivityLabelDto edit( CouponAndActivityLabelDto req);

    /**
     * 编辑标签
     * @param req 标签基本数据
     * @return 处理成功数量
     */
    Integer delete( BatchBaseLabelReq req);

    /**
     * 查询标签
     * @param req 标签基本查询请求体
     * @return 标签详情列表
     */
    List<CouponAndActivityLabelDto> findByCommon( BaseQryLabelReq req);

    /**
     * [分页]查询标签
     * @param req 标签基本查询请求体
     * @return 标签详情列表
     */
    PageData<CouponAndActivityLabelDto> findPageByCommon(BasePageQryLabelReq req);

}
