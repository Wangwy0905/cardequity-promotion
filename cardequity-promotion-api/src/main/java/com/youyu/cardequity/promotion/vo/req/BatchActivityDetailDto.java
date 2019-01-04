package com.youyu.cardequity.promotion.vo.req;

import com.youyu.cardequity.promotion.dto.ActivityDetailDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 批量活动请求体：用于批量删除、更新等
 */
@Data
public class BatchActivityDetailDto {
    @ApiModelProperty(value = "活动详情列表")
    private List<ActivityDetailDto> activityDetailList;
}
