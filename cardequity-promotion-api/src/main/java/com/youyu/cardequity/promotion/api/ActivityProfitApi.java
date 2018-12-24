package com.youyu.cardequity.promotion.api;


import com.youyu.cardequity.promotion.vo.req.QryProfitCommonReq;
import com.youyu.cardequity.promotion.vo.rsp.ActivityDefineRsp;
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
 * V1.0-V1 1004244-徐长焕-20181207 新建代码，findEnableGetActivity：获取客户可参与的活动
 */
@Api(tags = "活动信息管理：活动的的定义信息、使用规则、额度设置等")
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/tbActivityProfit")
public interface ActivityProfitApi {

    /**
     * 根据客户指定商品获取可参加的活动
     * @param req：里面的clientType-客户类型如果为空，需要在网关层调用客户信息查询接口，同理groupId-商品组信息
     * @return
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    @ApiOperation(value = "根据客户指定商品获取可参加的活动")
    @GetMapping(path = "/findEnableGetActivity")
    Result<List<ActivityDefineRsp>> findEnableGetActivity(@RequestBody QryProfitCommonReq req);
}
