package com.youyu.cardequity.promotion.biz.strategy.coupon;

import com.youyu.cardequity.common.base.annotation.StatusAndStrategyNum;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponRefProductMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.*;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.ClientCouponDto;
import com.youyu.cardequity.promotion.dto.OrderProductDetailDto;
import com.youyu.cardequity.promotion.enums.dict.ApplyProductFlag;
import com.youyu.cardequity.promotion.enums.dict.DiscountApplyStage;
import com.youyu.cardequity.promotion.enums.dict.TriggerByType;
import com.youyu.cardequity.promotion.vo.rsp.UseCouponRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by caiyi on 2018/12/26.
 */
@Slf4j
@StatusAndStrategyNum(superClass = CouponStrategy.class, number = "0", describe = "打折")
@Component
public class DiscountCouponStrategy extends CouponStrategy {

    @Autowired
    private CouponRefProductMapper couponRefProductMapper;

    @Override
    public UseCouponRsp applyCoupon(ClientCouponEntity clientCoupon, ProductCouponEntity coupon, List<OrderProductDetailDto> productList) {
        //装箱返回数据
        UseCouponRsp rsp = new UseCouponRsp();
        ClientCouponDto clientCouponDto = new ClientCouponDto();
        BeanUtils.copyProperties(clientCoupon, clientCouponDto);
        rsp.setClientCoupon(clientCouponDto);
        rsp.setProfitAmount(BigDecimal.ZERO);


        //保护一下如果券下面只有一个阶梯，算作使用该券
        CouponStageRuleEntity stage = protectCompletion(clientCoupon);
        if (stage != null && CommonUtils.isEmptyorNull(clientCoupon.getStageId())) {
            clientCouponDto.setStageId(stage.getId());
            clientCoupon.setStageId(stage.getId());
        }

        //临时变量
        boolean successFlg = false;
        String discountApplyStage = DiscountApplyStage.ALL.getDictValue();
        // TODO: 2018/12/26 应取自开关值

        BigDecimal countCondition = BigDecimal.ZERO;
        BigDecimal amountCondition = BigDecimal.ZERO;
        BigDecimal diff = BigDecimal.ZERO;
        BigDecimal applyNum = BigDecimal.ZERO;
        //优惠券适配商品统计，该productList按单价进行排序后的
        for (OrderProductDetailDto productItem : productList) {

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

            applyNum = product.getAppCount();
            if (stage != null && CommonUtils.isGtZeroDecimal(clientCoupon.getBeginValue())) {
                //按数量统计
                if (TriggerByType.NUMBER.getDictValue().equals(clientCoupon.getTriggerByType())) {
                    diff = stage.getBeginValue().subtract(countCondition);
                    if (applyNum.compareTo(diff) >= 0) {
                        successFlg = true;
                        //折扣券：虽然按折扣只从小到打排序，但是并不表示排最前的是订单最优惠的，因为其在订单中应用商品范围是不同的，所以需要对所有券循环
                        //算入门槛内的才进行打折，否则全部都要打折
                        if (DiscountApplyStage.CONDITION.getDictValue().equals(discountApplyStage)) {
                            applyNum = diff;
                            //优惠金额=达到优惠条件总额*(1-折扣),达到优惠条件总额=(买入数量-(总数量-达标门槛))*价格
                            calculationProfitAmount(rsp, product, clientCoupon, applyNum);
                            break;
                        }
                    }
                } else {
                    diff = stage.getBeginValue().subtract(amountCondition);

                    //满足门槛条件情况下
                    if (product.getTotalAmount().compareTo(diff) >= 0) {
                        //折扣券：虽然按折扣只从小到打排序，但是并不表示排最前的是订单最优惠的，因为其在订单中应用商品范围是不同的，所以需要对所有券循环
                        successFlg = true;
                        //算入门槛内的才进行打折，否则全部都要打折
                        if (DiscountApplyStage.CONDITION.getDictValue().equals(discountApplyStage)) {
                            //适用范围=向上取整(门槛差额/价格)
                            applyNum = diff.divide(product.getPrice()).setScale(0, BigDecimal.ROUND_UP);
                            //优惠金额=达到优惠条件总额*(1-折扣),达到优惠条件总额=(买入数量-(总数量-达标门槛))*价格
                            calculationProfitAmount(rsp, product, clientCoupon, applyNum);
                            break;
                        }
                    }
                }
            }
            else{
                successFlg = true;
            }
            //没有达到门槛
            calculationProfitAmount(rsp, product, clientCoupon, applyNum);

            countCondition = countCondition.add(product.getAppCount());
            amountCondition = amountCondition.add(product.getTotalAmount());
        }
        //循环完后没有达到门槛的返回空
        if (!successFlg)
            return null;
        return rsp;
    }


    /**
     * 计算优惠金额
     *
     * @param useCouponRsp
     * @param clientCoupon 领取券信息：领取人、编号
     * @param applyNum     适用的数量，对于等阶满减券而言表示等阶满减券的步长倍数
     * @return
     */
    private BigDecimal calculationProfitAmount(UseCouponRsp useCouponRsp, OrderProductDetailDto product, ClientCouponEntity clientCoupon, BigDecimal applyNum) {
        //无指定适用范围则按所有数量都打折
        if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
            product.setAppCount(applyNum);
            product.setTotalAmount(product.getPrice().multiply(applyNum));
        }
        //优惠金额=总额*(1-折扣)
        BigDecimal profitAmount = product.getTotalAmount().multiply(BigDecimal.ONE.subtract(clientCoupon.getCouponAmout()));

        product.setProfitAmount(profitAmount);
        useCouponRsp.setTotalAmount(useCouponRsp.getTotalAmount().add(product.getTotalAmount()));
        useCouponRsp.setProfitAmount(useCouponRsp.getProfitAmount().add(profitAmount));//加总优惠金额

        return profitAmount;
    }
}
