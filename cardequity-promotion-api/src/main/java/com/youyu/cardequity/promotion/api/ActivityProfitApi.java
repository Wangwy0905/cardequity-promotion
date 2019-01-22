package com.youyu.cardequity.promotion.api;


import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.dto.other.ActivityDetailDto;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.BasePriceActivityRsp;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;

import java.math.BigDecimal;
import java.util.List;

/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 * 开发日志
 * V1.0-V1 1004244-徐长焕-20181207 新建代码，findEnableGetActivity：获取客户可参与的活动
 */
@Api(tags = "活动信息管理：活动的的定义信息、使用规则、额度设置等")
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/activityProfit")
public interface ActivityProfitApi {

    /**
     * *********************************【APP接口】************************
     * 【App】根据客户指定商品获取可参加的活动
     *
     * @param req：里面的clientType-客户类型如果为空，需要在网关层调用客户信息查询接口，同理groupId-商品组信息
     * @return 活动详情列表
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    @ApiOperation(value = "【App】根据客户指定商品获取可参加的活动")
    @PostMapping(path = "/findEnableGetActivity")
    Result<List<ActivityDetailDto>> findEnableGetActivity(@RequestBody QryProfitCommonReq req);


    /**
     * *********************************【APP+内部接口】************************
     * 【App】【有效期内、已上架、有额度的】查询商品的活动
     * @param req 商品基本信息
     * @return 活动详情列表
     */
    @ApiOperation(value = "【App】【有效期内、已上架、有额度的】查询商品的活动")
    @PostMapping(path = "/findProductAboutActivity")
    Result<List<ActivityDetailDto>> findProductAboutActivity(@RequestBody BaseProductReq req);


    /**
     * 【App+内部】【有效期内、已上架、有额度的】获取商品有效的优惠价活动（排除了已达额度的活动）
     * @param req
     * @return
     *
     */
    @ApiOperation(value = "【App+内部】【有效期内、已上架、有额度的】获取商品有效的优惠价活动（排除了已达额度的活动）")
    @PostMapping(path = "/findValidActivityPrice")
    Result<List<ActivityDetailDto>> findValidActivityPrice(@RequestBody BaseProductReq req);

    /**
     * 查询抢购特价活动
     * @param req
     * @return
     */
    @ApiOperation(value = "【APP】查询抢购特价活动")
    @PostMapping(path = "/findFlashSalePriceActivity")
    Result<List<ActivityDetailDto>> findFlashSalePriceActivity(@RequestBody OperatQryReq req);

    /**
     * *********************************【通用接口】************************
     * 【App+内部+后台】查询指定活动
     * @param req 活动基本信息
     * @return 活动详情列表
     */
    @ApiOperation(value = "【通用】查询指定活动")
    @PostMapping(path = "/findActivityById")
    Result<ActivityDetailDto> findActivityById(@RequestBody BaseActivityReq req);


    /**
     * *********************************【内部接口】************************
     * 【内部-已过期】订单信息能参与的活动详情：某某活动，及其对应适用商品和数量、优惠金额
     *
     * @param req 预生成的订单信息
     * @return 返回活动使用详情
     * 开发日志
     * 1004258-徐长焕-20181226 新建
     */
    @ApiOperation(value = "【内部】订单能参与的活动详情：某某活动，及其对应适用商品和数量、优惠金额")
    @PostMapping(path = "/combActivityRefProductDeal")
    Result<List<UseActivityRsp>> combActivityRefProductDeal(@RequestBody GetUseEnableCouponReq req);

    /**
     * 【内部-已过期】获取商品活动优惠价：
     *
     * @param req 商品
     * @return 活动详情列表
     * 开发日志
     * 1004258-徐长焕-20181226 新建
     */
    @ApiOperation(value = "【内部-已过期】获取商品活动优惠价")
    @PostMapping(path = "/findActivityPriceValue")
    Result<List<BasePriceActivityRsp>> findActivityPriceValue(@RequestBody BaseProductReq req);


    /**
     * *********************************【后台接口：一般不过来下架活动】************************
     * 【后台】获取商品活动优惠价：
     *
     * @param req 商品
     * @return 活动详情列表
     * 开发日志
     * 1004258-徐长焕-20181226 新建
     */
    @ApiOperation(value = "【后台】获取商品活动优惠价")
    @PostMapping(path = "/findActivityPrice")
    Result<List<ActivityDetailDto>> findActivityPrice(@RequestBody BaseProductReq req);

    /**
     * 【后台-批量】添加活动
     *
     * @param req 批量活动详情
     * @return 成功后的批量活动详情
     */
    @ApiOperation(value = "【后台】批量添加活动")
    @PostMapping(path = "/batchAddActivity")
    Result<CommonBoolDto<BatchActivityDetailDto>> batchAddActivity(@RequestBody BatchActivityDetailDto req);

    /**
     * 【后台-批量】编辑活动
     *
     * @param req 批量活动详情
     * @return 成功后的批量活动详情
     */
    @ApiOperation(value = "【后台】批量添加活动")
    @PostMapping(path = "/batchEditActivity")
    Result<CommonBoolDto<BatchActivityDetailDto>> batchEditActivity(@RequestBody BatchActivityDetailDto req);

    /**
     * 【后台-批量】删除活动
     *
     * @param req 批量活动
     * @return 成功条数
     */
    @ApiOperation(value = "【后台】批量删除活动")
    @PostMapping(path = "/batchDelActivity")
    Result<CommonBoolDto<Integer>> batchDelActivity(@RequestBody BatchBaseActivityReq req);

    /**
     * 【后台】多条件查找活动
     *
     * @param req 通用查询请求
     * @return 活动详情列表
     */
    @ApiOperation(value = "【后台】通用查询活动")
    @PostMapping(path = "/findActivityByCommon")
    Result<PageData<ActivityDetailDto>> findActivityByCommon(@RequestBody BaseQryActivityReq req);

    /**
     * 【后台】查询活动汇总信息
     *
     * @param req 普通查询活动请求体
     * @return 活动汇总列表
     */
    @ApiOperation(value = "【后台】查询活动汇总信息")
    @PostMapping(path = "/findGatherActivityByCommon")
    Result<List<GatherInfoRsp>> findGatherActivityByCommon(@RequestBody BaseQryActivityReq req);

    /**
     * 【后台-分页】模糊指定关键字查找活动
     * @param req
     * @return
     */
    @ApiOperation(value = "【后台】模糊指定关键字查找活动：模糊指定id、商品编号、名称匹配其中之一进行查询")
    @PostMapping(path = "/findActivityList")
    Result<PageData<ActivityDetailDto>> findActivityList(@RequestBody BaseQryActivityReq req);

    /**
     * 【后台】通用查询活动列表
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "【后台】通用查询（不分页）活动列表")
    @PostMapping(path = "/findAllActivityByCommon")
    Result<List<ActivityDetailDto>> findAllActivityByCommon(@RequestBody BaseQryActivityReq req);

    /**
     * 上架活动
     * @param req
     * @return
     */
    @ApiOperation(value = "【后台】上架活动")
    @PostMapping(path = "/upActivity")
     Result<CommonBoolDto<Integer>> upActivity(@RequestBody BatchBaseActivityReq req);

    /**
     * 下架活动
     * @param req
     * @return
     */
    @ApiOperation(value = "【后台】下架活动")
    @PostMapping(path = "/downActivity")
    Result<CommonBoolDto<Integer>> downActivity(@RequestBody BatchBaseActivityReq req);


}
