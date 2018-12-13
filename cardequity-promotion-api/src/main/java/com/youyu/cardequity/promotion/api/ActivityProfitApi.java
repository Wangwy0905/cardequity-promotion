package com.youyu.cardequity.promotion.api;


import com.youyu.cardequity.promotion.vo.req.QryProfitCommonReq;
import com.youyu.cardequity.promotion.vo.rsp.ActivityDefineRsp;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
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
 * V1.0-V1 1004244-徐长焕-20181207 新建代码，findEnableGetActivity：获取客户可参与的活动
 */
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/tbActivityProfit")
public interface ActivityProfitApi {

    /**
     * 获取可领取的优惠券
     *@param req：里面的clientType-客户类型如果为空，需要在网关层调用客户信息查询接口，同理groupId-商品组信息
     * @return
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    @ApiOperation(value = "find EnableGet Activity")
    @GetMapping(path = "/findEnableGetActivity")
    Result<List<ActivityDefineRsp>> findEnableGetActivity(@RequestBody QryProfitCommonReq req);
}
