package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.promotion.biz.service.CouponStageRuleService;
import com.youyu.common.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponStageRuleEntity;
import com.youyu.cardequity.promotion.dto.CouponStageRuleDto;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponStageRuleMapper;

import java.util.List;


/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
@Service
public class CouponStageRuleServiceImpl extends AbstractService<String, CouponStageRuleDto, CouponStageRuleEntity, CouponStageRuleMapper> implements CouponStageRuleService {

    @Autowired
    private CouponStageRuleMapper couponStageRuleMapper;
    /**
     * 1004259-徐长焕-20181210 新增
     * 功能：查询指定优惠券阶梯详细信息
     * @param couponId 优惠券id
     * @return
     */
    public List<CouponStageRuleEntity> findCouponDetail(String couponId) {
           return couponStageRuleMapper.findStageByCouponId(couponId);
    }

}




