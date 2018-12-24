package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.CouponStageRuleApi;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.CouponStageRuleDto;
import com.youyu.cardequity.promotion.biz.service.CouponStageRuleService;
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
public class CouponStageRuleController implements CouponStageRuleApi {

    @Autowired
    private CouponStageRuleService tbCouponStageUseAndGetRuleService;

    @Override
    public Result<CouponStageRuleDto> get(@PathVariable(name = "id") String id) {
        CouponStageRuleDto dto = tbCouponStageUseAndGetRuleService.selectByPrimaryKey(id);
        if (dto == null) {
            return Result.fail(NO_DATA_FOUND);
        }
        return Result.ok(dto);
    }

    @Override
    public Result delete(@PathVariable(name = "id") String id) {
        int count = tbCouponStageUseAndGetRuleService.deleteByPrimaryKey(id);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok();
    }

    @Override
    public Result<CouponStageRuleDto> save(@RequestBody CouponStageRuleDto dto) {
        int count = tbCouponStageUseAndGetRuleService.insertSelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<CouponStageRuleDto> update(@RequestBody CouponStageRuleDto dto) {
        int count = tbCouponStageUseAndGetRuleService.updateByPrimaryKeySelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<List<CouponStageRuleDto>> findAll() {
        //默认不实现 findAll
        //Result.ok(tbCouponStageUseAndGetRuleService.selectAll());
        throw new UnsupportedOperationException();
    }
}
