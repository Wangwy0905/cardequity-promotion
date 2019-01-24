package com.youyu.cardequity.promotion.biz.strategy.coupon;

import com.youyu.cardequity.common.base.annotation.StatusAndStrategyNum;
import com.youyu.cardequity.promotion.biz.dal.entity.ClientCouponEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponStageRuleEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.ClientCouponDto;
import com.youyu.cardequity.promotion.dto.other.OrderProductDetailDto;
import com.youyu.cardequity.promotion.enums.dict.TriggerByType;
import com.youyu.cardequity.promotion.vo.rsp.UseCouponRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by caiyi on 2018/12/26.
 */
@Slf4j
@StatusAndStrategyNum(superClass = CouponStrategy.class, number = "4", describe = "每满N减M元")
@Component
public class EqualStageCouponStrategy extends CouponStrategy {

    @Override
    public UseCouponRsp applyCoupon(ClientCouponEntity clientCoupon, ProductCouponEntity coupon, List<OrderProductDetailDto> productList) {
        log.info("进入等阶满减优惠券处理策略，领取编号{}，优惠券编号为{}",clientCoupon.getId(),coupon.getId());
        //装箱返回数据
        UseCouponRsp rsp = new UseCouponRsp();
        ClientCouponDto clientCouponDto = new ClientCouponDto();
        BeanUtils.copyProperties(clientCoupon, clientCouponDto);
        rsp.setClientCoupon(clientCouponDto);
        //满减券优惠金额等于券的金额
        rsp.setProfitAmount(clientCoupon.getCouponAmout());

        //保护一下如果券下面只有一个阶梯，算作使用该券
        CouponStageRuleEntity stage = protectCompletion(clientCoupon);
        if (stage != null && CommonUtils.isEmptyorNull(clientCoupon.getStageId())) {
            clientCouponDto.setStageId(stage.getId());
            clientCoupon.setStageId(stage.getId());
        }

        //等阶满减券一定要有门槛阶梯
        if (stage == null)
            return null;

        //等阶满减券一定要有门槛值
        if (!CommonUtils.isGtZeroDecimal(clientCoupon.getBeginValue())) {
            return null;
        }

        //封顶值比门槛还低是废数据
        if (CommonUtils.isGtZeroDecimal(clientCoupon.getEndValue()) && clientCoupon.getEndValue().compareTo(clientCoupon.getBeginValue())<0){
            return null;
        }

        BigDecimal countCondition = BigDecimal.ZERO;
        BigDecimal amountCondition = BigDecimal.ZERO;

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
            //校验通过后加入适用商品列表
            rsp.getProductLsit().add(product);
            //总额做保护
            product.setTotalAmount(product.getAppCount().multiply(product.getPrice()));
            countCondition = countCondition.add(product.getAppCount());
            amountCondition = amountCondition.add(product.getTotalAmount());
        }

        BigDecimal applyNum = BigDecimal.ZERO;
        if (TriggerByType.NUMBER.getDictValue().equals(clientCoupon.getTriggerByType())) {
            if (countCondition.compareTo(clientCoupon.getBeginValue()) < 0)
                return null;
            //不能超过封顶值
            if (CommonUtils.isGtZeroDecimal(clientCoupon.getEndValue()) && clientCoupon.getEndValue().compareTo(amountCondition) < 0) {
                applyNum = clientCoupon.getEndValue().divide(clientCoupon.getBeginValue()).setScale(0, BigDecimal.ROUND_DOWN);
            } else {
                applyNum = amountCondition.divide(clientCoupon.getBeginValue()).setScale(0, BigDecimal.ROUND_DOWN);
            }
        } else {

            if (amountCondition.compareTo(clientCoupon.getBeginValue()) < 0)
                return null;
            //不能超过封顶值
            if (CommonUtils.isGtZeroDecimal(clientCoupon.getEndValue()) && clientCoupon.getEndValue().compareTo(countCondition) < 0) {
                applyNum = clientCoupon.getEndValue().divide(clientCoupon.getBeginValue()).setScale(0, BigDecimal.ROUND_DOWN);
            } else {
                applyNum = countCondition.divide(clientCoupon.getBeginValue()).setScale(0, BigDecimal.ROUND_DOWN);
            }
        }

        //达到门槛才返回
        if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
            //计算活动总优惠金额=步长倍数*每个步长优惠值
            BigDecimal totalProfitAmount = applyNum.multiply(clientCoupon.getCouponAmout());
            rsp.setProfitAmount(totalProfitAmount);

            //每种商品优惠的金额是按适用金额比例来的
            if (amountCondition.compareTo(BigDecimal.ZERO) > 0) {
                for (OrderProductDetailDto product : rsp.getProductLsit()) {
                    product.setProfitAmount(rsp.getProfitAmount().multiply(product.getTotalAmount().divide(amountCondition)));
                }
            }
            return rsp;
        }
        return rsp;
    }
}
