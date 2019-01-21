package com.youyu.cardequity.promotion.biz.service;

import com.github.pagehelper.PageInfo;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityProfitEntity;
import com.youyu.cardequity.promotion.dto.other.ActivityDetailDto;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.BasePriceActivityRsp;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import com.youyu.common.api.PageData;
import com.youyu.common.service.IService;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
public interface ActivityProfitService extends IService<ActivityProfitDto, ActivityProfitEntity> {

    /**
     * 查找可参与的活动
     * @param req
     * @return
     */
    List<ActivityDetailDto> findEnableGetActivity(QryProfitCommonReq req);

    /**
     * 订单预处理使用活动
     * @param req
     * @return
     */
    List<UseActivityRsp> combActivityRefProductDeal(GetUseEnableCouponReq req);

    /**
     * 获取商品活动优惠价
     * @param req
     * @return
     * 1004258-徐长焕-20181226 新建
     */
    List<ActivityDetailDto> findActivityPrice(BaseProductReq req);

    /**
     * 批量添加活动
     * @param req
     * @return
     */
    CommonBoolDto<BatchActivityDetailDto> batchAddActivity(BatchActivityDetailDto req);

    /**
     * 批量编辑活动
     * @param req
     * @return
     */
    CommonBoolDto<BatchActivityDetailDto> batchEditActivity(BatchActivityDetailDto req);

    /**
     * 批量删除活动
     * @param req
     * @return
     */
    CommonBoolDto<Integer> batchDelActivity(BatchBaseActivityReq req);

    /**
     * 查找活动
     * @param req
     * @return
     */
    PageData<ActivityDetailDto> findActivityByCommon(BaseQryActivityReq req);

    /**
     * 查询活动汇总信息
     *
     * @param req 普通查询活动请求体
     * @return 活动汇总列表
     */
     List<GatherInfoRsp> findGatherActivityByCommon(BaseQryActivityReq req);

    /**
     * 查询商品的活动
     * @param req 商品基本信息
     * @return 活动详情列表
     */
    List<ActivityDetailDto> findProductAboutActivity(BaseProductReq req);

    /**
     * 查询指定活动
     * @param req 活动基本信息
     * @return 活动详情列表
     */
    ActivityDetailDto findActivityById(BaseActivityReq req);

    /**
     * 查找活动:支持id、商品编号、名称只要其中之一匹配即返回
     *
     * @param req 普通查询活动请求体
     * @return 活动详情列表列表
     */
    PageData<ActivityDetailDto> findActivityList(BaseQryActivityReq req);


    /**
     * 查询活动特价
     * @param req
     * @return
     */
    List<BasePriceActivityRsp> findActivityPriceValue(BaseProductReq req);


    /**
     * 获取商品有效的优惠价活动（排除了已达额度的活动）
     *
     * @param req
     * @return 开发日志
     * 1004258-徐长焕-20181226 新建
     */
    List<ActivityDetailDto> findValidActivityPrice(BaseProductReq req);

    /**
     * 【后台】查询所有活动列表
     * @param req
     * @return
     */
    List<ActivityDetailDto> findAllActivityByCommon(BaseQryActivityReq req);

    /**
     * 上架活动
     * @param req
     * @return
     */
    CommonBoolDto<ActivityProfitDto> upActivity(BaseActivityReq req);

    /**
     * 下架活动
     * @param req
     * @return
     */
    CommonBoolDto<ActivityProfitDto> downActivity(BaseActivityReq req);
}




