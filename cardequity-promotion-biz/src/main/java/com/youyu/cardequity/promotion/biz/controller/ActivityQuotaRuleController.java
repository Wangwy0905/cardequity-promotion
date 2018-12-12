package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ActivityQuotaRuleApi;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.ActivityQuotaRuleDto;
import com.youyu.cardequity.promotion.biz.service.ActivityQuotaRuleService;
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
public class ActivityQuotaRuleController implements ActivityQuotaRuleApi {

    @Autowired
    private ActivityQuotaRuleService tbActivityQuotaRuleService;

    @Override
    public Result<ActivityQuotaRuleDto> get(@PathVariable(name = "id") String id) {
        ActivityQuotaRuleDto dto = tbActivityQuotaRuleService.selectByPrimaryKey(id);
        if (dto == null) {
            return Result.fail(NO_DATA_FOUND);
        }
        return Result.ok(dto);
    }

    @Override
    public Result delete(@PathVariable(name = "id") String id) {
        int count = tbActivityQuotaRuleService.deleteByPrimaryKey(id);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok();
    }

    @Override
    public Result<ActivityQuotaRuleDto> save(@RequestBody ActivityQuotaRuleDto dto) {
        int count = tbActivityQuotaRuleService.insertSelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<ActivityQuotaRuleDto> update(@RequestBody ActivityQuotaRuleDto dto) {
        int count = tbActivityQuotaRuleService.updateByPrimaryKeySelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<List<ActivityQuotaRuleDto>> findAll() {
        //默认不实现 findAll
        //Result.ok(tbActivityQuotaRuleService.selectAll());
        throw new UnsupportedOperationException();
    }
}
