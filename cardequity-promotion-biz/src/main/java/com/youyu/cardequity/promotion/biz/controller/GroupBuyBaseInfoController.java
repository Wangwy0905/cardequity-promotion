package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.GroupBuyBaseInfoApi;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.GroupBuyBaseInfoDto;
import com.youyu.cardequity.promotion.biz.service.GroupBuyBaseInfoService;
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
public class GroupBuyBaseInfoController implements GroupBuyBaseInfoApi {

    @Autowired
    private GroupBuyBaseInfoService tbGroupBuyBaseInfoService;

    @Override
    public Result<GroupBuyBaseInfoDto> get(@PathVariable(name = "id") String id) {
        GroupBuyBaseInfoDto dto = tbGroupBuyBaseInfoService.selectByPrimaryKey(id);
        if (dto == null) {
            return Result.fail(NO_DATA_FOUND);
        }
        return Result.ok(dto);
    }

    @Override
    public Result delete(@PathVariable(name = "id") String id) {
        int count = tbGroupBuyBaseInfoService.deleteByPrimaryKey(id);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok();
    }

    @Override
    public Result<GroupBuyBaseInfoDto> save(@RequestBody GroupBuyBaseInfoDto dto) {
        int count = tbGroupBuyBaseInfoService.insertSelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<GroupBuyBaseInfoDto> update(@RequestBody GroupBuyBaseInfoDto dto) {
        int count = tbGroupBuyBaseInfoService.updateByPrimaryKeySelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<List<GroupBuyBaseInfoDto>> findAll() {
        //默认不实现 findAll
        //Result.ok(tbGroupBuyBaseInfoService.selectAll());
        throw new UnsupportedOperationException();
    }
}
