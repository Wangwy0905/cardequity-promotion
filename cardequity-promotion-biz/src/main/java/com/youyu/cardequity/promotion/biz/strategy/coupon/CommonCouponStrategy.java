package com.youyu.cardequity.promotion.biz.strategy.coupon;

import com.youyu.cardequity.common.base.annotation.StatusAndStrategyNum;
import com.youyu.cardequity.promotion.biz.dal.entity.ClientCouponEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponStageRuleEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.ClientCouponDto;
import com.youyu.cardequity.promotion.dto.other.OrderProductDetailDto;
import com.youyu.cardequity.promotion.enums.dict.CouponApplyProductStage;
import com.youyu.cardequity.promotion.enums.dict.TriggerByType;
import com.youyu.cardequity.promotion.vo.rsp.UseCouponRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于普通优惠券：门槛优惠券、无门槛优惠券、随机优惠券
 * Created by caiyi on 2018/12/26.
 */
@Slf4j
@StatusAndStrategyNum(superClass = CouponStrategy.class, number = "1", describe = "满N减M元")
@Component
public class CommonCouponStrategy extends CouponStrategy {

    @Override
    public UseCouponRsp applyCoupon(ClientCouponEntity clientCoupon, ProductCouponEntity coupon, List<OrderProductDetailDto> productList) {
        log.info("进入普通满减优惠券处理策略，领取编号{}，优惠券编号为{}",clientCoupon.getId(),coupon.getId());
        //装箱返回数据
        UseCouponRsp rsp = new UseCouponRsp();
        ClientCouponDto clientCouponDto = new ClientCouponDto();
        BeanUtils.copyProperties(clientCoupon, clientCouponDto);
        rsp.setClientCoupon(clientCouponDto);
        //满减券优惠金额等于券的金额
        rsp.setProfitAmount(clientCoupon.getCouponAmout());

        //保护一下如果券下面只有一个阶梯，算作使用该券
        CouponStageRuleEntity stage = protectCompletion(clientCoupon);
        if (stage!=null && CommonUtils.isEmptyorNull(clientCoupon.getStageId())) {
            clientCouponDto.setStageId(stage.getId());
            clientCoupon.setStageId(stage.getId());
        }

        //临时变量
        boolean successFlg = false;
        String applyStage=CouponApplyProductStage.ALL.getDictValue();
        // TODO: 2018/12/27 应取自配置项

        BigDecimal countCondition = BigDecimal.ZERO;
        BigDecimal amountCondition = BigDecimal.ZERO;
        BigDecimal diff = BigDecimal.ZERO;
        BigDecimal applyNum = BigDecimal.ZERO;
        //优惠券适配商品统计，该productList按单价进行排序后的
        for (OrderProductDetailDto productItem : productList) {

            //校验符合商品基本属性
            //1.该商品是否适用于此活动
            if (!checkCouponForProduct(coupon, productItem.getProductId())) {
                continue;
            }

            //2.后续会多次设置product对象相关值，需要拷贝出来避免污染
            OrderProductDetailDto product = new OrderProductDetailDto();
            BeanUtils.copyProperties(productItem, product);
            //总额做保护
            product.setTotalAmount(product.getAppCount().multiply(product.getPrice()));
            //校验通过后加入适用商品列表
            rsp.getProductLsit().add(product);

            applyNum = product.getAppCount();
            if (stage != null && CommonUtils.isGtZeroDecimal(clientCoupon.getBeginValue())) {
                //按数量统计
                if (TriggerByType.NUMBER.getDictValue().equals(clientCoupon.getTriggerByType())) {
                    diff = stage.getBeginValue().subtract(countCondition);
                    if (applyNum.compareTo(diff) >= 0) {
                        successFlg=true;
                        //默认策略：折扣优惠值是平摊订单涉及到的券定义时适用范围内所有商品,不许要下面if处理
                        if (CouponApplyProductStage.CONDITION.getDictValue().equals(applyStage)) {
                            applyNum = diff;
                            product.setProfitCount(applyNum);
                            break;
                        }
                    }
                } else {
                    diff = stage.getBeginValue().subtract(amountCondition);
                    //满足门槛条件情况下
                    if (product.getTotalAmount().compareTo(diff) >= 0) {
                        successFlg=true;
                        //默认策略：折扣优惠值是平摊订单涉及到的券定义时适用范围内所有商品,不许要下面if处理；适用范围=向上取整(门槛差额/价格)
                        if (CouponApplyProductStage.CONDITION.getDictValue().equals(applyStage)) {
                            applyNum = diff.divide(product.getPrice()).setScale(0, BigDecimal.ROUND_UP);
                            product.setProfitCount(applyNum);
                            break;
                        }
                    }
                }
            }else {
                successFlg=true;
            }

            product.setProfitCount(applyNum);
            countCondition = countCondition.add(product.getAppCount());
            amountCondition = amountCondition.add(product.getTotalAmount());
        }
        //循环完后没有达到门槛的返回空
        if (!successFlg) {
            return null;
        }else{
            BigDecimal totalRealAmount = rsp.getProductLsit().stream().map(OrderProductDetailDto::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            rsp.setTotalAmount(totalRealAmount);
            //无门槛券可能出现优惠金额>总金额
            if (rsp.getProfitAmount().compareTo(totalRealAmount)>0)
                rsp.setProfitAmount(totalRealAmount);
            if (totalRealAmount.compareTo(BigDecimal.ZERO)>0) {
                //每种商品优惠的金额是按适用金额比例来的，如果是免邮券getProfitAmount是0
                for (OrderProductDetailDto product : rsp.getProductLsit()) {
                    product.setProfitAmount(rsp.getProfitAmount().multiply(product.getTotalAmount().divide(totalRealAmount)));
                }
            }
        }
        return rsp;
    }

}
