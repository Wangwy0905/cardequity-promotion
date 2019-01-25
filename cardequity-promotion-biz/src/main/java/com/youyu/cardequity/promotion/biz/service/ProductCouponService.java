package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.other.CouponDetailDto;
import com.youyu.cardequity.promotion.dto.ProductCouponDto;
import com.youyu.cardequity.promotion.dto.other.ObtainCouponViewDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.CouponPageQryRsp;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
import com.youyu.common.service.IService;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
public interface ProductCouponService extends IService<ProductCouponDto, ProductCouponEntity> {

    /**
     * 1004259-徐长焕-20181210 新增
     * 功能：查询【有效期内、上架的】指定商品可领取的优惠券
     * @return
     */
     List<CouponDetailDto> findEnableGetCoupon(QryProfitCommonReq qryProfitCommonReq);



    /**
     * 添加优惠券
     *
     * @param req
     * @return
     */
    CommonBoolDto<CouponDetailDto> addCoupon(CouponDetailDto req);


    /**
     * 编辑优惠券
     *
     * @param req
     * @return
     */
    CommonBoolDto<CouponDetailDto>  editCoupon(CouponDetailDto req);

    /**
     * 批量删除优惠券
     *
     * @param req
     * @return
     */
     CommonBoolDto<Integer> batchDelCoupon( BatchBaseCouponReq req);

    /**
     * 上架优惠券
     * @param req
     * @return
     */
    CommonBoolDto<Integer> upCoupon(BatchBaseCouponReq req);

    /**
     * 下架
     * @param req
     * @return
     */
    CommonBoolDto<Integer> downCoupon(BatchBaseCouponReq req);

    /**
     * 查看商品对应优惠券列表
     *
     * @param req
     * @return
     */
    List<CouponDetailDto> findCouponListByProduct(BaseProductReq req);

    /**
     * 查询所有优惠券列表
     *
     * @param req
     * @return
     */
    CouponPageQryRsp findCouponListByCommon(BaseQryCouponReq req);

    /**
     * 模糊查询所有优惠券列表
     *
     * @param req
     * @return
     */
    CouponPageQryRsp findCouponList(BaseQryCouponReq req);

    /**
     * 查询指定优惠券详情
     * @param req
     * @return
     */
    CouponDetailDto findCouponById(BaseCouponReq req);

    /**
     * 查询优惠汇总信息
     *
     * @param req 普通优惠活动请求体
     * @return 优惠汇总列表
     */
    List<GatherInfoRsp> findGatherCouponByCommon(BaseQryCouponReq req);


    /**
     * 查看商品对应优惠券列表
     *
     * @param req
     * @return
     */
    List<CouponDetailDto> findCouponListByIds(List<String> req);

    /**
     * 拼装优惠券详情
     *
     * @param entities 优惠券主体列表
     * @return 优惠券详情：含限额、频率、子券信息
     */
    List<CouponDetailDto> combinationCoupon(List<ProductCouponEntity> entities);

    /**
     * 拼装优惠券详情数据
     * @param entity
     * @return
     */
    CouponDetailDto combinationCoupon(ProductCouponEntity entity);

    /**
     * 查询H5首页权益优惠券
     *
     * @param req 查询请求体
     * @return
     */
    List<ObtainCouponViewDto> findFirstPageVipCoupon(PageQryProfitCommonReq req) ;

    /**
     * 查询H5指定月可领优惠券
     *
     * @param req 查询请求体
     * @return
     */
     List<ObtainCouponViewDto> findEnableObtainCouponByMonth(FindEnableObtainCouponByMonthReq req);

}




