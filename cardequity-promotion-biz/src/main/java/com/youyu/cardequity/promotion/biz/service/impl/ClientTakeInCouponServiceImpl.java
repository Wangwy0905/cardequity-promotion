package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.promotion.biz.constant.BusinessCode;
import com.youyu.cardequity.promotion.biz.dal.entity.ClientCouponEntity;
import com.youyu.cardequity.promotion.biz.service.ActivityProfitService;
import com.youyu.cardequity.promotion.biz.service.ClientCouponService;
import com.youyu.cardequity.promotion.biz.service.ClientTakeInActivityService;
import com.youyu.cardequity.promotion.biz.service.ClientTakeInCouponService;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.OrderProductDetailDto;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.CouponStatus;
import com.youyu.cardequity.promotion.enums.dict.CouponType;
import com.youyu.cardequity.promotion.enums.dict.CouponUseType;
import com.youyu.cardequity.promotion.vo.req.GetUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.rsp.OrderCouponAndActivityRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseCouponRsp;
import com.youyu.common.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.youyu.cardequity.promotion.biz.dal.entity.ClientTakeInCouponEntity;
import com.youyu.cardequity.promotion.dto.ClientTakeInCouponDto;
import com.youyu.cardequity.promotion.biz.dal.dao.ClientTakeInCouponMapper;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
@Service
public class ClientTakeInCouponServiceImpl extends AbstractService<String, ClientTakeInCouponDto, ClientTakeInCouponEntity, ClientTakeInCouponMapper> implements ClientTakeInCouponService {
    @Autowired
    private ClientCouponService clientCouponService;

    @Autowired
    private ActivityProfitService activityProfitService;

    @Autowired
    private ClientTakeInActivityService clientTakeInActivityService;

    /**
     * 在订单预生成时候使用活动及优惠券详情
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCouponAndActivityRsp preOrderCouponAndActivityDeal(GetUseEnableCouponReq req) {
        OrderCouponAndActivityRsp result = new OrderCouponAndActivityRsp();
        List<UseActivityRsp> useActivityRsps = activityProfitService.combActivityRefProductDeal(req);
        //先拷贝生成第二次请求的数据，第二次请求数据需要在原活动优惠上计算
        GetUseEnableCouponReq innerReq = BeanPropertiesConverter.copyProperties(req, GetUseEnableCouponReq.class);
        for (OrderProductDetailDto product : innerReq.getProductList()) {
            if (useActivityRsps != null) {
                for (UseActivityRsp useActivityRsp : useActivityRsps) {
                    if (useActivityRsp.getProductLsit()!=null) {
                        for (OrderProductDetailDto useProduct : useActivityRsp.getProductLsit()) {
                            if (product.getProductId().equals(useProduct.getProductId()) && product.getSkuId().equals(useProduct.getSkuId())) {
                                product.setTotalAmount(product.getTotalAmount().subtract(product.getProfitAmount()));
                            }
                        }
                    }
                }
            }
            //在活动优惠基础上重算价格和总价
            product.setPrice(product.getTotalAmount().divide(product.getAppCount()));
        }
        result.setActivities(useActivityRsps);
        //在活动基础上计算优惠券
        List<UseCouponRsp> useCouponRsps = clientCouponService.combCouponRefProductDeal(innerReq);

        if (useCouponRsps!=null) {
            for (UseCouponRsp useCouponRsp : useCouponRsps) {
                //运费券
                if (CouponType.TRANSFERFARE.getDictValue().equals(useCouponRsp.getClientCoupon().getCouponType()) ||
                        CouponType.FREETRANSFERFARE.getDictValue().equals(useCouponRsp.getClientCoupon().getCouponType())) {
                    result.getTransferCoupons().add(useCouponRsp);
                } else {
                    result.getCommonCoupons().add(useCouponRsp);
                }
            }
        }

        return result;
    }

    /**
     * 在下订单时候使用活动及优惠券详情，并记录使用记录，处理优惠券状态
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCouponAndActivityRsp orderCouponAndActivityDeal(GetUseEnableCouponReq req){
        OrderCouponAndActivityRsp result = preOrderCouponAndActivityDeal(req);
        if (!result.getActivities().isEmpty())
            clientTakeInActivityService.takeInActivity(result.getActivities(),req.getOrderId());
        if (!result.getCommonCoupons().isEmpty())
            clientCouponService.takeInCoupon(req.getOrderId(),result.getCommonCoupons());
        if (!result.getTransferCoupons().isEmpty())
            clientCouponService.takeInCoupon(req.getOrderId(),result.getTransferCoupons());

        return result;
    }



}




