package com.youyu.cardequity.promotion.biz.controller;

import com.youyu.cardequity.promotion.api.CouponIssueApi;
import com.youyu.cardequity.promotion.biz.service.CouponIssueService;
import com.youyu.cardequity.promotion.dto.req.*;
import com.youyu.cardequity.promotion.dto.rsp.CouponIssueDetailRsp;
import com.youyu.cardequity.promotion.dto.rsp.CouponIssueQueryRsp;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.youyu.common.api.Result.ok;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 优惠券发放Controller
 */
@RestController
@RequestMapping("/couponIssue")
public class CouponIssueController implements CouponIssueApi {

    @Autowired
    private CouponIssueService couponIssueService;


    @Override
    @PostMapping(path = "/createIssue")
    public Result createIssue(@RequestBody CouponIssueReq couponIssueReq) {
        couponIssueService.createIssue(couponIssueReq);
        return ok();
    }

    @Override
    @PostMapping(path = "/getCouponIssueQuery")
    public Result<PageData<CouponIssueQueryRsp>> getCouponIssueQuery(@RequestBody CouponIssueQueryReq couponIssueQueryReq) {
        return ok(couponIssueService.getCouponIssueQuery(couponIssueQueryReq));
    }

    @Override
    @PostMapping(path = "/getCouponIssueDetail")
    public Result<CouponIssueDetailRsp> getCouponIssueDetail(@RequestBody CouponIssueDetailReq couponIssueDetailReq) {
        return ok(couponIssueService.getCouponIssueDetail(couponIssueDetailReq));
    }

    @Override
    public Result delete(@RequestBody CouponIssueDeleteReq couponIssueDeleteReq) {
        couponIssueService.delete(couponIssueDeleteReq);
        return ok();
    }

    @Override
    public Result setVisible(@RequestBody CouponIssueVisibleReq couponIssueVisibleReq) {
        couponIssueService.setVisible(couponIssueVisibleReq);
        return ok();
    }
}
