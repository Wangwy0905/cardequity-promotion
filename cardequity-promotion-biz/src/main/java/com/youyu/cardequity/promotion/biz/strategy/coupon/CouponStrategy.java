package com.youyu.cardequity.promotion.biz.strategy.coupon;

import com.youyu.cardequity.promotion.biz.dal.dao.CouponRefProductMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponStageRuleMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.ClientCouponEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponRefProductEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponStageRuleEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.other.OrderProductDetailDto;
import com.youyu.cardequity.promotion.enums.dict.ApplyProductFlag;
import com.youyu.cardequity.promotion.vo.rsp.UseCouponRsp;
import com.youyu.common.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.youyu.cardequity.promotion.enums.ResultCode.COUPON_NOT_EXISTS;
import static com.youyu.cardequity.promotion.enums.ResultCode.PARAM_ERROR;

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
        if (CommonUtils.isEmptyorNull(clientCoupon.getStageId())) {
            List<CouponStageRuleEntity> stageByCouponId = couponStageRuleMapper.findStageByCouponId(clientCoupon.getCouponId());
            //领券只能指定某个阶梯
            if (stageByCouponId.size() > 1)
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getDesc());
            if (stageByCouponId.size() == 1) {
                stage = stageByCouponId.get(0);
            }
        } else {
            //先取得对应阶梯的信息
            stage = couponStageRuleMapper.findCouponStageById(clientCoupon.getCouponId(),
                    clientCoupon.getStageId());
            if (stage == null) {
                throw new BizException(COUPON_NOT_EXISTS.getCode(), COUPON_NOT_EXISTS.getDesc());
            }
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
}
