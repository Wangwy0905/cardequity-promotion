package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ProfitConflictOrReUseRefApi;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.TbProfitConflictOrReUseRefDto;
import com.youyu.cardequity.promotion.biz.service.ProfitConflictOrReUseRefService;
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
 * @date 2018-12-13
 */
@RestController
public class ProfitConflictOrReUseRefController implements ProfitConflictOrReUseRefApi {

    @Autowired
    private ProfitConflictOrReUseRefService ProfitConflictOrReUseRefService;

    @Override
    public Result<TbProfitConflictOrReUseRefDto> get(@PathVariable(name = "id") String id) {
        TbProfitConflictOrReUseRefDto dto = ProfitConflictOrReUseRefService.selectByPrimaryKey(id);
        if (dto == null) {
            return Result.fail(NO_DATA_FOUND);
        }
        return Result.ok(dto);
    }

    @Override
    public Result delete(@PathVariable(name = "id") String id) {
        int count = ProfitConflictOrReUseRefService.deleteByPrimaryKey(id);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok();
    }

    @Override
    public Result<TbProfitConflictOrReUseRefDto> save(@RequestBody TbProfitConflictOrReUseRefDto dto) {
        int count = ProfitConflictOrReUseRefService.insertSelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<TbProfitConflictOrReUseRefDto> update(@RequestBody TbProfitConflictOrReUseRefDto dto) {
        int count = ProfitConflictOrReUseRefService.updateByPrimaryKeySelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<List<TbProfitConflictOrReUseRefDto>> findAll() {
        //默认不实现 findAll
        //Result.ok(ProfitConflictOrReUseRefService.selectAll());
        throw new UnsupportedOperationException();
    }
}
