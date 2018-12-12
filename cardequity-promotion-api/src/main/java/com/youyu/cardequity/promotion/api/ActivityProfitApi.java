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
 */
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/tbActivityProfit")
public interface ActivityProfitApi {

    /**
     * select one
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "select one")
    @GetMapping("/{id}")
    Result<ActivityProfitDto> get(@PathVariable(name = "id") String id);

    /**
     * delete one
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "delete one")
    @DeleteMapping("/{id}")
    Result delete(@PathVariable(name = "id") String id);

    /**
     * save one
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "save one")
    @PostMapping("/")
    Result<ActivityProfitDto> save(@RequestBody ActivityProfitDto dto);


    /**
     * update one
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "update one")
    @PutMapping("/")
    Result<ActivityProfitDto> update(@RequestBody ActivityProfitDto dto);


    /**
     * 查询所有
     *
     * @return
     */
    @ApiOperation(value = "find all")
    @GetMapping(path = "/findAll")
    Result<List<ActivityProfitDto>> findAll();

    /**
     * 获取可领取的优惠券
     *@param req：里面的clientType-客户类型如果为空，需要在网关层调用客户信息查询接口，同理groupId-商品组信息
     * @return
     */
    @ApiOperation(value = "find EnableGet Activity")
    @GetMapping(path = "/findEnableGetActivity")
    Result<List<ActivityDefineRsp>> findEnableGetActivity(@RequestBody QryProfitCommonReq req);
}
