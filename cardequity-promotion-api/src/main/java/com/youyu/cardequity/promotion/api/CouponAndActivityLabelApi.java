package com.youyu.cardequity.promotion.api;


import com.youyu.cardequity.promotion.dto.CouponAndActivityLabelDto;
import com.youyu.cardequity.promotion.vo.req.BaseLabelReq;
import com.youyu.cardequity.promotion.vo.req.BaseQryLabelReq;
import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 权益标签管理
 *
 * @author 徐长焕
 * @date 2019-01-05
 * 修改日志：
 * V1.0-V1 1004259-徐长焕-20190105 新增
 */
@Api(tags = "优惠使用操作")
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/couponAndActivityLabelApi")
public interface CouponAndActivityLabelApi {

    /**
     * 添加标签
     * @param req 标签详情
     * @return 处理后的标签详情
     */
    @ApiOperation(value = "添加标签")
    @PostMapping(path = "/addCouponAndActivityLabel")
    Result<CouponAndActivityLabelDto> add(@RequestBody CouponAndActivityLabelDto req);

    /**
     * 编辑标签
     * @param req 标签详情
     * @return 处理后的标签详情
     */
    @ApiOperation(value = "编辑标签")
    @PostMapping(path = "/editCouponAndActivityLabel")
    Result<CouponAndActivityLabelDto> edit(@RequestBody CouponAndActivityLabelDto req);

    /**
     * 编辑标签
     * @param req 标签基本数据
     * @return 处理成功数量
     */
    @ApiOperation(value = "删除标签")
    @PostMapping(path = "/deleteCouponAndActivityLabel")
    Result<Integer> delete(@RequestBody BaseLabelReq req);

    /**
     * 查询标签
     * @param req 标签基本查询请求体
     * @return 标签详情列表
     */
    @ApiOperation(value = "查询标签")
    @PostMapping(path = "/findCouponAndActivityLabelByCommon")
    Result<List<CouponAndActivityLabelDto>> findByCommon(@RequestBody BaseQryLabelReq req);
}
