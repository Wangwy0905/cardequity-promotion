package com.youyu.cardequity.promotion.biz.controller;

import com.youyu.cardequity.promotion.api.ClientTakeInActivityApi;
import com.youyu.cardequity.promotion.biz.service.ClientTakeInActivityService;
import com.youyu.cardequity.promotion.vo.req.GetUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youyu.common.api.Result;

import java.util.List;

/**
 * 参加活动功能
 *
 * @author 徐长焕
 * @date 2019-01-03
 */
@RestController
@RequestMapping(path = "/ClientTakeInActivity")
public class ClientTakeInActivityController implements ClientTakeInActivityApi {

    @Autowired
    private ClientTakeInActivityService clientTakeInActivityService;

    /**
     * 通过选购信息进行参加活动处理
     *
     * @param req
     * @return
     */
    @Override
    @PostMapping(path = "/takeInActivityByOrder")
    public Result<List<UseActivityRsp>> takeInActivityByOrder(@RequestBody GetUseEnableCouponReq req) {
        return Result.ok(clientTakeInActivityService.takeInActivityByOrder(req));
    }
}
