package com.youyu.cardequity.promotion.biz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.youyu.cardequity.common.base.bean.CustomHandler;
import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.common.base.util.BeanPropertiesUtils;
import com.youyu.cardequity.common.base.util.StringUtil;
import com.youyu.cardequity.common.spring.service.BatchService;
import com.youyu.cardequity.promotion.biz.dal.dao.*;
import com.youyu.cardequity.promotion.biz.dal.entity.*;
import com.youyu.cardequity.promotion.biz.service.ActivityProfitService;
import com.youyu.cardequity.promotion.biz.service.ActivityRefProductService;
import com.youyu.cardequity.promotion.biz.strategy.activity.ActivityStrategy;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.biz.utils.SnowflakeIdWorker;
import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.cardequity.promotion.dto.*;
import com.youyu.cardequity.promotion.dto.other.*;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.*;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.BasePriceActivityRsp;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import com.youyu.common.api.PageData;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.youyu.cardequity.common.base.util.PaginationUtils.convert;
import static com.youyu.cardequity.promotion.enums.ResultCode.*;


/**
 * 代码生成器
 * <p>
 * 开发日志
 * V1.0-V1 1004244-徐长焕-20181207 新建，实现可参与活动列表
 */
@Slf4j
@Service
public class ActivityProfitServiceImpl extends AbstractService<String, ActivityProfitDto, ActivityProfitEntity, ActivityProfitMapper> implements ActivityProfitService {

    @Autowired
    private ActivityProfitMapper activityProfitMapper;

    @Autowired
    private ActivityStageCouponMapper activityStageCouponMapper;

    @Autowired
    private ActivityRefProductMapper activityRefProductMapper;

    @Autowired
    private CouponAndActivityLabelMapper couponAndActivityLabelMapper;

    @Autowired
    private ActivityRefProductService activityRefProductService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private ActivityQuotaRuleMapper activityQuotaRuleMapper;

    /**
     * 获取可参与的活动列表:
     * 查询满足客户条件的，有效期内
     *
     * @param req 查询优惠活动请求体
     * @return 活动详情列表
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    @Override
    public List<ActivityDetailDto> findEnableGetActivity(QryProfitCommonReq req) {

        List<ActivityDetailDto> result = new ArrayList<>();

        //获取普通活动列表
        if (CommonConstant.EXCLUSIONFLAG_ALL.equals(req.getExclusionFlag())) {
            List<ActivityProfitEntity> activityList = activityProfitMapper.findEnableGetCommonFirstActivity(req.getProductId(),
                    req.getClientType(),
                    req.getEntrustWay());
            //将其使用门槛阶梯与活动主信息组装后返回
            result.addAll(combinationActivity(activityList));
        } else {
            List<ActivityProfitEntity> activityList = activityProfitMapper.findEnableGetCommonActivity(req.getProductId(),
                    req.getClientType(),
                    req.getEntrustWay());
            //将其使用门槛阶梯与活动主信息组装后返回
            result.addAll(combinationActivity(activityList));
        }


        return result;
    }

    /**
     * 订单预生成时使用活动详情
     *
     * @param req 请求体
     * @return 使用详情
     */
    @Override
    public List<UseActivityRsp> combActivityRefProductDeal(GetUseEnableCouponReq req) {
        //定义返回结果
        List<UseActivityRsp> rsps = new ArrayList<>();

        //空订单或者没有可用活动直接返回
        if (req.getProductList() == null ||
                req.getProductList().isEmpty()) {
            return rsps;
        }

        CommonBoolDto boolDto = new CommonBoolDto(true);
        List<ActivityProfitEntity> activityList = new ArrayList<>();
        //获取所有可以参与的活动：按初始条件，有效日期内
        if (req.getActivityList() != null && !req.getActivityList().isEmpty()) {

            BatchBaseActivityReq innerReq = new BatchBaseActivityReq();
            innerReq.setBaseActivityList(req.getActivityList());
            activityList = activityProfitMapper.findActivityByIds(innerReq);

        } else {

            //获取普通活动列表
            activityList = activityProfitMapper.findEnableGetCommonActivity("", req.getClientType(), req.getEntrustWay());
        }


        //团购活动优先处理
        // TODO: 2018/12/25

        //循环活动进行计算优惠金额，优先顺序为：任选->折扣->满减
        for (ActivityProfitEntity item : activityList) {
            //校验基本信息：有效期的、商品属性、订单属性、支付属性、上架状态
            boolDto = checkActivityBase(item, req);
            if (!boolDto.getSuccess()) {
                log.info("该活动不适用于，活动编号{}，原因:{}", item.getId(), boolDto.getDesc());
                continue;
            }

            //根据策略得到该活动是否满足门槛，返回满足活动适用信息
            String key = ActivityStrategy.class.getSimpleName() + item.getActivityCouponType();
            log.info("获取活动策略key:{}", key);
            ActivityStrategy executor = (ActivityStrategy) CustomHandler.getBeanByName(key);
            UseActivityRsp rsp = executor.applyActivity(item, req.getProductList());
            if (rsp != null) {
                rsp.setClientId(req.getClientId());
                rsps.add(rsp);
            }
        }

        //使用
        return rsps;
    }

    /**
     * 新增活动
     *
     * @param req 批量活动详情
     * @return 批量活动详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<BatchActivityDetailDto> batchAddActivity(BatchActivityDetailDto req) {
        //权益中心标志为3，活动表标识为1，ProductCoupon表标识为2
        SnowflakeIdWorker stageWorker = new SnowflakeIdWorker(3, 1);
        CommonBoolDto<BatchActivityDetailDto> result = new CommonBoolDto<>(false);
        result.setCode(PARAM_ERROR.getCode());
        if (req == null || req.getActivityDetailList() == null || req.getActivityDetailList().isEmpty()) {
            result.setDesc("参数未指定");
            return result;
        }

        List<ActivityProfitEntity> activityList = new ArrayList<>();
        List<ActivityQuotaRuleEntity> quotaList = new ArrayList<>();
        List<ActivityStageCouponEntity> stageList = new ArrayList<>();
        List<ActivityRefProductEntity> refProductList = new ArrayList<>();
        for (ActivityDetailDto item : req.getActivityDetailList()) {
            //1.检查参数，并设置默认参数
            ActivityProfitDto profit = item.getActivityProfit();
            if (profit == null)
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定活动基本信息"));

            //活动暂时不分级
            if (CommonUtils.isEmptyorNull(profit.getActivityLevel()))
                profit.setActivityLevel(CouponActivityLevel.GLOBAL.getDictValue());
            //短描字段不为空，保护为活动名
            if (CommonUtils.isEmptyorNull(profit.getActivityShortDesc()))
                profit.setActivityShortDesc(profit.getActivityName());

            if (profit.getAllowUseBeginDate().isAfter(profit.getAllowUseEndDate()))
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("开始时间不能晚于结束时间"));

            //默认是自定义适用商品
            if (CommonUtils.isEmptyorNull(profit.getApplyProductFlag()))
                profit.setApplyProductFlag(ApplyProductFlag.APPOINTPRODUCT.getDictValue());
            //如果指定商品集合，默认为自定义配置
            if (item.getProductList() != null && !item.getProductList().isEmpty()) {
                profit.setApplyProductFlag(ApplyProductFlag.APPOINTPRODUCT.getDictValue());
            }


            //检查适用商品是否重复在不同活动配置中
            CommonBoolDto<List<ActivityRefProductEntity>> boolDto = activityRefProductService.checkProductReUse(item.getProductList(), profit);
            if (!boolDto.getSuccess()) {
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc(boolDto.getDesc()));
            }

            if (!CommonUtils.isGtZeroDecimal(profit.getProfitValue())) {
                //保护为阶梯中的优惠值
                if (item.getStageList() != null && item.getStageList().size() == 1) {
                    profit.setProfitValue(item.getStageList().get(0).getProfitValue());
                }
                if (!CommonUtils.isGtZeroDecimal(profit.getProfitValue()))
                    throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定对应的优惠值"));

            } else {
                //阶梯中没有的值保护为基本信息中的
                if (item.getStageList() != null && item.getStageList().size() == 1) {
                    if (!CommonUtils.isGtZeroDecimal(item.getStageList().get(0).getProfitValue()))
                        item.getStageList().get(0).setProfitValue(profit.getProfitValue());
                }
            }

            //校验特价活动必须指定商品
            if (ActivityCouponType.PRICE.getDictValue().equals(profit.getActivityCouponType())) {
                if (item.getProductList() == null || item.getProductList().isEmpty() || CommonUtils.isEmptyorNull(item.getProductList().get(0).getProductId())) {
                    throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("特价活动必须指定商品，活动编号" + profit.getId()));
                }
                if (item.getProductList().size() > 1) {
                    throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("特价活动只能指定一个商品，活动编号" + profit.getId()));
                }

                profit.setApplyProductFlag(ApplyProductFlag.APPOINTPRODUCT.getDictValue());
                if (CommonUtils.isEmptyorNull(item.getProductList().get(0).getSkuId())) {
                    profit.setStatus(CouponStatus.NO.getDictValue());
                    //需要检查该商品是否已经存在下架的一个活动
                    List<ActivityProfitEntity> entities = activityProfitMapper.findPriceTempActivityByProductId(item.getProductList().get(0).getProductId(), item.getProductList().get(0).getSkuId(), "");
                    if (!entities.isEmpty())
                        throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc(String.format("该商品已存在其他活动，活动数量%s，商品编号%s，sku编号%s", entities.size(), item.getProductList().get(0).getProductId(), item.getProductList().get(0).getSkuId())));
                }
            }

            //2.处理基本信息
            profit.setId(stageWorker.nextId() + "");

            ActivityProfitEntity profitEntity = BeanPropertiesUtils.copyProperties(profit, ActivityProfitEntity.class);
            if (profit.getLabelDto() != null) {
                profitEntity.setActivityLable(profit.getLabelDto().getId());
            }
            profitEntity.setIsEnable(CommonDict.IF_YES.getCode());
            profitEntity.setUpdateAuthor(req.getOperator());
            profitEntity.setCreateAuthor(req.getOperator());
            activityList.add(profitEntity);

            //3.处理限额信息
            ActivityQuotaRuleDto quotaRule = item.getActivityQuotaRule();
            if (quotaRule != null) {
                //将编号传出
                quotaRule.setActivityId(profitEntity.getId());
                ActivityQuotaRuleEntity quotaRuleEntity = BeanPropertiesUtils.copyProperties(quotaRule, ActivityQuotaRuleEntity.class);
                //将操作者传入，一般存ip
                quotaRuleEntity.setUpdateAuthor(req.getOperator());
                quotaRuleEntity.setCreateAuthor(req.getOperator());
                quotaRuleEntity.setIsEnable(CommonDict.IF_YES.getCode());
                quotaList.add(quotaRuleEntity);
            }

            //4.处理阶梯信息
            if (item.getStageList() != null) {
                for (ActivityStageCouponDto stage : item.getStageList()) {

                    if (ActivityCouponType.DISCOUNT.getDictValue().equals(profit.getActivityCouponType())) {
                        if (BigDecimal.ONE.compareTo(stage.getProfitValue()) <= 0 ||
                                !CommonUtils.isGtZeroDecimal(stage.getProfitValue())) {
                            result.setDesc("折扣优惠券优惠折扣不能高于等于1且不能低于等于0，参数值" + stage.getProfitValue());
                            return result;
                        }
                        profit.setProfitValue(stage.getProfitValue());
                    }

                    stage.setActivityId(profitEntity.getId());
                    stage.setId(CommonUtils.getUUID());

                    if (CommonUtils.isEmptyorNull(stage.getActivityShortDesc()))
                        stage.setActivityShortDesc(profitEntity.getActivityShortDesc());

                    if (!CommonUtils.isGtZeroDecimal(stage.getEndValue())) {
                        stage.setEndValue(CommonConstant.IGNOREVALUE);
                    }

                    if (CommonUtils.isEmptyorNull(stage.getTriggerByType())) {
                        stage.setTriggerByType(TriggerByType.NUMBER.getDictValue());
                    }
                    if (!CommonUtils.isGtZeroDecimal(stage.getProfitValue()))
                        throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定对应的优惠值"));

                    ActivityStageCouponEntity stageCouponEntity = BeanPropertiesUtils.copyProperties(stage, ActivityStageCouponEntity.class);
                    stageCouponEntity.setUpdateAuthor(req.getOperator());
                    stageCouponEntity.setCreateAuthor(req.getOperator());
                    stageCouponEntity.setIsEnable(CommonDict.IF_YES.getCode());
                    stageList.add(stageCouponEntity);
                }
            }

            //5.配置适用商品
            if (item.getProductList() != null) {
                for (BaseProductReq product : item.getProductList()) {
                    ActivityRefProductEntity refProductEntity = BeanPropertiesUtils.copyProperties(product, ActivityRefProductEntity.class);
                    refProductEntity.setActivityId(profitEntity.getId());
                    refProductEntity.setCreateAuthor(req.getOperator());
                    refProductEntity.setUpdateAuthor(req.getOperator());
                    refProductEntity.setId(CommonUtils.getUUID());
                    refProductEntity.setIsEnable(CommonDict.IF_YES.getCode());
                    refProductList.add(refProductEntity);
                }
            }
        }

        //数据库操作
        if (!activityList.isEmpty())
            batchService.batchDispose(activityList, ActivityProfitMapper.class, "insert");

        if (!quotaList.isEmpty())
            batchService.batchDispose(quotaList, ActivityQuotaRuleMapper.class, "insert");

        if (!stageList.isEmpty())
            batchService.batchDispose(stageList, ActivityStageCouponMapper.class, "insert");

        if (!refProductList.isEmpty())
            batchService.batchDispose(refProductList, ActivityRefProductMapper.class, "insert");

        result.setSuccess(true);
        result.setCode(NET_ERROR.getCode());
        if (req.getActivityDetailList().size() < 3)
            result.setData(req);
        return result;
    }

    /**
     * 批量编辑活动
     *
     * @param req 活动详情列表请求体
     * @return 编辑后活动详情列表请求体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<BatchActivityDetailDto> batchEditActivity(BatchActivityDetailDto req) {
        CommonBoolDto<BatchActivityDetailDto> result = new CommonBoolDto<>(false);
        result.setCode(PARAM_ERROR.getCode());
        if (req == null || req.getActivityDetailList() == null || req.getActivityDetailList().isEmpty()) {
            result.setDesc("参数未指定");
            return result;
        }

        List<ActivityProfitEntity> activityList = new ArrayList<>();
        List<ActivityRefProductEntity> refProductList = new ArrayList<>();
        List<String> activityIds = new ArrayList<>();

        List<String> configProductActivityIds = new ArrayList<>();
        List<ActivityStageCouponEntity> modStageList = new ArrayList<>();
        List<ActivityStageCouponEntity> addStageList = new ArrayList<>();
        List<ActivityQuotaRuleEntity> addQuotaList = new ArrayList<>();
        List<ActivityQuotaRuleEntity> modQuotaList = new ArrayList<>();
        for (ActivityDetailDto item : req.getActivityDetailList()) {
            //1.检查参数，并设置默认参数
            ActivityProfitDto profit = item.getActivityProfit();
            if (profit == null)
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定活动基本信息"));

            if (profit.getAllowUseBeginDate().isAfter(profit.getAllowUseEndDate()))
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("开始时间不能晚于结束时间"));

            //活动暂时不分级
            if (CommonUtils.isEmptyorNull(profit.getActivityLevel()))
                profit.setActivityLevel(CouponActivityLevel.GLOBAL.getDictValue());
            //短描字段不为空，保护为活动名
            if (CommonUtils.isEmptyorNull(profit.getActivityShortDesc()))
                profit.setActivityShortDesc(profit.getActivityName());

            //默认是自定义适用商品
            if (CommonUtils.isEmptyorNull(profit.getApplyProductFlag()))
                profit.setApplyProductFlag(ApplyProductFlag.APPOINTPRODUCT.getDictValue());
            //如果指定商品集合，默认为自定义配置
            if (item.getProductList() != null && !item.getProductList().isEmpty()) {
                profit.setApplyProductFlag(ApplyProductFlag.APPOINTPRODUCT.getDictValue());
            }


            //检查适用商品是否重复在不同活动配置中
            //非特价上架商品时需要校验
            //if (!ActivityCouponType.PRICE.getDictValue().equals(item.getActivityProfit().getActivityCouponType()) &&
            //        (item.getProductList() == null || item.getProductList().isEmpty())) {
            //        List<BaseProductReq> productReqs = activityRefProductMapper.findForbidCifgProductByActivity(item.getActivityProfit());
            //        if (!productReqs.isEmpty())
            //            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("编辑活动有效日期区间时导致原配置的商品在某一时间点同时存在于两个活动中"));
            //} else {

            List<BaseProductReq> checkproductList = item.getProductList();
            if (item.getProductList() == null) {
                checkproductList = new ArrayList<>();
                //如果适用日期区间有变化时需要再次检查适用商品的
                List<ActivityRefProductEntity> byActivityId = activityRefProductMapper.findByActivityId(profit.getId());
                for (ActivityRefProductEntity refItem : byActivityId) {
                    BaseProductReq refreq = new BaseProductReq();
                    refreq.setProductId(refItem.getProductId());
                    refreq.setSkuId(refItem.getSkuId());
                    checkproductList.add(refreq);
                }
            }
            CommonBoolDto<List<ActivityRefProductEntity>> boolDto = activityRefProductService.checkProductReUse(checkproductList, profit);
            if (!boolDto.getSuccess()) {
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc(boolDto.getDesc()));
            }
            //}

            if (!CommonUtils.isGtZeroDecimal(profit.getProfitValue())) {
                //保护为阶梯中的优惠值
                if (item.getStageList() != null && item.getStageList().size() == 1) {
                    profit.setProfitValue(item.getStageList().get(0).getProfitValue());
                }
                if (!CommonUtils.isGtZeroDecimal(profit.getProfitValue()))
                    throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定对应的优惠值"));

            } else {
                //阶梯中没有的值保护为基本信息中的
                if (item.getStageList() != null && item.getStageList().size() == 1) {
                    if (!CommonUtils.isGtZeroDecimal(item.getStageList().get(0).getProfitValue()))
                        item.getStageList().get(0).setProfitValue(profit.getProfitValue());
                }
            }

            //校验特价活动必须指定商品
            if (ActivityCouponType.PRICE.getDictValue().equals(profit.getActivityCouponType())) {
                if (item.getProductList() == null || item.getProductList().isEmpty() || CommonUtils.isEmptyorNull(item.getProductList().get(0).getProductId())) {
                    throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("特价活动必须指定商品，活动编号" + profit.getId()));
                }
                if (item.getProductList().size() > 1) {
                    throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("特价活动只能指定一个商品，活动编号" + profit.getId()));
                }
                if (CommonUtils.isEmptyorNull(item.getProductList().get(0).getSkuId())) {
                    profit.setStatus(CouponStatus.NO.getDictValue());
                }
            }
            //2.处理基本信息
            activityIds.add(profit.getId());
            ActivityProfitEntity profitEntity = activityProfitMapper.findById(profit.getId());
            if (profitEntity == null) {
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("指定活动不存在，活动编号" + profit.getId()));
            }
            BeanPropertiesUtils.copyProperties(profit, profitEntity);
            if (profit.getLabelDto() != null && !CommonUtils.isEmptyorNull(profit.getLabelDto().getId())) {
                profitEntity.setActivityLable(profit.getLabelDto().getId());
            }
            profitEntity.setUpdateAuthor(req.getOperator());
            profitEntity.setIsEnable(CommonDict.IF_YES.getCode());
            activityList.add(profitEntity);

            ActivityQuotaRuleDto quotaRule = item.getActivityQuotaRule();
            if (quotaRule != null) {
                //3.处理限额信息
                ActivityQuotaRuleEntity quotaRuleEntity = activityQuotaRuleMapper.findActivityQuotaRuleById(profitEntity.getId());
                //如果是新增做插入操作
                if (quotaRuleEntity == null) {
                    quotaRuleEntity = new ActivityQuotaRuleEntity();
                    quotaRuleEntity.setCreateAuthor(req.getOperator());
                    addQuotaList.add(quotaRuleEntity);
                } else {//非新增做更新操作
                    modQuotaList.add(quotaRuleEntity);
                }

                quotaRule.setActivityId(profitEntity.getId());
                BeanPropertiesUtils.copyProperties(quotaRule, quotaRuleEntity);
                //补全默认信息
                quotaRuleEntity.setUpdateAuthor(req.getOperator());
                quotaRuleEntity.setIsEnable(CommonDict.IF_YES.getCode());
            }

            //4.处理阶梯信息：先逻辑删除再更新或增加
            if (item.getStageList() != null) {

                for (ActivityStageCouponDto stage : item.getStageList()) {
                    stage.setActivityId(profitEntity.getId());


                    if (CommonUtils.isEmptyorNull(stage.getActivityShortDesc()))
                        stage.setActivityShortDesc(profitEntity.getActivityShortDesc());

                    if (!CommonUtils.isGtZeroDecimal(stage.getEndValue())) {
                        stage.setEndValue(CommonConstant.IGNOREVALUE);
                    }

                    if (CommonUtils.isEmptyorNull(stage.getTriggerByType())) {
                        stage.setTriggerByType(TriggerByType.NUMBER.getDictValue());
                    }
                    if (!CommonUtils.isGtZeroDecimal(stage.getProfitValue()))
                        throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定对应的优惠值"));

                    ActivityStageCouponEntity stageCouponEntity = new ActivityStageCouponEntity();
                    if (CommonUtils.isEmptyorNull(stage.getId())) {
                        stage.setId(CommonUtils.getUUID());
                        stageCouponEntity.setCreateAuthor(req.getOperator());
                        addStageList.add(stageCouponEntity);
                    } else {
                        modStageList.add(stageCouponEntity);
                    }
                    BeanPropertiesUtils.copyProperties(stage, stageCouponEntity);
                    stageCouponEntity.setUpdateAuthor(req.getOperator());
                    stageCouponEntity.setIsEnable(CommonDict.IF_YES.getCode());
                }
            }

            //5.配置适用商品：先删后插
            if (item.getProductList() != null) {
                if (!StringUtil.isEmpty(item.getActivityProfit().getId()))
                    configProductActivityIds.add(item.getActivityProfit().getId());
                for (BaseProductReq product : item.getProductList()) {
                    ActivityRefProductEntity refProductEntity = BeanPropertiesUtils.copyProperties(product, ActivityRefProductEntity.class);
                    refProductEntity.setUpdateAuthor(req.getOperator());
                    refProductEntity.setCreateAuthor(req.getOperator());
                    refProductEntity.setActivityId(profitEntity.getId());
                    refProductEntity.setId(CommonUtils.getUUID());
                    refProductEntity.setIsEnable(CommonDict.IF_YES.getCode());
                    refProductList.add(refProductEntity);
                }
            }
        }

        //数据库操作
        if (!activityList.isEmpty())
            batchService.batchDispose(activityList, ActivityProfitMapper.class, "updateByPrimaryKey");

        if (!activityIds.isEmpty()) {
            batchService.batchDispose(activityIds, ActivityStageCouponMapper.class, "logicDelByActivityId");
            if (!configProductActivityIds.isEmpty()) {
                batchService.batchDispose(configProductActivityIds, ActivityRefProductMapper.class, "deleteByActivityId");
            }
        }

        if (!addQuotaList.isEmpty())
            batchService.batchDispose(addQuotaList, ActivityQuotaRuleMapper.class, "insert");

        if (!modQuotaList.isEmpty())
            batchService.batchDispose(modQuotaList, ActivityQuotaRuleMapper.class, "updateByPrimaryKey");

        if (!addStageList.isEmpty())
            batchService.batchDispose(addStageList, ActivityStageCouponMapper.class, "insert");

        if (!modStageList.isEmpty())
            batchService.batchDispose(modStageList, ActivityStageCouponMapper.class, "updateByPrimaryKey");

        if (!refProductList.isEmpty())
            batchService.batchDispose(refProductList, ActivityRefProductMapper.class, "insert");

        log.info("batchEditActivity数据库处理完毕");
        result.setSuccess(true);
        result.setCode(NET_ERROR.getCode());
        if (req.getActivityDetailList().size() < 3)
            result.setData(req);
        return result;
    }

    /**
     * 批量删除活动
     *
     * @param req 基本活动列表请求体
     * @return 删除成功数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<Integer> batchDelActivity(BatchBaseActivityReq req) {
        CommonBoolDto<Integer> result = new CommonBoolDto<>(true);
        result.setCode(NET_ERROR.getCode());
        if (req == null || req.getBaseActivityList() == null || req.getBaseActivityList().isEmpty()) {
            result.setSuccess(false);
            result.setCode(PARAM_ERROR.getCode());
            result.setData(0);
            return result;
        } else {
            if (req.getBaseActivityList().size() > 50) {
                result.setSuccess(false);
                result.setCode(PARAM_ERROR.getCode());
                result.setData(0);
                result.setDesc("每次批量删除不能超过50条");
                return result;
            }
        }

        //删除基本信息
        batchService.batchDispose(req.getBaseActivityList(), ActivityProfitMapper.class, "logicDelById");

        //删除额度信息
        batchService.batchDispose(req.getBaseActivityList(), ActivityQuotaRuleMapper.class, "logicDelById");

        // 删除门槛信息
        batchService.batchDispose(req.getBaseActivityList(), ActivityStageCouponMapper.class, "logicDelByBaseActivity");

        //删除商品配置
        batchService.batchDispose(req.getBaseActivityList(), ActivityRefProductMapper.class, "deleteByBaseActivity");

        result.setData(req.getBaseActivityList().size());
        return result;
    }

    /**
     * 查找活动
     *
     * @param req 普通查询活动请求体
     * @return 活动详情列表列表
     */
    @Override
    public PageData<ActivityDetailDto> findActivityByCommon(BaseQryActivityReq req) {
        if (req == null)
            req = new BaseQryActivityReq();

        // pagination
        PageHelper.startPage(req.getPageNo(), req.getPageSize());
        // 获取活动分页信息
        PageInfo<ActivityProfitEntity> entitiesPage = new PageInfo<>(activityProfitMapper.findActivityListByCommon(req));
        List<ActivityDetailDto> dtoList = new ArrayList<>();
        for (ActivityProfitEntity item : entitiesPage.getList()) {
            ActivityDetailDto dto = combinationActivity(item);
            dtoList.add(dto);
        }
        return convert(entitiesPage, dtoList);
    }

    /**
     * 查找活动:支持id、商品编号、名称只要其中之一匹配即返回
     *
     * @param req 普通查询活动请求体
     * @return 活动详情列表列表
     */
    @Override
    public PageData<ActivityDetailDto> findActivityList(BaseQryActivityReq req) {
        if (req == null)
            req = new BaseQryActivityReq();

        // pagination
        PageHelper.startPage(req.getPageNo(), req.getPageSize());
        // 获取活动分页信息
        PageInfo<ActivityProfitEntity> entitiesPage = new PageInfo<>(activityProfitMapper.findActivityList(req));
        List<ActivityDetailDto> dtoList = new ArrayList<>();
        for (ActivityProfitEntity item : entitiesPage.getList()) {
            ActivityDetailDto dto = combinationActivity(item);
            dtoList.add(dto);
        }
        return convert(entitiesPage, dtoList);
    }

    /**
     * 查询活动汇总信息
     *
     * @param req 普通查询活动请求体
     * @return 活动详情列表列表
     */
    @Override
    public List<GatherInfoRsp> findGatherActivityByCommon(BaseQryActivityReq req) {
        if (req == null)
            req = new BaseQryActivityReq();
        return activityProfitMapper.findGatherActivityListByCommon(req);
    }


    /**
     * 【后台】获取商品活动优惠价
     *
     * @param req 商品编号
     * @return 活动详情
     * 开发日志
     * 1004258-徐长焕-20181226 新建
     */
    @Override
    public List<ActivityDetailDto> findActivityPrice(BaseProductReq req) {

        List<ActivityProfitEntity> entities = activityProfitMapper.findPriceActivityByProductId(req.getProductId(), req.getSkuId());
        List<ActivityDetailDto> result = new ArrayList<>();

        //多个特价活动以最新的为准
        for (ActivityProfitEntity entity : entities) {
            result.add(combinationActivity(entity));
        }
        return result;
    }


    /**
     * 【App+后台】查询商品相关的活动
     *
     * @param req 商品基本信息
     * @return 活动详情列表
     */
    @Override
    public List<ActivityDetailDto> findProductAboutActivity(BaseProductReq req) {
        //查询已上架和有效的
        List<ActivityProfitEntity> entities = activityProfitMapper.findActivityByProductId(req.getProductId(), req.getSkuId(), "1", "1");
        return combinationActivity(entities);
    }


    /**
     * 查询指定活动
     *
     * @param req 活动基本信息
     * @return 活动详情列表
     */
    @Override
    public ActivityDetailDto findActivityById(BaseActivityReq req) {
        ActivityProfitEntity entitie = activityProfitMapper.findById(req.getActivityId());
        if (entitie == null)
            return null;
        return combinationActivity(entitie);
    }


    /**
     * 获取商品有效的优惠价活动（排除了已达额度的活动）
     *
     * @param req 商品编号
     * @return 活动详情
     * 开发日志
     * 1004258-徐长焕-20181226 新建
     */
    @Override
    public List<ActivityDetailDto> findValidActivityPrice(BaseProductReq req) {

        List<ActivityProfitEntity> entities = activityProfitMapper.findValidPriceActivityByProduct(req.getProductId(), req.getSkuId());
        return combinationActivity(entities);
    }

    /**
     * 校验活动基本信息
     *
     * @param activity 活动实体
     * @param req      获取可用活动优惠券请求体
     * @return 返回是否校验成功
     */
    private CommonBoolDto checkActivityBase(ActivityProfitEntity activity,
                                            GetUseEnableCouponReq req) {
        CommonBoolDto dto = new CommonBoolDto(true);

        //a.客户属性校验
        // 客户类型是否允许领取
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(activity.getClientTypeSet(),
                req.getClientType())) {

            dto.setSuccess(false);
            dto.setDesc(ACTIVITY_NOT_ALLOW_CLIENTTYPE.getFormatDesc(req.getClientType()));
            return dto;
        }

        //b.商品属性校验，不校验其买入卖出门槛
        if (req.getProductList() != null && !req.getProductList().isEmpty()) {
            dto.setSuccess(false);
            for (OrderProductDetailDto item : req.getProductList()) {
                dto = checkRefProduct(activity, item.getProductId(), item.getSkuId());
                if (dto.getSuccess()) {
                    break;
                }
            }
            if (!dto.getSuccess()) {
                dto.setDesc("该活动不适用本次选择的任何商品");
                return dto;
            }
        }

        //c.订单属性校验
        //该渠道信息是否允许领取
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(activity.getEntrustWaySet(),
                req.getEntrustWay())) {
            dto.setSuccess(false);
            dto.setDesc(ACTIVITY_NOT_ALLOW_ENTRUSTWAY.getFormatDesc(req.getEntrustWay()));
            return dto;
        }

        //d.支付属性校验
        //该银行卡是否允许领取该券
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(activity.getBankCodeSet(),
                req.getBankCode())) {

            dto.setSuccess(false);
            dto.setDesc(ACTIVITY_NOT_ALLOW_BANKCODE.getFormatDesc(req.getBankCode()));
            return dto;
        }

        //该支付类型是否允许使用该活动
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(activity.getPayTypeSet(),
                req.getPayType())) {
            dto.setSuccess(false);
            dto.setDesc(ACTIVITY_NOT_ALLOW_PAYTYPE.getFormatDesc(req.getPayType()));
            return dto;
        }

        //是否在允許使用期間
        if ((activity.getAllowUseBeginDate() != null && activity.getAllowUseBeginDate().compareTo(LocalDateTime.now()) > 0) ||
                (activity.getAllowUseEndDate() != null && activity.getAllowUseEndDate().compareTo(LocalDateTime.now()) < 0)) {

            dto.setSuccess(false);
            dto.setDesc(ACTIVITY_NOT_ALLOW_DATE.getFormatDesc(activity.getAllowUseBeginDate(), activity.getAllowUseEndDate()));
            return dto;
        }

        //是否上架状态
        if (!CouponStatus.YES.getDictValue().equals(activity.getStatus())) {
            dto.setSuccess(false);
            return dto;
        }
        return dto;
    }

    /**
     * 检查商品是否适用
     *
     * @param activity  活动实体
     * @param productId 商品编号
     * @return 返回是否校验成功
     */
    private CommonBoolDto checkRefProduct(ActivityProfitEntity activity, String productId, String skuId) {
        CommonBoolDto dto = new CommonBoolDto(false);
        // ApplyProductFlag空值做保护
        if (!ApplyProductFlag.ALL.getDictValue().equals(activity.getApplyProductFlag())) {
            //该商品属性是否允许参与活动
            ActivityRefProductEntity entity = activityRefProductMapper.findByActivityAndSkuId(activity.getId(), productId, skuId);
            if (entity != null) {
                dto.setSuccess(true);
                return dto;
            }
        } else {
            dto.setSuccess(true);
            return dto;
        }
        return dto;
    }

    /**
     * 组装活动信息列表（活动主信息+阶梯信息)，避免多次查询
     *
     * @param activityList 活动实体列表
     * @return 活动详情列表：含活动信息、门槛阶梯、额度、配置商品
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    private List<ActivityDetailDto> combinationActivity(List<ActivityProfitEntity> activityList) {

        if (activityList == null || activityList.isEmpty()) return new ArrayList<>();

        //使用LinkedHashMap保留添加顺序
        Map<String, ActivityDetailDto> result = new LinkedHashMap();
        List<String> labelIdList = new ArrayList<>();
        for (ActivityProfitEntity item : activityList) {
            ActivityDetailDto detailDto = new ActivityDetailDto();

            //基本信息转换为传出参数
            ActivityProfitDto activityProfit = BeanPropertiesUtils.copyProperties(item, ActivityProfitDto.class);
            detailDto.setActivityProfit(activityProfit);

            if (!StringUtil.isEmpty(item.getActivityLable())) {
                activityProfit.setLabelDto(new CouponAndActivityLabelDto());
                activityProfit.getLabelDto().setId(item.getActivityLable());
                labelIdList.add(item.getActivityLable());
            }
            result.put(item.getId(), detailDto);
        }

        List<String> idList = new ArrayList<>(result.keySet());
        //获取指定活动的使用门槛阶梯
        List<ActivityStageCouponEntity> stageList = activityStageCouponMapper.findActivityProfitDetailByIds(idList);
        for (ActivityStageCouponEntity item : stageList) {
            if (result.containsKey(item.getActivityId())) {
                ActivityDetailDto detailDto = result.get(item.getActivityId());
                if (detailDto.getStageList() == null) {
                    detailDto.setStageList(new ArrayList<>());
                }
                detailDto.getStageList().add(BeanPropertiesUtils.copyProperties(item, ActivityStageCouponDto.class));
            }
        }

        //获取额度
        List<ActivityQuotaRuleEntity> quotaRuleEntityList = activityQuotaRuleMapper.findActivityQuotaRuleByIds(idList);
        for (ActivityQuotaRuleEntity item : quotaRuleEntityList) {

            ActivityQuotaRuleDto quotaRuleDto = BeanPropertiesUtils.copyProperties(item, ActivityQuotaRuleDto.class);
            if (result.containsKey(item.getActivityId())) {
                result.get(item.getActivityId()).setActivityQuotaRule(quotaRuleDto);
            }
        }

        //获取标签
        if (!labelIdList.isEmpty()) {
            List<CouponAndActivityLabelEntity> labelEntityList = couponAndActivityLabelMapper.findLabelByIds(labelIdList);
            for (CouponAndActivityLabelEntity item : labelEntityList) {
                for (ActivityDetailDto dto : result.values()) {
                    if (dto.getActivityProfit().getLabelDto() != null && item.getId().equals(dto.getActivityProfit().getLabelDto().getId())) {
                        dto.getActivityProfit().setLabelDto(BeanPropertiesUtils.copyProperties(item, CouponAndActivityLabelDto.class));
                    }
                }
            }
        }


        //获取适用商品
        List<ActivityRefProductEntity> refProductEntities = activityRefProductMapper.findByActivityIds(idList);
        for (ActivityRefProductEntity item : refProductEntities) {
            if (result.containsKey(item.getActivityId())) {
                ActivityDetailDto detailDto = result.get(item.getActivityId());
                if (detailDto.getProductList() == null) {
                    detailDto.setProductList(new ArrayList<>());
                }
                detailDto.getProductList().add(BeanPropertiesUtils.copyProperties(item, BaseProductReq.class));

            }
        }
        ArrayList<ActivityDetailDto> finalResult = new ArrayList<>(result.values());
        result.clear();
        result = null;
        return finalResult;
    }

    /**
     * 组装活动信息（活动主信息+阶梯信息)
     *
     * @param entity 活动主体
     * @return 活动详情：含活动信息、门槛阶梯、额度、配置商品
     */
    private ActivityDetailDto combinationActivity(ActivityProfitEntity entity) {
        if (entity == null)
            return null;

        ActivityDetailDto result = new ActivityDetailDto();

        //转换为传出参数
        ActivityProfitDto activityProfit = BeanPropertiesUtils.copyProperties(entity, ActivityProfitDto.class);
        result.setActivityProfit(activityProfit);

        //获取指定活动的使用门槛阶梯
        List<ActivityStageCouponEntity> stageList = activityStageCouponMapper.findActivityProfitDetail(entity.getId());
        if (!stageList.isEmpty()) {
            result.setStageList(BeanPropertiesConverter.copyPropertiesOfList(stageList, ActivityStageCouponDto.class));
        }

        //获取额度
        ActivityQuotaRuleEntity quotaRuleEntity = activityQuotaRuleMapper.findActivityQuotaRuleById(activityProfit.getUuid());
        if (quotaRuleEntity != null) {
            ActivityQuotaRuleDto quotaRuleDto = BeanPropertiesUtils.copyProperties(quotaRuleEntity, ActivityQuotaRuleDto.class);
            result.setActivityQuotaRule(quotaRuleDto);
        }

        //获取标签
        if (!CommonUtils.isEmptyorNull(entity.getActivityLable())) {
            CouponAndActivityLabelEntity labelEntity = couponAndActivityLabelMapper.findLabelById(entity.getActivityLable());
            activityProfit.setLabelDto(BeanPropertiesUtils.copyProperties(labelEntity, CouponAndActivityLabelDto.class));
        }

        //获取适用商品
        List<ActivityRefProductEntity> refProductEntities = activityRefProductMapper.findByActivityId(activityProfit.getUuid());
        result.setProductList(BeanPropertiesConverter.copyPropertiesOfList(refProductEntities, BaseProductReq.class));

        return result;
    }


    /**
     * 查询活动特价
     *
     * @param req 商品编号
     * @return 特价基础信息列表
     */
    @Override
    public List<BasePriceActivityRsp> findActivityPriceValue(BaseProductReq req) {
        return activityProfitMapper.findBasePriceByProduct(req.getProductId(), req.getSkuId());
    }

    /**
     * 查找所有活动，不分页
     *
     * @param req 普通查询特价活动请求体
     * @return 活动详情列表列表
     */
    @Override
    public List<ActivityDetailDto> findAllActivityByCommon(BaseQryActivityReq req) {
        if (req == null)
            req = new BaseQryActivityReq();

        List<ActivityProfitEntity> entities = activityProfitMapper.findActivityListByCommon(req);
        List<ActivityDetailDto> result = new ArrayList<>();
        for (ActivityProfitEntity item : entities) {
            ActivityDetailDto dto = combinationActivity(item);
            result.add(dto);
        }
        return result;
    }


    /**
     * 上架活动
     *
     * @param req 批量活动编号
     * @return 执行数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<Integer> upActivity(BatchBaseActivityReq req) {
        CommonBoolDto<Integer> result = new CommonBoolDto<>(true);
        result.setCode(NET_ERROR.getCode());
        result.setData(0);
        result.setDesc("");

        if (req == null || req.getBaseActivityList() == null || req.getBaseActivityList().isEmpty()) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("必须指定一个活动"));
        }

        List<ActivityProfitEntity> entities = activityProfitMapper.findActivityByIds(req);
        List<ActivityProfitEntity> dealList = new ArrayList<>();
        for (ActivityProfitEntity item : entities) {
            if (CouponStatus.YES.getDictValue().equals(item.getStatus())) {
                result.setDesc(result.getDesc() + item.getId() + "状态已上架，无需处理|");
                continue;
            }

            ActivityProfitDto dto = new ActivityProfitDto();
            dto.setId(item.getId());
            dto.setAllowUseBeginDate(item.getAllowUseBeginDate());
            dto.setAllowUseEndDate(item.getAllowUseEndDate());
            List<BaseProductReq> productReqs = activityRefProductMapper.findForbidCifgProductByActivity(dto);
            if (!productReqs.isEmpty())
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("编辑活动上下架导致原配置的商品在某一时间点同时存在于两个活动中"));

            item.setStatus(CouponStatus.YES.getDictValue());
            item.setUpdateAuthor(req.getOperator());
            item.setRemark("上架活动");
            result.setData(result.getData() + 1);
            dealList.add(item);
        }

        batchService.batchDispose(dealList, ActivityProfitMapper.class, "updateByPrimaryKeySelective");
        return result;
    }

    /**
     * 下架活动
     *
     * @param req 批量活动编号
     * @return 执行数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<Integer> downActivity(BatchBaseActivityReq req) {
        CommonBoolDto<Integer> result = new CommonBoolDto<>(true);
        result.setCode(NET_ERROR.getCode());
        result.setData(0);
        result.setDesc("");

        if (req == null || req.getBaseActivityList() == null || req.getBaseActivityList().isEmpty()) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("必须指定一个活动"));
        }

        List<ActivityProfitEntity> entities = activityProfitMapper.findActivityByIds(req);
        List<ActivityProfitEntity> dealList = new ArrayList<>();
        for (ActivityProfitEntity item : entities) {
            if (!CouponStatus.YES.getDictValue().equals(item.getStatus())) {
                result.setDesc(result.getDesc() + item.getId() + "状态已下架，无需处理|");
                continue;
            }
            item.setStatus(CouponStatus.NO.getDictValue());
            item.setUpdateAuthor(req.getOperator());
            item.setRemark("下架活动");
            result.setData(result.getData() + 1);
            dealList.add(item);
        }

        batchService.batchDispose(dealList, ActivityProfitMapper.class, "updateByPrimaryKeySelective");
        return result;

    }


    /**
     * 查询抢购特价活动
     *
     * @param req 查询请求体
     * @return
     */
    @Override
    public List<ActivityDetailDto> findFlashSalePriceActivity(OperatQryReq req) {
        List<ActivityDetailDto> result = new ArrayList<>();
        //先按商品分组，最近更新的前N条
        if (req.getPageSize() > 0) {
            //先获取有效的
            List<GroupProductDto> dtos = activityProfitMapper.findLeastPriceProductActivity(req);
            if (!dtos.isEmpty()) {
                List<String> ids = new ArrayList<>();
                for (GroupProductDto item : dtos) {
                    ids.add(item.getProductId());
                }

                List<ActivityProfitEntity> entities = activityProfitMapper.findPriceActivityByProductIds(ids, "1", "1");
                for (ActivityDetailDto item : combinationActivity(entities)) {
                    item.setActivityStatus(CommonConstant.VIEW_ACTIVITYSTATUS_COMMON);
                    result.add(item);
                }
            }

            if (dtos.size() >= req.getPageSize())
                return result;
            BaseQryActivityReq innerReq = new BaseQryActivityReq();
            innerReq.setUpAndDownStatus(CouponStatus.YES.getDictValue());
            innerReq.setActivityCouponType(ActivityCouponType.PRICE.getDictValue());
            List<ActivityProfitEntity> listByCommon = activityProfitMapper.findActivityListByCommon(innerReq);
            //有效期无额度的>未到期的>过期的
            Collections.sort(listByCommon, new Comparator<ActivityProfitEntity>() {
                @Override
                public int compare(ActivityProfitEntity entity1, ActivityProfitEntity entity2) {
                    //有效期内排最前
                    if (entity1.getAllowUseBeginDate().isBefore(LocalDateTime.now()) &&
                            entity1.getAllowUseEndDate().isAfter(LocalDateTime.now())) {
                        return -1;
                    }
                    if (entity1.getAllowUseEndDate().isBefore(LocalDateTime.now()))
                        return 1;
                    return 0;
                }
            });


            List<ActivityProfitEntity> filterList = new ArrayList<>();
            for (ActivityProfitEntity item : listByCommon) {
                //过滤已查询的有效的
                for (ActivityDetailDto dtoitem : result) {
                    if (item.getId().equals(dtoitem.getActivityProfit().getId())) {
                        continue;
                    }
                }
                ActivityDetailDto detail = combinationActivity(item);
                detail.setActivityStatus(CommonConstant.VIEW_ACTIVITYSTATUSE_UNSTART);
                if (detail.getActivityProfit().getAllowUseBeginDate().isBefore(LocalDateTime.now()) &&
                        detail.getActivityProfit().getAllowUseEndDate().isAfter(LocalDateTime.now())) {
                    detail.setActivityStatus(CommonConstant.VIEW_ACTIVITYSTATUS_NOT_QUOTA);
                } else if (detail.getActivityProfit().getAllowUseEndDate().isBefore(LocalDateTime.now())) {
                    detail.setActivityStatus(CommonConstant.VIEW_ACTIVITYSTATUS_OVERDUE);
                }

                boolean isExist = false;
                for (GroupProductDto groupItem : dtos) {
                    if (groupItem.getProductId().equals(detail.getProductList().get(0).getProductId())) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    GroupProductDto groupItem = new GroupProductDto();
                    groupItem.setProductId(detail.getProductList().get(0).getProductId());
                    groupItem.setLastTime(item.getCreateTime());
                    dtos.add(groupItem);
                }
                result.add(detail);

                if (dtos.size() == (req.getPageSize() - dtos.size())) {
                    dtos = null;
                    break;
                }

            }

            //全部查询时
        } else {
            //先获取有效的
            List<ActivityProfitEntity> entities = activityProfitMapper.findValidPriceActivityByProduct("", "");

            BaseQryActivityReq innerReq = new BaseQryActivityReq();
            innerReq.setUpAndDownStatus(CouponStatus.YES.getDictValue());
            innerReq.setActivityCouponType(ActivityCouponType.PRICE.getDictValue());
            List<ActivityProfitEntity> listByCommon = activityProfitMapper.findActivityListByCommon(innerReq);
            //有效期无额度的>未到期的>过期的
            Collections.sort(listByCommon, new Comparator<ActivityProfitEntity>() {
                @Override
                public int compare(ActivityProfitEntity entity1, ActivityProfitEntity entity2) {
                    //有效期内排最前
                    if (entity1.getAllowUseBeginDate().isBefore(LocalDateTime.now()) &&
                            entity1.getAllowUseEndDate().isAfter(LocalDateTime.now())) {
                        return -1;
                    }
                    if (entity1.getAllowUseEndDate().isBefore(LocalDateTime.now()))
                        return 1;
                    return 0;
                }
            });

            List<ActivityProfitEntity> filterList = new ArrayList<>();
            for (ActivityProfitEntity item : listByCommon) {
                //过滤已查询的有效的
                for (ActivityProfitEntity dtoitem : entities) {
                    if (item.getId().equals(dtoitem.getId())) {
                        continue;
                    }
                }
                filterList.add(item);
            }

            filterList.addAll(entities);
            result.addAll(combinationActivity(filterList));
            for (ActivityDetailDto item : result) {
                for (ActivityProfitEntity dtoitem : entities) {
                    if (item.getActivityProfit().getId().equals(dtoitem.getId())) {
                        item.setActivityStatus(CommonConstant.VIEW_ACTIVITYSTATUS_COMMON);
                        continue;
                    }
                }
                item.setActivityStatus(CommonConstant.VIEW_ACTIVITYSTATUSE_UNSTART);
                if (item.getActivityProfit().getAllowUseBeginDate().isBefore(LocalDateTime.now()) &&
                        item.getActivityProfit().getAllowUseEndDate().isAfter(LocalDateTime.now())) {
                    item.setActivityStatus(CommonConstant.VIEW_ACTIVITYSTATUS_NOT_QUOTA);

                } else if (item.getActivityProfit().getAllowUseEndDate().isBefore(LocalDateTime.now())) {
                    item.setActivityStatus(CommonConstant.VIEW_ACTIVITYSTATUS_OVERDUE);

                }
            }
        }

        return result;
    }

    /**
     * 查询抢购商品最后结束时间
     */
    @Override
    public Date findFlashSalePriceActivityEndTime(OperatReq req) {
        if (req == null)
            req = new OperatReq();
        Date lastTime = activityProfitMapper.findValidPriceLastTime(req);
        return lastTime;
    }

}




