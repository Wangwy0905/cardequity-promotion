package com.youyu.cardequity.promotion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class ClientGroupBuyInfoDto  implements IBaseDto<String> {

    @ApiModelProperty(value = "团购编号:")
    private String uuid;

    @ApiModelProperty(value = "客户编号:")
    private String clientId;

    @ApiModelProperty(value = "开团码:")
    private String openGroupCode;

    @ApiModelProperty(value = "银行账号:")
    private String bankAccountId;

    @ApiModelProperty(value = "委托方式:")
    private String entrustWay;

    @ApiModelProperty(value = "状态:0-团购中 1-团购失败 2-返现中 3-成功")
    private String status;

    @ApiModelProperty(value = "订单编号:只有这些买入商品组的商品才提供优惠，为*标识所有")
    private String orderId;

    @ApiModelProperty(value = "商品编号:只有这些买入商品组的商品才提供优惠，为*标识所有")
    private String productId;

    @ApiModelProperty(value = "SkuId:网易:ApiSkuTo.Id，对于苏宁填子编号")
    private String skuId;

    @ApiModelProperty(value = "数量:")
    private BigDecimal productCount;

    @ApiModelProperty(value = "优惠值:主要优惠金额、")
    private BigDecimal profitValue;

    @ApiModelProperty(value = "团购开始时间:到分秒级别")
    private LocalDate beginDate;

    @ApiModelProperty(value = "团购结束时间:")
    private LocalDate endDate;

    @ApiModelProperty(value = "团购价:")
    private BigDecimal groupPrice;

    @ApiModelProperty(value = "支付价格:")
    private BigDecimal realPrice;

    @ApiModelProperty(value = "数量:")
    private Integer buyCount;

    @ApiModelProperty(value = "团购返现金额:返现成功后填入")
    private BigDecimal backAmount;

    @ApiModelProperty(value = "备注:")
    private String remark;

    @ApiModelProperty(value = "产生者:")
    private String createAuthor;

    @ApiModelProperty(value = "更新者:")
    private String updateAuthor;

    @ApiModelProperty(value = "更新时间:")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "产生时间:")
    private LocalDateTime createTime;

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void setId(String id) {
        this.uuid = id;
    }
}

