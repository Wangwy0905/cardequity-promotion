package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.bean.CustomHandler;
import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.common.spring.service.BatchService;
import com.youyu.cardequity.promotion.biz.constant.CommonConstant;
import com.youyu.cardequity.promotion.biz.dal.dao.*;
import com.youyu.cardequity.promotion.biz.dal.entity.*;
import com.youyu.cardequity.promotion.biz.service.ActivityProfitService;
import com.youyu.cardequity.promotion.biz.service.ActivityRefProductService;
import com.youyu.cardequity.promotion.biz.strategy.activity.ActivityStrategy;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.biz.utils.SnowflakeIdWorker;
import com.youyu.cardequity.promotion.dto.*;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.*;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.ActivityDefineRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.youyu.cardequity.promotion.enums.ResultCode.*;


/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 * 开发日志
 * V1.0-V1 1004244-徐长焕-20181207 新建，实现可参与活动列表
 */
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
    private ClientTakeInActivityMapper clientTakeInActivityMapper;

    @Autowired
    private BatchService batchService;

    @Autowired
    private ActivityQuotaRuleMapper activityQuotaRuleMapper;

    /**
     * 获取可参与的活动列表
     *
     * @param req 查询优惠活动请求体
     * @return 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    @Override
    public List<ActivityDefineRsp> findEnableGetActivity(QryProfitCommonReq req) {

        List<ActivityDefineRsp> rspList = new ArrayList<>();

        //获取普通活动列表
        List<ActivityProfitEntity> activityList = activityProfitMapper.findEnableGetCommonActivity(req.getProductId(), req.getEntrustWay());

        //将其使用门槛阶梯与活动主信息组装后返回
        rspList.addAll(combinationActivity(activityList, false));

        //获取会员活动列表
        List<ActivityProfitEntity> activityForMemberList = activityProfitMapper.findEnableGetMemberActivity(req.getProductId(), req.getEntrustWay(), req.getClinetType());
        //将会员活动使用门槛阶梯与活动主信息组装后返回
        rspList.addAll(combinationActivity(activityForMemberList, true));

        return rspList;
    }

    @Override
    public List<UseActivityRsp> combActivityRefProductDeal(GetUseEnableCouponReq req) {
        //定义返回结果
        List<UseActivityRsp> rsps = new ArrayList<>();
        //获取所有可以参与的活动：按初始条件，有效日期内
        //获取普通活动列表
        List<ActivityProfitEntity> activityList = activityProfitMapper.findEnableGetCommonActivity("", req.getEntrustWay());
        //空订单或者没有可用活动直接返回
        if (req.getProductList() == null ||
                activityList == null ||
                req.getProductList().size() <= 0 ||
                activityList.size() <= 0) {
            return rsps;
        }

        //团购活动优先处理
        // TODO: 2018/12/25

        //循环活动进行计算优惠金额，优先顺序为：任选->折扣->满减
        for (ActivityProfitEntity item : activityList) {
            //校验基本信息：有效期的、商品属性、订单属性、支付属性
            checkActivityBase(item, req);

            //根据策略得到该活动是否满足门槛，返回满足活动适用信息
            String key = ActivityStrategy.class.getSimpleName() + item.getActivityCouponType();
            ActivityStrategy executor = (ActivityStrategy) CustomHandler.getBeanByName(key);
            UseActivityRsp rsp = executor.applyActivity(item, req.getProductList());
            if (rsp != null)
                rsps.add(rsp);
        }

        //使用
        return rsps;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<BatchActivityDetailDto> batchAddActivity(BatchActivityDetailDto req) {
        //权益中心标志为3，活动表标识为1，ProductCoupon表标识为2
        SnowflakeIdWorker stageWorker = new SnowflakeIdWorker(3, 1);
        CommonBoolDto<BatchActivityDetailDto> result = new CommonBoolDto<>(false);
        if (req == null || req.getActivityDetailList() == null || req.getActivityDetailList().isEmpty()) {
            result.setDesc("参数未指定");
            return result;
        }

        List<ActivityProfitEntity> activityList = new ArrayList<>();
        List<ActivityQuotaRuleEntity> quotaList = new ArrayList<>();
        List<ActivityStageCouponEntity> stageList= new ArrayList<>();
        List<ActivityRefProductEntity> refProductList=new ArrayList<>();
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

            //如果没有指定商品集合，默认为全部商品
            if (CommonUtils.isEmptyorNull(profit.getApplyProductFlag())) {
                if (item.getProductList() != null || item.getProductList().isEmpty()) {
                    profit.setApplyProductFlag(ApplyProductFlag.ALL.getDictValue());
                }
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

            //2.处理基本信息
            profit.setId(stageWorker.nextId() + "");
            ActivityProfitEntity profitEntity = new ActivityProfitEntity();
            profitEntity.setIsEnable(CommonDict.IF_YES.getCode());
            BeanUtils.copyProperties(profit, profitEntity);
            activityList.add(profitEntity);

            //3.处理限额信息
            ActivityQuotaRuleEntity quotaRuleEntity = new ActivityQuotaRuleEntity();
            ActivityQuotaRuleDto quotaRule = item.getActivityQuotaRule();
            quotaRule.setActivityId(profitEntity.getId());
            BeanUtils.copyProperties(quotaRule, quotaRuleEntity);
            quotaRuleEntity.setIsEnable(CommonDict.IF_YES.getCode());
            quotaList.add(quotaRuleEntity);

            //4.处理阶梯信息
            if (item.getStageList() != null) {
                for (ActivityStageCouponDto stage : item.getStageList()) {
                    stage.setActivityId(profitEntity.getId());
                    stage.setId(CommonUtils.getUUID());

                    if (CommonUtils.isEmptyorNull(stage.getActivityShortDesc()))
                        stage.setActivityShortDesc(profitEntity.getActivityShortDesc());

                    if (!CommonUtils.isGtZeroDecimal(stage.getEndValue())){
                        stage.setEndValue(CommonConstant.IGNOREVALUE);
                    }

                    if (CommonUtils.isEmptyorNull(stage.getTriggerByType())){
                        stage.setTriggerByType(TriggerByType.NUMBER.getDictValue());
                    }
                    if (!CommonUtils.isGtZeroDecimal(stage.getProfitValue()))
                        throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定对应的优惠值"));

                    ActivityStageCouponEntity stageCouponEntity = new ActivityStageCouponEntity();
                    BeanUtils.copyProperties(stage,stageCouponEntity);
                    stageCouponEntity.setIsEnable(CommonDict.IF_YES.getCode());
                    stageList.add(stageCouponEntity);
                }
            }

            //5.配置适用商品
            if (item.getProductList()!=null){
                for (BaseProductReq product:item.getProductList()){
                    ActivityRefProductEntity refProductEntity=new ActivityRefProductEntity();
                    BeanUtils.copyProperties(product,refProductEntity);
                    refProductEntity.setActivityId(profitEntity.getId());
                    refProductEntity.setId(CommonUtils.getUUID());
                    refProductEntity.setIsEnable(CommonDict.IF_YES.getCode());
                    refProductList.add(refProductEntity);
                }
            }
        }

        //数据库操作
        batchService.batchDispose(activityList, ActivityProfitMapper.class, "insert");

        batchService.batchDispose(quotaList, ActivityQuotaRuleMapper.class, "insert");

        batchService.batchDispose(stageList, ActivityStageCouponMapper.class, "insert");

        batchService.batchDispose(refProductList, ActivityRefProductMapper.class, "insert");

        result.setSuccess(true);
        return result;
    }

    /**
     * 批量编辑活动
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<BatchActivityDetailDto> batchEditActivity(BatchActivityDetailDto req) {
        CommonBoolDto<BatchActivityDetailDto> result = new CommonBoolDto<>(false);
        if (req == null || req.getActivityDetailList() == null || req.getActivityDetailList().isEmpty()) {
            result.setDesc("参数未指定");
            return result;
        }

        List<ActivityProfitEntity> activityList = new ArrayList<>();
        List<ActivityQuotaRuleEntity> quotaList = new ArrayList<>();
        List<ActivityRefProductEntity> refProductList=new ArrayList<>();
        List<String> activityIds=new ArrayList<>();
        List<ActivityStageCouponEntity> modStageList=new ArrayList<>();
        List<ActivityStageCouponEntity> addStageList=new ArrayList<>();
        List<ActivityQuotaRuleEntity> addQuotaList=new ArrayList<>();
        List<ActivityQuotaRuleEntity> modQuotaList=new ArrayList<>();
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

            //如果没有指定商品集合，默认为全部商品
            if (CommonUtils.isEmptyorNull(profit.getApplyProductFlag())) {
                if (item.getProductList() != null || item.getProductList().isEmpty()) {
                    profit.setApplyProductFlag(ApplyProductFlag.ALL.getDictValue());
                }
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

            //校验活动是否存在


            //2.处理基本信息
            activityIds.add(profit.getId());
            ActivityProfitEntity profitEntity = activityProfitMapper.findById(profit.getId());
            if (profitEntity==null){
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("指定活动不存在，活动编号"+profit.getId()));
            }
            profitEntity.setIsEnable(CommonDict.IF_YES.getCode());
            BeanUtils.copyProperties(profit, profitEntity);
            activityList.add(profitEntity);

            //3.处理限额信息
            ActivityQuotaRuleEntity quotaRuleEntity =activityQuotaRuleMapper.findActivityQuotaRuleById(profitEntity.getId());
            //如果是新增做插入操作
            if (quotaRuleEntity==null){
                quotaRuleEntity=new ActivityQuotaRuleEntity();
                addQuotaList.add(quotaRuleEntity);
            }else{//非新增做更新操作
                modQuotaList.add(quotaRuleEntity);
            }
            ActivityQuotaRuleDto quotaRule = item.getActivityQuotaRule();
            quotaRule.setActivityId(profitEntity.getId());
            BeanUtils.copyProperties(quotaRule, quotaRuleEntity);
            quotaRuleEntity.setIsEnable(CommonDict.IF_YES.getCode());
            quotaList.add(quotaRuleEntity);

            //4.处理阶梯信息：先逻辑删除再更新或增加
            if (item.getStageList() != null) {

                for (ActivityStageCouponDto stage : item.getStageList()) {
                    stage.setActivityId(profitEntity.getId());


                    if (CommonUtils.isEmptyorNull(stage.getActivityShortDesc()))
                        stage.setActivityShortDesc(profitEntity.getActivityShortDesc());

                    if (!CommonUtils.isGtZeroDecimal(stage.getEndValue())){
                        stage.setEndValue(CommonConstant.IGNOREVALUE);
                    }

                    if (CommonUtils.isEmptyorNull(stage.getTriggerByType())){
                        stage.setTriggerByType(TriggerByType.NUMBER.getDictValue());
                    }
                    if (!CommonUtils.isGtZeroDecimal(stage.getProfitValue()))
                        throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定对应的优惠值"));

                    ActivityStageCouponEntity stageCouponEntity = new ActivityStageCouponEntity();
                    if (CommonUtils.isEmptyorNull(stage.getId())) {
                        stage.setId(CommonUtils.getUUID());
                        addStageList.add(stageCouponEntity);
                    }else {
                        modStageList.add(stageCouponEntity);
                    }
                    BeanUtils.copyProperties(stage,stageCouponEntity);
                    stageCouponEntity.setIsEnable(CommonDict.IF_YES.getCode());
                }
            }

            //5.配置适用商品：先删后插
            if (item.getProductList()!=null){
                for (BaseProductReq product:item.getProductList()){
                    ActivityRefProductEntity refProductEntity=new ActivityRefProductEntity();
                    BeanUtils.copyProperties(product,refProductEntity);
                    refProductEntity.setActivityId(profitEntity.getId());
                    refProductEntity.setId(CommonUtils.getUUID());
                    refProductEntity.setIsEnable(CommonDict.IF_YES.getCode());
                    refProductList.add(refProductEntity);
                }
            }
        }

        //数据库操作
        batchService.batchDispose(activityList, ActivityProfitMapper.class, "insert");

        batchService.batchDispose(addQuotaList, ActivityQuotaRuleMapper.class, "insert");
        batchService.batchDispose(modQuotaList, ActivityQuotaRuleMapper.class, "updateByPrimaryKey");

        batchService.batchDispose(activityIds, ActivityStageCouponMapper.class, "logicDelByActivityId");
        batchService.batchDispose(addStageList, ActivityStageCouponMapper.class, "insert");
        batchService.batchDispose(modStageList, ActivityStageCouponMapper.class, "updateByPrimaryKey");

        batchService.batchDispose(activityIds, ActivityRefProductMapper.class, "deleteByActivityId");
        batchService.batchDispose(refProductList, ActivityRefProductMapper.class, "insert");

        result.setSuccess(true);
        return result;
    }

    /**
     * 批量删除活动
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<Integer> batchDelActivity(BatchBaseActivityReq req) {
        CommonBoolDto<Integer> result=new CommonBoolDto<Integer>(true);
        if (req ==null || req.getBaseActivityList()==null || req.getBaseActivityList().isEmpty()){
            result.setSuccess(false);
            result.setData(0);
            return result;
        }else{
            if (req.getBaseActivityList().size()>50) {
                result.setSuccess(false);
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
     * @param req
     * @return
     */
    @Override
    public List<ActivityDetailDto> findActivityByCommon(BaseQryActivityReq req) {
        if (req==null)
            return null;
        List<ActivityDetailDto> result=new ArrayList<>();
        List<ActivityProfitEntity> entities = activityProfitMapper.findActivityListByCommon(req);
        for (ActivityProfitEntity item:entities){
            ActivityDetailDto dto=new ActivityDetailDto();

            ActivityProfitDto profitDto = new ActivityProfitDto();
            BeanUtils.copyProperties(item,profitDto);
            dto.setActivityProfit(profitDto);
            //查询额度
            ActivityQuotaRuleDto quotaRuleDto = new ActivityQuotaRuleDto();
            ActivityQuotaRuleEntity quotaRuleEntity = activityQuotaRuleMapper.findActivityQuotaRuleById(item.getId());
            BeanUtils.copyProperties(quotaRuleEntity,quotaRuleDto);
            dto.setActivityQuotaRule(quotaRuleDto);
            //查询门槛
            List<ActivityStageCouponEntity> detailList = activityStageCouponMapper.findActivityProfitDetail(item.getId());
            dto.setStageList(BeanPropertiesConverter.copyPropertiesOfList(detailList,ActivityStageCouponDto.class));
            //查询配置的商品
            List<ActivityRefProductEntity> refProductEntities = activityRefProductMapper.findByActivityId(item.getId());
            dto.setProductList(BeanPropertiesConverter.copyPropertiesOfList(refProductEntities,BaseProductReq.class));
            result.add(dto);
        }

        return result;
    }

    /**
     * 获取商品活动优惠价
     *
     * @param req
     * @return 1004258-徐长焕-20181226 新建
     */
    @Override
    public ActivityViewDto findActivityPrice(BaseProductReq req) {
        List<ActivityProfitEntity> entities = activityProfitMapper.findPriceActivityByProductId(req.getProductId(), req.getSkuId());
        if (entities.isEmpty())
            return null;
        if (entities.size()>1)
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("配置错误，该商品配置了多个特价活动"));

        ActivityViewDto result=new ActivityViewDto();
        BeanUtils.copyProperties(entities.get(0),result);
        //获取额度
        ActivityQuotaRuleEntity quotaRuleEntity = activityQuotaRuleMapper.findActivityQuotaRuleById(result.getUuid());
        if (quotaRuleEntity!=null)
            result.setMaxCount(quotaRuleEntity.getMaxCount());
        //获取适用商品
        List<ActivityRefProductEntity> refProductEntities = activityRefProductMapper.findByActivityId(result.getUuid());
        result.setProductList(BeanPropertiesConverter.copyPropertiesOfList(refProductEntities,BaseProductReq.class));

        //特价商品没有门槛
        return result;
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
                req.getClinetType())) {

            dto.setSuccess(false);
            dto.setDesc(ACTIVITY_NOT_ALLOW_CLIENTTYPE.getFormatDesc(req.getClinetType()));
            return dto;
        }

        //b.商品属性校验，不校验其买入卖出门槛
        if (req.getProductList() != null && req.getProductList().size() > 0) {
            dto.setSuccess(false);
            for (OrderProductDetailDto item : req.getProductList()) {
                dto = checkRefProduct(activity, item.getProductId());
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

        //该支付类型是否允许领取该券
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
        return dto;
    }

    /**
     * 检查商品是否适用
     *
     * @param activity  活动实体
     * @param productId 商品编号
     * @return 返回是否校验成功
     */
    private CommonBoolDto checkRefProduct(ActivityProfitEntity activity, String productId) {
        CommonBoolDto dto = new CommonBoolDto(false);
        // ApplyProductFlag空值做保护
        if (!ApplyProductFlag.ALL.getDictValue().equals(activity.getApplyProductFlag())) {
            //该商品属性是否允许参与活动
            ActivityRefProductEntity entity = activityRefProductMapper.findByBothId(activity.getId(), productId);
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
     * 组装活动信息（活动主信息+阶梯信息)
     *
     * @param activityList 活动实体列表
     * @param isMember     是否会员活动
     * @return 返回活动信息及其门槛阶梯
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    private List<ActivityDefineRsp> combinationActivity(List<ActivityProfitEntity> activityList, boolean isMember) {

        List<ActivityDefineRsp> rspList = new ArrayList<>();
        //循环获取其阶梯信息
        for (ActivityProfitEntity item : activityList) {

            //转换为传出参数
            ActivityDefineRsp rsp = new ActivityDefineRsp();
            BeanUtils.copyProperties(item, rsp);

            CouponAndActivityLabelDto labelDto = null;
            //查找标签信息
            if (!CommonUtils.isEmptyorNull(item.getActivityLable())) {
                CouponAndActivityLabelEntity labelEntity = couponAndActivityLabelMapper.findLabelById(item.getActivityLable());
                if (labelEntity != null) {
                    labelDto = new CouponAndActivityLabelDto();
                    BeanUtils.copyProperties(labelEntity, labelDto);

                }
            }

            rsp.setIsAboutMember(isMember ? "1" : "0");
            rsp.setCouponAndActivityLabel(labelDto);

            //获取指定活动的使用门槛阶梯
            List<ActivityStageCouponEntity> stageList = activityStageCouponMapper.findActivityProfitDetail(item.getId());
            if (stageList.size() > 0) {

                //数据转换：从实体类转换为Dto
                List<ActivityStageCouponDto> stageDtoList = new ArrayList<>();
                for (ActivityStageCouponEntity stage : stageList) {
                    ActivityStageCouponDto dto = new ActivityStageCouponDto();
                    BeanUtils.copyProperties(stage, dto);
                    stageDtoList.add(dto);
                }

                //BeanPropertiesConverter.copyPropertiesOfList无法对LocalDate数据进行拷贝
                //List<ActivityStageCouponDto> stageDtoList=BeanPropertiesConverter.copyPropertiesOfList(stageList, ActivityStageCouponDto.class);
                rsp.setActivityStageCouponDtoList(stageDtoList);
            }
            rspList.add(rsp);
        }
        return rspList;
    }


}




