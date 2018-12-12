package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.CouponGetorUseFreqRuleApi;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.CouponGetorUseFreqRuleDto;
import com.youyu.cardequity.promotion.biz.service.CouponGetorUseFreqRuleService;
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
public class CouponGetorUseFreqRuleController implements CouponGetorUseFreqRuleApi {

    @Autowired
    private CouponGetorUseFreqRuleService tbCouponGetorUseFreqRuleService;

    @Override
    public Result<CouponGetorUseFreqRuleDto> get(@PathVariable(name = "id") String id) {
        CouponGetorUseFreqRuleDto dto = tbCouponGetorUseFreqRuleService.selectByPrimaryKey(id);
        if (dto == null) {
            return Result.fail(NO_DATA_FOUND);
        }
        return Result.ok(dto);
    }

    @Override
    public Result delete(@PathVariable(name = "id") String id) {
        int count = tbCouponGetorUseFreqRuleService.deleteByPrimaryKey(id);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok();
    }

    @Override
    public Result<CouponGetorUseFreqRuleDto> save(@RequestBody CouponGetorUseFreqRuleDto dto) {
        int count = tbCouponGetorUseFreqRuleService.insertSelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<CouponGetorUseFreqRuleDto> update(@RequestBody CouponGetorUseFreqRuleDto dto) {
        int count = tbCouponGetorUseFreqRuleService.updateByPrimaryKeySelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<List<CouponGetorUseFreqRuleDto>> findAll() {
        //默认不实现 findAll
        //Result.ok(tbCouponGetorUseFreqRuleService.selectAll());
        throw new UnsupportedOperationException();
    }
}
