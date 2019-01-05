package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.common.spring.service.BatchService;
import com.youyu.cardequity.promotion.biz.service.CouponRefProductService;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.CommonBoolDto;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.vo.req.BaseCouponReq;
import com.youyu.cardequity.promotion.vo.req.BaseProductReq;
import com.youyu.cardequity.promotion.vo.req.BatchRefProductReq;
import com.youyu.common.api.Result;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponRefProductEntity;
import com.youyu.cardequity.promotion.dto.CouponRefProductDto;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponRefProductMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.youyu.cardequity.promotion.enums.ResultCode.PARAM_ERROR;


/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-20
 */
@Service
public class CouponRefProductServiceImpl extends AbstractService<String, CouponRefProductDto, CouponRefProductEntity, CouponRefProductMapper> implements CouponRefProductService {
    @Autowired
    private CouponRefProductMapper couponRefProductMapper;

    @Autowired
    private BatchService batchService;

    /**
     * 配置优惠的适用商品范围
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<Integer> batchAddCouponRefProduct(BatchRefProductReq req) {

        CommonBoolDto<Integer> dto = new CommonBoolDto(true);
        List<CouponRefProductEntity> productList = new ArrayList<>();
        //【配置适用商品】:传入了代表着需要更新配置
        if (req.getProductList() != null) {
            couponRefProductMapper.deleteByCouponId(req.getId());
            for (BaseProductReq item : req.getProductList()) {
                CouponRefProductEntity couponRefProductEntity = new CouponRefProductEntity();
                couponRefProductEntity.setCouponId(req.getId());
                couponRefProductEntity.setProductId(item.getProductId());
                couponRefProductEntity.setSkuId(item.getSkuId());
                couponRefProductEntity.setId(CommonUtils.getUUID());
                couponRefProductEntity.setIsEnable(CommonDict.IF_YES.getCode());
                productList.add(couponRefProductEntity);
            }

            batchService.batchDispose(productList, CouponRefProductMapper.class, "insert");
            dto.setData(req.getProductList().size());
        }
        return dto;
    }


    /**
     * 查询优惠券配置的商品信息
     *
     * @param req
     * @return
     */
    @Override
    public List<CouponRefProductDto> findJoinProductByCoupon(BaseCouponReq req) {
        if (req == null || CommonUtils.isEmptyorNull(req.getCouponId())) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定优惠券id 参数"));
        }
        List<CouponRefProductEntity> entities = couponRefProductMapper.findListByCommon(req.getCouponId(), "");
        return BeanPropertiesConverter.copyPropertiesOfList(entities, CouponRefProductDto.class);
    }
}




