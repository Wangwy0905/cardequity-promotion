package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ActivityProfitApi;
import com.youyu.cardequity.promotion.vo.req.GetUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.req.QryProfitCommonReq;
import com.youyu.cardequity.promotion.vo.rsp.ActivityDefineRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.biz.service.ActivityProfitService;
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


    @Override
    @PostMapping(path = "/findEnableGetActivity")
    public Result<List<ActivityDefineRsp>> findEnableGetActivity(@RequestBody QryProfitCommonReq req) {
        List<ActivityDefineRsp> result = activityProfitService.findEnableGetActivity(req);
        return Result.ok(result);
    }

    @Override
    @PostMapping(path = "/combActivityRefProductDeal")
    public Result<List<UseActivityRsp>> combActivityRefProductDeal(GetUseEnableCouponReq req) {

        List<UseActivityRsp> result = activityProfitService.combActivityRefProductDeal(req);

        return Result.ok(result);
    }

}
