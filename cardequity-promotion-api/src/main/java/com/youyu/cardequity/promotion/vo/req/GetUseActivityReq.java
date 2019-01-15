package com.youyu.cardequity.promotion.vo.req;

import com.youyu.cardequity.promotion.dto.other.OrderProductDetailDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by caiyi on 2018/12/18.
 */
@Getter
@Setter
public class GetUseActivityReq {
    @ApiModelProperty(value = "客户编号:必填" ,required= true)
    private String clientId;

    @ApiModelProperty(value = "客户类型:冗余，服务层调用时传入空，需要调用用户中心进行查询该字段")
    private String clientType;

    @ApiModelProperty(value = "委托方式:验证该渠道操作方式是否可以领取",required= true)
    private String entrustWay;

    @ApiModelProperty(value = "银行代码:传入用于校验是否该银行卡可用")
    private String bankCode;

    @ApiModelProperty(value = "支付类型:传入用于校验是否该支付类型可用")
    private String payType;

    @ApiModelProperty(value = "相关商品明细，用于判断是否满足使用阶梯门槛" ,required= true)
    private List<OrderProductDetailDto> productList;
}
