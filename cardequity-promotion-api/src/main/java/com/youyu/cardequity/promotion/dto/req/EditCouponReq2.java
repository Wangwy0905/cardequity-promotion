package com.youyu.cardequity.promotion.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youyu.cardequity.common.base.util.LocalDateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月13日 10:00:00
 * @work 优惠券编辑req
 * 注:对历史问题重构,所有后面跟前缀2
 */
@Setter
@Getter
@ApiModel("优惠券编辑req")
public class EditCouponReq2 implements Serializable {

    private static final long serialVersionUID = 5275790215497469262L;

    @ApiModelProperty(value = "优惠券编号")
    private String uuid;

    @ApiModelProperty(value = "优惠名称:")
    private String couponName;

    @ApiModelProperty(value = "门槛短描:如满3件减20")
    private String couponShortDesc;

    @ApiModelProperty(value = "使用说明")
    private String couponDesc;

    @ApiModelProperty(value = "领取开始日:格式yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = LocalDateUtils.DATETIME_FORMAT)
    private LocalDateTime allowGetBeginDate;

    @ApiModelProperty(value = "领取结束日:格式yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = LocalDateUtils.DATETIME_FORMAT)
    private LocalDateTime allowGetEndDate;

    @ApiModelProperty(value = "频率周期类型:0-天 1-周 2-月 3-年 a-有效期内")
    private String unit;

    @ApiModelProperty(value = "每个客户领取总数")
    private Integer personTotalNum;

    @ApiModelProperty(value = "周期内允许领取数量")
    private Integer allowCount;
}
