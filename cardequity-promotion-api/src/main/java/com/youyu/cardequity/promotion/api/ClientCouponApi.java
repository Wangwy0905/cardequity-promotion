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
     */
    @ApiOperation(value = "find Client Coupon")
    @GetMapping(path = "/FindClientCoupon")
     Result<List<ClientCouponDto>> FindClientCoupon(@PathVariable(name = "clientId") String clientId);

    /**
     * 领取优惠券
     *
     * @return 是否领取成功
     * @Param req:有参数clientId-客户号（必填），couponId-领取的券Id（必填）
     */
    @ApiOperation(value = "Client Obtain Coupon")
    @GetMapping(path = "/ObtainCoupon")
    Result<ObtainRspDto> ObtainCoupon(@RequestBody ClientObtainCouponReq req);
}
