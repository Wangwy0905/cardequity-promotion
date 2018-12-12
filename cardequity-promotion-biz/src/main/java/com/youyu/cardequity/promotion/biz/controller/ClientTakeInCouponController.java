package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ClientTakeInCouponApi;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.ClientTakeInCouponDto;
import com.youyu.cardequity.promotion.biz.service.ClientTakeInCouponService;
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
public class ClientTakeInCouponController implements ClientTakeInCouponApi {

    @Autowired
    private ClientTakeInCouponService tbClientTakeInCouponService;

    @Override
    public Result<ClientTakeInCouponDto> get(@PathVariable(name = "id") String id) {
        ClientTakeInCouponDto dto = tbClientTakeInCouponService.selectByPrimaryKey(id);
        if (dto == null) {
            return Result.fail(NO_DATA_FOUND);
        }
        return Result.ok(dto);
    }

    @Override
    public Result delete(@PathVariable(name = "id") String id) {
        int count = tbClientTakeInCouponService.deleteByPrimaryKey(id);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok();
    }

    @Override
    public Result<ClientTakeInCouponDto> save(@RequestBody ClientTakeInCouponDto dto) {
        int count = tbClientTakeInCouponService.insertSelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<ClientTakeInCouponDto> update(@RequestBody ClientTakeInCouponDto dto) {
        int count = tbClientTakeInCouponService.updateByPrimaryKeySelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<List<ClientTakeInCouponDto>> findAll() {
        //默认不实现 findAll
        //Result.ok(tbClientTakeInCouponService.selectAll());
        throw new UnsupportedOperationException();
    }
}
