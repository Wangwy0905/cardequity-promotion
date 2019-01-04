package com.youyu.cardequity.promotion.api;

import com.youyu.cardequity.promotion.dto.ActivityRefProductDto;
import com.youyu.cardequity.promotion.vo.req.BaseActivityReq;
import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 配置活动的商品信息
 *
 * @author 技术平台
 * @date 2018-12-07
 * 开发日志
 */
@Api(tags = "配置活动的商品信息")
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/activityRefProductApi")
public interface ActivityRefProductApi {

    /**
     * 查询已经配置了活动的商品
     *
     * @return
     */
    @ApiOperation(value = "查询已经配置了活动的商品")
    @PostMapping(path = "/findAllProductInValidActivity")
    Result<List<ActivityRefProductDto>> findAllProductInValidActivity(@RequestBody BaseActivityReq req);

}
