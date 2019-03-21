package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.common.spring.service.BatchService;
import com.youyu.cardequity.promotion.biz.dal.dao.ProductCouponMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.biz.service.CouponRefProductService;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
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

    @Autowired
    private ProductCouponMapper productCouponMapper;

    /**
     * 配置优惠的适用商品范围
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<Integer> batchAddCouponRefProduct(BatchRefProductReq req) {
        if (req == null || CommonUtils.isEmptyorNull(req.getId())) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定优惠券id 参数"));
        }


        CommonBoolDto<Integer> dto = new CommonBoolDto<>(true);
        //优惠活动定义适用商品表
        List<CouponRefProductEntity> productList = new ArrayList<>();
        //【配置适用商品】:传入了代表着需要更新配置
        if (req.getProductList() != null) {
            //全量时才允许删除    改动
              //if ("1" != req.getOperatFlag())
              //  couponRefProductMapper.deleteByCouponId(req.getId());
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
     * 删除取消配置优惠的适用商品
     * @param req
     * @return
     */
    //改动代码
    @Override
    public  CommonBoolDto<Integer> batchDeleteCouponRefProduct(BatchRefProductReq req){
        if (req == null || CommonUtils.isEmptyorNull(req.getId())) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定优惠券id 参数"));
        }
        CommonBoolDto<Integer> delDto =new CommonBoolDto<>(true);
        //优惠活动定义适用商品表
        List<CouponRefProductEntity> delProductList = new ArrayList<>();
        //【配置适用商品】:传入了代表着需要更新配置
        if(req.getDelProductList() !=null){
            for(BaseProductReq item :req.getDelProductList()){
                CouponRefProductEntity couponRefProductEntity = new CouponRefProductEntity();
                couponRefProductEntity.setCouponId(req.getId());
                couponRefProductEntity.setProductId(item.getProductId());
                couponRefProductEntity.setSkuId(item.getSkuId());
                //couponRefProductEntity.setId(CommonUtils.getUUID());
                // couponRefProductEntity.setIsEnable(CommonDict.IF_YES.getCode());
                delProductList.add(couponRefProductEntity);
            }
            batchService.batchDispose(delProductList,CouponRefProductMapper.class,"delete");
            delDto.setData(req.getDelProductList().size());
        }
        return delDto;
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

    /**
     * 查询商品对应的优惠券数量
     *
     * @param req 商品列表
     * @return 商品对应优惠券数量
     */
    @Override
    public List<GatherInfoRsp> findProductAboutCouponNum(BatchBaseProductReq req) {
        if (req == null || req.getProductList() == null || req.getProductList().isEmpty())
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定商品"));

        List<GatherInfoRsp> result = productCouponMapper.findCouponNumByProducts(req);

        List<ProductCouponEntity> entities = productCouponMapper.findUnlimitedProductCoupon();
        if (!entities.isEmpty()) {
            for (BaseProductReq item : req.getProductList()) {
                boolean isExist = false;
                String key = item.getProductId() + (CommonUtils.isEmptyorNull(item.getSkuId()) ? "|EMPTY" : "|" + item.getSkuId());
                for (GatherInfoRsp gather : result) {
                    //如果有统计数量的加上无产品限制的数量
                    if (key.equals(gather.getGatherItem())) {
                        gather.setGatherValue(gather.getGatherValue() + entities.size());
                        isExist = true;
                        break;
                    }
                }
                //如果找到统计数据，该没有产品限制的数量即为其统计数量
                if (!isExist) {
                    GatherInfoRsp rsp = new GatherInfoRsp();
                    rsp.setGatherItem(key);
                    rsp.setGatherValue(entities.size());
                    result.add(rsp);
                }
            }
        }

        return result;
    }


}




