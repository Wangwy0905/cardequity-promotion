package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.spring.service.BatchService;
import com.youyu.cardequity.promotion.biz.constant.BusinessCode;
import com.youyu.cardequity.promotion.biz.service.ActivityProfitService;
import com.youyu.cardequity.promotion.biz.service.ClientTakeInActivityService;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.other.OrderProductDetailDto;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.CouponUseStatus;
import com.youyu.cardequity.promotion.vo.req.BaseOrderInPromotionReq;
import com.youyu.cardequity.promotion.vo.req.GetUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import com.youyu.common.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.youyu.cardequity.promotion.biz.dal.entity.ClientTakeInActivityEntity;
import com.youyu.cardequity.promotion.dto.ClientTakeInActivityDto;
import com.youyu.cardequity.promotion.biz.dal.dao.ClientTakeInActivityMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
@Service
public class ClientTakeInActivityServiceImpl extends AbstractService<String, ClientTakeInActivityDto, ClientTakeInActivityEntity, ClientTakeInActivityMapper> implements ClientTakeInActivityService {

    @Autowired
    private BatchService batchService;

    @Autowired
    private ClientTakeInActivityMapper clientTakeInActivityMapper;

    @Autowired
    private ActivityProfitService activityProfitService;

    /**
     * 通过选购信息进行参加活动处理
     * @param req
     * @return
     */
    @Override
    public List<UseActivityRsp> takeInActivityByOrder(GetUseEnableCouponReq req) {
        //计算参加获得信息
        List<UseActivityRsp> useActivityRspList= activityProfitService.combActivityRefProductDeal(req);
        //进行数据库处理
        takeInActivity(useActivityRspList,req.getOrderId(),req.getOperator());
        return useActivityRspList;
    }

    /**
     * 参加活动数据库层面处理:内部服务
     * @param req
     * @param orderId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ClientTakeInActivityEntity> takeInActivity(List<UseActivityRsp> req, String orderId,String operator) {
        List<ClientTakeInActivityEntity> entities = new ArrayList<>();
        if (req != null)
            for (UseActivityRsp useItem : req) {
                for (OrderProductDetailDto produt : useItem.getProductLsit()) {
                    ClientTakeInActivityEntity takeInActivityEntity = new ClientTakeInActivityEntity();
                    takeInActivityEntity.setActivityId(useItem.getActivity().getId());
                    takeInActivityEntity.setBusinCode(BusinessCode.USEACTIVITY);
                    takeInActivityEntity.setClientId(useItem.getClientId());
                    takeInActivityEntity.setId(CommonUtils.getUUID());
                    takeInActivityEntity.setIsEnable(CommonDict.IF_YES.getCode());
                    takeInActivityEntity.setOrderId(orderId);
                    takeInActivityEntity.setProductAmount(produt.getTotalAmount());
                    takeInActivityEntity.setProductCount(produt.getAppCount());
                    takeInActivityEntity.setProductId(produt.getProductId());
                    takeInActivityEntity.setProfitCount(produt.getProfitCount());
                    takeInActivityEntity.setProfitValue(produt.getProfitAmount());
                    takeInActivityEntity.setSkuId(produt.getSkuId());
                    takeInActivityEntity.setUpdateAuthor(operator);
                    takeInActivityEntity.setCreateAuthor(operator);
                    if (useItem.getStage() != null)
                        takeInActivityEntity.setStageId(useItem.getStage().getId());
                    takeInActivityEntity.setStatus(CouponUseStatus.USED.getDictValue());
                    entities.add(takeInActivityEntity);
                }
            }
        batchService.batchDispose(entities, ClientTakeInActivityMapper.class, "insert");
        return entities;
    }


    /**
     * 撤销使用优惠券数据库处理：内部服务
     *
     * @param req  订单情况
     * @return 是否处理成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<Integer> cancelTakeInActivity(BaseOrderInPromotionReq req) {
        CommonBoolDto<Integer> result = new CommonBoolDto(true);
        int i = clientTakeInActivityMapper.modRecoverByOrderinfo(req);
        result.setData(i);
        return result;
    }
}




