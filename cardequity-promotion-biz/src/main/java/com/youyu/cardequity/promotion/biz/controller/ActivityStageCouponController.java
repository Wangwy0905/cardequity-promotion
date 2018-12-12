package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ActivityStageCouponApi;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.ActivityStageCouponDto;
import com.youyu.cardequity.promotion.biz.service.ActivityStageCouponService;
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
public class ActivityStageCouponController implements ActivityStageCouponApi {

    @Autowired
    private ActivityStageCouponService tbActivityStageCouponService;

    @Override
    public Result<ActivityStageCouponDto> get(@PathVariable(name = "id") String id) {
        ActivityStageCouponDto dto = tbActivityStageCouponService.selectByPrimaryKey(id);
        if (dto == null) {
            return Result.fail(NO_DATA_FOUND);
        }
        return Result.ok(dto);
    }

    @Override
    public Result delete(@PathVariable(name = "id") String id) {
        int count = tbActivityStageCouponService.deleteByPrimaryKey(id);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok();
    }

    @Override
    public Result<ActivityStageCouponDto> save(@RequestBody ActivityStageCouponDto dto) {
        int count = tbActivityStageCouponService.insertSelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<ActivityStageCouponDto> update(@RequestBody ActivityStageCouponDto dto) {
        int count = tbActivityStageCouponService.updateByPrimaryKeySelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<List<ActivityStageCouponDto>> findAll() {
        //默认不实现 findAll
        //Result.ok(tbActivityStageCouponService.selectAll());
        throw new UnsupportedOperationException();
    }
}
