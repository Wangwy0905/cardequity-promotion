package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.CouponGetOrUseFreqRuleEntity;
import com.youyu.cardequity.promotion.dto.ShortCouponDetailDto;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
public interface CouponGetOrUseFreqRuleMapper extends YyMapper<CouponGetOrUseFreqRuleEntity> {

    /**
     * 根据指定券，获取到不满足领取频率规则的券及其阶梯
     * @param couponId 优惠券id
     * @param clientId 客户编号
     * @return
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    List<ShortCouponDetailDto> findClinetFreqForbidCouponDetailListById(@Param("clientId") String clientId,
                                                                        @Param("couponId") String couponId,
                                                                        @Param("stageId") String stageId);

}




