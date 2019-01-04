package com.youyu.cardequity.promotion.biz.controller;

import com.youyu.cardequity.promotion.api.ActivityRefProductApi;
import com.youyu.cardequity.promotion.biz.service.ActivityRefProductService;
import com.youyu.cardequity.promotion.dto.ActivityRefProductDto;
import com.youyu.cardequity.promotion.vo.req.BaseActivityReq;
import com.youyu.common.api.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Created by caiyi on 2019/1/4.
 */
public class ActivityRefProductController implements ActivityRefProductApi {
    @Autowired
    public ActivityRefProductService activityRefProductService;

    /**
     * 查询已经配置了活动的商品
     *
     * @return
     */
    @Override
    @ApiOperation(value = "查询已经配置了活动的商品")
    @PostMapping(path = "/findAllProductInValidActivity")
    public Result<List<ActivityRefProductDto>> findAllProductInValidActivity(@RequestBody BaseActivityReq req) {
        return Result.ok(activityRefProductService.findAllProductInValidActivity(req));
    }
}
