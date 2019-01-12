package com.youyu.cardequity.promotion.biz.dal.dao;

import com.youyu.cardequity.promotion.biz.dal.entity.ActivityQuotaRuleEntity;
import com.youyu.cardequity.promotion.vo.req.BaseActivityReq;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
public interface ActivityQuotaRuleMapper extends YyMapper<ActivityQuotaRuleEntity> {

    /**
     * 查询活动额度详情
     * @param id 活动id
     * @return
     */
    ActivityQuotaRuleEntity findActivityQuotaRuleById(@Param("id") String id);

    /**
     * 逻辑删除
     * @param baseActivity
     * @return
     */
    int logicDelById(BaseActivityReq baseActivity);
}




