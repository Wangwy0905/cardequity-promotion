package com.youyu.cardequity.promotion.api;


import com.youyu.cardequity.promotion.dto.ObtainRspDto;
import com.youyu.cardequity.promotion.vo.req.ClientObtainCouponReq;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.ClientCouponDto;
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
 * V1.2-V1 1004246-徐长焕-20181213 修改代码，findEnableUseCoupon：获取可用的优惠券
 * V1.1-V1 1004247-徐长焕-20181213 修改代码，实现ObtainCoupon：领取优惠券
 * V1.0-V1 1004258-徐长焕-20181207 新建代码，实现FindClientCoupon：获取客户已领取的券
 */
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/ClientCoupon")
public interface ClientCouponApi {

    /**
     * 获取客户已领取的券,含：
     * 已使用(status=1和2)；
     * 未使用（status=0且有效期内）；
     * 已过期（status=0且未在有效期内）
     *
     * @return 返回已领取的券
     * @Param clientId:指定客户号，必填
     * 开发日志：
     * 1004258-徐长焕-20181207 实现
     */
    @ApiOperation(value = "find Client Coupon")
    @GetMapping(path = "/findClientCoupon")
     Result<List<ClientCouponDto>> findClientCoupon(@PathVariable(name = "clientId") String clientId);

    /**
     * 领取优惠券
     *
     * @return 是否领取成功
     * @Param req:有参数clientId-客户号（必填），couponId-领取的券Id（必填）
     * 开发日志：
     * 1004247-徐长焕-20181213 实现
     */
    @ApiOperation(value = "Client Obtain Coupon")
    @GetMapping(path = "/obtainCoupon")
    Result<ObtainRspDto> obtainCoupon(@RequestBody ClientObtainCouponReq req);


    /**
     * 获取相关属性可用的优惠券
     *
     * @return 可以使用的优惠券
     * @Param req:有参数clientId-客户号（必填），couponId-领取的券Id（必填）
     * 开发日志：
     * 1004246-徐长焕-20181213 实现
     */
    @ApiOperation(value = "Client Enable Use Coupon")
    @GetMapping(path = "/findEnableUseCoupon")
    Result<List<ClientCouponDto>> findEnableUseCoupon(@RequestBody ClientObtainCouponReq req);
}
