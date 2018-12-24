package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponStageRuleMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponStageRuleEntity;
import com.youyu.cardequity.promotion.biz.service.ProductCouponService;
import com.youyu.cardequity.promotion.dto.CouponStageRuleDto;
import com.youyu.cardequity.promotion.dto.ShortCouponDetailDto;
import com.youyu.cardequity.promotion.vo.req.QryProfitCommonReq;
import com.youyu.cardequity.promotion.vo.rsp.CouponDefineRsp;
import com.youyu.common.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.dto.ProductCouponDto;
import com.youyu.cardequity.promotion.biz.dal.dao.ProductCouponMapper;

import java.util.ArrayList;
import java.util.List;


/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 * 开发日志
 * V1.0-V1 1004244-徐长焕-20181207 新建代码，findEnableGetCoupon：获取客户可领取的券
 */
@Service
public class ProductCouponServiceImpl extends AbstractService<String, ProductCouponDto, ProductCouponEntity, ProductCouponMapper> implements ProductCouponService {

    @Autowired
    private ProductCouponMapper productCouponMapper;

    @Autowired
    private CouponStageRuleMapper couponStageRuleMapper;
    /**
     * 1004259-徐长焕-20181210 新增
     * 功能：查询指定商品可领取的优惠券
     * @return
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    public List<CouponDefineRsp> findEnableGetCoupon(QryProfitCommonReq qryProfitCommonReq) {


        //获取满足条件的优惠券：1.满足对应商品属性(指定商品或组)、客户属性(指定客户类型)、订单属性(指定客户类型)；2.满足券额度(券每日领取池，券总金额池，券总量池)
        List<ProductCouponEntity> productCouponlist= productCouponMapper.findEnableGetCouponListByCommon(qryProfitCommonReq.getProductId(), qryProfitCommonReq.getEntrustWay(), qryProfitCommonReq.getClinetType());

        List<CouponDefineRsp> CouponDefinelist=new ArrayList<CouponDefineRsp>();

        //根据客户对上述券领取情况，以及该券领取频率限制进行排除
        for (ProductCouponEntity item : productCouponlist){
            List<CouponStageRuleEntity> stageList= couponStageRuleMapper.findStageByCouponId(item.getId());
            //做保护，后面直接通过素组长度判断
            if (stageList==null){
                stageList=new ArrayList<>(0);
            }

            List<ShortCouponDetailDto> shortStageList= productCouponMapper.findClinetFreqForbidCouponDetailListById(qryProfitCommonReq.getClinetId(),item.getId(),"");
            //做保护，后面直接通过素组长度判断
            if (shortStageList==null){
                shortStageList=new ArrayList<>(0);
            }

            //有阶梯的券进行排除
            if (stageList.size()>0  && shortStageList.size()>0){

                List<CouponStageRuleDto> couponStageList=new ArrayList<CouponStageRuleDto>();

                for (CouponStageRuleEntity stageItem:stageList) {
                    //排除用户领取频率限制的
                    boolean isExsit=false;
                    for (ShortCouponDetailDto shortItem:shortStageList) {
                        if (stageItem.getUuid().equals(shortItem.getStageId())) {
                            isExsit = true;
                            shortStageList.remove(stageItem);//线程安全
                            break;
                        }
                    }
                    //该阶梯没有领取频率限制则可领取
                    if (!isExsit)
                        couponStageList.add(BeanPropertiesConverter.copyProperties(stageItem, CouponStageRuleDto.class));
                }
                //有阶梯的优惠券，如果有0个阶梯能领取该券可领取，否则该券不能领取
                if (couponStageList.size()>0) {
                    CouponDefineRsp rsp=BeanPropertiesConverter.copyProperties(item, CouponDefineRsp.class);
                    rsp.setCouponStageDtoList(couponStageList);
                    CouponDefinelist.add(rsp);
                }
            }
            //没有阶梯但是也没有任何领取限制的全部可领取
            else if (stageList.size()>0 && shortStageList.size()<=0){
                List<CouponStageRuleDto> couponStageList=BeanPropertiesConverter.copyPropertiesOfList(stageList, CouponStageRuleDto.class);
                CouponDefineRsp rsp=BeanPropertiesConverter.copyProperties(item, CouponDefineRsp.class);
                rsp.setCouponStageDtoList(couponStageList);
                CouponDefinelist.add(rsp);
            }
            //没有阶梯，同时也没有任何领取限制的全部可领取
            else if (stageList.size()<=0 && shortStageList.size()<=0){
                CouponDefineRsp rsp=BeanPropertiesConverter.copyProperties(item, CouponDefineRsp.class);
                CouponDefinelist.add(rsp);
            }
            else{
                boolean isExsit=false;
                //正常数据没有阶梯的券，此处只会有一条在shortStageList中，循环是为了容错
                for (ShortCouponDetailDto shortItem:shortStageList) {
                    if (item.getUuid().equals(shortItem.getCouponId()) && shortItem.getStageId()==null) {
                        isExsit = true;
                        break;
                    }
                }
                //该券没有领取频率限制则可领取
                if (!isExsit){
                    CouponDefineRsp rsp=BeanPropertiesConverter.copyProperties(item, CouponDefineRsp.class);
                    CouponDefinelist.add(rsp);
                }

            }

        }

        return CouponDefinelist;

    }

}




