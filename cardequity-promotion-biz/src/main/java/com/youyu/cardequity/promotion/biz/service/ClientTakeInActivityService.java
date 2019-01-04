package com.youyu.cardequity.promotion.biz.service;

import com.youyu.cardequity.promotion.biz.dal.entity.ClientTakeInActivityEntity;
import com.youyu.cardequity.promotion.dto.ClientTakeInActivityDto;
import com.youyu.cardequity.promotion.vo.req.GetUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import com.youyu.common.service.IService;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
public interface ClientTakeInActivityService extends IService<ClientTakeInActivityDto, ClientTakeInActivityEntity> {

    /**
     * 参加活动
     * @param req
     * @return
     */
    List<UseActivityRsp> takeInActivityByOrder(GetUseEnableCouponReq req);

    /**
     * 参加活动 数据库层面操作
     * @param req
     * @return
     */
    List<ClientTakeInActivityEntity> takeInActivity(List<UseActivityRsp> req, String orderId);
}




