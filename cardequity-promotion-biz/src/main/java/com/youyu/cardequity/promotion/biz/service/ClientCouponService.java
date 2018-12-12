package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.biz.dal.entity.ClientCouponEntity;
import com.youyu.cardequity.promotion.dto.ObtainRspDto;
import com.youyu.cardequity.promotion.dto.ClientCouponDto;
import com.youyu.cardequity.promotion.vo.req.ClientObtainCouponReq;
import com.youyu.common.service.IService;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
public interface ClientCouponService extends IService<ClientCouponDto, ClientCouponEntity> {

    /**
     * 获取客户已领取的券含：
     * 已使用(status=1和2);
     * 未使用（status=0且有效期内）;
     * 已过期（status=0且未在有效期内）
     *
     * @return 返回已领取的券
     * @Param clientId:指定客户号，必填
     */
    List<ClientCouponDto>  FindClientCoupon(String clientId);

    /**
     * 领取优惠券
     *
     * @return 是否领取成功
     * @Param req:有参数clientId-客户号（必填），couponId-领取的券Id（必填）
     */
    ObtainRspDto ObtainCoupon(ClientObtainCouponReq req);
}




