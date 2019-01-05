package com.youyu.cardequity.promotion.biz.controller;

import com.youyu.cardequity.promotion.api.CouponAndActivityLabelApi;
import com.youyu.cardequity.promotion.biz.service.CouponAndActivityLabelService;
import com.youyu.cardequity.promotion.dto.CouponAndActivityLabelDto;
import com.youyu.cardequity.promotion.vo.req.BaseLabelReq;
import com.youyu.cardequity.promotion.vo.req.BaseQryLabelReq;
import com.youyu.common.api.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Created by caiyi on 2019/1/5.
 */
public class CouponAndActivityLabelController implements CouponAndActivityLabelApi {
    @Autowired
   private CouponAndActivityLabelService couponAndActivityLabelService;

    /**
     * 添加标签
     * @param req 标签详情
     * @return 处理后的标签详情
     */
    @ApiOperation(value = "添加标签")
    @PostMapping(path = "/addCouponAndActivityLabel")
    public Result<CouponAndActivityLabelDto> add(@RequestBody CouponAndActivityLabelDto req){
        return Result.ok(couponAndActivityLabelService.add(req));
    }

    /**
     * 编辑标签
     * @param req 标签详情
     * @return 处理后的标签详情
     */
    @ApiOperation(value = "编辑标签")
    @PostMapping(path = "/editCouponAndActivityLabel")
    public Result<CouponAndActivityLabelDto> edit(@RequestBody CouponAndActivityLabelDto req){
        return Result.ok(couponAndActivityLabelService.edit(req));
    }

    /**
     * 编辑标签
     * @param req 标签基本数据
     * @return 处理成功数量
     */
    @ApiOperation(value = "删除标签")
    @PostMapping(path = "/deleteCouponAndActivityLabel")
    public Result<Integer> delete(@RequestBody BaseLabelReq req){
        return Result.ok(couponAndActivityLabelService.delete(req));
    }

    /**
     * 查询标签
     * @param req 标签基本查询请求体
     * @return 标签详情列表
     */
    @ApiOperation(value = "查询标签")
    @PostMapping(path = "/findCouponAndActivityLabelByCommon")
    public Result<List<CouponAndActivityLabelDto>> findByCommon(@RequestBody BaseQryLabelReq req){
        return Result.ok(couponAndActivityLabelService.findByCommon(req));
    }

}
