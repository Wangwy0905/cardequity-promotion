package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ActivityProfitApi;
import com.youyu.cardequity.promotion.dto.other.ActivityDetailDto;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.BasePriceActivityRsp;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import com.youyu.common.api.PageData;
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
     * 【App】查询可参与的活动
     *
     * @param req：里面的clientType-客户类型如果为空，需要在网关层调用客户信息查询接口，同理groupId-商品组信息
     * @return
     */
    @Override
    @ApiOperation(value = "【App】查询可参与的活动")
    @PostMapping(path = "/findEnableGetActivity")
    public Result<List<ActivityDetailDto>> findEnableGetActivity(@RequestBody QryProfitCommonReq req) {
        List<ActivityDetailDto> result = activityProfitService.findEnableGetActivity(req);
        return Result.ok(result);
    }

    /**
     * 【内部】订单预处理活动详情：处理对应适用商品及其优惠值
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "【内部】订单预处理活动详情")
    @PostMapping(path = "/combActivityRefProductDeal")
    public Result<List<UseActivityRsp>> combActivityRefProductDeal(@RequestBody GetUseEnableCouponReq req) {
        List<UseActivityRsp> result = activityProfitService.combActivityRefProductDeal(req);
        return Result.ok(result);
    }

    /**
     * 【后台】获取商品活动优惠价:
     *
     * @param req
     * @return 一般获取到Result<ActivityDetailDto>后通过ActivityDetailDto对象switchToView转换为ActivityViewDto
     * 1004258-徐长焕-20181226 新建
     */
    @Override
    @ApiOperation(value = "【后台】获取商品活动优惠价")
    @PostMapping(path = "/findActivityPrice")
    public Result<List<ActivityDetailDto>> findActivityPrice(@RequestBody BaseProductReq req) {
        return Result.ok(activityProfitService.findActivityPrice(req));
    }

    /**
     * 【后台】批量添加活动
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "【后台】批量添加活动")
    @PostMapping(path = "/batchAddActivity")
    public Result<CommonBoolDto<BatchActivityDetailDto>> batchAddActivity(@RequestBody BatchActivityDetailDto req) {
        return Result.ok(activityProfitService.batchAddActivity(req));
    }

    /**
     * 【后台】批量编辑活动
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "【后台】批量添加活动")
    @PostMapping(path = "/batchEditActivity")
    public Result<CommonBoolDto<BatchActivityDetailDto>> batchEditActivity(@RequestBody BatchActivityDetailDto req) {
        return Result.ok(activityProfitService.batchEditActivity(req));
    }

    /**
     * 【后台】批量删除活动
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "【后台】批量删除活动")
    @PostMapping(path = "/batchDelActivity")
    public Result<CommonBoolDto<Integer>> batchDelActivity(@RequestBody BatchBaseActivityReq req) {

        return Result.ok(activityProfitService.batchDelActivity(req));
    }

    /**
     * 【后台】查找活动
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "【后台】查找活动")
    @PostMapping(path = "/findActivityByCommon")
    public Result<PageData<ActivityDetailDto>> findActivityByCommon(@RequestBody BaseQryActivityReq req) {

        return Result.ok(activityProfitService.findActivityByCommon(req));
    }


    /**
     * 【后台】查询活动汇总信息
     *
     * @param req 普通查询活动请求体
     * @return 活动汇总列表
     */
    @Override
    @ApiOperation(value = "【后台】查询活动汇总信息")
    @PostMapping(path = "/findGatherActivityByCommon")
    public Result<List<GatherInfoRsp>> findGatherActivityByCommon(@RequestBody BaseQryActivityReq req) {
        return Result.ok(activityProfitService.findGatherActivityByCommon(req));
    }


    /**
     * 【App】【有效期内、已上架、有额度的】查询商品的活动
     *
     * @param req 商品基本信息
     * @return 活动详情列表
     */
    @Override
    @ApiOperation(value = "【App】【有效期内、已上架、有额度的】查找商品相关活动")
    @PostMapping(path = "/findProductAboutActivity")
    public Result<List<ActivityDetailDto>> findProductAboutActivity(@RequestBody BaseProductReq req) {
        return Result.ok(activityProfitService.findProductAboutActivity(req));
    }

    /**
     * 【App+内部+后台】查询指定活动
     *
     * @param req 活动基本信息
     * @return 活动详情列表
     */
    @Override
    @ApiOperation(value = "【通用】查询指定活动")
    @PostMapping(path = "/findActivityById")
    public Result<ActivityDetailDto> findActivityById(@RequestBody BaseActivityReq req) {
        return Result.ok(activityProfitService.findActivityById(req));

    }


    /**
     * 【后台】模糊指定关键字查找活动
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "【后台】查找活动列表：模糊指定id、商品编号、名称匹配其中之一进行查询")
    @PostMapping(path = "/findActivityList")
    public Result<PageData<ActivityDetailDto>> findActivityList(@RequestBody BaseQryActivityReq req) {

        return Result.ok(activityProfitService.findActivityList(req));
    }

    /**
     * 【内部】获取商品活动优惠价：
     *
     * @param req 商品
     * @return 商品对应特价基本信息列表
     * 开发日志
     * 1004258-徐长焕-20190111 新建
     */
    @Override
    @ApiOperation(value = "【内部-已过期】获取商品活动优惠价")
    @PostMapping(path = "/findActivityPriceValue")
    public Result<List<BasePriceActivityRsp>> findActivityPriceValue(@RequestBody BaseProductReq req) {
        return Result.ok(activityProfitService.findActivityPriceValue(req));
    }


    /**
     * 【App+内部】【有效期内、已上架、有额度的】获取商品有效的优惠价活动（排除了已达额度的活动）
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "【App+内部】【有效期内、已上架、有额度的】获取商品有效的优惠价活动")
    @PostMapping(path = "/findValidActivityPrice")
    public Result<List<ActivityDetailDto>> findValidActivityPrice(@RequestBody BaseProductReq req) {
        return Result.ok(activityProfitService.findValidActivityPrice(req));
    }

    /**
     * 【后台】查询所有活动列表
     *
     * @param req
     * @return
     */
    @Override
    @ApiOperation(value = "【后台】查询所有(不分页)活动列表")
    @PostMapping(path = "/findAllActivityByCommon")
    public Result<List<ActivityDetailDto>> findAllActivityByCommon(@RequestBody BaseQryActivityReq req) {
        return Result.ok(activityProfitService.findAllActivityByCommon(req));
    }

}
