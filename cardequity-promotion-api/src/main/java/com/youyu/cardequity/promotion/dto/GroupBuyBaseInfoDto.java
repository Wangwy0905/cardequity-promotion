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
public class GroupBuyBaseInfoDto implements IBaseDto<String>{

    @ApiModelProperty(value = "编号:")
    private String uuid;

    @ApiModelProperty(value = "团购名称:")
    private String groupName;

    @ApiModelProperty(value = "成团人数:为0即时生效")
    private String groupMinAcc;

    @ApiModelProperty(value = "专属商品集合:只有这些买入商品才提供优惠，但是具体优惠需要通过阶梯表获取，为*标识所有")
    private String productSet;

    @ApiModelProperty(value = "专属商品组集合:只有这些买入商品组的商品才提供优惠，为*标识所有")
    private String productGroupSet;

    @ApiModelProperty(value = "专属委托方式集合:只有指定的委托方式去买才提供优惠。如可以配置值允许移动端参与，为*标识所有")
    private String entrustWaySet;

    @ApiModelProperty(value = "团购开始日:到分秒级别")
    private LocalDate beginDate;

    @ApiModelProperty(value = "团购结束日:")
    private LocalDate endDate;

    @ApiModelProperty(value = "开团持续时间:控制开团方式为“个人私团”时的开团有效时间")
    private Integer termNum;

    @ApiModelProperty(value = "参与团购商品数量:如控制前一百件有优惠等")
    private Integer productNum;

    @ApiModelProperty(value = "允许每人团购数量:如控制是否一人只允许参与一次，或者每次团购只允许买一件等")
    private Integer perAccMaxNum;

    @ApiModelProperty(value = "团人数上限:如果开团方式为1-个人私团时，每个团的最大人数")
    private Integer subGroupMaxAcc;

    @ApiModelProperty(value = "团人数下限:最少多少人成团")
    private Integer subGroupMinAcc;

    @ApiModelProperty(value = "团购优惠方式:0-直减金额 1-折扣 2-指定价")
    private String groupProfitType;

    @ApiModelProperty(value = "团购优惠值:可以设置折扣值、直减金额、价格，如果指定一商品编号时可以设置价格")
    private BigDecimal profitValue;

    @ApiModelProperty(value = "关联活动Id:关联什么活动")
    private String joinActivityId;

    @ApiModelProperty(value = "叠加码:定义为8位码。相同标识码可叠加，多个以逗号相隔(设置该券时，应向操作员自动展示可叠加券列表)，为空代表所有")
    private String reCouponCode;

    @ApiModelProperty(value = "叠加标志:0-不可叠加 1-可叠加 2-自定义（建议规则简单点，不采用该值）")
    private String reCouponFlag;



    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void setId(String id) {
        this.uuid = id;
    }
}

