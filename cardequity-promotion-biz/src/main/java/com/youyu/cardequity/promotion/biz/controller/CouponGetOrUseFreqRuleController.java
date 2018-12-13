package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.CouponGetOrUseFreqRuleApi;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.CouponGetoOrUseFreqRuleDto;
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
public class CouponGetOrUseFreqRuleController implements CouponGetOrUseFreqRuleApi {

    @Autowired
    private com.youyu.cardequity.promotion.biz.service.CouponGetOrUseFreqRuleService CouponGetOrUseFreqRuleService;

    @Override
    public Result<CouponGetoOrUseFreqRuleDto> get(@PathVariable(name = "id") String id) {
        CouponGetoOrUseFreqRuleDto dto = CouponGetOrUseFreqRuleService.selectByPrimaryKey(id);
        if (dto == null) {
            return Result.fail(NO_DATA_FOUND);
        }
        return Result.ok(dto);
    }

    @Override
    public Result delete(@PathVariable(name = "id") String id) {
        int count = CouponGetOrUseFreqRuleService.deleteByPrimaryKey(id);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok();
    }

    @Override
    public Result<CouponGetoOrUseFreqRuleDto> save(@RequestBody CouponGetoOrUseFreqRuleDto dto) {
        int count = CouponGetOrUseFreqRuleService.insertSelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<CouponGetoOrUseFreqRuleDto> update(@RequestBody CouponGetoOrUseFreqRuleDto dto) {
        int count = CouponGetOrUseFreqRuleService.updateByPrimaryKeySelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<List<CouponGetoOrUseFreqRuleDto>> findAll() {
        //默认不实现 findAll
        //Result.ok(CouponGetOrUseFreqRuleService.selectAll());
        throw new UnsupportedOperationException();
    }
}
