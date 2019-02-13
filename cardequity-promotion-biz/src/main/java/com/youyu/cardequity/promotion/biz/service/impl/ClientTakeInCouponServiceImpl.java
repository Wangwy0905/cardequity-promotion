package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.common.base.util.BeanPropertiesUtils;
import com.youyu.cardequity.promotion.biz.service.ActivityProfitService;
import com.youyu.cardequity.promotion.biz.service.ClientCouponService;
import com.youyu.cardequity.promotion.biz.service.ClientTakeInActivityService;
import com.youyu.cardequity.promotion.biz.service.ClientTakeInCouponService;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.other.OrderProductDetailDto;
import com.youyu.cardequity.promotion.enums.dict.CouponType;
import com.youyu.cardequity.promotion.vo.req.BaseOrderInPromotionReq;
import com.youyu.cardequity.promotion.vo.req.GetUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.req.OrderUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.req.PromotionDealReq;
import com.youyu.cardequity.promotion.vo.rsp.FindCouponListByOrderDetailRsp;
import com.youyu.cardequity.promotion.vo.rsp.OrderCouponAndActivityRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseCouponRsp;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.youyu.cardequity.promotion.biz.dal.entity.ClientTakeInCouponEntity;
import com.youyu.cardequity.promotion.dto.ClientTakeInCouponDto;
import com.youyu.cardequity.promotion.biz.dal.dao.ClientTakeInCouponMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.youyu.cardequity.promotion.enums.ResultCode.NET_ERROR;
import static com.youyu.cardequity.promotion.enums.ResultCode.PARAM_ERROR;


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

    @Autowired
    private ClientTakeInCouponMapper clientTakeInCouponMapper;

    /**
     * 在订单预生成时候使用活动及优惠券详情
     *
     * @param req
     * @return
     */
    @Override
    public OrderCouponAndActivityRsp preOrderCouponAndActivityDeal(GetUseEnableCouponReq req) {
        OrderCouponAndActivityRsp result = new OrderCouponAndActivityRsp();
        List<UseActivityRsp> useActivityRsps = activityProfitService.combActivityRefProductDeal(req);
        //先拷贝生成第二次请求的数据，第二次请求数据需要在原活动优惠上计算
        GetUseEnableCouponReq innerReq = BeanPropertiesConverter.copyProperties(req, GetUseEnableCouponReq.class);
        for (OrderProductDetailDto product : innerReq.getProductList()) {
            if (useActivityRsps != null) {
                for (UseActivityRsp useActivityRsp : useActivityRsps) {
                    if (useActivityRsp.getProductList() != null) {
                        for (OrderProductDetailDto useProduct : useActivityRsp.getProductList()) {
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

        if (useCouponRsps != null) {
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
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCouponAndActivityRsp orderCouponAndActivityDeal(PromotionDealReq req) {
        if (req == null)
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("参数为空"));
        if (req.getOrderPromotion() != null) {
            if (req.getOrderPromotion().getActivities() != null && !req.getOrderPromotion().getActivities().isEmpty())
                clientTakeInActivityService.takeInActivity(req.getOrderPromotion().getActivities(), req.getOrderId(),req.getOperator());
            if (req.getOrderPromotion().getCommonCoupons() != null && !req.getOrderPromotion().getCommonCoupons().isEmpty())
                clientCouponService.takeInCoupon(req.getOrderId(),req.getOperator(), req.getOrderPromotion().getCommonCoupons());
            if (req.getOrderPromotion().getTransferCoupons() != null && !req.getOrderPromotion().getTransferCoupons().isEmpty())
                clientCouponService.takeInCoupon(req.getOrderId(),req.getOperator(), req.getOrderPromotion().getTransferCoupons());
        }
        return req.getOrderPromotion();
    }

    /**
     * 【内部服务】取消订单预使用活动及优惠券详情
     * 考虑了幂等性
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto cancelOrderCouponAndActivityDeal(BaseOrderInPromotionReq req) {
        CommonBoolDto<Integer> result = new CommonBoolDto<>(true);
        result.setCode(NET_ERROR.getCode());
        if (req == null || CommonUtils.isEmptyorNull(req.getOrderId()))
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("参数为空"));

        //解冻客户的优惠券
        Integer i = clientCouponService.cancelTakeInCoupon(req).getData();

        //撤销优惠券使用记录
        i = i + clientTakeInCouponMapper.modRecoverByOrderinfo(req);

        //撤销活动使用记录
        i = i + clientTakeInActivityService.cancelTakeInActivity(req).getData();
        result.setData(i);
        return result;
    }


    /**
     * 获取订单适用的所有优惠券
     *
     * @param req
     * @return
     */
    @Override
    public FindCouponListByOrderDetailRsp orderDetailApplyAllCouponList(OrderUseEnableCouponReq req) {
        GetUseEnableCouponReq innerReq=new GetUseEnableCouponReq();
        BeanPropertiesUtils.copyProperties(req,innerReq);
        innerReq.setProductList(req.getProductList());

        List<UseActivityRsp> useActivityRsps = activityProfitService.combActivityRefProductDeal(innerReq);
        //先拷贝生成第二次请求的数据，第二次请求数据需要在原活动优惠上计算
        for (OrderProductDetailDto product : req.getProductList()) {
            if (useActivityRsps != null) {
                for (UseActivityRsp useActivityRsp : useActivityRsps) {
                    if (useActivityRsp.getProductList() != null) {
                        for (OrderProductDetailDto useProduct : useActivityRsp.getProductList()) {
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
        return clientCouponService.findCouponListByOrderDetail(req);

    }

}




