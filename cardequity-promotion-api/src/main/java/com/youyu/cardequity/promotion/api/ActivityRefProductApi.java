package com.youyu.cardequity.promotion.api;

import com.youyu.cardequity.promotion.dto.ActivityRefProductDto;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.vo.req.BaseActivityReq;
import com.youyu.cardequity.promotion.vo.req.BaseProductReq;
import com.youyu.cardequity.promotion.vo.req.BatchBaseProductReq;
import com.youyu.cardequity.promotion.vo.req.BatchRefProductReq;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * 配置活动的商品信息
 *
 * @author 技术平台
 * @date 2018-12-07
 * 开发日志
 */
@Api(tags = "配置活动的商品信息")
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/activityRefProduct")
public interface ActivityRefProductApi {

    /**
     * 查询已经配置了活动的商品
     *
     * @return
     */
    @ApiOperation(value = "查询已经配置了活动的商品")
    @PostMapping(path = "/findAllProductInValidActivity")
    Result<List<ActivityRefProductDto>> findAllProductInValidActivity(@RequestBody BaseActivityReq req);

    /**
     * 查询活动配置的商品
     * @param req 活动基本信息
     * @return 商品基本信息
     */
    @ApiOperation(value = "查询活动配置的商品")
    @PostMapping(path = "/findActivityProducts")
    Result<List<BaseProductReq>> findActivityProducts(@RequestBody BaseActivityReq req);


    /**
     * 配置优惠的适用商品范围
     * @param req
     * @return
     */
    @ApiOperation(value = "配置优惠的适用商品范围")
    @PostMapping(path = "/batchAddActivityRefProduct")
    Result<CommonBoolDto<Integer>> batchAddActivityRefProduct(@RequestBody BatchRefProductReq req);

    /**
     * 查询商品的活动数量
     * @param req 商品基本信息
     * @return 活动数量列表
     */
    @ApiOperation(value = "查询商品的活动数量")
    @PostMapping(path = "/notcontrol/cardequity/promotion/findProductAboutActivityNum")
    Result<List<GatherInfoRsp>> findProductAboutActivityNum(@RequestBody BatchBaseProductReq req);
}
