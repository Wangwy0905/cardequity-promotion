package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.dto.CouponAndActivityLabelDto;
import com.youyu.cardequity.promotion.vo.req.BaseLabelReq;
import com.youyu.cardequity.promotion.vo.req.BaseQryLabelReq;
import com.youyu.common.api.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Created by caiyi on 2019/1/5.
 */
public interface CouponAndActivityLabelService {

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
    Integer delete( BaseLabelReq req);

    /**
     * 查询标签
     * @param req 标签基本查询请求体
     * @return 标签详情列表
     */
    List<CouponAndActivityLabelDto> findByCommon( BaseQryLabelReq req);

}
