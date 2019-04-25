package com.youyu.cardequity.promotion.dto.other;

import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.cardequity.promotion.dto.*;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.*;
import com.youyu.cardequity.promotion.vo.req.BaseProductReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券详情
 */
@Getter
@Setter
public class CouponDetailDto {
    @ApiModelProperty(value = "优惠券基本信息", required = true)
    private ProductCouponDto productCouponDto;

    @ApiModelProperty(value = "优惠券涉及的商品")
    private List<BaseProductReq> productList;
    //改动
    @ApiModelProperty(value = "优惠券需要删除的商品")
    private List<BaseProductReq> delProductList;

    @ApiModelProperty(value = "领取或使用频率规则")
    private List<CouponGetOrUseFreqRuleDto> freqRuleList;

    @ApiModelProperty(value = "额度规则")
    private CouponQuotaRuleDto quotaRule;

    @ApiModelProperty(value = "子券信息")
    private List<CouponStageRuleDto> stageList;

    @ApiModelProperty(value = "操作者：用于更新产生者或更新者,一般存IP地址")
    private String operator;

    public CouponViewDto switchToView() {
        //优惠卷视图
        CouponViewDto dto = new CouponViewDto();
        //商品详情展示标识
        dto.setProductDetailShowFlag(CommonDict.IF_YES.getCode());
        if (productCouponDto != null) {
            BeanUtils.copyProperties(productCouponDto, dto);
            // 领取对象：0-全部用户 1-新用户 2-会员
            //全部用户
            dto.setTargetFlag(CommonDict.FRONDEND_ALL.getCode());
            //优惠标签:标签：满返券、促销等
            dto.setLabelDto(productCouponDto.getLabelDto());

            //转义领取对象
            //注册用户    //getStage  获取阶段:0-付后 1-即时 2-确认收货后 3-注册 4-推荐 5-分享 6-平台指定发放
            if (UsedStage.Register.getDictValue().equals(productCouponDto.getGetStage())) {
                dto.setTargetFlag(CommonDict.FRONDEND_NEW.getCode());  //新用户
            }
            if (productCouponDto.getClientTypeSet() != null) {
                //会员专属
                if (productCouponDto.getClientTypeSet().equals(ClientType.MEMBER.getDictValue())) {
                    dto.setTargetFlag(CommonDict.FRONDEND_MEMBER.getCode());  //会员专属
                }
            }

            //转义优惠券类型    0-消费劵
            dto.setCouponViewType("0");
            //运费劵
            if (CouponType.TRANSFERFARE.getDictValue().equals(productCouponDto.getCouponType()) ||
                    //免邮劵
                    CouponType.FREETRANSFERFARE.getDictValue().equals(productCouponDto.getCouponType()))
                dto.setCouponViewType("1");
            //转义展示的标识    //平台发放
            if (CouponGetType.GRANT.getDictValue().equals(productCouponDto.getGetType()))
                //不展示
                dto.setProductDetailShowFlag(CommonDict.IF_NO.getCode());

        }
        //规则额度
        if("1".equals(productCouponDto.getGetType())){
            if (quotaRule != null) {
                dto.setMaxCount(quotaRule.getMaxCount());//最大发放数量(券池数量):优惠券数量池
            }else{
                dto.setMaxCount(CommonConstant.IGNOREINTVALUE);// 数值参数的边界有效上限````
            }
            //领取或使用规则

            if (freqRuleList != null && freqRuleList.size() > 0){
                for (CouponGetOrUseFreqRuleDto freq : freqRuleList) {
                    //使用
                    if (OpCouponType.USERULE.getDictValue().equals(freq.getOpCouponType()))
                        continue;
                    dto.setFreqId(freq.getUuid());
                    //频率周期
                    dto.setUnit(freq.getUnit());
                    //周期内允许数量
                    dto.setAllowCount(freq.getAllowCount());
                    //客户获取总数/客户每次使用数
                    dto.setPersonTotalNum(freq.getPersonTotalNum());
                }
            }
        }
        //使用门槛
        dto.setConditionValue(BigDecimal.ZERO);
        //每张券最大优惠金额
        dto.setPerProfitTopValue(BigDecimal.ZERO);
        if (stageList != null && stageList.size() > 0) {
            for (CouponStageRuleDto stage : stageList) {
                //起始值赋值给使用门槛
                dto.setConditionValue(stage.getBeginValue());
                //优惠值:如果是阶梯或随机的填0，存折扣、金额
                dto.setProfitValue(stage.getCouponValue());

                //如果是等阶的消费券，需要转换计算每人最大优惠额
                //等阶门槛优惠券
                if (CouponStrategyType.equalstage.getDictValue().equals(productCouponDto.getCouponStrategyType()) &&
                        stage.getEndValue()!=null && stage.getEndValue().compareTo(BigDecimal.ZERO)>0 && stage.getEndValue().compareTo(CommonConstant.IGNOREVALUE)<0 &&
                        stage.getBeginValue()!=null && stage.getBeginValue().compareTo(BigDecimal.ZERO)>0) {


                    dto.setPerProfitTopValue(stage.getEndValue().divide(stage.getBeginValue()).multiply(stage.getCouponValue()));
                }
                dto.setStageId(stage.getUuid());
                break;//首期只有一个阶梯的数据
            }

        }


        dto.setProductList(productList);
        return dto;
    }
}
