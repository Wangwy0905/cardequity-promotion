package com.youyu.cardequity.promotion.biz.service.impl;

import com.github.pagehelper.PageInfo;
import com.youyu.cardequity.common.base.uidgenerator.UidGenerator;
import com.youyu.cardequity.common.spring.service.BatchService;
import com.youyu.cardequity.promotion.biz.dal.dao.*;
import com.youyu.cardequity.promotion.biz.dal.entity.*;
import com.youyu.cardequity.promotion.biz.enums.CouponTypeEnum;
import com.youyu.cardequity.promotion.biz.enums.CouponUnitEnum;
import com.youyu.cardequity.promotion.biz.enums.ProductCouponGetTypeEnum;
import com.youyu.cardequity.promotion.biz.service.ProductCouponService2;
import com.youyu.cardequity.promotion.dto.CouponRefAllProductDto;
import com.youyu.cardequity.promotion.dto.req.*;
import com.youyu.cardequity.promotion.dto.rsp.CouponRefProductQueryRsp;
import com.youyu.cardequity.promotion.dto.rsp.ProductCouponGetStatisticsRsp;
import com.youyu.cardequity.promotion.dto.rsp.ProductCouponQueryRsp;
import com.youyu.cardequity.promotion.dto.rsp.ProductCouponViewRsp;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.common.api.PageData;
import com.youyu.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.github.pagehelper.page.PageMethod.startPage;
import static com.youyu.cardequity.common.base.util.CollectionUtils.isEmpty;
import static com.youyu.cardequity.common.base.util.EnumUtil.getCardequityEnum;
import static com.youyu.cardequity.common.base.util.LocalDateUtils.DATETIME_FORMAT;
import static com.youyu.cardequity.common.base.util.LocalDateUtils.string2LocalDateTime;
import static com.youyu.cardequity.common.base.util.MoneyUtil.eqZero;
import static com.youyu.cardequity.common.base.util.MoneyUtil.gtZero;
import static com.youyu.cardequity.common.base.util.ObjectUtil.defaultIfNull;
import static com.youyu.cardequity.common.base.util.PaginationUtils.convert;
import static com.youyu.cardequity.common.base.util.StringUtil.eq;
import static com.youyu.cardequity.promotion.biz.constant.RedissonKeyConstant.CARDEQUITY_PRODUCT_COUPON_ALL_PRODUCT_CACHE_MAP;
import static com.youyu.cardequity.promotion.enums.CommonDict.IF_YES;
import static com.youyu.cardequity.promotion.enums.ProductCouponClientTypeSetEnum.REGISTRY_USER;
import static com.youyu.cardequity.promotion.enums.ResultCode.CACHE_PRODUCT_QUANTITY_CANNOT_ATTAIN_PRODUCT_SUM;
import static com.youyu.cardequity.promotion.enums.dict.CouponStrategyType.equalstage;
import static com.youyu.cardequity.promotion.enums.dict.CouponStrategyType.stage;
import static com.youyu.cardequity.promotion.enums.dict.OpCouponType.GETRULE;
import static com.youyu.cardequity.promotion.enums.dict.TriggerByType.CAPITAL;
import static com.youyu.cardequity.promotion.enums.dict.UseGeEndDateFlag.NO;
import static com.youyu.cardequity.promotion.enums.dict.UsedStage.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月20日 10:00:00
 * @work 优惠券商品信息管理Service impl
 */
@Slf4j
@Service
public class ProductCouponServiceImpl2 implements ProductCouponService2 {
    /**
     * 允许使用开始时间
     */
    public static final String ALLOW_USE_BEGIN_DATE = "2019-01-01 00:00:00";
    /**
     * 允许使用截止时间
     */
    public static final String ALLOW_USE_END_DATE = "2099-01-01 00:00:00";
    /**
     * 默认最大值
     */
    public static final BigDecimal MAX_VALUE = new BigDecimal("999999999.00");
    /**
     * 重试次数
     */
    public static final Integer RETRY_TIME = 3;

    @Autowired
    private UidGenerator uidGenerator;
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private BatchService batchService;

    @Autowired
    private ProductCouponMapper productCouponMapper;
    @Autowired
    private CouponQuotaRuleMapper couponQuotaRuleMapper;
    @Autowired
    private CouponStageRuleMapper couponStageRuleMapper;
    @Autowired
    private CouponGetOrUseFreqRuleMapper couponGetOrUseFreqRuleMapper;
    @Autowired
    private CouponRefProductMapper couponRefProductMapper;

    @Override
    @Transactional
    public void add(ProductCouponAddReq productCouponAddReq) {
        String productCouponId = uidGenerator.getUID2();
        ProductCouponEntity productCouponEntity = getProductCoupon(productCouponAddReq, productCouponId);
        productCouponMapper.insertSelective(productCouponEntity);

        String couponStageId = uidGenerator.getUID2();
        createCouponQuotaRule(productCouponAddReq, productCouponId);
        createCouponStageRule(productCouponAddReq, productCouponId, couponStageId);
        createCouponGetOrUseFreqRule(productCouponAddReq, productCouponId, couponStageId);
    }

    @Override
    @Transactional
    public void edit(ProductCouponEditReq productCouponEditReq) {
        ProductCouponEntity existProductCoupon = productCouponMapper.selectByPrimaryKey(productCouponEditReq.getProductCouponId());
        ProductCouponEntity productCoupon = getProductCoupon(productCouponEditReq, existProductCoupon);
        productCouponMapper.updateByPrimaryKeySelective(productCoupon);

        editCouponGetOrUseFreqRule(productCouponEditReq, existProductCoupon.getGetType());
    }

    @Override
    @Transactional
    public void setStatus(ProductCouponStatusReq productCouponStatusReq) {
        List<String> productCouponIds = productCouponStatusReq.getProductCouponIds();
        if (isEmpty(productCouponIds)) {
            return;
        }

        doSetStatus(productCouponStatusReq, productCouponIds);
    }

    @Override
    public PageData<ProductCouponQueryRsp> getPage(ProductCouponQueryReq productCouponQueryReq) {
        startPage(productCouponQueryReq.getPageNo(), productCouponQueryReq.getPageSize());
        List<ProductCouponQueryRsp> productCoupons = productCouponMapper.getProductCouponQuery(productCouponQueryReq);
        PageInfo<ProductCouponQueryRsp> productCouponPageInfo = new PageInfo<>(productCoupons);
        return convert(productCouponPageInfo);
    }

    @Override
    public List<ProductCouponGetStatisticsRsp> getStatisticsByClientType(ProductCouponGetStatisticsReq productCouponGetStatisticsReq) {
        List<ProductCouponGetStatisticsRsp> productCouponGetStatistics = productCouponMapper.getStatisticsByClientType();
        if (isEmpty(productCouponGetStatistics)) {
            return null;
        }

        productCouponGetStatistics.add(getAllProductCouponGetStatistics(productCouponGetStatistics));
        return productCouponGetStatistics;
    }

    @Override
    public ProductCouponViewRsp getDetail(ProductCouponViewReq productCouponViewReq) {
        ProductCouponViewRsp productCouponViewRsp = productCouponMapper.getByProductCouponId(productCouponViewReq.getProductCouponId());

        BigDecimal conditionValue = productCouponViewRsp.getConditionValue();
        if (eqZero(conditionValue)) {
            return productCouponViewRsp;
        }
        productCouponViewRsp.setPerProfitTopValue(productCouponViewRsp.getPerProfitTopValue().divide(conditionValue).multiply(productCouponViewRsp.getProfitValue()));
        return productCouponViewRsp;
    }

    @Override
    @Transactional
    public void addProduct(CouponRefProductReq couponRefProductReq) {
        List<CouponRefProductDetailReq> couponRefProductDetailReqs = couponRefProductReq.getCouponRefProductDetailReqs();
        if (isEmpty(couponRefProductDetailReqs)) {
            return;
        }
        doAddProduct(couponRefProductReq, couponRefProductDetailReqs);
    }

    @Override
    public void addAllProduct(CouponRefAllProductReq couponRefAllProductReq) {
        Integer productSum = couponRefAllProductReq.getProductSum();
        if (productSum == 0) {
            return;
        }

        RMap<String, CouponRefAllProductDto> productInfoMap = redissonClient.getMap(CARDEQUITY_PRODUCT_COUPON_ALL_PRODUCT_CACHE_MAP);
        if (isEmpty(productInfoMap)) {
            return;
        }

        productInfoMap = checkProductCacheSize(productSum, productInfoMap);
        doAddAllProduct(couponRefAllProductReq, productInfoMap);
    }

    @Override
    public PageData<CouponRefProductQueryRsp> getCouponRefProductPage(CouponRefProductQueryReq couponRefProductQueryReq) {
        startPage(couponRefProductQueryReq.getPageNo(), couponRefProductQueryReq.getPageSize());
        List<CouponRefProductQueryRsp> couponRefProductQueryRsps = couponRefProductMapper.getCouponRefProductQuery(couponRefProductQueryReq);
        PageInfo<CouponRefProductQueryRsp> productQueryRspPage = new PageInfo<>(couponRefProductQueryRsps);
        return convert(productQueryRspPage);
    }

    /**
     * 检查缓存里的商品总数是否与优惠券商品总数相等
     *
     * @param productSum
     * @param productInfoMap
     * @return
     */
    private RMap<String, CouponRefAllProductDto> checkProductCacheSize(Integer productSum, RMap<String, CouponRefAllProductDto> productInfoMap) {
        int size = productInfoMap.size();
        int retryTime = RETRY_TIME;
        try {
            while (size < productSum && retryTime > 0) {
                productInfoMap = redissonClient.getMap(CARDEQUITY_PRODUCT_COUPON_ALL_PRODUCT_CACHE_MAP);
                size = productInfoMap.size();
                retryTime--;
                MILLISECONDS.sleep(300);
            }
        } catch (InterruptedException ex) {
            log.error("优惠券添加所有商品异常信息:[{}]", getFullStackTrace(ex));
        }

        if (size < productSum) {
            throw new BizException(CACHE_PRODUCT_QUANTITY_CANNOT_ATTAIN_PRODUCT_SUM);
        }
        return productInfoMap;
    }

    /**
     * 执行添加所有商品
     *
     * @param couponRefAllProductReq
     * @param productInfoMap
     */
    private void doAddAllProduct(CouponRefAllProductReq couponRefAllProductReq, RMap<String, CouponRefAllProductDto> productInfoMap) {
        couponRefProductMapper.deleteByCouponId(couponRefAllProductReq.getProductCouponId());

        List<CouponRefProductEntity> couponRefProductEntities = new ArrayList<>();
        CouponRefProductEntity couponRefProduct = null;
        for (CouponRefAllProductDto couponRefAllProductDto : productInfoMap.values()) {
            couponRefProduct = getCouponRefProduct(couponRefAllProductReq, couponRefAllProductDto);
            couponRefProductEntities.add(couponRefProduct);
        }
        batchService.batchDispose(couponRefProductEntities, CouponRefProductMapper.class, "insertSelective", 10000);
//        productInfoMap.clear();
    }

    /**
     * 获取优惠券关联商品对象
     *
     * @param couponRefAllProductReq
     * @param couponRefAllProductDto
     * @return
     */
    private CouponRefProductEntity getCouponRefProduct(CouponRefAllProductReq couponRefAllProductReq, CouponRefAllProductDto couponRefAllProductDto) {
        CouponRefProductEntity couponRefProduct = new CouponRefProductEntity();
        couponRefProduct.setUuid(uidGenerator.getUID2());
        couponRefProduct.setCouponId(couponRefAllProductReq.getProductCouponId());
        couponRefProduct.setIsEnable(IF_YES.getCode());
        couponRefProduct.setProductId(couponRefAllProductDto.getProductId());
        couponRefProduct.setProductName(couponRefAllProductDto.getProductName());
        couponRefProduct.setSupplierName(couponRefAllProductDto.getSupplierName());
        couponRefProduct.setThirdCategoryName(couponRefAllProductDto.getThirdCategoryName());
        return couponRefProduct;
    }

    /**
     * 添加优惠券关联商品
     *
     * @param couponRefProductReq
     * @param couponRefProductDetailReqs
     */
    private void doAddProduct(CouponRefProductReq couponRefProductReq, List<CouponRefProductDetailReq> couponRefProductDetailReqs) {
        List<CouponRefProductEntity> couponRefProductEntities = new ArrayList<>();
        CouponRefProductEntity couponRefProduct = null;
        for (CouponRefProductDetailReq couponRefProductDetailReq : couponRefProductDetailReqs) {
            couponRefProduct = getCouponRefProduct(couponRefProductReq, couponRefProductDetailReq);
            couponRefProductEntities.add(couponRefProduct);
        }
        batchService.batchDispose(couponRefProductEntities, CouponRefProductMapper.class, "insertSelective");
    }

    /**
     * 获取CouponRefProductEntity对象
     *
     * @param couponRefProductReq
     * @param couponRefProductDetailReq
     * @return
     */
    private CouponRefProductEntity getCouponRefProduct(CouponRefProductReq couponRefProductReq, CouponRefProductDetailReq couponRefProductDetailReq) {
        CouponRefProductEntity couponRefProduct;
        couponRefProduct = new CouponRefProductEntity();
        couponRefProduct.setUuid(uidGenerator.getUID2());
        couponRefProduct.setCouponId(couponRefProductReq.getProductCouponId());
        couponRefProduct.setProductId(couponRefProductDetailReq.getProductId());
        couponRefProduct.setProductName(couponRefProductDetailReq.getProductName());
        couponRefProduct.setSupplierName(couponRefProductDetailReq.getSupplierName());
        couponRefProduct.setThirdCategoryName(couponRefProductDetailReq.getThirdCategoryName());
        couponRefProduct.setIsEnable(IF_YES.getCode());
        return couponRefProduct;
    }

    /**
     * 获取全部商品优惠券的会员类型统计
     *
     * @param productCouponGetStatistics
     * @return
     */
    private ProductCouponGetStatisticsRsp getAllProductCouponGetStatistics(List<ProductCouponGetStatisticsRsp> productCouponGetStatistics) {
        int quantity = productCouponGetStatistics.stream().mapToInt(productCouponGetStatisticsRsp -> productCouponGetStatisticsRsp.getQuantity()).sum();

        ProductCouponGetStatisticsRsp productCouponGetStatisticsRsp = new ProductCouponGetStatisticsRsp();
        productCouponGetStatisticsRsp.setGetObject("全部对象");
        productCouponGetStatisticsRsp.setQuantity(quantity);
        return productCouponGetStatisticsRsp;
    }

    /**
     * 创建优惠券用户领取频率
     *
     * @param productCouponAddReq
     * @param productCouponId
     * @param couponStageId
     */
    private void createCouponGetOrUseFreqRule(ProductCouponAddReq productCouponAddReq, String productCouponId, String couponStageId) {
        ProductCouponGetTypeEnum productCouponGetTypeEnum = getCardequityEnum(ProductCouponGetTypeEnum.class, productCouponAddReq.getGetType());
        if (!productCouponGetTypeEnum.isUserGet()) {
            return;
        }

        CouponGetOrUseFreqRuleEntity couponGetOrUseFreqRule = getCouponGetOrUseFreqRule(productCouponAddReq, productCouponId, couponStageId);
        couponGetOrUseFreqRuleMapper.insertSelective(couponGetOrUseFreqRule);
    }

    /**
     * 获取优惠券用户领取频率
     *
     * @param productCouponAddReq
     * @param productCouponId
     * @param couponStageId
     * @return
     */
    private CouponGetOrUseFreqRuleEntity getCouponGetOrUseFreqRule(ProductCouponAddReq productCouponAddReq, String productCouponId, String couponStageId) {
        CouponGetOrUseFreqRuleEntity couponGetOrUseFreqRule = new CouponGetOrUseFreqRuleEntity();
        couponGetOrUseFreqRule.setUuid(uidGenerator.getUID2());
        couponGetOrUseFreqRule.setCouponId(productCouponId);
        couponGetOrUseFreqRule.setOpCouponType(GETRULE.getDictValue());
        couponGetOrUseFreqRule.setUnit(productCouponAddReq.getUnit());
        couponGetOrUseFreqRule.setValue(1);
        couponGetOrUseFreqRule.setPersonTotalNum(productCouponAddReq.getPersonTotalNum());
        couponGetOrUseFreqRule.setIsEnable(CommonDict.IF_YES.getCode());
        couponGetOrUseFreqRule.setStageId(couponStageId);

        CouponUnitEnum couponUnitEnum = getCardequityEnum(CouponUnitEnum.class, productCouponAddReq.getUnit());
        couponUnitEnum.setAllowCount(couponGetOrUseFreqRule, productCouponAddReq.getPersonTotalNum(), productCouponAddReq.getAllowCount());
        return couponGetOrUseFreqRule;
    }

    /**
     * 创建优惠券阶段信息
     *
     * @param productCouponAddReq
     * @param productCouponId
     * @param couponStageId
     */
    private void createCouponStageRule(ProductCouponAddReq productCouponAddReq, String productCouponId, String couponStageId) {
        CouponStageRuleEntity couponStageRule = getCouponStageRule(productCouponAddReq, productCouponId, couponStageId);
        couponStageRuleMapper.insertSelective(couponStageRule);
    }

    /**
     * 获取优惠券阶段规则信息
     *
     * @param productCouponAddReq
     * @param productCouponId
     * @param couponStageId
     * @return
     */
    private CouponStageRuleEntity getCouponStageRule(ProductCouponAddReq productCouponAddReq, String productCouponId, String couponStageId) {
        CouponStageRuleEntity couponStageRule = new CouponStageRuleEntity();
        couponStageRule.setUuid(couponStageId);
        couponStageRule.setCouponId(productCouponId);
        couponStageRule.setCouponShortDesc(productCouponAddReq.getCouponShortDesc());
        couponStageRule.setCouponValue(productCouponAddReq.getProfitValue());
        couponStageRule.setIsEnable(IF_YES.getCode());
        couponStageRule.setTriggerByType(CAPITAL.getDictValue());
        couponStageRule.setBeginValue(productCouponAddReq.getConditionValue());
        couponStageRule.setEndValue(getStageRuleEndValue(productCouponAddReq));
        return couponStageRule;
    }

    /**
     * 获取优惠券阶段截止金额
     *
     * @param productCouponAddReq
     * @return
     */
    private BigDecimal getStageRuleEndValue(ProductCouponAddReq productCouponAddReq) {
        BigDecimal perProfitTopValue = productCouponAddReq.getPerProfitTopValue();
        return perProfitTopValue.divide(productCouponAddReq.getProfitValue(), 0, RoundingMode.DOWN).multiply(productCouponAddReq.getConditionValue());
    }

    /**
     * 创建商品优惠券额度规则
     *
     * @param productCouponAddReq
     * @param productCouponId
     */
    private void createCouponQuotaRule(ProductCouponAddReq productCouponAddReq, String productCouponId) {
        CouponQuotaRuleEntity couponQuotaRuleEntity = getCouponQuotaRule(productCouponAddReq, productCouponId);
        couponQuotaRuleMapper.insertSelective(couponQuotaRuleEntity);
    }

    /**
     * 获取CouponQuotaRuleEntity对象
     *
     * @param productCouponAddReq
     * @param productCouponId
     * @return
     */
    private CouponQuotaRuleEntity getCouponQuotaRule(ProductCouponAddReq productCouponAddReq, String productCouponId) {
        CouponQuotaRuleEntity couponQuotaRuleEntity = new CouponQuotaRuleEntity();
        couponQuotaRuleEntity.setMaxCount(productCouponAddReq.getMaxCount());
        couponQuotaRuleEntity.setPerMaxAmount(MAX_VALUE);
        couponQuotaRuleEntity.setPerDateAndAccMaxAmount(MAX_VALUE);
        couponQuotaRuleEntity.setPerDateMaxAmount(MAX_VALUE);
        couponQuotaRuleEntity.setPersonMaxAmount(MAX_VALUE);
        couponQuotaRuleEntity.setMaxAmount(MAX_VALUE);
        couponQuotaRuleEntity.setIsEnable(IF_YES.getCode());
        couponQuotaRuleEntity.setCouponId(productCouponId);
        return couponQuotaRuleEntity;
    }

    /**
     * 获取ProductCouponEntity对象
     *
     * @param productCouponAddReq
     * @param productCouponId
     * @return
     */
    private ProductCouponEntity getProductCoupon(ProductCouponAddReq productCouponAddReq, String productCouponId) {
        ProductCouponEntity productCoupon = new ProductCouponEntity();
        productCoupon.setUuid(productCouponId);
        productCoupon.setCouponStrategyType(getCouponStrategyType(productCouponAddReq));
        productCoupon.setUseGeEndDateFlag(NO.getDictValue());
        productCoupon.setUsedStage(AfterPay.getDictValue());
        productCoupon.setIsEnable(IF_YES.getCode());
        productCoupon.setCouponLevel(productCouponAddReq.getCouponLevel());
        productCoupon.setCouponType(productCouponAddReq.getCouponType());
        productCoupon.setCouponName(productCouponAddReq.getCouponName());
        productCoupon.setCouponLable(productCouponAddReq.getCouponLabel());
        productCoupon.setCouponShortDesc(productCouponAddReq.getCouponShortDesc());
        productCoupon.setCouponDesc(productCouponAddReq.getCouponDesc());
        productCoupon.setProfitValue(productCouponAddReq.getProfitValue());
        productCoupon.setStatus(productCouponAddReq.getStatus());
        productCoupon.setGetType(productCouponAddReq.getGetType());
        productCoupon.setValidTimeType(productCouponAddReq.getValidTimeType());
        productCoupon.setAllowUseBeginDate(defaultIfNull(productCouponAddReq.getAllowUseBeginDate(), string2LocalDateTime(ALLOW_USE_BEGIN_DATE, DATETIME_FORMAT)));
        productCoupon.setAllowUseEndDate(defaultIfNull(productCouponAddReq.getAllowUseEndDate(), string2LocalDateTime(ALLOW_USE_END_DATE, DATETIME_FORMAT)));
        productCoupon.setAllowGetBeginDate(productCouponAddReq.getAllowGetBeginDate());
        productCoupon.setAllowGetEndDate(productCouponAddReq.getAllowGetEndDate());
        productCoupon.setValIdTerm(defaultIfNull(productCouponAddReq.getValidTerm(), 0));
        productCoupon.setClientTypeSet(productCouponAddReq.getClientTypeSet());
        productCoupon.setGetStage(Other.getDictValue());
        if (eq(REGISTRY_USER.getCode(), productCouponAddReq.getClientTypeSet())) {
            productCoupon.setGetStage(Register.getDictValue());
        }
        CouponTypeEnum couponTypeEnum = getCardequityEnum(CouponTypeEnum.class, productCouponAddReq.getCouponType());
        couponTypeEnum.setApplyProductFlagCouponLevel(productCoupon);

        ProductCouponGetTypeEnum productCouponGetTypeEnum = getCardequityEnum(ProductCouponGetTypeEnum.class, productCouponAddReq.getGetType());
        productCouponGetTypeEnum.checkAllowGetDate(productCoupon);
        return productCoupon;
    }

    /**
     * 获取优惠券策略类型
     *
     * @param productCouponAddReq
     * @return
     */
    private String getCouponStrategyType(ProductCouponAddReq productCouponAddReq) {
        BigDecimal perProfitTopValue = productCouponAddReq.getPerProfitTopValue();
        return gtZero(perProfitTopValue) ? equalstage.getDictValue() : stage.getDictValue();
    }

    /**
     * 编辑优惠券使用频率
     *
     * @param productCouponEditReq
     * @param getType
     */
    private void editCouponGetOrUseFreqRule(ProductCouponEditReq productCouponEditReq, String getType) {
        ProductCouponGetTypeEnum productCouponGetTypeEnum = getCardequityEnum(ProductCouponGetTypeEnum.class, getType);
        if (!productCouponGetTypeEnum.isUserGet()) {
            return;
        }

        List<CouponGetOrUseFreqRuleEntity> couponGetOrUseFreqRuleEntities = couponGetOrUseFreqRuleMapper.findByCouponId(productCouponEditReq.getProductCouponId());
        if (isEmpty(couponGetOrUseFreqRuleEntities)) {
            return;
        }
        CouponGetOrUseFreqRuleEntity couponGetOrUseFreqRule = getCouponGetOrUseFreqRule(productCouponEditReq, couponGetOrUseFreqRuleEntities);
        couponGetOrUseFreqRuleMapper.updateByPrimaryKeySelective(couponGetOrUseFreqRule);
    }

    /**
     * 获取CouponGetOrUseFreqRuleEntity对象
     *
     * @param productCouponEditReq
     * @param couponGetOrUseFreqRuleEntities
     * @return
     */
    private CouponGetOrUseFreqRuleEntity getCouponGetOrUseFreqRule(ProductCouponEditReq productCouponEditReq, List<CouponGetOrUseFreqRuleEntity> couponGetOrUseFreqRuleEntities) {
        CouponGetOrUseFreqRuleEntity existCouponGetOrUseFreqRuleEntity = couponGetOrUseFreqRuleEntities.get(0);

        CouponGetOrUseFreqRuleEntity couponGetOrUseFreqRule = new CouponGetOrUseFreqRuleEntity();
        couponGetOrUseFreqRule.setUuid(existCouponGetOrUseFreqRuleEntity.getUuid());
        couponGetOrUseFreqRule.setUnit(productCouponEditReq.getUnit());
        couponGetOrUseFreqRule.setPersonTotalNum(productCouponEditReq.getPersonTotalNum());

        CouponUnitEnum couponUnitEnum = getCardequityEnum(CouponUnitEnum.class, productCouponEditReq.getUnit());
        couponUnitEnum.setAllowCount(couponGetOrUseFreqRule, productCouponEditReq.getPersonTotalNum(), productCouponEditReq.getAllowCount());
        return couponGetOrUseFreqRule;
    }

    /**
     * 获取ProductCouponEntity对象
     *
     * @param productCouponEditReq
     * @param existProductCoupon
     * @return
     */
    private ProductCouponEntity getProductCoupon(ProductCouponEditReq productCouponEditReq, ProductCouponEntity existProductCoupon) {
        ProductCouponEntity productCoupon = new ProductCouponEntity();
        productCoupon.setUuid(productCouponEditReq.getProductCouponId());
        productCoupon.setCouponName(productCouponEditReq.getCouponName());
        productCoupon.setCouponDesc(productCouponEditReq.getCouponDesc());
        productCoupon.setCouponShortDesc(productCouponEditReq.getCouponShortDesc());

        ProductCouponGetTypeEnum productCouponGetTypeEnum = getCardequityEnum(ProductCouponGetTypeEnum.class, existProductCoupon.getGetType());
        if (productCouponGetTypeEnum.isUserGet()) {
            productCoupon.setAllowGetEndDate(productCouponEditReq.getAllowGetEndDate());
        }
        return productCoupon;
    }

    /**
     * 设置商品优惠券上下架状态
     *
     * @param productCouponStatusReq
     * @param productCouponIds
     */
    private void doSetStatus(ProductCouponStatusReq productCouponStatusReq, List<String> productCouponIds) {
        String status = productCouponStatusReq.getStatus();
        List<ProductCouponEntity> productCoupons = new ArrayList<>();
        ProductCouponEntity productCoupon = null;

        for (String productCouponId : productCouponIds) {
            productCoupon = new ProductCouponEntity();
            productCoupon.setUuid(productCouponId);
            productCoupon.setStatus(status);
            productCoupons.add(productCoupon);
        }
        batchService.batchDispose(productCoupons, ProductCouponMapper.class, "updateByPrimaryKeySelective");
    }
}
