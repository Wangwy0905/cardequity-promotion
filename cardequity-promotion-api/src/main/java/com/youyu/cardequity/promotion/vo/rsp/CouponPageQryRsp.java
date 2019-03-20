package com.youyu.cardequity.promotion.vo.rsp;

import com.youyu.cardequity.promotion.dto.other.CouponDetailDto;
import com.youyu.common.api.PageData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caiyi on 2019/1/9.
 */
@Data
public class CouponPageQryRsp {

    @ApiModelProperty(value = "查询结果")
    private PageData<CouponDetailDto> result;

    @ApiModelProperty(value = "汇总结果")
    private List<GatherInfoRsp> gatherResult;
}
