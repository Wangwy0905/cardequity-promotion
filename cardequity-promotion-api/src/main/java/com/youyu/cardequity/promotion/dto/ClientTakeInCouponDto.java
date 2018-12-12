package com.youyu.cardequity.promotion.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.youyu.common.dto.IBaseDto;
import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;


/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
@Data
@ApiModel
public class ClientTakeInCouponDto implements IBaseDto<String>{

    @ApiModelProperty(value = "编号:")
    private String uuid;

    @ApiModelProperty(value = "领取编号:")
    private String getId;

    @ApiModelProperty(value = "订单号:")
    private String orderId;

    @ApiModelProperty(value = "客户号:")
    private String clintId;

    @ApiModelProperty(value = "商品编号:")
    private String productId;

    @ApiModelProperty(value = "SkuId:网易:ApiSkuTo.Id，对于苏宁填子编号")
    private String skuId;

    @ApiModelProperty(value = "商品总额:如果是运费券")
    private BigDecimal productAmount;

    @ApiModelProperty(value = "数量:")
    private BigDecimal productCount;

    @ApiModelProperty(value = "优惠值:主要优惠金额、如果一张券应用到多个商品上则按商品价值总额按比例分配优惠额")
    private BigDecimal profitValue;

    @ApiModelProperty(value = "业务代码:优惠券使用、券撤销")
    private String businCode;

    @ApiModelProperty(value = "券状态:0-正常 1-使用中 2-已使用")
    private BigDecimal status;

    @ApiModelProperty(value = "备注:")
    private String remark;

    @ApiModelProperty(value = "产生者:")
    private String createAuthor;

    @ApiModelProperty(value = "更新者:")
    private String updateAuthor;

    @ApiModelProperty(value = "更新时间:")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "产生时间:使用时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "是否有效:当恢复正常时设置为0")
    private String isEnable;

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void setId(String id) {
        this.uuid = id;
    }
}

