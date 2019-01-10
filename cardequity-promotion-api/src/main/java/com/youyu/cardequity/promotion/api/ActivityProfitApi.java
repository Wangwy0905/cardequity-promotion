package com.youyu.cardequity.promotion.api;


import com.youyu.cardequity.promotion.dto.other.ActivityDetailDto;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;

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
     * 根据客户指定商品获取可参加的活动
     *
     * @param req：里面的clientType-客户类型如果为空，需要在网关层调用客户信息查询接口，同理groupId-商品组信息
     * @return 活动详情列表
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    @ApiOperation(value = "根据客户指定商品获取可参加的活动")
    @PostMapping(path = "/findEnableGetActivity")
    Result<List<ActivityDetailDto>> findEnableGetActivity(@RequestBody QryProfitCommonReq req);

    /**
     * 【内部服务】订单信息能参与的活动详情：某某活动，及其对应适用商品和数量、优惠金额
     *已过期
     * @param req 预生成的订单信息
     * @return 返回活动使用详情
     * 开发日志
     * 1004258-徐长焕-20181226 新建
     */
    @ApiOperation(value = "订单能参与的活动详情：某某活动，及其对应适用商品和数量、优惠金额")
    @PostMapping(path = "/combActivityRefProductDeal")
    Result<List<UseActivityRsp>> combActivityRefProductDeal(@RequestBody GetUseEnableCouponReq req);

    /**
     * 【内部服务】获取商品活动优惠价：
     *
     * @param req 商品
     * @return 活动详情列表
     * 开发日志
     * 1004258-徐长焕-20181226 新建
     */
    @ApiOperation(value = "获取商品活动优惠价")
    @PostMapping(path = "/findActivityPrice")
    Result<ActivityDetailDto> findActivityPrice(@RequestBody BaseProductReq req);

    /**
     * 批量添加活动
     *
     * @param req 批量活动详情
     * @return 成功后的批量活动详情
     */
    @ApiOperation(value = "批量添加活动")
    @PostMapping(path = "/batchAddActivity")
    Result<CommonBoolDto<BatchActivityDetailDto>> batchAddActivity(@RequestBody BatchActivityDetailDto req);

    /**
     * 批量编辑活动
     *
     * @param req 批量活动详情
     * @return 成功后的批量活动详情
     */
    @ApiOperation(value = "批量添加活动")
    @PostMapping(path = "/batchEditActivity")
    Result<CommonBoolDto<BatchActivityDetailDto>> batchEditActivity(@RequestBody BatchActivityDetailDto req);

    /**
     * 批量删除活动
     *
     * @param req 批量活动
     * @return 成功条数
     */
    @ApiOperation(value = "批量删除活动")
    @PostMapping(path = "/batchDelActivity")
    Result<CommonBoolDto<Integer>> batchDelActivity(@RequestBody BatchBaseActivityReq req);

    /**
     * 查找活动
     *
     * @param req 通用查询请求
     * @return 活动详情列表
     */
    @ApiOperation(value = "查找活动")
    @PostMapping(path = "/findActivityByCommon")
    Result<PageData<ActivityDetailDto>> findActivityByCommon(@RequestBody BaseQryActivityReq req);

    /**
     * 查询活动汇总信息
     *
     * @param req 普通查询活动请求体
     * @return 活动汇总列表
     */
    @ApiOperation(value = "查找活动")
    @PostMapping(path = "/findGatherActivityByCommon")
    Result<List<GatherInfoRsp>> findGatherActivityByCommon(@RequestBody BaseQryActivityReq req);


    /**
     * 查询商品的活动
     * @param req 商品基本信息
     * @return 活动详情列表
     */
    @ApiOperation(value = "查找活动")
    @PostMapping(path = "/findProductAboutActivity")
    Result<List<ActivityDetailDto>> findProductAboutActivity(@RequestBody BaseProductReq req);
}
