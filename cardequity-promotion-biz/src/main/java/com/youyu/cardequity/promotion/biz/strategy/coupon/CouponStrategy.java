package com.youyu.cardequity.promotion.biz.strategy.coupon;

import com.youyu.cardequity.promotion.biz.dal.dao.CouponRefProductMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponStageRuleMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.ClientCouponEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponRefProductEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponStageRuleEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.cardequity.promotion.dto.other.OrderProductDetailDto;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.ApplyProductFlag;
import com.youyu.cardequity.promotion.vo.rsp.UseCouponRsp;
import com.youyu.common.exception.BizException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.youyu.cardequity.promotion.enums.ResultCode.COUPON_NOT_EXISTS;
import static com.youyu.cardequity.promotion.enums.ResultCode.PARAM_ERROR;
import static java.math.BigDecimal.ZERO;
import static org.apache.commons.collections.CollectionUtils.*;

/**
 * Created by caiyi on 2018/12/26.
 */
public abstract class CouponStrategy {

    @Autowired
    private CouponStageRuleMapper couponStageRuleMapper;

    @Autowired
    CouponRefProductMapper couponRefProductMapper;

    /**
     * 计算优惠券适用信息
     *
     * @param clientCoupon
     * @param coupon
     * @param productList
     * @return
     */
    public abstract UseCouponRsp applyCoupon(ClientCouponEntity clientCoupon, ProductCouponEntity coupon, List<OrderProductDetailDto> productList);

    /**
     * 保护并补全阶梯信息
     *
     * @param clientCoupon
     * @return
     */
    protected CouponStageRuleEntity protectCompletion(ClientCouponEntity clientCoupon) {

        CouponStageRuleEntity stage = null;
        //保护一下如果券下面只有一个阶梯，算作使用该券
        //if (CommonUtils.isEmptyorNull(clientCoupon.getStageId())) {
        //    List<CouponStageRuleEntity> stageByCouponId = couponStageRuleMapper.findStageByCouponId(clientCoupon.getCouponId());
        //领券只能指定某个阶梯
        //   if (stageByCouponId.size() > 1)
        //       throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc(clientCoupon.getCouponId()+"存在多个子券"));
        //   if (stageByCouponId.size() == 1) {
        //      stage = stageByCouponId.get(0);
        //   }
        //}

        if (!CommonUtils.isEmptyorNull(clientCoupon.getStageId())) {
            //可能原阶梯已经被删除，仍按初始值进行处理
            stage = new CouponStageRuleEntity();
            stage.setBeginValue(clientCoupon.getBeginValue());
            stage.setCouponId(clientCoupon.getCouponId());
            stage.setCouponShortDesc(clientCoupon.getCouponShortDesc());
            stage.setCouponValue(clientCoupon.getCouponAmout());
            stage.setEndValue(clientCoupon.getEndValue());
            stage.setId(clientCoupon.getStageId());
            stage.setTriggerByType(clientCoupon.getTriggerByType());
            stage.setIsEnable(CommonDict.IF_YES.getCode());
            //先取得对应阶梯的信息
            //    stage = couponStageRuleMapper.findCouponStageById(clientCoupon.getCouponId(),
            //            clientCoupon.getStageId());
            //    if (stage == null) {
            //throw new BizException(COUPON_NOT_EXISTS.getCode(), COUPON_NOT_EXISTS.getFormatDesc(clientCoupon.getCouponId()+",子券不存在："+clientCoupon.getStageId()));
            //   }
        }
        return stage;
    }

    /**
     * 校验优惠对应商品属性是否匹配
     *
     * @param coupon
     * @param productId
     * @return
     */
    public boolean checkCouponForProduct(ProductCouponEntity coupon, String productId) {
        // ApplyProductFlag空值做保护
        if (!ApplyProductFlag.ALL.getDictValue().equals(coupon.getApplyProductFlag())) {
            //该商品属性是否允许领取该券
            CouponRefProductEntity entity = couponRefProductMapper.findByBothId(coupon.getId(), productId);
            if (entity == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * 优惠金额分配
     *
     * @param totalProfitAmount 优惠金额
     * @param totalProductAmount 适配商品总价
     * @param orderProducts 适配商品明细
     */
    protected void distributeProfitAmount(BigDecimal totalProfitAmount, BigDecimal totalProductAmount, List<OrderProductDetailDto> orderProducts) {

        if (isEmpty(orderProducts)) {
            return;
        }

        BigDecimal subTotalProfitAmount = ZERO; // 已适配优惠金额
        OrderProductDetailDto product;
        for (int i = 0; i < orderProducts.size() - 1; i++) {
            product = orderProducts.get(i);
            product.setProfitAmount(totalProfitAmount.multiply(product.getTotalAmount().divide(totalProductAmount, 2, RoundingMode.DOWN)));
            subTotalProfitAmount = subTotalProfitAmount.add(product.getProfitAmount());
        }
        //最后一个商品的优惠金额=优惠金额-已适配优惠金额
        product = orderProducts.get(orderProducts.size() - 1);
        product.setProfitAmount(totalProfitAmount.subtract(subTotalProfitAmount));
    }
}
