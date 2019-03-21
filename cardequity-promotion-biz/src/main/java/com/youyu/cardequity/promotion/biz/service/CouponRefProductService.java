package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.biz.dal.entity.CouponRefProductEntity;
import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.CouponRefProductDto;
import com.youyu.cardequity.promotion.vo.req.BaseCouponReq;
import com.youyu.cardequity.promotion.vo.req.BatchBaseProductReq;
import com.youyu.cardequity.promotion.vo.req.BatchRefProductReq;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
import com.youyu.common.service.IService;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-20
 */
public interface CouponRefProductService extends IService<CouponRefProductDto, CouponRefProductEntity> {
    /**
     * 配置优惠的适用商品范围
     * @param req
     * @return
     */
     CommonBoolDto<Integer> batchAddCouponRefProduct(BatchRefProductReq req);

    /**
     * 删除不需要的适用商品
     * @Param req
     * @return
     */
     CommonBoolDto<Integer> batchDeleteCouponRefProduct(BatchRefProductReq req);

    /**
     * 查询优惠券配置的商品信息
     * @param req
     * @return
     */
    List<CouponRefProductDto> findJoinProductByCoupon(BaseCouponReq req);


    /**
     * 查询商品相关活动数量
     * @param req 商品列表
     * @return 商品对应活动数量
     */
    List<GatherInfoRsp> findProductAboutCouponNum(BatchBaseProductReq req);


}




