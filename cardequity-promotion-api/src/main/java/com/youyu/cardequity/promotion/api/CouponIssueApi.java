package com.youyu.cardequity.promotion.api;

import com.youyu.cardequity.promotion.dto.req.*;
import com.youyu.cardequity.promotion.dto.rsp.CouponIssueDetailRsp;
import com.youyu.cardequity.promotion.dto.rsp.CouponIssueEditRsp;
import com.youyu.cardequity.promotion.dto.rsp.CouponIssueQueryRsp;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 优惠券发放Api
 */
@Api(tags = "优惠券发放Api")
@FeignClient(name = "cardequity-promotion")
@RequestMapping("/couponIssue")
public interface CouponIssueApi {

    /**
     * 创建发放优惠券
     *
     * @param couponIssueReq
     * @return
     */
    @ApiOperation(value = "创建发放优惠券")
    @PostMapping(path = "/createIssue")
    Result createIssue(@RequestBody CouponIssueReq couponIssueReq);

    /**
     * 根据查询条件查询优惠券发放列表
     *
     * @param couponIssueQueryReq
     * @return
     */
    @ApiOperation(value = "根据查询条件查询优惠券发放列表")
    @PostMapping(path = "/getCouponIssueQuery")
    Result<PageData<CouponIssueQueryRsp>> getCouponIssueQuery(@RequestBody CouponIssueQueryReq couponIssueQueryReq);

    /**
     * 根据查询条件查询发放明细
     *
     * @param couponIssueDetailReq
     * @return
     */
    @ApiOperation(value = "根据查询条件查询发放明细")
    @PostMapping(path = "/getCouponIssueDetail")
    Result<CouponIssueDetailRsp> getCouponIssueDetail(@RequestBody CouponIssueDetailReq couponIssueDetailReq);

    /**
     * 根据条件删除
     *
     * @param couponIssueDeleteReq
     * @return
     */
    @ApiOperation(value = "根据条件删除")
    @PostMapping(path = "/delete")
    Result delete(@RequestBody CouponIssueDeleteReq couponIssueDeleteReq);

    /**
     * 设置优惠券上下架
     *
     * @param couponIssueVisibleReq
     * @return
     */
    @ApiOperation(value = "设置优惠券上下架")
    @PostMapping(path = "/setVisible")
    Result setVisible(@RequestBody CouponIssueVisibleReq couponIssueVisibleReq);

    /**
     * 优惠券发放编辑
     *
     * @param couponIssueEditReq
     * @return
     */
    @ApiOperation(value = "优惠券发放编辑")
    @PostMapping(path = "/edit")
    Result<CouponIssueEditRsp> edit(@RequestBody CouponIssueEditReq couponIssueEditReq);
}
