package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ClientTakeInActivityApi;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.ClientTakeInActivityDto;
import com.youyu.cardequity.promotion.biz.service.ClientTakeInActivityService;
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
public class ClientTakeInActivityController implements ClientTakeInActivityApi {

    @Autowired
    private ClientTakeInActivityService tbClientTakeInActivityService;

    @Override
    public Result<ClientTakeInActivityDto> get(@PathVariable(name = "id") String id) {
        ClientTakeInActivityDto dto = tbClientTakeInActivityService.selectByPrimaryKey(id);
        if (dto == null) {
            return Result.fail(NO_DATA_FOUND);
        }
        return Result.ok(dto);
    }

    @Override
    public Result delete(@PathVariable(name = "id") String id) {
        int count = tbClientTakeInActivityService.deleteByPrimaryKey(id);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok();
    }

    @Override
    public Result<ClientTakeInActivityDto> save(@RequestBody ClientTakeInActivityDto dto) {
        int count = tbClientTakeInActivityService.insertSelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<ClientTakeInActivityDto> update(@RequestBody ClientTakeInActivityDto dto) {
        int count = tbClientTakeInActivityService.updateByPrimaryKeySelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<List<ClientTakeInActivityDto>> findAll() {
        //默认不实现 findAll
        //Result.ok(tbClientTakeInActivityService.selectAll());
        throw new UnsupportedOperationException();
    }
}
