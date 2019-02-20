package com.youyu.cardequity.promotion.api;


import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.other.ObtainCouponViewDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.FindClientCouponNumReq;
import com.youyu.cardequity.promotion.vo.rsp.FindCouponListByOrderDetailRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseCouponRsp;
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
 * 修改日志：
 * V1.3-V1 1004259-徐长焕-20181217 修改，新增函数combCouponRefProductDeal：按策略得到优惠券使用组合
 * V1.2-V1 1004246-徐长焕-20181213 修改，新增函数findEnableUseCoupon：获取可用的优惠券
 * V1.1-V1 1004247-徐长焕-20181213 修改，新增函数ObtainCoupon：领取优惠券
 * V1.0-V1 1004258-徐长焕-20181207 新建，新增函数FindClientCoupon：获取客户已领取的券
 */
@Api(tags = "客户优惠券管理：客户对优惠券的操作")
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/clientCoupon")
public interface ClientCouponApi {

    /**
     * *********************************【App接口】************************
     * 获取客户已领取的券领取信息,含：
     * 已使用(status=1和2)；
     * 未使用（status=0且有效期内）；
     * 已过期（status=0且未在有效期内）
     *
     * @return 返回已领取的券
     * @Param clientId:指定客户号，必填
     * 开发日志：
     * 1004258-徐长焕-20181207 实现
     */
    @ApiOperation(value = "【App】获取客户已领取的券")
    @PostMapping(path = "/findClientCoupon")
    Result<List<ObtainCouponViewDto>> findClientCoupon(@RequestBody QryComonClientCouponReq req);

    /**
     * 领取优惠券
     *
     * @return 是否领取成功
     * @Param req:有参数clientId-客户号（必填），couponId-领取的券Id（必填）
     * 开发日志：
     * 1004247-徐长焕-20181213 实现
     */
    @ApiOperation(value = "【App】客户领取优惠券：唯一确定领取优惠券是'券id+阶梯id'，其中阶梯id可为空")
    @PostMapping(path = "/obtainCoupon")
    Result<CommonBoolDto<ObtainCouponViewDto>> obtainCoupon(@RequestBody ClientObtainCouponReq req);


    /**
     * 根据客户指定商品获取可用的优惠券
     *
     * @return 可以使用的优惠券
     * @Param req:有参数clientId-客户号（必填），couponId-领取的券Id（必填）
     * 开发日志：
     * 1004246-徐长焕-20181213 实现
     */
    @ApiOperation(value = "【App-有效期内-满足使用频率】根据客户指定商品获取可用的优惠券:1.指定商品填充与参数List中；2.获取的券中没有计算冲突关系；3.列表中含运费券")
    @PostMapping(path = "/findEnableUseCoupon")
    Result<List<ObtainCouponViewDto>> findEnableUseCoupon(@RequestBody GetUseEnableCouponReq req);


    /**
     * 获取客户当前有效的券
     *
     * @param req 客户及商品信息
     * @return 返回已领取的券
     * 开发日志
     */
    @ApiOperation(value = "【App-有效期-满足使用频率】获取客户当前有效的券")
    @PostMapping(path = "/findValidClientCouponForProduct")
    Result<List<ObtainCouponViewDto>> findValidClientCouponForProduct(@RequestBody BaseClientProductReq req);


    /**
     * 查看订单相关优惠券（可用，不可用）
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "【App】根据订单计算优惠券状态（可用，不可用）情况")
    @PostMapping(path = "/findCouponListByOrderDetail")
    Result<FindCouponListByOrderDetailRsp> findCouponListByOrderDetail(@RequestBody OrderUseEnableCouponReq req);

    /**
     * *********************************【内部接口】************************
     * 【内部】按策略得到优惠券使用组合:不含运费券
     * 1.根据订单或待下单商品列表校验了使用门槛
     * 2.根据冲突关系按策略计算能使用的券
     * 3.计算出每张券的适配使用的商品列表
     *
     * @param req 本次订单详情
     * @return 推荐使用券组合及应用对应商品详情
     * 开发日志：
     * 1004246-徐长焕-20181213 实现
     */
    @ApiOperation(value = "【内部】按策略得到优惠券使用组合:1.根据订单或待下单商品列表校验了使用门槛;2.根据冲突关系按策略计算能使用的券;3.计算出每张券的适配使用的商品列表")
    @PostMapping(path = "/combCouponRefProductDeal")
    Result<List<UseCouponRsp>> combCouponRefProductDeal(@RequestBody GetUseEnableCouponReq req);

    /**
     * 【内部-已过时】根据指定的优惠券进行校验其适用情况，并变动其状态和增加使用记录
     *
     * @param req 本次订单详情
     * @return 推荐使用券组合及应用对应商品详情
     */
    @ApiOperation(value = "【内部-已过时】根据指定的优惠券进行校验其适用情况，并变动其状态和增加使用记录")
    @PostMapping(path = "/combCouponRefProductAndUse")
    Result<List<UseCouponRsp>> combCouponRefProductAndUse(@RequestBody GetUseEnableCouponReq req);

    /**
     * 【App】查询客户领取券统计数量
     * @param req
     * @return
     */
    @ApiOperation(value = "【App】查询客户领取券统计数量")
    @PostMapping(path = "/findClientCouponNum")
    Result<FindClientCouponNumReq> findClientCouponNum(@RequestBody QryComonClientCouponReq req);

}