package com.youyu.cardequity.promotion.api;


import com.youyu.cardequity.promotion.dto.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.ProductCouponDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.dto.CouponDetailDto;
import com.youyu.cardequity.promotion.vo.rsp.CouponDefineRsp;
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
 * V1.0-V1 1004244-徐长焕-20181207 新建代码，findEnableGetCoupon：获取客户可领取的券
 */
@Api(tags = "优惠券信息管理：券的定义信息、使用规则、领取规则、额度设置等")
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/productCoupon")
public interface ProductCouponApi {

    /**
     * 获取可领取的优惠券
     *
     * @param req：里面的clientType-客户类型如果为空，需要在网关层调用客户信息查询接口，同理groupId-商品组信息
     * @return 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    @ApiOperation(value = "获取可以领取的优惠券")
    @PostMapping(path = "/findEnableGetCoupon")
    Result<List<CouponDefineRsp>> findEnableGetCoupon(@RequestBody QryProfitCommonReq req);

    /**
     * 添加优惠券
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "添加优惠券：添加基本信息、领取频率、使用门槛、关联商品等")
    @PostMapping(path = "/addCoupon")
    Result<CommonBoolDto<CouponDetailDto>> addCoupon(@RequestBody CouponDetailDto req);

    /**
     * 编辑优惠券
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "编辑优惠券：编辑基本信息、领取频率、使用门槛、关联商品等")
    @PostMapping(path = "/editCoupon")
    Result<CommonBoolDto<CouponDetailDto>> editCoupon(@RequestBody CouponDetailDto req);

    /**
     * 批量删除优惠券
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "批量删除优惠券：商品对应关系、额度值、频率规则、门槛")
    @PostMapping(path = "/editCoupon")
    Result<CommonBoolDto<Integer>> batchDelCoupon(@RequestBody BatchBaseCouponReq req);

    /**
     * 查看商品对应优惠券列表
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "查看商品对应优惠券列表")
    @PostMapping(path = "/findCouponListByProduct")
    Result<List<CouponDetailDto>> findCouponListByProduct(@RequestBody BaseProductReq req);

    /**
     * 查询所有优惠券列表
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "查询所有优惠券列表")
    @PostMapping(path = "/findCouponListByCommon")
    Result<List<CouponDetailDto>> findCouponListByCommon(@RequestBody BaseQryCouponReq req);


}
