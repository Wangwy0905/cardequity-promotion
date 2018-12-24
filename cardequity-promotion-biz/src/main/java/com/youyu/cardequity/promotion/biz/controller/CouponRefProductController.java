package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.CouponRefProductApi;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.CouponRefProductDto;
import com.youyu.cardequity.promotion.biz.service.CouponRefProductService;
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
 * @date 2018-12-20
 */
@RestController
public class CouponRefProductController implements CouponRefProductApi {

    @Autowired
    private CouponRefProductService tbCouponRefProductService;


}
