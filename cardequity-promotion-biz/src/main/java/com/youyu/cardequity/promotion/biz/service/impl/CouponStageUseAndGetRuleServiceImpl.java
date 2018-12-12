package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.common.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponStageUseAndGetRuleEntity;
import com.youyu.cardequity.promotion.dto.CouponStageUseAndGetRuleDto;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponStageUseAndGetRuleMapper;
import com.youyu.cardequity.promotion.biz.service.CouponStageUseAndGetRuleService;

import java.util.List;


/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
@Service
public class CouponStageUseAndGetRuleServiceImpl extends AbstractService<String, CouponStageUseAndGetRuleDto, CouponStageUseAndGetRuleEntity, CouponStageUseAndGetRuleMapper> implements CouponStageUseAndGetRuleService {

    @Autowired
    private  CouponStageUseAndGetRuleMapper couponStageUseAndGetRuleMapper;
    /**
     * 1004259-徐长焕-20181210 新增
     * 功能：查询指定优惠券阶梯详细信息
     * @param couponId 优惠券id
     * @return
     */
    public List<CouponStageUseAndGetRuleEntity> findCouponDetail(String couponId) {
           return couponStageUseAndGetRuleMapper.findStageByCouponId(couponId);
    }

}




