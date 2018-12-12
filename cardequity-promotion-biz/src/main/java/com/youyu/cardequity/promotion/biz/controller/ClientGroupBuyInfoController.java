package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ClientGroupBuyInfoApi;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.ClientGroupBuyInfoDto;
import com.youyu.cardequity.promotion.biz.service.ClientGroupBuyInfoService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.youyu.common.enums.BaseResultCode.NO_DATA_FOUND;


/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
@RestController
public class ClientGroupBuyInfoController implements ClientGroupBuyInfoApi {

    @Autowired
    private ClientGroupBuyInfoService tbClientGroupBuyInfoService;

    @Override
    public Result<ClientGroupBuyInfoDto> get(@PathVariable(name = "id") String id) {
        ClientGroupBuyInfoDto dto = tbClientGroupBuyInfoService.selectByPrimaryKey(id);
        if (dto == null) {
            return Result.fail(NO_DATA_FOUND);
        }
        return Result.ok(dto);
    }

    @Override
    public Result delete(@PathVariable(name = "id") String id) {
        int count = tbClientGroupBuyInfoService.deleteByPrimaryKey(id);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok();
    }

    @Override
    public Result<ClientGroupBuyInfoDto> save(@RequestBody ClientGroupBuyInfoDto dto) {
        int count = tbClientGroupBuyInfoService.insertSelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<ClientGroupBuyInfoDto> update(@RequestBody ClientGroupBuyInfoDto dto) {
        int count = tbClientGroupBuyInfoService.updateByPrimaryKeySelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<List<ClientGroupBuyInfoDto>> findAll() {
        //默认不实现 findAll
        //Result.ok(tbClientGroupBuyInfoService.selectAll());
        throw new UnsupportedOperationException();
    }
}
