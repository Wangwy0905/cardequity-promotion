package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.promotion.biz.constant.CommonConstant;
import com.youyu.cardequity.promotion.biz.dal.dao.*;
import com.youyu.cardequity.promotion.biz.dal.entity.*;
import com.youyu.cardequity.promotion.biz.service.ClientCouponService;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.*;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.*;
import com.youyu.cardequity.promotion.vo.req.ClientObtainCouponReq;
import com.youyu.cardequity.promotion.vo.req.GetUseEnableCouponReq;
import com.youyu.cardequity.promotion.vo.rsp.UseCouponRsp;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
    private ActivityProfitMapper activityProfitMapper;

    @Autowired
    private ProfitConflictOrReUseRefMapper profitConflictOrReUseRefMapper;

    @Autowired
    private CouponRefProductMapper couponRefProductMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCouponServiceImpl.class);


    /**
     * 获取客户已领取的券,含：已使用(status=1和2)，未使用（status=0且有效期内），已过期（status=0且未在有效期内）
     *
     * @return 返回已领取的券
     * @Param clientId:指定客户号，必填
     * 开发日志
     * 1004247-徐长焕-20181213 新增
     */
    @Override
    public List<ClientCouponDto> findClientCoupon(String clientId) {
        List<ClientCouponEntity> clientCouponEnts = clientCouponMapper.findClientCoupon(clientId);
        return BeanPropertiesConverter.copyPropertiesOfList(clientCouponEnts, ClientCouponDto.class);

    }

    /**
     * 领取优惠券
     *
     * @return 是否领取成功
     * @Param req:有参数clientId-客户号（必填），couponId-领取的券Id（必填）
     * 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<ClientCouponDto> obtainCoupon(ClientObtainCouponReq req) {
        CommonBoolDto<ClientCouponDto> dto = new CommonBoolDto<ClientCouponDto>();
        dto.setSuccess(true);

        //领取后存储的信息
        ClientCouponEntity entity = new ClientCouponEntity();
        BeanUtils.copyProperties(req, entity);

        GetUseEnableCouponReq checkreq = new GetUseEnableCouponReq();
        BeanUtils.copyProperties(req, checkreq);
        //如果需要校验相关联产品
        if (!CommonUtils.isEmptyorNull(req.getProductId())) {
            OrderProductDetailDto orderProductDetailDto = new OrderProductDetailDto();
            orderProductDetailDto.setProductId(req.getProductId());
            List<OrderProductDetailDto> productLsit = new ArrayList<>(1);
            productLsit.add(orderProductDetailDto);
            checkreq.setProductList(productLsit);
        }

        //1.校验券基本信息是否允许领取：
        CommonBoolDto<ProductCouponEntity> fristdto = checkCouponFrist(entity, checkreq, false);
        if (!fristdto.getSuccess()) {
            BeanUtils.copyProperties(fristdto, dto);
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

        //获取领取的阶梯
        CouponStageRuleEntity couponStage = null;
        if (!CommonUtils.isEmptyorNull(req.getStageId())) {
            couponStage = couponStageRuleMapper.findCouponStageById(coupon.getId(), req.getStageId());
            //如果找不到阶梯则传入参数有误
            if (couponStage == null) {
                throw new BizException(COUPON_NOT_EXISTS.getCode(), COUPON_NOT_EXISTS.getDesc());
            }

        } else {//保护一下如果没有传入StageId，且该券下只有一个id则自动补全
            List<CouponStageRuleEntity> stageByCouponId = couponStageRuleMapper.findStageByCouponId(coupon.getId());
            if (stageByCouponId.size() == 1)
                couponStage = stageByCouponId.get(0);
            if (stageByCouponId.size() > 1) {
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getDesc());
            }
        }

        //3.增加客户已领优惠券
        entity.setId(CommonUtils.getUUID());
        entity.setValidStartDate(LocalDate.now());
        //默认有效时间1个月
        LocalDate validEndDate = LocalDate.now().plusMonths(1);
        //如果定义了持有时间，则需要从当前领取日期上加持有时间作为最后有效日
        if (coupon.getValIdTerm() != null && coupon.getValIdTerm().intValue() > 0) {
            validEndDate = LocalDate.now().plusDays(coupon.getValIdTerm());
        }

        //如果算法是：有效结束日=min(优惠结束日,(实际领取日+期限))
        if (coupon.getUseGeEndDateFlag().equals(UseGeEndDateFlag.YES)) {
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

        entity.setCouponStrategyType(coupon.getCouponStrategyType());
        entity.setCouponShortDesc(coupon.getCouponShortDesc());
        entity.setCouponType(coupon.getCouponType());
        entity.setCouponLable(coupon.getCouponLable());
        entity.setCouponLevel(coupon.getCouponLevel());
        entity.setIsEnable(CommonDict.IF_YES.getCode());
        entity.setStatus(CouponStatus.NORMAL.getDictValue());
        entity.setJoinOrderId(req.getActivityId());
        int count = clientCouponMapper.insertSelective(entity);
        if (count <= 0) {
            throw new BizException(COUPON_FAIL_OBTAIN.getCode(), COUPON_FAIL_OBTAIN.getDesc());
        }
        ClientCouponDto rsp = new ClientCouponDto();
        BeanUtils.copyProperties(entity, rsp);
        dto.setData(rsp);

        return dto;
    }


    /**
     * 获取可用的优惠券:
     * 1.获取满足基本条件、使用频率、等条件
     * 2.没有校验使用门槛，使用门槛是需要和购物车选择商品列表进行计算
     *
     * @param req
     * @return 开发日志
     * 1004246-徐长焕-20181213 新增
     */
    @Override
    public List<ClientCouponDto> findEnableUseCoupon(GetUseEnableCouponReq req) {

        if (CommonUtils.isEmptyorNull(req.getClinetId())) {
            throw new BizException(COUPON_NOT_EXISTS.getCode(), COUPON_NOT_EXISTS.getDesc());
        }
        //获取已领取的有效优惠券：排除过期，已使用、使用中的券
        List<ClientCouponEntity> clientCouponList = clientCouponMapper.findClientValidCoupon(req.getClinetId());
        //返回的结果，数组长度最大不超过有效的优惠券数量
        List<ClientCouponDto> rsp = new ArrayList<>(clientCouponList.size());
        ClientCouponDto rspdto = null;

        for (ClientCouponEntity item : clientCouponList) {
            //校验基本信息，校验阶梯使用门槛是根据买入金额和数量在下订单时进行计算
            CommonBoolDto dto = checkCouponFrist(item, req, true);
            if (!dto.getSuccess()) {
                continue;
            }

            rspdto = new ClientCouponDto();
            BeanUtils.copyProperties(item, rspdto);
            rsp.add(rspdto);
        }

        return rsp;
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

        //普通券的使用情况
        Map<String, UseCouponRsp> optimalUseCouponRsp = new HashedMap(2);
        optimalUseCouponRsp.put(CouponActivityLevel.GLOBAL.getDictValue(), null);
        optimalUseCouponRsp.put(CouponActivityLevel.PART.getDictValue(), null);
        //临时的券使用情况
        UseCouponRsp useCouponRsp = null;
        //运费券使用情况
        UseCouponRsp useTransferCouponRsp = null;
        //临时变量
        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);

        //优惠券基本信息
        ProductCouponEntity coupon = null;

        List<ClientCouponEntity> enableCouponList = null;
        //如果是指定了使用的券，检验后用使用的券
        if (req.getObtainCouponList() != null && req.getObtainCouponList().size() > 0) {
            enableCouponList = clientCouponMapper.findClientCouponByIds(req.getClinetId(), req.getObtainCouponList());
        } else {
            //获取已领取的有效优惠券：排除过期，已使用、使用中的券，按优惠金额已排序后的
            //等阶券无法参与排序
            enableCouponList = clientCouponMapper.findClientCoupon(req.getClinetId());
        }

        //空订单或者没有可用优惠券直接返回
        if (req.getProductList() == null ||
                enableCouponList == null ||
                req.getProductList().size() <= 0 ||
                enableCouponList.size() <= 0) {
            return rsps;
        }
        //输入参数不要变动，需要copy副本
        List<OrderProductDetailDto> productList = BeanPropertiesConverter.copyPropertiesOfList(req.getProductList(), OrderProductDetailDto.class);

        for (ClientCouponEntity clientCoupon : enableCouponList) {
            //校验基本信息
            dto = checkCouponFrist(clientCoupon, req, true);
            if (!dto.getSuccess()) {
                continue;
            }

            coupon = (ProductCouponEntity) dto.getData();

            //装箱返回数据
            useCouponRsp = new UseCouponRsp();
            ClientCouponDto clientCouponDto = new ClientCouponDto();
            BeanUtils.copyProperties(clientCoupon,clientCouponDto);
            useCouponRsp.setClientCoupon(clientCouponDto);
            useCouponRsp.setProfitAmount(clientCoupon.getCouponAmout());

            //存在运费时才处理运费相关券，运费券也只能使用一张
            if (req.getTransferFare() == null ||
                    req.getTransferFare().compareTo(BigDecimal.ZERO) <= 0) {
                //运费券，条件1：选择免邮券，条件2：选择>运费的最接近，如果条件2不满足选择<运费的最接近的券
                if (clientCoupon.getCouponType().equals(CouponType.TRANSFERFARE.getDictValue()) ||
                        clientCoupon.getCouponType().equals(CouponType.FREETRANSFERFARE.getDictValue())) {
                    continue;
                }
            } else {
                if (clientCoupon.getCouponType().equals(CouponType.TRANSFERFARE.getDictValue()) ||
                        clientCoupon.getCouponType().equals(CouponType.FREETRANSFERFARE.getDictValue())) {
                    //优惠金额刚好等于运费券则不需要再处理：1.运费券金额刚刚好等于运费，2.免邮券
                    if (useTransferCouponRsp != null && useTransferCouponRsp.getProfitAmount().compareTo(req.getTransferFare()) == 0)
                        continue;
                }
            }

            //保护一下如果券下面只有一个阶梯，算作使用该券
            CouponStageRuleEntity stage = protectCompletion(clientCoupon);

            if (!CommonUtils.isEmptyorNull(clientCoupon.getStageId())) {
                clientCouponDto.setStageId(stage.getId());
                clientCoupon.setStageId(stage.getId());

            }
            /**
             * 1.计算本券的适配商品时
             * 2.必须达到使用条件
             */
            if (VaildCouponForUse(productList, useCouponRsp, clientCoupon, coupon)) {

                //运费券，条件1：选择免邮券，条件2：选择>运费的最接近，如果条件2不满足选择<运费的最接近的券
                if (clientCoupon.getCouponType().equals(CouponType.TRANSFERFARE.getDictValue())) {
                    if (useTransferCouponRsp == null)
                        useTransferCouponRsp = useCouponRsp;
                    else {
                        BigDecimal diff = useTransferCouponRsp.getProfitAmount().subtract(req.getTransferFare());
                        BigDecimal diff2 = useCouponRsp.getProfitAmount().subtract(req.getTransferFare());
                        //1.都高于原运费，取最小的;2.都低于运费取最大值，本券金额大于运费使用本券
                        if ((diff.compareTo(BigDecimal.ZERO) > 0 && diff2.compareTo(BigDecimal.ZERO) > 0 && diff.compareTo(diff2) > 0) ||
                                (diff.compareTo(BigDecimal.ZERO) < 0 && diff.compareTo(diff2) < 0)) {
                            useTransferCouponRsp = useCouponRsp;
                        }
                    }
                }
                //免邮券：优惠金额不确定
                else if (clientCoupon.getCouponType().equals(CouponType.FREETRANSFERFARE.getDictValue())) {
                    useCouponRsp.setProfitAmount(req.getTransferFare());//重置免邮券优惠金额=运费
                    useTransferCouponRsp = useCouponRsp;
                } else {
                    //普通的券比较两者得到最优惠
                    if (optimalUseCouponRsp.get(clientCoupon.getCouponLevel()) == null ||
                            useCouponRsp.getProfitAmount().compareTo(optimalUseCouponRsp.get(clientCoupon.getCouponLevel()).getProfitAmount()) < 0) {
                        optimalUseCouponRsp.put(clientCoupon.getCouponLevel(), useCouponRsp);
                    }
                }
            }
        }

        //装箱返回
        if (optimalUseCouponRsp.get(CouponActivityLevel.GLOBAL.getDictValue()) != null)
            rsps.add(optimalUseCouponRsp.get(CouponActivityLevel.GLOBAL.getDictValue()));
        if (optimalUseCouponRsp.get(CouponActivityLevel.PART.getDictValue()) != null)
            rsps.add(optimalUseCouponRsp.get(CouponActivityLevel.PART.getDictValue()));
        if (useTransferCouponRsp != null)
            rsps.add(useTransferCouponRsp);
        optimalUseCouponRsp.clear();

        return rsps;
    }


    /**
     * 保护并补全阶梯信息
     *
     * @param clientCoupon
     * @return
     */
    private CouponStageRuleEntity protectCompletion(ClientCouponEntity clientCoupon) {
        CouponStageRuleEntity stage = null;
        //保护一下如果券下面只有一个阶梯，算作使用该券
        if (CommonUtils.isEmptyorNull(clientCoupon.getStageId())) {
            List<CouponStageRuleEntity> stageByCouponId = couponStageRuleMapper.findStageByCouponId(clientCoupon.getCouponId());
            //领券只能指定某个阶梯
            if (stageByCouponId.size() > 1)
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getDesc());
            if (stageByCouponId.size() == 1) {
                stage = stageByCouponId.get(0);
            }
        } else {
            //先取得对应阶梯的信息
            stage = couponStageRuleMapper.findCouponStageById(clientCoupon.getCouponId(),
                    clientCoupon.getStageId());
            if (stage == null) {
                throw new BizException(COUPON_NOT_EXISTS.getCode(), COUPON_NOT_EXISTS.getDesc());
            }
        }
        return stage;
    }

    /**
     * 计算优惠金额
     *
     * @param useCouponRsp
     * @param productItem  适用上的商品信息：含价格、数量
     * @param clientCoupon 领取券信息：领取人、编号
     * @param applyNum     适用的数量，对于等阶满减券而言表示等阶满减券的步长倍数
     * @return
     */
    private BigDecimal calculationProfitAmount(UseCouponRsp useCouponRsp, OrderProductDetailDto productItem, ClientCouponEntity clientCoupon, BigDecimal applyNum) {
        //因为后面券也会改变product对象信息，所以每个券能适用的商品都需要拷贝一个副本
        OrderProductDetailDto product = new OrderProductDetailDto();
        BeanUtils.copyProperties(productItem, product);
        useCouponRsp.getProductLsit().add(product);
        //优惠金额
        BigDecimal profitAmount = BigDecimal.ZERO;
        //买入总额
        BigDecimal buyAmount = BigDecimal.ZERO;
        //折扣券
        if (CouponStrategyType.discount.getDictValue().equals(clientCoupon.getCouponStrategyType())) {
            //无指定适用范围则按所有数量都打折
            if (applyNum.compareTo(BigDecimal.ZERO) <= 0) {
                applyNum = product.getAppCount();
            }
            //优惠金额=总额*(1-折扣)
            buyAmount = product.getPrice().multiply(applyNum);
            profitAmount = buyAmount.multiply(BigDecimal.ONE.subtract(clientCoupon.getCouponAmout()));
            product.setProfitAmount(profitAmount);
            useCouponRsp.setProfitAmount(useCouponRsp.getProfitAmount().add(profitAmount));//加总优惠金额
            //满减券只需记录总的优惠金额，记到每个商品即单位上是无意义的（比如5个都满足，哪几个算有优惠？）
        } else if (CouponStrategyType.equalstage.getDictValue().equals(clientCoupon.getCouponStrategyType())) {
            //每满200减20，discountApplyNum表示等阶满减券的步长倍数
            profitAmount = clientCoupon.getCouponAmout().multiply(applyNum);
            useCouponRsp.setProfitAmount(useCouponRsp.getProfitAmount().add(profitAmount));//加总优惠金额
        } else {//普通满减券，每次适配到商品都不会改变其优惠值
            profitAmount = clientCoupon.getCouponAmout();
            useCouponRsp.setProfitAmount(useCouponRsp.getProfitAmount().add(profitAmount));//加总优惠金额
        }
        return profitAmount;
    }


    /**
     * 校验优惠券使用门槛
     *
     * @param productList
     * @param useCouponRsp
     * @param clientCoupon
     * @param coupon
     * @return
     */
    private boolean VaildCouponForUse(List<OrderProductDetailDto> productList, UseCouponRsp useCouponRsp, ClientCouponEntity clientCoupon, ProductCouponEntity coupon) {
        //满足使用条件
        boolean flag = false;
        //临时变量
        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);
        BigDecimal condition = BigDecimal.ZERO;
        String discountApplyStage = DiscountApplyStage.ALL.getDictValue();

        if (CommonUtils.isEmptyorNull(clientCoupon.getStageId()))
            clientCoupon.setBeginValue(BigDecimal.ZERO);
        //优惠券适配商品统计，该productList按单价进行排序后的
        for (OrderProductDetailDto productItem : productList) {

            //校验符合商品基本属性
            dto = checkCouponForProduct(coupon, productItem.getProductId());
            if (dto.getSuccess()) {

                //按数量统计
                if (clientCoupon.getTriggerByType().equals(TriggerByType.NUMBER.getDictValue())) {
                    condition = condition.add(productItem.getAppCount());
                    if (condition.compareTo(clientCoupon.getBeginValue()) >= 0) {
                        flag = true;
                        BigDecimal applyNum = productItem.getAppCount();
                        //折扣券：虽然按折扣只从小到打排序，但是并不表示排最前的是订单最优惠的，因为其在订单中应用商品范围是不同的，所以需要对所有券循环
                        if (CouponStrategyType.discount.getDictValue().equals(clientCoupon.getCouponStrategyType())) {
                            //算入门槛内的才进行打折，否则全部都要打折
                            if (DiscountApplyStage.CONDITION.getDictValue().equals(discountApplyStage)) {
                                applyNum = productItem.getAppCount().subtract(condition.subtract(clientCoupon.getBeginValue()));
                                //优惠金额=达到优惠条件总额*(1-折扣),达到优惠条件总额=(买入数量-(总数量-达标门槛))*价格
                                calculationProfitAmount(useCouponRsp, productItem, clientCoupon, applyNum);
                                break;
                            }

                        } else if (CouponStrategyType.equalstage.getDictValue().equals(clientCoupon.getCouponStrategyType())) {
                            if (clientCoupon.getBeginValue().compareTo(BigDecimal.ZERO) > 0)
                                applyNum = condition.divide(clientCoupon.getBeginValue()).setScale(0, BigDecimal.ROUND_DOWN);
                        } else {
                            calculationProfitAmount(useCouponRsp, productItem, clientCoupon, applyNum);
                            break;//普通满减券满足条件后计算下一个券
                        }
                        calculationProfitAmount(useCouponRsp, productItem, clientCoupon, applyNum);

                    }
                } else {
                    condition = condition.add(productItem.getAppCount().multiply(productItem.getPrice()));
                    if (condition.compareTo(clientCoupon.getBeginValue()) >= 0) {
                        flag = true;
                        BigDecimal applyNum = productItem.getAppCount();
                        //折扣券：虽然按折扣只从小到打排序，但是并不表示排最前的是订单最优惠的，因为其在订单中应用商品范围是不同的，所以需要对所有券循环
                        if (CouponStrategyType.discount.getDictValue().equals(clientCoupon.getCouponStrategyType())) {
                            //算入门槛内的才进行打折，否则全部都要打折
                            if (DiscountApplyStage.CONDITION.getDictValue().equals(discountApplyStage)) {

                                BigDecimal t_count = condition.subtract(clientCoupon.getBeginValue()).divide(productItem.getPrice()).setScale(0, BigDecimal.ROUND_DOWN);
                                applyNum = productItem.getAppCount().subtract(t_count);
                                //优惠金额=达到优惠条件总额*(1-折扣),达到优惠条件总额=(买入数量-(总数量-达标门槛))*价格
                                calculationProfitAmount(useCouponRsp, productItem, clientCoupon, applyNum);
                                break;
                            }

                        } else if (CouponStrategyType.equalstage.getDictValue().equals(clientCoupon.getCouponStrategyType())) {
                            if (clientCoupon.getBeginValue().compareTo(BigDecimal.ZERO) > 0)
                                applyNum = condition.divide(clientCoupon.getBeginValue()).setScale(0, BigDecimal.ROUND_DOWN);
                        } else {//普通满减券、运费券等，达到门槛后优惠金额固定
                            calculationProfitAmount(useCouponRsp, productItem, clientCoupon, applyNum);
                            break;
                        }
                        calculationProfitAmount(useCouponRsp, productItem, clientCoupon, applyNum);

                    }
                }

            }
        }
        return flag;
    }

    /**
     * 保守策略下计算[普通券]最优券组合及其使用情况
     *
     * @param req
     * @return
     */
    public List<UseCouponRsp> stageFastCompByCommon(GetUseEnableCouponReq req, String couponLevel) {
        //定义返回结果
        List<UseCouponRsp> rsp = new ArrayList<>();

        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);

        //获取已领取的有效优惠券：排除过期，已使用、使用中的券
        List<ClientCouponEntity> enableCouponList = clientCouponMapper.findClientValidCommonCoupon(req.getClinetId(),
                couponLevel);
        List<OrderProductDetailDto> productList = req.getProductList();

        //空订单或者没有可用优惠券直接返回
        if (productList == null ||
                enableCouponList == null ||
                productList.size() <= 0 ||
                enableCouponList.size() <= 0) {
            return rsp;
        }

        //券数据缓存
        Map<String, ProductCouponEntity> couponMap = new HashedMap();

        //从缓存中读取优惠券基本信息
        ProductCouponEntity coupon = null;

        //先按优惠金额排序，因为不优惠券中不会出现折扣性质，所以ProfitValue不会是折扣值
        Collections.sort(enableCouponList, new Comparator<ClientCouponEntity>() {
            @Override
            public int compare(ClientCouponEntity entity1, ClientCouponEntity entity2) {
                int result = entity1.getCouponAmout().compareTo(entity2.getCouponAmout());
                if (result == 0)//如果优惠金额相等则优先用最近结束有效日
                    return entity1.getValidEndDate().isBefore(entity2.getValidEndDate()) ? 1 : -1;
                return result;
            }
        });

        //先按单价排序，价格约高的优先参与优惠券活动（如任选等，满N减M）
        Collections.sort(productList, new Comparator<OrderProductDetailDto>() {
            @Override
            public int compare(OrderProductDetailDto entity1, OrderProductDetailDto entity2) {
                int result = entity1.getPrice().compareTo(entity2.getPrice());
                if (result == 0)//如果价格相等按数量排序
                    return entity1.getAppCount().compareTo(entity2.getAppCount());
                return result;
            }
        });

        /**
         * 速算策略:
         * 假设如按优惠金额排序有券1,2,3,4；
         * 场景1-1：如果券1和券2、券3、券4互斥，保证券1的使用，哪怕券2、券3、券4总优惠金额>券1的总优惠金额；
         * 场景1-2：在券1已经明确可以使用情况下，如果券2和券3、券4互斥，保证券2的使用，哪怕券3、券4总优惠金额>券2的总优惠金额
         * 场景2：如果优惠券1优惠200元，但是涉及到商品A和B，而如果优惠券2只优惠150,优惠券3只优惠100，但是他们可以分别应用于商品A和商品B，两者之和会大于优惠券1的，根据此策略也只使用优惠券1
         * 场景3：如果券1和券2互斥，订单中涉及商品A、B、C、D、E；应用券1对应商品A和B，但是当券2可以应用于C和D时，券2也是可以用的
         * 场景4：如果券1和券2是同一种券的不同使用门槛（如券1是满500减50元，券2是满400减40），分两种规则：规则1同上述场景3互斥但是同一订单可使用，规则2只会用金额最大的券1
         * 场景5：如果券1和券2是同一种券的无使用门槛（如券1券2是减50元，根据合法规则领了两次），则券1和券2在无设置冲突关系时都可叠加使用
         * 场景6：如果券1和券2是同一种券的相同使用门槛（如券1和2都是满500减50元）只会使用券1
         * 订单互斥最优策略：同一个订单对于非全局类的券只能应用一张
         * 最优策略：暂不实现
         * 多张有门槛同种券使用规则-保守规则：每种券每次只能使用一张；默认
         * 多张有门槛同种券使用规则-最优规则：每种券每次可以使用多张，但是不能同时适用于同一商品
         */
        String stageCouponUseRuleConfig = StageCouponUseRule.SIMPLE.getDictValue();//todo应获取于配置300002参数表，获取于redis
        Map<String, OrderProductDetailDto> orderProductDetailDtoMap = new HashedMap();
        for (ClientCouponEntity clientCoupon : enableCouponList) {

            dto = checkCouponFrist(clientCoupon, req, true);
            if (!dto.getSuccess()) {
                continue;
            }
            coupon = (ProductCouponEntity) dto.getData();

            //硬性规定全部设置为冲突，现在profitConflictOrReUseRef表暂时不再使用
            coupon.setReCouponFlag(ReCouponFlag.DICTCOMMENT);

            //查询冲突的券已适用的商品列表，在本券映射适用商品列表时需要排除掉
            orderProductDetailDtoMap = checkAndMutexProducts(rsp,
                    coupon);

            //装箱返回数据
            UseCouponRsp useCouponRsp = new UseCouponRsp();
            ClientCouponDto clientCouponDto = new ClientCouponDto();
            BeanUtils.copyProperties(clientCoupon,clientCouponDto);
            useCouponRsp.setClientCoupon(clientCouponDto);
            useCouponRsp.setProfitAmount(clientCoupon.getCouponAmout());

            //多张有门槛同种券使用规则-保守规则
            if (stageCouponUseRuleConfig.equals(StageCouponUseRule.SIMPLE.getDictValue())) {
                //有门槛同种券使用规则-保守规则：有门槛券每次只能使用一张
                boolean isExist = CollectionUtils.exists(rsp, new Predicate() {
                    public boolean evaluate(Object object) {
                        UseCouponRsp item = (UseCouponRsp) object;
                        if (item.getClientCoupon().getCouponId().equals(clientCoupon.getCouponId()) &&
                                !CommonUtils.isEmptyorNull(clientCoupon.getStageId()))
                            return true;
                        return false;
                    }
                });
                //因为已经按优惠金额进行了排序，所以有同一个券的不同阶梯的已经适用的就是优惠最大的，则本券直接算作不适用
                if (isExist) {
                    continue;
                }
            }

            /**
             * 有门槛券适配使用的商品统计:
             * 1.计算本券的适配商品时，排除已使用的冲突券适配的商品
             * 2.必须达到使用条件
             */
            if (!CommonUtils.isEmptyorNull(clientCoupon.getStageId())) {
                CouponStageRuleEntity stage = couponStageRuleMapper.findCouponStageById(clientCoupon.getCouponId(),
                        clientCoupon.getStageId());
                if (stage == null) {
                    throw new BizException(COUPON_NOT_EXISTS.getCode(), COUPON_NOT_EXISTS.getDesc());
                }

                //优惠金额为指定阶梯的优惠金额
                useCouponRsp.setProfitAmount(stage.getCouponValue());
                BigDecimal condition = BigDecimal.ZERO;
                //优惠券适配商品统计，该productList按单价进行排序后的
                for (OrderProductDetailDto product : productList) {

                    //将冲突券适配商品排除
                    if (orderProductDetailDtoMap.containsKey(product.getProductId()))
                        continue;


                    //校验符合商品基本属性
                    dto = checkCouponForProduct(coupon, product.getProductId());
                    if (dto.getSuccess()) {

                        useCouponRsp.getProductLsit().add(product);

                        //按数量统计
                        if (stage.getTriggerByType().equals(TriggerByType.NUMBER.getDictValue())) {
                            condition = condition.add(product.getAppCount());
                            //达到门槛才放入数组
                            if (condition.compareTo(stage.getBeginValue()) >= 0) {
                                rsp.add(useCouponRsp);
                                break;
                            }
                        } else {
                            condition = condition.add(product.getAppCount().multiply(product.getPrice()));
                            //达到门槛才放入数组
                            if (condition.compareTo(stage.getBeginValue()) >= 0) {
                                rsp.add(useCouponRsp);
                                break;
                            }
                        }
                    }
                }

            } else {
                /**
                 * 无门槛券适配使用的商品统计
                 * 无门槛的券如红包券是可以叠加使用的，但是该种券应不设置使用频率限制
                 */
                //按单价进行排序后的list
                for (OrderProductDetailDto product : productList) {

                    //排除冲突券适配的商品
                    if (orderProductDetailDtoMap.containsKey(product.getProductId()))
                        continue;
                    //校验符合的的商品属性第一个即为适用上的商品
                    dto = checkCouponForProduct(coupon, product.getProductId());
                    if (dto.getSuccess()) {
                        useCouponRsp.getProductLsit().add(product);
                        rsp.add(useCouponRsp);
                        break;
                    }
                }
            }
            //循环内清除缓存
            orderProductDetailDtoMap.clear();
        }

        //释放缓存
        couponMap.clear();
        couponMap = null;
        return rsp;
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
     * @param item
     * @param req
     * @return
     */
    private CommonBoolDto<ProductCouponEntity> checkCouponFrist(ClientCouponEntity item, GetUseEnableCouponReq req, boolean useFlag) {

        //根据券ID获取优惠券信息
        ProductCouponEntity coupon = productCouponMapper.findProductCouponById(item.getCouponId());
        if (coupon == null) {
            throw new BizException(COUPON_NOT_EXISTS.getCode(), COUPON_NOT_EXISTS.getDesc());
        }


        //校验基本信息是否符合使用条件
        CommonBoolDto dto = checkCouponBase(coupon, req);
        dto.setSuccess(true);
        dto.setData(coupon);
        //校验不通过继续
        if (!dto.getSuccess()) {
            return dto;
        }

        //校验使用时间窗口
        if (useFlag) {
            dto = checkCouponUseValidDate(coupon);
            //校验不通过继续
            if (!dto.getSuccess()) {
                return dto;
            }
            //校验使用频率是否符合
            dto = checkCouponUseFreqLimit(item.getClientId(), item.getCouponId(), item.getStageId());
            if (!dto.getSuccess()) {
                return dto;
            }
        } else {
            dto = checkCouponGetValidDate(coupon);
            //校验不通过继续
            if (!dto.getSuccess()) {
                return dto;
            }
            dto = checkCouponGetFreqLimit(item.getClientId(), item.getCouponId(), item.getStageId());
            if (!dto.getSuccess()) {
                return dto;
            }
        }

        //和指定活動是否存在冲突
        if (!CommonUtils.isEmptyorNull(req.getActivityId())) {
            ActivityProfitEntity activeEntity = activityProfitMapper.findById(req.getActivityId());
            if (activeEntity == null) {
                throw new BizException(ACTIVE_NOT_EXIST.getCode(), ACTIVE_NOT_EXIST.getDesc());
            }

            dto = checkReUseLimit(coupon, activeEntity);
            if (!dto.getSuccess()) {
                return dto;
            }
        }


        return dto;
    }

    /**
     * 获取优惠券基本信息
     *
     * @param couponId
     * @param cache
     * @return
     */
    private ProductCouponEntity findCouponInCacheAndDb(String couponId, Map<String, ProductCouponEntity> cache) {
        ProductCouponEntity coupon = cache.get(couponId);
        if (coupon == null) {
            //根据券ID获取优惠券信息
            coupon = productCouponMapper.findProductCouponById(couponId);
            if (coupon == null) {
                throw new BizException(COUPON_NOT_EXISTS.getCode(), COUPON_NOT_EXISTS.getDesc());
            }
            cache.put(coupon.getUuid(), coupon);
        }
        return coupon;
    }


    /**
     * 获取已适配冲突关系券适用的商品列表
     *
     * @param rsp    已计算适用的券及其对应商品列表
     * @param coupon 优惠券信息
     * @return
     */
    private Map<String, OrderProductDetailDto> checkAndMutexProducts(List<UseCouponRsp> rsp,
                                                                     ProductCouponEntity coupon) {

        Map<String, OrderProductDetailDto> mutexMap = new HashMap<String, OrderProductDetailDto>();

        if (rsp != null) {
            return mutexMap;
        }

        /**
         * 无需根据冲突对应关系表进行的处理
         * 1.已适用的券存在“不允许叠加”设置时，无论是否配置了两者之间的冲突关系都做为冲突处理
         * 2.有门槛同一券设置视为冲突，在同一商品上不能叠加；无论是否配置了两者之间的冲突关系
         * 3.如果该券时不可叠加的，代表和任何券都是冲突的
         */
        for (UseCouponRsp useCouponRsp : rsp) {
            if (ReCouponFlag.CONFLICT.equals(useCouponRsp.getReCouponFlag()) ||
                    (useCouponRsp.getClientCoupon().getCouponId().equals(coupon.getId()) &&
                            !CommonUtils.isEmptyorNull(useCouponRsp.getClientCoupon().getStageId())) ||
                    ReCouponFlag.CONFLICT.equals(coupon.getReCouponFlag())) { //
                List<OrderProductDetailDto> entities = useCouponRsp.getProductLsit();
                if (entities != null)
                    //将冲突商品集合去重合并
                    for (OrderProductDetailDto en : entities) {
                        if (!mutexMap.containsKey(en.getProductId()))
                            mutexMap.put(en.getProductId(), en);
                    }
            }
        }
        //如果该券时不可叠加的无需查询冲突关系
        if (ReCouponFlag.CONFLICT.equals(coupon.getReCouponFlag())) {//ReCouponFlag字段为空保护为可叠加
            return mutexMap;
        }

        //查询该券是否和指定的券冲突，如果冲突则不能应用于同一商品上
        List<ProfitConflictOrReUseRefEntity> ConflictList = profitConflictOrReUseRefMapper.findBySpecifyId(ActiveOrCouponType.COUPON.getDictValue(),
                coupon.getId(),
                ConflictFlag.CONFLICT.getDictValue());

        for (ProfitConflictOrReUseRefEntity item : ConflictList) {
            //做参数保护
            if (item.getObjType().equals(ActiveOrCouponType.ACTIVITY) ||
                    item.getTargetObjType().equals(ActiveOrCouponType.ACTIVITY) ||
                    item.getRefType().equals(ConflictFlag.SUPERPOSE))
                continue;

            //查找冲突的券id
            String key = item.getTargetObjType();
            if (item.getTargetObjId().equals(coupon.getId())) {
                key = item.getObjId();
            }

            //查找和该券冲突券已适配的商品集合
            for (UseCouponRsp useCouponRsp : rsp) {
                if (useCouponRsp.getClientCoupon().getCouponId().equals(key)) {
                    List<OrderProductDetailDto> entities = useCouponRsp.getProductLsit();
                    if (entities != null)
                        //将冲突商品集合去重合并
                        for (OrderProductDetailDto en : entities) {
                            if (!mutexMap.containsKey(en.getProductId()))
                                mutexMap.put(en.getProductId(), en);
                        }
                }
            }

        }
        return mutexMap;
    }


    /**
     * 校验优惠券与活动是否可叠加
     *
     * @param coupon
     * @param activeEntity
     * @return
     */
    private CommonBoolDto checkReUseLimit(ProductCouponEntity coupon, ActivityProfitEntity activeEntity) {
        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);
        if (coupon == null || activeEntity == null) {
            return dto;
        }

        //券设置是不可叠加的，直接返回
        if (coupon.getReCouponFlag().equals(ReCouponFlag.CONFLICT.getDictValue())) {
            dto.setSuccess(false);
            dto.setDesc("该优惠券设置了不可叠加");
        }

        //只要活动设置是不可叠加的，直接返回
        if (activeEntity.getReCouponFlag().equals(ReCouponFlag.CONFLICT.getDictValue())) {
            dto.setSuccess(false);
            dto.setDesc("该活动设置了不可叠加");
        }

        //只有两者都可叠加(设置不可叠加的有效性优先级大于可叠加)，或没有设置冲突关系，该优惠券才可以使用
        //获取冲突关联表
        ProfitConflictOrReUseRefEntity refEntity = profitConflictOrReUseRefMapper.findByBothId(ActiveOrCouponType.COUPON.getDictValue(),
                coupon.getId(),
                ActiveOrCouponType.ACTIVITY.getDictValue(),
                activeEntity.getId(),
                ConflictFlag.CONFLICT.getDictValue());
        if (refEntity != null) {
            dto.setSuccess(false);
            dto.setDesc("该优惠券与指定活动设置了不可叠加关系");
        }
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
        List<ShortCouponDetailDto> couponDetailListByIds =
                productCouponMapper.findClinetFreqForbidUseCouponDetailListById(clientId, couponId, stageId);

        //逐一进行排除
        return excludeFreqLimit(couponDetailListByIds, couponId, stageId);

    }


    /**
     * 指定券是否受频率限制
     *
     * @param couponDetailListByIds
     * @param couponId
     * @param stageId
     * @return
     */
    private CommonBoolDto excludeFreqLimit(List<ShortCouponDetailDto> couponDetailListByIds, String couponId, String stageId) {
        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);
        if (couponDetailListByIds == null || CommonUtils.isEmptyorNull(couponId))
            return dto;

        //逐一进行排除
        boolean isExist = CollectionUtils.exists(couponDetailListByIds, new Predicate() {
            public boolean evaluate(Object object) {
                ShortCouponDetailDto item = (ShortCouponDetailDto) object;
                if (item.getCouponId().equals(couponId) &&
                        ((!CommonUtils.isEmptyorNull(item.getStageId()) && item.getStageId().equals(stageId)) ||
                                CommonUtils.isEmptyorNull(item.getStageId()) && CommonUtils.isEmptyorNull(stageId))
                        )
                    return true;
                return false;
            }
        });
        if (isExist) {
            dto.setSuccess(false);
            dto.setDesc(COUPON_FAIL_OP_FREQ.getDesc());
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
                productCouponMapper.findClinetFreqForbidCouponDetailListById(clientId,
                        couponId, stageId);

        //逐一进行排除
        return excludeFreqLimit(couponDetailListByIds, couponId, stageId);

    }


    /**
     * 根据优惠券是否在允许使用时间窗口内
     *
     * @param coupon 优惠券基本信息
     * @return 开发日志
     * 1004246-徐长焕-20181213 新增
     */
    private CommonBoolDto checkCouponUseValidDate(ProductCouponEntity coupon) {
        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);
        //是否在允許使用期間
        if ((coupon.getAllowUseBeginDate() != null && coupon.getAllowUseBeginDate().compareTo(LocalDate.now()) > 0) ||
                (coupon.getAllowUseEndDate() != null && coupon.getAllowUseEndDate().compareTo(LocalDate.now()) < 0)) {

            dto.setSuccess(false);
            dto.setDesc(COUPON_NOT_ALLOW_DATE.getDesc());
            return dto;
        }
        return dto;
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
        //是否在允許領取期間
        if ((coupon.getAllowGetBeginDate() != null && coupon.getAllowGetBeginDate().compareTo(LocalDate.now()) > 0) ||
                (coupon.getAllowGetEndDate() != null && coupon.getAllowGetEndDate().compareTo(LocalDate.now()) < 0)) {

            dto.setSuccess(false);
            dto.setDesc(COUPON_NOT_ALLOW_DATE.getDesc());
            return dto;
        }
        return dto;
    }

    /**
     * 校验优惠对应商品属性是否匹配
     *
     * @param coupon
     * @param productId
     * @return
     */
    private CommonBoolDto checkCouponForProduct(ProductCouponEntity coupon, String productId) {
        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);

        // ApplyProductFlag空值做保护
        if (!ApplyProductFlag.ALL.equals(coupon.getApplyProductFlag())) {
            //该商品属性是否允许领取该券
            CouponRefProductEntity entity = couponRefProductMapper.findByBothId(coupon.getId(), productId);
            if (entity == null) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_NOT_ALLOW_PRODUCT.getDesc());
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
                                          GetUseEnableCouponReq req) {
        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);

        //a.客户属性校验
        // 客户类型是否允许领取
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(coupon.getClientTypeSet(),
                req.getClinetType())) {

            dto.setSuccess(false);
            dto.setDesc(COUPON_NOT_ALLOW_CLIENTTYPE.getDesc());
            return dto;
        }

        //b.商品属性校验，不校验其买入卖出门槛
        if (req.getProductList() != null && req.getProductList().size() > 0) {
            dto.setSuccess(false);
            for (OrderProductDetailDto item : req.getProductList()) {
                dto = checkCouponForProduct(coupon, item.getProductId());
                //该券不适用任何商品，则该券不能用
                if (dto.getSuccess())
                    dto.setSuccess(true);
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
            dto.setDesc(COUPON_NOT_ALLOW_ENTRUSTWAY.getDesc());
            return dto;
        }

        //d.支付属性校验
        //该银行卡是否允许领取该券
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(coupon.getBankCodeSet(),
                req.getBankCode())) {

            dto.setSuccess(false);
            dto.setDesc(COUPON_NOT_ALLOW_ENTRUSTWAY.getDesc());
            return dto;
        }

        //该支付类型是否允许领取该券
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(coupon.getPayTypeSet(),
                req.getPayType())) {
            dto.setSuccess(false);
            dto.setDesc(COUPON_NOT_ALLOW_ENTRUSTWAY.getDesc());
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
    private CommonBoolDto checkCouponPersonQuota(CouponQuotaRuleEntity quota,
                                                 String clientId) {
        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);

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
            ClientCoupStatisticsQuotaDto statisticsQuotaDto = null;

            //校验每客每天最大优惠额
            int validflag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPerDateAndAccMaxAmount());
            if (validflag == 2) {

                //如果没做过统计，则统计获取客户已领取的优惠券金额数量情况
                if (statisticsQuotaDto == null || statisticsQuotaDto.getStatisticsFlag().equals("0"))
                    statisticsQuotaDto = statisticsClientCouponQuota(clientId, quota.getCouponId());

                //判断是否客户当日已领取的优惠金额是否超限
                if (quota.getPerDateAndAccMaxAmount().compareTo(statisticsQuotaDto.getClientPerDateAmount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_PERACCANDDATEQUOTA.getDesc());
                    return dto;
                }

            } else if (validflag == 0) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_PERACCANDDATEQUOTA.getDesc());
                return dto;
            }

            //校验每客最大优惠额
            validflag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPersonMaxAmount());
            if (validflag == 2) {

                //统计获取客户已领取的优惠券金额数量情况
                if (statisticsQuotaDto == null || statisticsQuotaDto.getStatisticsFlag().equals("0"))
                    statisticsQuotaDto = statisticsClientCouponQuota(clientId, quota.getCouponId());

                //判断是否客户已领取的优惠金额是否超限
                if (quota.getPerMaxAmount().compareTo(statisticsQuotaDto.getClientAmount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_PERACCQUOTA.getDesc());
                    return dto;
                }
            } else if (validflag == 0) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_PERACCQUOTA.getDesc());
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
    private CommonBoolDto checkCouponAllQuota(CouponQuotaRuleEntity quota) {
        CommonBoolDto dto = new CommonBoolDto();
        dto.setSuccess(true);

        if (quota != null) {
            ClientCoupStatisticsQuotaDto statisticsQuotaDto = null;
            //校验所有客户每天最大优惠额getPerDateMaxAmount
            int validflag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPerDateMaxAmount());
            if (validflag == 2) {

                //如果没做过统计，则统计获取客户已领取的优惠券金额数量情况
                if (statisticsQuotaDto == null || statisticsQuotaDto.getStatisticsFlag().equals("0"))
                    statisticsQuotaDto = statisticsCouponQuota("", "", quota.getCouponId(), "");

                //判断是否所有客户当日已领取的优惠金额是否超限
                if (quota.getPerDateMaxAmount().compareTo(statisticsQuotaDto.getClientPerDateAmount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_PERDATEQUOTA.getDesc());
                    return dto;
                }
            } else if (validflag == 0) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_PERDATEQUOTA.getDesc());
                return dto;
            }

            //校验所有客户最大优惠额getMaxAmount
            validflag = CommonUtils.isQuotaValueNeedValidFlag(quota.getMaxAmount());
            if (validflag == 2) {

                //如果没做过统计，则统计获取客户已领取的优惠券金额数量情况
                if (statisticsQuotaDto == null || statisticsQuotaDto.getStatisticsFlag().equals("0"))
                    statisticsQuotaDto = statisticsCouponQuota("", "", quota.getCouponId(), "");

                //判断是否所有客户已领取的优惠金额是否超限
                if (quota.getMaxAmount().compareTo(statisticsQuotaDto.getClientAmount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_QUOTA.getDesc());
                    return dto;
                }

            } else if (validflag == 0) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_QUOTA.getDesc());
                return dto;
            }

            BigDecimal maxCount = null;
            if (quota.getMaxCount() != null)
                maxCount = new BigDecimal(quota.getMaxCount().toString());
            //校验所有客户最大领取数量maxCount:quota.getMaxCount()
            validflag = CommonUtils.isQuotaValueNeedValidFlag(maxCount);
            if (validflag == 2) {

                //如果没做过统计，则统计获取客户已领取的优惠券金额数量情况
                if (statisticsQuotaDto == null || statisticsQuotaDto.getStatisticsFlag().equals("0"))
                    statisticsQuotaDto = statisticsCouponQuota("", "", quota.getCouponId(), "");

                //判断是否所有客户已领取的优惠金额是否超限
                if (maxCount.compareTo(statisticsQuotaDto.getClientCount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_COUNT_QUOTA.getDesc());
                    return dto;
                }
            } else if (validflag == 0) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_COUNT_QUOTA.getDesc());
                return dto;
            }

        }

        return dto;
    }


    /**
     * 统计指定优惠券的领取情况信息
     *
     * @param couponId 指定统计的优惠券
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
        dto.setStatisticsFlag("1");

        return dto;
    }

}




