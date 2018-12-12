package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ProductCouponApi;
import com.youyu.cardequity.promotion.vo.req.QryProfitCommonReq;
import com.youyu.cardequity.promotion.vo.rsp.CouponDefineRsp;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.ProductCouponDto;
import com.youyu.cardequity.promotion.biz.service.ProductCouponService;
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
public class ProductCouponController implements ProductCouponApi {

    @Autowired
    private ProductCouponService productCouponService;

    @Override
    public Result<ProductCouponDto> get(@PathVariable(name = "id") String id) {
        ProductCouponDto dto = productCouponService.selectByPrimaryKey(id);
        if (dto == null) {
            return Result.fail(NO_DATA_FOUND);
        }
        return Result.ok(dto);
    }

    @Override
    public Result delete(@PathVariable(name = "id") String id) {
        int count = productCouponService.deleteByPrimaryKey(id);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok();
    }

    @Override
    public Result<ProductCouponDto> save(@RequestBody ProductCouponDto dto) {
        int count = productCouponService.insertSelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<ProductCouponDto> update(@RequestBody ProductCouponDto dto) {
        int count = productCouponService.updateByPrimaryKeySelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<List<ProductCouponDto>> findAll() {
        //默认不实现 findAll
        //Result.ok(productCouponService.selectAll());
        throw new UnsupportedOperationException();
    }

    /**
     * 获取可以领取的优惠券
     * @param req
     * @return
     */
    public Result<List<CouponDefineRsp>> findEnableGetCoupon(@RequestBody QryProfitCommonReq req) {
        List<CouponDefineRsp> rspList = productCouponService.findEnableGetCoupon(req);
        return Result.ok(rspList);
    }
}
