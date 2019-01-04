package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.dto.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.CouponDetailDto;
import com.youyu.cardequity.promotion.dto.CouponViewDto;
import com.youyu.cardequity.promotion.dto.ProductCouponDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.CouponDefineRsp;
import com.youyu.common.service.IService;
import org.springframework.web.bind.annotation.RequestBody;

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
     * 功能：查询指定商品可领取的优惠券
     * @return
     */
     List<CouponDefineRsp> findEnableGetCoupon(QryProfitCommonReq qryProfitCommonReq);



    /**
     * 添加优惠券
     *
     * @param req
     * @return
     */
    CommonBoolDto<CouponDetailDto> addCoupon(@RequestBody CouponDetailDto req);


    /**
     * 编辑优惠券
     *
     * @param req
     * @return
     */
    CommonBoolDto<CouponDetailDto>  editCoupon(@RequestBody CouponDetailDto req);

    /**
     * 批量删除优惠券
     *
     * @param req
     * @return
     */
     CommonBoolDto<Integer> batchDelCoupon(@RequestBody BatchBaseCouponReq req);

    /**
     * 开始发放
     * @param req
     * @return
     */
    CommonBoolDto<ProductCouponDto> startGetCoupon(@RequestBody BaseCouponReq req);

    /**
     * 结束发放
     * @param req
     * @return
     */
    CommonBoolDto<ProductCouponDto> stopGetCoupon(@RequestBody BaseCouponReq req);

    /**
     * 查看商品对应优惠券列表
     *
     * @param req
     * @return
     */
    List<CouponDetailDto> findCouponListByProduct(@RequestBody BaseProductReq req);

    /**
     * 查询所有优惠券列表
     *
     * @param req
     * @return
     */
    List<CouponDetailDto> findCouponListByCommon(@RequestBody BaseQryCouponReq req);

}




