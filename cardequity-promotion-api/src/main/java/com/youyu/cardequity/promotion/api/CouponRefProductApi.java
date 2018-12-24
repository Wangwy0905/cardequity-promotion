package com.youyu.cardequity.promotion.api;


import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.CouponRefProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-20
 */
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/tbCouponRefProduct")
public interface CouponRefProductApi {


}
