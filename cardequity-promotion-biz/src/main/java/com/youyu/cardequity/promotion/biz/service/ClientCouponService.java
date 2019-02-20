package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.biz.dal.entity.ClientCouponEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponQuotaRuleEntity;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.ClientCouponDto;
import com.youyu.cardequity.promotion.dto.other.ObtainCouponViewDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.FindClientCouponNumReq;
import com.youyu.cardequity.promotion.vo.rsp.FindCouponListByOrderDetailRsp;
import com.youyu.cardequity.promotion.vo.rsp.FullClientCouponRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseCouponRsp;
import com.youyu.common.api.Result;
import com.youyu.common.service.IService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
     * @Param req 指定客户号，必填
     */
    List<ObtainCouponViewDto>  findClientCoupon(QryComonClientCouponReq req);

    /**
     * 领取优惠券
     *
     * @return 是否领取成功
     * @Param req:有参数clientId-客户号（必填），couponId-领取的券Id（必填）
     */
    CommonBoolDto<ObtainCouponViewDto> obtainCoupon(ClientObtainCouponReq req);

    /**
     * 获取可用的优惠券:
     * 1.获取满足基本条件、使用频率、等条件
     * 2.没有校验使用门槛，使用门槛是需要和购物车选择商品列表进行计算
     *
     * @param req 本次操作商品详情，其中productList填充商品的详情
     * @return 开发日志
     * 1004246-徐长焕-20181213 新增
     */
    List<ObtainCouponViewDto> findEnableUseCoupon(GetUseEnableCouponReq req);

    /**
     * 按策略获取可用券的组合:含运费券
     * 1.根据订单或待下单商品列表校验了使用门槛
     * 2.根据冲突关系按策略计算能使用的券
     * 3.计算出每张券的适配使用的商品列表
     *
     * @param req 本次订单详情
     * @return 推荐使用券组合及应用对应商品详情
     */
    List<UseCouponRsp> combCouponRefProductDeal(GetUseEnableCouponReq req);

    /**
     * 根据指定的优惠券进行校验其适用情况，并变动其状态和使用记录
     * @param req
     * @return
     */
     List<UseCouponRsp> combCouponRefProductAndUse(GetUseEnableCouponReq req);

    /**
     * 使用优惠券数据库处理：内部服务
     *
     * @param orderId  订单编号
     * @param rsps 优惠券的使用情况
     * @return 是否处理成功
     */
    CommonBoolDto takeInCoupon(String orderId, String operator,List<UseCouponRsp> rsps);


    /**
     * 撤销使用优惠券数据库处理：内部服务
     *
     * @param req  订单情况
     * @return 是否处理成功
     */
     CommonBoolDto<Integer> cancelTakeInCoupon(BaseOrderInPromotionReq req);

    /**
     * 获取客户当前有效的券
     *
     * @param req 客户及商品信息
     * @return 返回已领取的券
     * 开发日志
     */
    List<ObtainCouponViewDto> findValidClientCouponForProduct(BaseClientProductReq req);

    /**
     * 获取订单可用和不可用的优惠券
     * @param req
     * @return
     */
    FindCouponListByOrderDetailRsp findCouponListByOrderDetail(OrderUseEnableCouponReq req);

    /**
     * 组合对象
     * @param clientCouponEnts
     * @return
     */
    List<ObtainCouponViewDto> combClientObtainCouponList(List<ClientCouponEntity> clientCouponEnts);

    /**
     * 组合对象
     * @param item
     * @return
     */
    ObtainCouponViewDto combClientObtainCouponOne(ClientCouponEntity item);

    /**
     * 组合详情列表
     * @param clientCouponEnts
     * @return
     */
    List<FullClientCouponRsp> combClientFullObtainCouponList(List<ClientCouponEntity> clientCouponEnts);

    /**
     * 组合详情
     * @param clientCoupon
     * @return
     */
    FullClientCouponRsp combClientFullObtainCouponOne(ClientCouponEntity clientCoupon);

    /**
     * 校验个人的优惠限额
     *
     * @param quota    优惠券额度信息
     * @param clientId 指定校验的客户
     * @return 开发日志
     * 1004258-徐长焕-20181213 新增
     */
     CommonBoolDto checkCouponPersonQuota(CouponQuotaRuleEntity quota,String clientId);

    /**
     * 校验所有客户的优惠限额
     *
     * @param quota    优惠券额度信息
     * @return 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    CommonBoolDto checkCouponAllQuota(CouponQuotaRuleEntity quota);

    /**
     * 查询客户领取券统计数量
     * @param req
     * @return
     */
    FindClientCouponNumReq findClientCouponNum(QryComonClientCouponReq req);

    /**
     * 【App】客户领取券变更new标识
     * @param req
     * @return
     */
    CommonBoolDto changeClientCouponNewFlag(BaseClientReq req);
}




