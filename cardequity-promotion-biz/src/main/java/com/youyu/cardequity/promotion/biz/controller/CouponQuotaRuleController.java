package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.CouponQuotaRuleApi;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.CouponQuotaRuleDto;
import com.youyu.cardequity.promotion.biz.service.CouponQuotaRuleService;
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
public class CouponQuotaRuleController implements CouponQuotaRuleApi {

    @Autowired
    private CouponQuotaRuleService tbCouponQuotaRuleService;

    @Override
    public Result<CouponQuotaRuleDto> get(@PathVariable(name = "id") String id) {
        CouponQuotaRuleDto dto = tbCouponQuotaRuleService.selectByPrimaryKey(id);
        if (dto == null) {
            return Result.fail(NO_DATA_FOUND);
        }
        return Result.ok(dto);
    }

    @Override
    public Result delete(@PathVariable(name = "id") String id) {
        int count = tbCouponQuotaRuleService.deleteByPrimaryKey(id);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok();
    }

    @Override
    public Result<CouponQuotaRuleDto> save(@RequestBody CouponQuotaRuleDto dto) {
        int count = tbCouponQuotaRuleService.insertSelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<CouponQuotaRuleDto> update(@RequestBody CouponQuotaRuleDto dto) {
        int count = tbCouponQuotaRuleService.updateByPrimaryKeySelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<List<CouponQuotaRuleDto>> findAll() {
        //默认不实现 findAll
        //Result.ok(tbCouponQuotaRuleService.selectAll());
        throw new UnsupportedOperationException();
    }
}
