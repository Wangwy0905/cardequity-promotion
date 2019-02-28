package com.youyu.cardequity.promotion.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.youyu.cardequity.common.base.bean.CustomHandler;
import com.youyu.cardequity.common.base.util.BeanPropertiesUtils;
import com.youyu.cardequity.common.base.util.StringUtil;
import com.youyu.cardequity.promotion.biz.constant.BusinessCode;
import com.youyu.cardequity.promotion.biz.dal.dao.*;
import com.youyu.cardequity.promotion.biz.dal.entity.*;
import com.youyu.cardequity.promotion.biz.service.ClientCouponService;
import com.youyu.cardequity.promotion.biz.service.ProductCouponService;
import com.youyu.cardequity.promotion.biz.strategy.coupon.CouponStrategy;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.cardequity.promotion.dto.*;
import com.youyu.cardequity.promotion.dto.other.*;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.*;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.FindClientCouponNumReq;
import com.youyu.cardequity.promotion.vo.rsp.FindCouponListByOrderDetailRsp;
import com.youyu.cardequity.promotion.vo.rsp.FullClientCouponRsp;
import com.youyu.cardequity.promotion.vo.rsp.UseCouponRsp;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
 * V1.3-V1 1004259-徐长焕-20181217 修改，实现按策略获取可用券的组合
 * V1.2-V1 1004246-徐长焕-20181213 修改，获取可使用优惠券功能开发
 * V1.1-V1 1004258-徐长焕-20181213 修改，获取已领取优惠券功能开发
 * V1.0-V1 1004247-徐长焕-20181207 新增，领取优惠券功能开发
 */
@Slf4j
@Service
public class ClientCouponServiceImpl extends AbstractService<String, ClientCouponDto, ClientCouponEntity, ClientCouponMapper> implements ClientCouponService {

    @Autowired
    private ClientCouponMapper clientCouponMapper;

    @Autowired
    private ProductCouponMapper productCouponMapper;

    @Autowired
    private CouponStageRuleMapper couponStageRuleMapper;

    @Autowired
    private CouponQuotaRuleMapper couponQuotaRuleMapper;

    @Autowired
    private ProfitConflictOrReUseRefMapper profitConflictOrReUseRefMapper;

    @Autowired
    private CouponRefProductMapper couponRefProductMapper;

    @Autowired
    private ClientTakeInCouponMapper clientTakeInCouponMapper;

    @Autowired
    private CouponGetOrUseFreqRuleMapper couponGetOrUseFreqRuleMapper;

    @Autowired
    private ProductCouponService productCouponService;

    /**
     * 获取客户已领取的券,含：已使用(status=1和2)，未使用（status=0且有效期内），已过期（status=0且未在有效期内）
     *
     * @param req 指定客户号，必填
     * @return 返回已领取的券
     * 开发日志
     * 1004247-徐长焕-20181213 新增
     */
    @Override
    public List<ObtainCouponViewDto> findClientCoupon(QryComonClientCouponReq req) {

        List<ClientCouponEntity> clientCouponEnts = clientCouponMapper.findClientCoupon(req.getClientId(), req.getObtainState());
        return combClientObtainCouponList(clientCouponEnts);
    }

    /**
     * 领取优惠券
     *
     * @param req 有参数clientId-客户号（必填），couponId-领取的券Id（必填）
     *            开发日志
     *            1004258-徐长焕-20181213 新增
     * @return 是否领取成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<ObtainCouponViewDto> obtainCoupon(ClientObtainCouponReq req) {
        CommonBoolDto<ObtainCouponViewDto> dto = new CommonBoolDto<>();
        dto.setSuccess(true);
        dto.setCode(NET_ERROR.getCode());

        if (req == null || StringUtil.isEmpty(req.getClientId()) || StringUtil.isEmpty(req.getClientType()) || StringUtil.isEmpty(req.getCouponId()))
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("客户编号、客户类型、优惠券编号不能为空"));

        //领取后存储的信息
        ClientCouponEntity entity = BeanPropertiesUtils.copyProperties(req, ClientCouponEntity.class);

        GetUseEnableCouponReq checkreq = BeanPropertiesUtils.copyProperties(req, GetUseEnableCouponReq.class);
        //如果需要校验相关联产品
        if (!CommonUtils.isEmptyorNull(req.getProductId())) {
            OrderProductDetailDto orderProductDetailDto = new OrderProductDetailDto();
            orderProductDetailDto.setProductId(req.getProductId());
            List<OrderProductDetailDto> productLsit = new ArrayList<>(1);
            productLsit.add(orderProductDetailDto);
            checkreq.setProductList(productLsit);
        }

        //获取领取的阶梯
        CouponStageRuleEntity couponStage = null;
        if (!CommonUtils.isEmptyorNull(req.getStageId())) {
            couponStage = couponStageRuleMapper.findCouponStageById(req.getCouponId(), req.getStageId());
            //如果找不到阶梯则传入参数有误
            if (couponStage == null) {
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("找不到指定的子券StageId=" + req.getStageId()));
            }

        } else {//保护一下如果没有传入StageId，且该券下只有一个id则自动补全
            List<CouponStageRuleEntity> stageByCouponId = couponStageRuleMapper.findStageByCouponId(req.getCouponId());
            if (stageByCouponId.size() == 1) {
                couponStage = stageByCouponId.get(0);
                entity.setStageId(couponStage.getId());
            }
            if (stageByCouponId.size() > 1) {
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("StageId不能为空,该券有多个子券无法确定领取的券"));
            }
        }

        //1.校验券基本信息是否允许领取：
        CommonBoolDto<ProductCouponEntity> fristdto = checkCouponFrist(entity, checkreq, false);
        if (!fristdto.getSuccess()) {
            BeanPropertiesUtils.copyProperties(fristdto, dto);
            return dto;
        }
        ProductCouponEntity coupon = fristdto.getData();

        //2.校验券的额度限制是否满足
        //检查指定客户的额度信息
        CouponQuotaRuleEntity quota = couponQuotaRuleMapper.findCouponQuotaRuleById(req.getCouponId());
        dto = checkCouponPersonQuota(quota, req.getClientId());
        //校验不通过直接返回
        if (!dto.getSuccess()) {
            return dto;
        }

        //检查所有客户领取额度情况
        dto = checkCouponAllQuota(quota);
        //校验不通过直接返回
        if (!dto.getSuccess()) {
            return dto;
        }

        //3.增加客户已领优惠券
        entity.setId(CommonUtils.getUUID());
        if (coupon.getAllowUseBeginDate() != null && LocalDate.now().isBefore(coupon.getAllowUseBeginDate().toLocalDate())) {
            entity.setValidStartDate(coupon.getAllowUseBeginDate());
        } else {
            entity.setValidStartDate(LocalDateTime.now());
        }
        //默认有效时间1个月
        LocalDateTime validEndDate = entity.getValidStartDate().plusMonths(1);
        //如果定义了持有时间，则需要从当前领取日期上加持有时间作为最后有效日
        if (coupon.getValIdTerm() != null && coupon.getValIdTerm() > 0) {
            validEndDate = entity.getValidStartDate().plusDays(coupon.getValIdTerm());
        } else if (coupon.getAllowUseEndDate() != null) {
            validEndDate = coupon.getAllowUseEndDate();
        }

        //如果算法是：有效结束日=min(优惠结束日,(实际领取日+期限))
        if (coupon.getUseGeEndDateFlag().equals(UseGeEndDateFlag.YES.getDictValue())) {
            if (coupon.getAllowUseEndDate() != null && validEndDate.isAfter(coupon.getAllowUseEndDate())) {
                validEndDate = coupon.getAllowUseEndDate();
            }
        }
        entity.setValidEndDate(validEndDate);

        //优惠金额以阶段设置为准
        if (couponStage != null) {
            entity.setCouponAmout(couponStage.getCouponValue());
            entity.setCouponShortDesc(couponStage.getCouponShortDesc());
            entity.setTriggerByType(couponStage.getTriggerByType());
            entity.setBeginValue(couponStage.getBeginValue());
            entity.setEndValue(couponStage.getEndValue());
        } else {
            entity.setCouponAmout(coupon.getProfitValue());
            entity.setCouponShortDesc(coupon.getCouponShortDesc());
            entity.setTriggerByType(TriggerByType.NUMBER.getDictValue());
            entity.setBeginValue(BigDecimal.ZERO);
            entity.setEndValue(CommonConstant.IGNOREVALUE);
        }

        //entity.setBusinDate(LocalDate.now());//使用时候才填入
        entity.setApplyProductFlag(coupon.getApplyProductFlag());
        entity.setCouponStrategyType(coupon.getCouponStrategyType());
        entity.setCouponShortDesc(coupon.getCouponShortDesc());
        entity.setCouponType(coupon.getCouponType());
        entity.setCouponLable(coupon.getCouponLable());
        entity.setCouponLevel(coupon.getCouponLevel());
        entity.setUpdateAuthor(req.getOperator());
        entity.setCreateAuthor(req.getOperator());
        entity.setNewFlag(CommonDict.IF_YES.getCode());
        entity.setIsEnable(CommonDict.IF_YES.getCode());
        entity.setStatus(CouponUseStatus.NORMAL.getDictValue());
        entity.setJoinOrderId(req.getActivityId());
        int count = clientCouponMapper.insertSelective(entity);
        if (count <= 0) {
            throw new BizException(COUPON_FAIL_OBTAIN.getCode(), COUPON_FAIL_OBTAIN.getFormatDesc("增加数据失败"));
        }

        ObtainCouponViewDto result = combClientObtainCouponOne(entity);
        dto.setData(result);

        return dto;
    }


    /**
     * 获取可用的优惠券:
     * 1.获取满足基本条件、使用频率、等条件
     * 2.没有校验使用门槛，使用门槛是需要和购物车选择商品列表进行计算
     *
     * @param req 获取可用券请求体
     * @return 返回可用的券
     * 开发日志
     * 1004246-徐长焕-20181213 新增
     */
    @Override
    public List<ObtainCouponViewDto> findEnableUseCoupon(GetUseEnableCouponReq req) {

        if (CommonUtils.isEmptyorNull(req.getClientId())) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("客户编号为空，无法指定客户无法获取数据"));
        }
        //获取已领取的有效优惠券：排除过期，已使用、使用中的券
        List<ClientCouponEntity> clientCouponList = clientCouponMapper.findClientValidCoupon(req.getClientId());
        //返回的结果，数组长度最大不超过有效的优惠券数量
        List<ClientCouponEntity> rsp = new ArrayList<>(clientCouponList.size());

        for (ClientCouponEntity item : clientCouponList) {
            //校验基本信息，校验阶梯使用门槛是根据买入金额和数量在下订单时进行计算
            CommonBoolDto dto = checkCouponFrist(item, req, true);
            if (!dto.getSuccess()) {
                continue;
            }
            rsp.add(item);
        }
        return combClientObtainCouponList(rsp);
    }


    /**
     * 按策略获取可用券的组合:含运费券
     * 1.根据订单或待下单商品列表校验了使用门槛
     * 2.根据冲突关系按策略计算能使用的券
     * 3.计算出每张券的适配使用的商品列表
     * 4.折扣形式设置为活动
     *
     * @param req 本次订单详情
     * @return 推荐使用券组合及应用对应商品详情
     */
    @Override
    public List<UseCouponRsp> combCouponRefProductDeal(GetUseEnableCouponReq req) {
        //返回值
        List<UseCouponRsp> rsps = new ArrayList<>(3);

        //券使用情况
        UseCouponRsp useTransferCouponRsp = null;
        UseCouponRsp globalCouponRsp = null;
        UseCouponRsp partCouponRsp = null;

        //临时变量
        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);

        List<ClientCouponEntity> enableCouponList = new ArrayList<>();
        log.info("订单指定使用优惠券列表:{}", JSONObject.toJSONString(req.getObtainCouponList()));
        //如果是指定了使用的券，检验后用使用的券
        if (req.getObtainCouponList() != null && req.getObtainCouponList().size() > 0) {
            enableCouponList = clientCouponMapper.findClientCouponByIds(req.getClientId(), req.getObtainCouponList());
        } else {
            //获取已领取的有效优惠券：排除过期，已使用、使用中的券，按优惠金额已排序后的
            enableCouponList = clientCouponMapper.findClientCoupon(req.getClientId(), "1");
        }
        log.info("订单待处理优惠券列表:{}", JSONObject.toJSONString(enableCouponList));


        //空订单或者没有可用优惠券直接返回
        if (req.getProductList() == null ||
                enableCouponList == null ||
                req.getProductList().isEmpty() ||
                enableCouponList.isEmpty()) {
            return rsps;
        }

        BigDecimal totalAmount = req.getProductList().stream().map(OrderProductDetailDto::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        for (ClientCouponEntity clientCoupon : enableCouponList) {
            //优惠券已使用的不处理
            if (!CouponUseStatus.NORMAL.getDictValue().equals(clientCoupon.getStatus()))
                continue;

            //优惠券过期的不处理
            if (!(clientCoupon.getValidStartDate().toLocalDate().compareTo(LocalDate.now()) <= 0 && clientCoupon.getValidEndDate().toLocalDate().compareTo(LocalDate.now()) >= 0))
                continue;

            //没有指定运费时运费券或免邮券不能使用
            if (!CommonUtils.isGtZeroDecimal(req.getTransferFare()) &&
                    (CouponType.TRANSFERFARE.getDictValue().equals(clientCoupon.getCouponType()) ||
                            CouponType.FREETRANSFERFARE.getDictValue().equals(clientCoupon.getCouponType()))) {
                continue;
            }

            //校验基本信息
            dto = checkCouponFrist(clientCoupon, req, true);
            if (!dto.getSuccess()) {
                log.info("该优惠券校验不通过，领取编号{}，优惠券编号{}，原因:{}", clientCoupon.getId(), clientCoupon.getCouponId(), dto.getDesc());
                continue;
            }
            ProductCouponEntity coupon = (ProductCouponEntity) dto.getData();

            //根据策略得到该活动是否满足门槛，返回满足活动适用信息
            String key = CouponStrategy.class.getSimpleName() + (CouponStrategyType.fix.getDictValue().equals(clientCoupon.getCouponStrategyType())?CouponStrategyType.stage.getDictValue():clientCoupon.getCouponStrategyType());

            CouponStrategy executor = (CouponStrategy) CustomHandler.getBeanByName(key);
            UseCouponRsp useCouponRsp = executor.applyCoupon(clientCoupon, coupon, req.getProductList());
            if (useCouponRsp != null) {
                //useCouponRsp.set(req.getClientId());
                //运费券，条件1：选择免邮券，条件2：选择>运费的最接近，如果条件2不满足选择<运费的最接近的券
                if (clientCoupon.getCouponType().equals(CouponType.TRANSFERFARE.getDictValue())) {
                    if (useTransferCouponRsp == null)
                        useTransferCouponRsp = useCouponRsp;
                    else {
                        boolean diviation = CommonUtils.minDiviation(useTransferCouponRsp.getProfitAmount(), useCouponRsp.getProfitAmount(), req.getTransferFare());
                        if (diviation) {
                            useTransferCouponRsp = useCouponRsp;
                        }
                    }
                    if (useTransferCouponRsp.getProfitAmount().compareTo(req.getTransferFare()) > 0)
                        useTransferCouponRsp.setProfitAmount(req.getTransferFare());//重置保护运费券优惠金额=运费
                }
                //免邮券：优惠金额不确定
                else if (clientCoupon.getCouponType().equals(CouponType.FREETRANSFERFARE.getDictValue())) {
                    useCouponRsp.setProfitAmount(req.getTransferFare());//重置免邮券优惠金额=运费
                    useTransferCouponRsp = useCouponRsp;
                } else {
                    //大鱼券
                    if (CouponActivityLevel.GLOBAL.getDictValue().equals(clientCoupon.getCouponLevel())) {
                        if (globalCouponRsp == null) {
                            globalCouponRsp = useCouponRsp;
                        } else {
                            BigDecimal otherProfitAmount = partCouponRsp == null ? BigDecimal.ZERO : partCouponRsp.getProfitAmount();
                            globalCouponRsp = optimalCouponBetweenMult(globalCouponRsp, useCouponRsp, otherProfitAmount, totalAmount);
                        }
                    } else {
                        if (partCouponRsp == null) {
                            partCouponRsp = useCouponRsp;
                        } else {
                            BigDecimal otherProfitAmount = globalCouponRsp == null ? BigDecimal.ZERO : globalCouponRsp.getProfitAmount();
                            partCouponRsp = optimalCouponBetweenMult(partCouponRsp, useCouponRsp, otherProfitAmount, totalAmount);
                        }
                    }
                }
            }

        }

        if (useTransferCouponRsp != null)
            rsps.add(useTransferCouponRsp);
        //装箱返回
        if (globalCouponRsp != null) {
            rsps.add(globalCouponRsp);
            //如果大鱼券的优惠金额比总金额还大，则不需要小鱼券了,其实策略实现做了保护后，这里只会出现等于
            if (totalAmount.compareTo(globalCouponRsp.getProfitAmount()) <= 0) {
                return rsps;
            }

        }
        if (partCouponRsp != null) {
            rsps.add(partCouponRsp);
            BigDecimal globalAmont = globalCouponRsp == null ? BigDecimal.ZERO : globalCouponRsp.getProfitAmount();
            if (totalAmount.compareTo(partCouponRsp.getProfitAmount().add(globalAmont)) < 0) {

                UseCouponRsp dealCouponRsp = partCouponRsp;
                //只有满减券才会导致此现象，将满减券实际优惠金额处理
                if (CouponStrategyType.discount.getDictValue().equals(partCouponRsp.getClientCoupon().getCouponStrategyType())) {
                    //此时globalCouponRsp一定不为空
                    dealCouponRsp = globalCouponRsp;
                }
                BigDecimal oldProfitAmount = dealCouponRsp.getProfitAmount();
                dealCouponRsp.setProfitAmount(partCouponRsp.getProfitAmount().add(globalAmont).subtract(totalAmount));
                for (OrderProductDetailDto item : dealCouponRsp.getProductLsit()) {
                    item.setProfitAmount(dealCouponRsp.getProfitAmount().divide(oldProfitAmount,4, RoundingMode.DOWN));
                }
            }
        }
        return rsps;
    }

    /**
     * 取最优的优惠券使用
     *
     * @param a             原认定使用优惠
     * @param b             即将比较是否使用的优惠
     * @param profitedAmont 已优惠的金额
     * @return
     */
    private UseCouponRsp optimalCouponBetweenMult(UseCouponRsp a, UseCouponRsp b, BigDecimal profitedAmont, BigDecimal totalAmount) {
        //当：优惠金额相等且等于总金额时，只会适用原始设置优惠最小的那张券
        if (a.getProfitAmount().compareTo(b.getProfitAmount()) == 0 &&
                a.getClientCoupon().getCouponAmout().compareTo(b.getClientCoupon().getCouponAmout()) > 0) {
            return b;
        }
        //取偏离度最小的组合
        boolean diviation = CommonUtils.minDiviation(a.getProfitAmount().add(profitedAmont),
                b.getProfitAmount().add(profitedAmont),
                totalAmount);
        if (diviation) {
            return b;
        }
        return a;
    }

    /**
     * 根据指定的优惠券进行校验其适用情况，并变动其状态和使用记录
     *
     * @param req 获取可用优惠券请求体
     * @return 实际使用优惠券情况
     */
    @Override
    public List<UseCouponRsp> combCouponRefProductAndUse(GetUseEnableCouponReq req) {
        List<UseCouponRsp> rsps = combCouponRefProductDeal(req);

        //将相关领券状态变更为使用中，并记录使用情况
        CommonBoolDto dto = takeInCoupon(req.getOrderId(), req.getOperator(), rsps);
        if (!dto.getSuccess()) {
            throw new BizException(COUPON_FAIL_USE.getCode(), COUPON_FAIL_USE.getFormatDesc(dto.getDesc()));
        }

        return rsps;

    }

    /**
     * 使用优惠券数据库处理：内部服务
     *
     * @param orderId  订单编号
     * @param operator 操作者
     * @param rsps     优惠券的使用情况
     * @return 是否处理成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto takeInCoupon(String orderId, String operator, List<UseCouponRsp> rsps) {
        CommonBoolDto boolDto = new CommonBoolDto(true);
        boolDto.setCode(NET_ERROR.getCode());
        //应获取自配置项
        String useType = CouponUseType.ORDER.getDictValue();
        BigDecimal productRealAmout = BigDecimal.ZERO;
        BigDecimal productProfitValue = BigDecimal.ZERO;

        for (UseCouponRsp rsp : rsps) {
            //冻结clientCoupon
            ClientCouponEntity clientCoupon = clientCouponMapper.findClientCouponById(rsp.getClientCoupon().getUuid());
            clientCoupon.setBusinDate(LocalDate.now());
            clientCoupon.setJoinOrderId(orderId);
            if (CouponUseType.CONFIRM.getDictValue().equals(useType)) {
                clientCoupon.setStatus(CouponUseStatus.USING.getDictValue());
            } else {
                clientCoupon.setStatus(CouponUseStatus.USED.getDictValue());
            }
            if (clientCouponMapper.updateByPrimaryKeySelective(clientCoupon) <= 0) {
                boolDto.setSuccess(false);
                boolDto.setCode(PARAM_ERROR.getCode());
                boolDto.setDesc("更新已领优惠券状态失败");
            }

            //增加ClientTakeInCoupon 数据
            for (OrderProductDetailDto productDetailDto : rsp.getProductLsit()) {
                ClientTakeInCouponEntity clientTakeInCoupon = new ClientTakeInCouponEntity();
                clientTakeInCoupon.setId(CommonUtils.getUUID());
                clientTakeInCoupon.setClientId(clientCoupon.getClientId());
                clientTakeInCoupon.setGetId(clientCoupon.getId());
                clientTakeInCoupon.setOrderId(clientCoupon.getJoinOrderId());
                clientTakeInCoupon.setProductId(productDetailDto.getProductId());
                clientTakeInCoupon.setSkuId(productDetailDto.getSkuId());
                clientTakeInCoupon.setProductAmount(productDetailDto.getAppCount().multiply(productDetailDto.getPrice()));
                clientTakeInCoupon.setProductCount(productDetailDto.getAppCount());
                if (CommonUtils.isGtZeroDecimal(productDetailDto.getProfitAmount())) {
                    clientTakeInCoupon.setProfitValue(productDetailDto.getProfitAmount());
                } else {
                    //主要针对满减券进行计算、免邮
                    if (CommonUtils.isGtZeroDecimal(rsp.getProfitAmount())) {
                        if (CommonUtils.isGtZeroDecimal(rsp.getTotalAmount())) {
                            //优惠金额=总优惠额*该商品总额/订单总该券涉及总金额
                            productProfitValue = rsp.getProfitAmount().multiply(productDetailDto.getTotalAmount().divide(rsp.getTotalAmount(),4, RoundingMode.DOWN));
                            clientTakeInCoupon.setProfitValue(productProfitValue);
                        } else {
                            productRealAmout = rsp.getProductLsit().stream().map(OrderProductDetailDto::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                            //做算数保护
                            if (CommonUtils.isGtZeroDecimal(productRealAmout)) {
                                productProfitValue = productDetailDto.getTotalAmount().divide(productRealAmout,4, RoundingMode.DOWN);
                                clientTakeInCoupon.setProfitValue(productProfitValue);
                            }
                        }
                    }
                }

                clientTakeInCoupon.setBusinCode(BusinessCode.USECOUPON);
                clientTakeInCoupon.setStatus(clientCoupon.getStatus());
                clientTakeInCoupon.setRemark("确认订单时处理优惠券信息");
                if (!CommonUtils.isEmptyorNull(operator)) {
                    clientTakeInCoupon.setUpdateAuthor(operator);
                    clientTakeInCoupon.setCreateAuthor(operator);
                }
                clientTakeInCoupon.setIsEnable(CommonDict.IF_YES.getCode());
                if (clientTakeInCouponMapper.insert(clientTakeInCoupon) <= 0) {
                    boolDto.setSuccess(false);
                    boolDto.setDesc("更新使用优惠券状况信息失败");
                }
            }
        }

        return boolDto;

    }


    /**
     * 撤销使用优惠券数据库处理：内部服务
     *
     * @param req 订单情况
     * @return 是否处理成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<Integer> cancelTakeInCoupon(BaseOrderInPromotionReq req) {
        CommonBoolDto<Integer> result = new CommonBoolDto<>(true);
        result.setCode(NET_ERROR.getCode());
        int i = clientCouponMapper.modRecoverByOrderinfo(req);
        result.setData(i);
        return result;
    }


    /**
     * 初步校验：
     * 1.校验内容和商品无关
     * 2.校验内容和订单金额无关
     * 3.校验的只是单一券，不存在券与券之间冲突关系校验
     * 4.会校验指定活动的冲突关系
     * 传参：
     * 1.如果要指定相关某个商品的可用优惠券，需要填充GetUseEnableCouponReq.productLsit
     *
     * @param item 领取的券
     * @param req  订单相关详情
     * @return 校验情况及对应优惠券基础信息
     */
    private CommonBoolDto<ProductCouponEntity> checkCouponFrist(ClientCouponEntity item, OrderUseEnableCouponReq req, boolean useFlag) {

        //根据券ID获取优惠券信息
        ProductCouponEntity coupon = productCouponMapper.findProductCouponById(item.getCouponId());
        if (coupon == null) {
            throw new BizException(COUPON_NOT_EXISTS.getCode(), COUPON_NOT_EXISTS.getFormatDesc("找不到指定优惠券CouponId=" + item.getCouponId()));
        }

        //校验基本信息是否符合使用条件
        CommonBoolDto dto = checkCouponBase(coupon, req);
        //校验不通过继续
        if (!dto.getSuccess()) {
            dto.setData(coupon);
            return dto;
        }

        //校验使用时间窗口
        if (useFlag) {
            //校验使用频率是否符合
            dto = checkCouponUseFreqLimit(item.getClientId(), item.getCouponId(), item.getStageId());
            if (!dto.getSuccess()) {
                dto.setData(coupon);
                return dto;
            }
        } else {
            //校验上下架状态,默认为下架状态
            if (!CouponStatus.YES.getDictValue().equals(coupon.getStatus())) {
                dto.setDesc("该优惠券已下架");
                dto.setData(coupon);
                return dto;
            }

            dto = checkCouponGetValidDate(coupon);
            //校验不通过继续
            if (!dto.getSuccess()) {
                dto.setData(coupon);
                return dto;
            }
            dto = checkCouponGetFreqLimit(item.getClientId(), item.getCouponId(), item.getStageId());
            if (!dto.getSuccess()) {
                dto.setData(coupon);
                return dto;
            }
        }

        dto.setData(coupon);
        return dto;
    }


    /**
     * 校验优惠券使用是否在允许频率内
     *
     * @param clientId 客户id
     * @param couponId 优惠券id
     * @param stageId  详细阶梯券，可为空
     * @return 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    private CommonBoolDto checkCouponUseFreqLimit(String clientId, String couponId, String stageId) {

        //获取因为频率限制无法获取的券
        List<ShortCouponDetailDto> couponDetailListByIds = couponGetOrUseFreqRuleMapper.findClinetFreqForbidCouponDetailListById(clientId, couponId, stageId,OpCouponType.USERULE.getDictValue());

        //逐一进行排除
        return excludeFreqLimit(couponDetailListByIds, couponId, stageId);

    }


    /**
     * 指定券是否受频率限制
     *
     * @param couponDetailListByIds 优惠券及阶梯基础信息
     * @param couponId              优惠券id
     * @param stageId               子券阶梯id
     * @return 检查情况
     */
    private CommonBoolDto excludeFreqLimit(List<ShortCouponDetailDto> couponDetailListByIds, String couponId, String stageId) {
        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);
        dto.setCode(NET_ERROR.getCode());
        if (couponDetailListByIds == null|| couponDetailListByIds.isEmpty() || CommonUtils.isEmptyorNull(couponId))
            return dto;

        for (ShortCouponDetailDto item:couponDetailListByIds) {
            if (item.getCouponId().equals(couponId) &&
                    ((!CommonUtils.isEmptyorNull(item.getStageId()) && item.getStageId().equals(stageId)) ||
                            (CommonUtils.isEmptyorNull(item.getStageId()) && CommonUtils.isEmptyorNull(stageId)))
                    ) {
                dto.setSuccess(false);
                dto.setCode(COUPON_FAIL_OP_FREQ.getCode());
                dto.setDesc(String.format("优惠券编号%s,阶梯编号%s超使用或获取频率限额",couponId,item.getStageId()));
                return dto;
            }
        }


        return dto;
    }


    /**
     * 校验优惠券領取是否在允许频率内
     *
     * @param clientId 客户id
     * @param couponId 优惠券id
     * @param stageId  详细阶梯券，可为空
     * @return 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    private CommonBoolDto checkCouponGetFreqLimit(String clientId, String couponId, String stageId) {

        //获取因为频率限制无法获取的券
        List<ShortCouponDetailDto> couponDetailListByIds =
                couponGetOrUseFreqRuleMapper.findClinetFreqForbidCouponDetailListById(clientId,
                        couponId, stageId,OpCouponType.GETRULE.getDictValue());

        //逐一进行排除
        return excludeFreqLimit(couponDetailListByIds, couponId, stageId);

    }

    /**
     * 根据优惠券是否在允许领取时间窗口内
     *
     * @param coupon 优惠券基本信息
     * @return 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    private CommonBoolDto checkCouponGetValidDate(ProductCouponEntity coupon) {
        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);
        dto.setCode(NET_ERROR.getCode());
        //是否在允許領取期間
        if ((coupon.getAllowGetBeginDate() != null && coupon.getAllowGetBeginDate().toLocalDate().compareTo(LocalDate.now()) > 0) ||
                (coupon.getAllowGetEndDate() != null && coupon.getAllowGetEndDate().toLocalDate().compareTo(LocalDate.now()) < 0)) {

            dto.setSuccess(false);
            dto.setDesc(COUPON_NOT_ALLOW_DATE.getFormatDesc(coupon.getAllowGetBeginDate().toLocalDate(), coupon.getAllowGetEndDate().toLocalDate()));
            return dto;
        }
        return dto;
    }

    /**
     * 校验优惠对应商品属性是否匹配
     *
     * @param coupon    优惠券
     * @param productId 商品id
     * @return 检查情况
     */
    private CommonBoolDto checkCouponForProduct(ProductCouponEntity coupon, String productId) {
        CommonBoolDto dto = new CommonBoolDto(true);

        // ApplyProductFlag空值做保护
        if (!ApplyProductFlag.ALL.getDictValue().equals(coupon.getApplyProductFlag())) {
            //该商品属性是否允许领取该券
            CouponRefProductEntity entity = couponRefProductMapper.findByBothId(coupon.getId(), productId);
            if (entity == null) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_NOT_ALLOW_PRODUCT.getFormatDesc(productId, "无", coupon.getId(), "无"));
                return dto;
            }
        }

        return dto;
    }


    /**
     * 根据优惠券基本信息校验是否可领取，涉及多个商品的
     *
     * @param coupon 优惠券基本信息
     * @param req    用于校验的商品相关属性、客户相关属性、订单相关属性、支付相关属性值
     * @return 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    private CommonBoolDto checkCouponBase(ProductCouponEntity coupon,
                                          OrderUseEnableCouponReq req) {
        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);

        //a.客户属性校验
        // 客户类型是否允许领取
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(coupon.getClientTypeSet(),
                req.getClientId())) {

            dto.setSuccess(false);
            dto.setDesc(COUPON_NOT_ALLOW_CLIENTTYPE.getFormatDesc(req.getClientType()));
            return dto;
        }

        //b.商品属性校验
        if (req.getProductList() != null && !req.getProductList().isEmpty()) {
            dto.setSuccess(false);
            if (ApplyProductFlag.ALL.getDictValue().equals(coupon.getApplyProductFlag())) {
                dto.setSuccess(true);
            } else {
                for (OrderProductDetailDto item : req.getProductList()) {
                    dto = checkCouponForProduct(coupon, item.getProductId());
                    //该券不适用任何商品，则该券不能用
                    if (dto.getSuccess())
                        dto.setSuccess(true);
                }
            }
            if (!dto.getSuccess()) {
                dto.setDesc("该券不适用本次选择的任何商品");
                return dto;
            }
        }

        //c.订单属性校验
        //该渠道信息是否允许领取
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(coupon.getEntrustWaySet(),
                req.getEntrustWay())) {
            dto.setSuccess(false);
            dto.setDesc(COUPON_NOT_ALLOW_ENTRUSTWAY.getFormatDesc(req.getEntrustWay()));
            return dto;
        }

        //d.支付属性校验
        //该银行卡是否允许领取该券
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(coupon.getBankCodeSet(),
                req.getBankCode())) {

            dto.setSuccess(false);
            dto.setDesc(COUPON_NOT_ALLOW_BANKCODE.getFormatDesc(req.getBankCode()));
            return dto;
        }

        //该支付类型是否允许领取该券
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(coupon.getPayTypeSet(),
                req.getPayType())) {
            dto.setSuccess(false);
            dto.setDesc(COUPON_NOT_ALLOW_PAYTYPE.getFormatDesc(req.getPayType()));
            return dto;
        }
        return dto;
    }


    /**
     * 校验个人的优惠限额
     *
     * @param quota    优惠券额度信息
     * @param clientId 指定校验的客户
     * @return 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    @Override
    public CommonBoolDto checkCouponPersonQuota(CouponQuotaRuleEntity quota,
                                                String clientId) {
        CommonBoolDto dto = new CommonBoolDto(true);

        //存在规则才进行校验
        /**
         * 每客每天最大优惠额
         * 每客最大优惠额
         * 每天最大优惠额
         * 最大优惠金额(资金池数量)
         * 最大发放数量(券池数量)
         */
        //大于等于该999999999值都标识不控制
        if (quota != null) {
            ClientCoupStatisticsQuotaDto statisticsQuotaDto = statisticsClientCouponQuota(clientId, quota.getCouponId());
            dto.setData(statisticsQuotaDto);

            //1.校验每客每天最大优惠额
            String validflag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPerDateAndAccMaxAmount());
            if (CommonDict.CONTINUEVALID.getCode().equals(validflag)) {

                //判断是否客户当日已领取的优惠金额是否超限
                if (quota.getPerDateAndAccMaxAmount().compareTo(statisticsQuotaDto.getClientPerDateAmount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_PERACCANDDATEQUOTA.getFormatDesc(quota.getPerDateAndAccMaxAmount(), statisticsQuotaDto.getClientPerDateAmount(), clientId));
                    return dto;
                }

            } else if (CommonDict.FAILVALID.getCode().equals(validflag)) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_PERACCANDDATEQUOTA.getFormatDesc(BigDecimal.ZERO, "忽略", clientId));
                return dto;
            }

            //2.校验每客最大优惠额
            validflag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPersonMaxAmount());
            if (CommonDict.CONTINUEVALID.getCode().equals(validflag)) {

                //判断是否客户已领取的优惠金额是否超限
                if (quota.getPerMaxAmount().compareTo(statisticsQuotaDto.getClientAmount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_PERACCQUOTA.getFormatDesc(quota.getPerMaxAmount(), statisticsQuotaDto.getClientAmount(), clientId));
                    return dto;
                }
            } else if (CommonDict.FAILVALID.getCode().equals(validflag)) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_PERACCQUOTA.getFormatDesc(BigDecimal.ZERO, "忽略", clientId));
                return dto;
            }

            //检查领取数量PersonTotalNum
            BigDecimal personTotalNum = BigDecimal.ZERO;
            List<CouponGetOrUseFreqRuleEntity> freqRuleEntities = couponGetOrUseFreqRuleMapper.findByCouponId(quota.getCouponId());
            for (CouponGetOrUseFreqRuleEntity freq : freqRuleEntities) {
                if (freq.getPersonTotalNum() != null && freq.getPersonTotalNum() > 0) {
                    personTotalNum = personTotalNum.add(new BigDecimal(freq.getPersonTotalNum().toString()));
                }
            }
            if (CommonUtils.isGtZeroDecimal(personTotalNum) && personTotalNum.compareTo(statisticsQuotaDto.getClientCount()) <= 0) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_COUNT_PERACCQUOTA.getFormatDesc(personTotalNum, statisticsQuotaDto.getClientCount(), quota.getCouponId()));
                return dto;
            }

        }

        return dto;
    }


    /**
     * 统计指定客户指定优惠券的优惠券信息，不建议使用，建议用statisticsCouponQuota进行统计
     *
     * @param clientId 指定统计的客户
     * @param couponId 指定统计的优惠券
     * @return 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    private ClientCoupStatisticsQuotaDto statisticsClientCouponQuota(String clientId,
                                                                     String couponId) {
        ClientCoupStatisticsQuotaDto dto = new ClientCoupStatisticsQuotaDto();
        dto.setClientId(clientId);
        dto.setCouponId(couponId);
        //统计获取客户当日已领取的优惠券金额总额
        List<ClientCouponEntity> clientCouponList = clientCouponMapper.findClientCouponByCouponId(clientId, couponId);
        for (ClientCouponEntity item : clientCouponList) {
            dto.setClientAmount(dto.getClientAmount().add(item.getCouponAmout()));
            dto.setClientCount(dto.getClientCount().add(BigDecimal.ONE));
            if (item.getCreateTime().compareTo(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)) >= 0) {
                dto.setClientPerDateAmount(dto.getClientPerDateAmount().add(item.getCouponAmout()));
                dto.setClientPerDateCount(dto.getClientPerDateCount().add(BigDecimal.ONE));
            }
        }
        //已经获取自数据库
        dto.setStatisticsFlag("1");
        return dto;
    }


    /**
     * 校验所有的优惠限额
     *
     * @param quota 优惠券额度信息
     * @return 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    @Override
    public CommonBoolDto checkCouponAllQuota(CouponQuotaRuleEntity quota) {
        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);

        if (quota != null) {
            ClientCoupStatisticsQuotaDto statisticsQuotaDto = statisticsCouponQuota("", "", quota.getCouponId(), "");

            //校验所有客户每天最大优惠额getPerDateMaxAmount
            String validflag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPerDateMaxAmount());
            if (CommonDict.CONTINUEVALID.getCode().equals(validflag)) {

                //判断是否所有客户当日已领取的优惠金额是否超限
                if (quota.getPerDateMaxAmount().compareTo(statisticsQuotaDto.getClientPerDateAmount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_PERDATEQUOTA.getFormatDesc(quota.getPerDateMaxAmount(), statisticsQuotaDto.getClientPerDateAmount(), quota.getCouponId()));
                    return dto;
                }
            } else if (CommonDict.FAILVALID.getCode().equals(validflag)) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_PERDATEQUOTA.getFormatDesc(BigDecimal.ZERO, "忽略", quota.getCouponId()));
                return dto;
            }

            //校验所有客户最大优惠额getMaxAmount
            validflag = CommonUtils.isQuotaValueNeedValidFlag(quota.getMaxAmount());
            if (CommonDict.CONTINUEVALID.getCode().equals(validflag)) {

                //判断是否所有客户已领取的优惠金额是否超限
                if (quota.getMaxAmount().compareTo(statisticsQuotaDto.getClientAmount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_QUOTA.getFormatDesc(quota.getMaxAmount(), statisticsQuotaDto.getClientAmount(), quota.getCouponId()));
                    return dto;
                }

            } else if (CommonDict.FAILVALID.getCode().equals(validflag)) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_QUOTA.getFormatDesc(BigDecimal.ZERO, "忽略", quota.getCouponId()));
                return dto;
            }

            BigDecimal maxCount = BigDecimal.ZERO;
            if (quota.getMaxCount() != null)
                maxCount = new BigDecimal(quota.getMaxCount().toString());
            //校验所有客户最大领取数量maxCount:quota.getMaxCount()
            validflag = CommonUtils.isQuotaValueNeedValidFlag(maxCount);
            if (CommonDict.CONTINUEVALID.getCode().equals(validflag)) {

                //判断是否所有客户已领取的优惠金额是否超限
                if (maxCount.compareTo(statisticsQuotaDto.getClientCount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_COUNT_QUOTA.getFormatDesc(maxCount, statisticsQuotaDto.getClientCount(), quota.getCouponId()));
                    return dto;
                }
            } else if (CommonDict.FAILVALID.getCode().equals(validflag)) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_COUNT_QUOTA.getFormatDesc(BigDecimal.ZERO, "忽略", quota.getCouponId()));
                return dto;
            }

        }

        return dto;
    }


    /**
     * 统计指定优惠券的领取情况信息
     *
     * @param couponId 指定统计的优惠券
     * @param id       领取编号
     * @param clientId 客户编号
     * @param stageId  子券编号
     * @return 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    private ClientCoupStatisticsQuotaDto statisticsCouponQuota(String id,
                                                               String clientId,
                                                               String couponId,
                                                               String stageId) {
        //统计所有客户领取的优惠券金额总额，直接通过sql统计增加效率
        ClientCoupStatisticsQuotaDto dto = clientCouponMapper.statisticsCouponByCommon(id, clientId, couponId, stageId);
        if (dto == null)
            dto = new ClientCoupStatisticsQuotaDto();
        dto.setCouponId(couponId);
        //已经获取自数据库
        dto.setStatisticsFlag(CommonDict.IF_YES.getCode());

        return dto;
    }

    /**
     * 获取客户当前有效的券
     *
     * @param req 客户及商品信息
     * @return 返回已领取的券
     * 开发日志
     */
    @Override
    public List<ObtainCouponViewDto> findValidClientCouponForProduct(BaseClientProductReq req) {
        List<ClientCouponEntity> clientCouponEnts = clientCouponMapper.findClientValidCouponByProduct(req.getClientId(), req.getProductId(), req.getSkuId());
        return combClientObtainCouponList(clientCouponEnts);
    }


    /**
     * 按订单信息获取可用券
     *
     * @param req 本次订单详情
     * @return 推荐使用券组合及应用对应商品详情
     */
    @Override
    public FindCouponListByOrderDetailRsp findCouponListByOrderDetail(OrderUseEnableCouponReq req) {
        //返回值
        FindCouponListByOrderDetailRsp result = new FindCouponListByOrderDetailRsp();

        //临时变量
        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);

        List<ClientCouponEntity> enableCouponList = clientCouponMapper.findClientCoupon(req.getClientId(), "1");

        //空订单或者没有可用优惠券直接返回
        if (req.getProductList() == null ||
                enableCouponList == null ||
                req.getProductList().size() <= 0 ||
                enableCouponList.size() <= 0) {
            return result;
        }
        for (ClientCouponEntity clientCoupon : enableCouponList) {

            if (!CouponUseStatus.NORMAL.getDictValue().equals(clientCoupon.getStatus()))
                continue;
            if (LocalDate.now().compareTo(clientCoupon.getValidEndDate().toLocalDate()) > 0 || LocalDate.now().compareTo(clientCoupon.getValidStartDate().toLocalDate()) < 0)
                continue;
            //校验基本信息
            dto = checkCouponFrist(clientCoupon, req, true);
            ProductCouponEntity coupon = (ProductCouponEntity) dto.getData();
            ClientCouponDto clientCouponDto = BeanPropertiesUtils.copyProperties(clientCoupon, ClientCouponDto.class);
            //如果领取后被删除
            if (coupon == null) {
                coupon = BeanPropertiesUtils.copyProperties(clientCouponDto.switchSimpleMol().getProductCouponDto(), ProductCouponEntity.class);
            }
            if (!dto.getSuccess()) {
                //不是有效期外的,计入有效但是订单不可用的列表
                if (clientCoupon.getValidEndDate().toLocalDate().compareTo(LocalDate.now()) >= 0) {
                    FullClientCouponRsp item = combClientFullObtainCouponOne(clientCoupon);
                    result.getCouponUnEnableList().add(item);
                }
                continue;
            }
            //根据策略得到该活动是否满足门槛，返回满足活动适用信息
            String key = CouponStrategy.class.getSimpleName() + (CouponStrategyType.fix.getDictValue().equals(clientCoupon.getCouponStrategyType())?CouponStrategyType.stage.getDictValue():clientCoupon.getCouponStrategyType());
            CouponStrategy executor = (CouponStrategy) CustomHandler.getBeanByName(key);
            UseCouponRsp useCouponRsp = executor.applyCoupon(clientCoupon, coupon, req.getProductList());
            if (useCouponRsp != null) {
                FullClientCouponRsp item = combClientFullObtainCouponOne(clientCoupon);
                result.getCouponEnableList().add(item);
            } else {
                FullClientCouponRsp item = combClientFullObtainCouponOne(clientCoupon);
                result.getCouponUnEnableList().add(item);
            }

        }

        return result;
    }


    /**
     * 组合领取视图对象集合
     *
     * @param clientCouponEnts 领取券集合
     * @return 领取券视图集合
     */
    @Override
    public List<ObtainCouponViewDto> combClientObtainCouponList(List<ClientCouponEntity> clientCouponEnts) {
        List<ObtainCouponViewDto> result = new ArrayList<>();
        if (clientCouponEnts == null || clientCouponEnts.isEmpty())
            return result;
        Set<String> ids = new HashSet<>();
        for (ClientCouponEntity item : clientCouponEnts) {
            if (!ids.contains(item.getCouponId()))
                ids.add(item.getCouponId());
            ObtainCouponViewDto viewDto = BeanPropertiesUtils.copyProperties(item, ObtainCouponViewDto.class);

            viewDto.setProfitValue(item.getCouponAmout());
            viewDto.setStatus(CouponStatus.YES.getDictValue());//拷贝会覆盖状态
            viewDto.setObtainId(item.getId());
            viewDto.setUuid(item.getCouponId());
            viewDto.setStageId(item.getStageId());
            viewDto.setObtainState(CommonConstant.OBTAIN_STATE_YES);
            if (CouponUseStatus.USED.getDictValue().equals(item.getStatus()) ||
                    CouponUseStatus.USING.getDictValue().equals(item.getStatus())) {
                viewDto.setObtainState(CommonConstant.OBTAIN_STATE_USE);
            } else if (item.getValidEndDate().toLocalDate().compareTo(LocalDate.now()) < 0) {
                viewDto.setObtainState(CommonConstant.OBTAIN_STATE_OVERDUE);
            } else if (item.getValidStartDate().toLocalDate().compareTo(LocalDate.now()) > 0) {
                viewDto.setObtainState(CommonConstant.OBTAIN_STATE_UNSTART);
            }
            result.add(viewDto);
        }

        if (!ids.isEmpty()) {
            List<CouponDetailDto> detailDtos = productCouponService.findCouponListByIds(new ArrayList<>(ids));
            //已经按券id排序后的
            List<CouponRefProductEntity> productEntities = couponRefProductMapper.findByCouponIds(new ArrayList<>(ids));
            for (ObtainCouponViewDto item : result) {
                for (CouponDetailDto dto : detailDtos) {
                    if (item.getUuid().equals(dto.getProductCouponDto().getId())) {
                        item = BeanPropertiesUtils.copyProperties(dto.switchToView(), item);
                        item.setLabelDto(dto.getProductCouponDto().getLabelDto());
                        break;
                    }
                }
                Iterator<CouponRefProductEntity> it = productEntities.iterator();
                boolean isExists = false;
                while (it.hasNext()) {
                    CouponRefProductEntity entity = it.next();
                    if (item.getUuid().equals(entity.getCouponId())) {
                        isExists = true;
                        if (item.getProductList() == null)
                            item.setProductList(new ArrayList<>());
                        BaseProductReq productReq = new BaseProductReq();
                        productReq.setProductId(entity.getProductId());
                        productReq.setSkuId(entity.getSkuId());
                        item.getProductList().add(productReq);
                        //与后续循环不再需要
                        it.remove();
                    } else {
                        //productEntities是一个有序集合
                        if (isExists) {
                            break;
                        }
                    }

                }

            }
        }
        return result;
    }


    /**
     * 组合单个领取视图对象
     *
     * @param item 领取券
     * @return 领取券视图
     */
    @Override
    public ObtainCouponViewDto combClientObtainCouponOne(ClientCouponEntity item) {
        if (item == null)
            return null;

        ObtainCouponViewDto result = BeanPropertiesUtils.copyProperties(item, ObtainCouponViewDto.class);
        result.setUuid(item.getCouponId());
        result.setStageId(item.getStageId());
        result.setObtainState(CommonConstant.OBTAIN_STATE_YES);
        result.setStatus(CouponStatus.YES.getDictValue());
        if (CouponUseStatus.USED.getDictValue().equals(item.getStatus()) ||
                CouponUseStatus.USING.getDictValue().equals(item.getStatus())) {
            result.setObtainState(CommonConstant.OBTAIN_STATE_USE);
        } else if (item.getValidEndDate().toLocalDate().compareTo(LocalDate.now()) < 0) {
            result.setObtainState(CommonConstant.OBTAIN_STATE_OVERDUE);
        } else if (item.getValidStartDate().toLocalDate().compareTo(LocalDate.now()) > 0) {
            result.setObtainState(CommonConstant.OBTAIN_STATE_UNSTART);
        }


        BaseCouponReq req = new BaseCouponReq();
        req.setCouponId(item.getCouponId());
        CouponDetailDto detailDto = productCouponService.findCouponById(req);
        result = BeanPropertiesUtils.copyProperties(detailDto.switchToView(), result);
        result.setLabelDto(detailDto.getProductCouponDto().getLabelDto());
        List<String> ids = new ArrayList<>();
        ids.add(item.getCouponId());
        List<CouponRefProductEntity> productEntities = couponRefProductMapper.findByCouponIds(ids);
        for (CouponRefProductEntity entity : productEntities) {
            if (result.getProductList() == null)
                result.setProductList(new ArrayList<>());
            BaseProductReq productReq = new BaseProductReq();
            productReq.setProductId(entity.getProductId());
            productReq.setSkuId(entity.getSkuId());
            result.getProductList().add(productReq);
        }

        return result;
    }


    /**
     * 组合领取详情对象集合
     *
     * @param clientCouponEnts 领取券集合
     * @return 领取券视图集合
     */
    @Override
    public List<FullClientCouponRsp> combClientFullObtainCouponList(List<ClientCouponEntity> clientCouponEnts) {
        List<FullClientCouponRsp> result = new ArrayList<>();
        if (clientCouponEnts == null || clientCouponEnts.isEmpty())
            return result;
        List<String> ids = new ArrayList<>();
        for (ClientCouponEntity item : clientCouponEnts) {
            ids.add(item.getCouponId());
            ClientCouponDto dto = BeanPropertiesUtils.copyProperties(item, ClientCouponDto.class);
            FullClientCouponRsp one = new FullClientCouponRsp();
            one.setClientCoupon(dto);
            result.add(one);
        }

        if (!ids.isEmpty()) {
            List<CouponDetailDto> detailDtos = productCouponService.findCouponListByIds(ids);
            for (FullClientCouponRsp item : result) {
                for (CouponDetailDto dto : detailDtos) {
                    if (item.getClientCoupon().getCouponId().equals(dto.getProductCouponDto().getId())) {
                        item.setCoupon(dto);
                        break;
                    }
                }
            }
        }

        return result;
    }


    /**
     * 组合单个领取详情对象
     *
     * @param item 领取券
     * @return 领取券视图
     */
    @Override
    public FullClientCouponRsp combClientFullObtainCouponOne(ClientCouponEntity item) {
        if (item == null)
            return null;

        ClientCouponDto dto = BeanPropertiesUtils.copyProperties(item, ClientCouponDto.class);
        FullClientCouponRsp result = new FullClientCouponRsp();
        result.setClientCoupon(dto);

        BaseCouponReq req = new BaseCouponReq();
        req.setCouponId(item.getCouponId());
        CouponDetailDto detailDto = productCouponService.findCouponById(req);
        result.setCoupon(detailDto);

        return result;
    }


    /**
     * 获取客户已领取的券,含：已使用(status=1和2)，未使用（status=0且有效期内），已过期（status=0且未在有效期内）
     *
     * @param req 指定客户号，必填
     * @return 返回已领取的券数量
     */
    @Override
    public FindClientCouponNumReq findClientCouponNum(QryComonClientCouponReq req) {

        FindClientCouponNumReq result = clientCouponMapper.findClientCouponNnm(req.getClientId(), req.getObtainState());
        return result;
    }


    /**
     * 【App】客户领取券变更new标识
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto changeClientCouponNewFlag(BaseClientReq req) {
        CommonBoolDto result = new CommonBoolDto(true);
        result.setCode(NET_ERROR.getCode());
        clientCouponMapper.modClientCouponNewFlag(req.getClientId());

        return result;
    }

}




