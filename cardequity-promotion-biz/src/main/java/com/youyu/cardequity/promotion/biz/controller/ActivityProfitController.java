package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ActivityProfitApi;
import com.youyu.cardequity.promotion.dto.ActivityDetailDto;
import com.youyu.cardequity.promotion.dto.CommonBoolDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.biz.service.ActivityProfitService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
@RestController
@RequestMapping(path = "/activityProfit")
public class ActivityProfitController implements ActivityProfitApi {

    @Autowired
    private ActivityProfitService activityProfitService;


    /**
     * 查询可参与的活动
     * @param req：里面的clientType-客户类型如果为空，需要在网关层调用客户信息查询接口，同理groupId-商品组信息
     * @return
     */
    @Override
    @ApiOperation(value = "查询可参与的活动")
    @PostMapping(path = "/findEnableGetActivity")
    public Result<List<ActivityDetailDto>> findEnableGetActivity(@RequestBody QryProfitCommonReq req) {
        List<ActivityDetailDto> result = activityProfitService.findEnableGetActivity(req);
        return Result.ok(result);
    }

    /**
     * 订单预处理活动详情：处理对应适用商品及其优惠值
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "订单预处理活动详情")
    @PostMapping(path = "/combActivityRefProductDeal")
    public Result<List<UseActivityRsp>> combActivityRefProductDeal(@RequestBody GetUseEnableCouponReq req) {
        List<UseActivityRsp> result = activityProfitService.combActivityRefProductDeal(req);
        return Result.ok(result);
    }

    /**
     * 获取商品活动优惠价:
     * @param req
     * @return  一般获取到Result<ActivityDetailDto>后通过ActivityDetailDto对象switchToView转换为ActivityViewDto
     * 1004258-徐长焕-20181226 新建
     */
    @Override
    @ApiOperation(value = "获取商品活动优惠价")
    @PostMapping(path = "/findActivityPrice")
    public Result<ActivityDetailDto> findActivityPrice(@RequestBody BaseProductReq req){
        return Result.ok(activityProfitService.findActivityPrice(req));
    }

    /**
     * 批量添加活动
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "批量添加活动")
    @PostMapping(path = "/batchAddActivity")
    public Result<CommonBoolDto<BatchActivityDetailDto>> batchAddActivity(@RequestBody BatchActivityDetailDto req){
        return Result.ok(activityProfitService.batchAddActivity(req));
    }

    /**
     * 批量编辑活动
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "批量添加活动")
    @PostMapping(path = "/batchEditActivity")
    public Result<CommonBoolDto<BatchActivityDetailDto>> batchEditActivity(@RequestBody BatchActivityDetailDto req){
        return Result.ok(activityProfitService.batchEditActivity(req));
    }

    /**
     * 批量删除活动
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "批量删除活动")
    @PostMapping(path = "/batchDelActivity")
    public Result<CommonBoolDto<Integer>> batchDelActivity(@RequestBody BatchBaseActivityReq req){

        return Result.ok(activityProfitService.batchDelActivity(req));
    }

    /**
     * 查找活动
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "查找活动")
    @PostMapping(path = "/findActivityByCommon")
    public Result<List<ActivityDetailDto>> findActivityByCommon(@RequestBody BaseQryActivityReq req){

        return Result.ok(activityProfitService.findActivityByCommon(req));
    }

}
