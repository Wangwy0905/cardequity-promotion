package com.youyu.cardequity.promotion.api;


import com.youyu.cardequity.promotion.vo.req.GetUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.youyu.common.api.Result;
import java.util.List;

/**
 * 参加活动相关动作
 *
 * @author 徐长焕
 * @date 2019-01-03
 * 修改日志：
 */
@Api(tags = "参加活动相关动作")
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/ClientTakeInActivityApi")
public interface ClientTakeInActivityApi {

    /**
     * 通过选购信息进行参加活动处理
     * @param req
     * @return
     */
    @ApiOperation(value = "通过选购信息进行参加活动处理")
    @PostMapping(path = "/takeInActivityByOrder")
    Result<List<UseActivityRsp>> takeInActivityByOrder(@RequestBody GetUseEnableCouponReq req);
}
