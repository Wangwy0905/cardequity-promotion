package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.promotion.biz.constant.CommonConstant;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponQuotaRuleMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponStageUseAndGetRuleMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.ProductCouponMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponQuotaRuleEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponStageUseAndGetRuleEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.biz.service.ClientCouponService;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.ObtainRspDto;
import com.youyu.cardequity.promotion.dto.ClientCoupStatisticsQuotaDto;
import com.youyu.cardequity.promotion.dto.ShortCouponDetailDto;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.CouponStatus;
import com.youyu.cardequity.promotion.enums.dict.OpCouponType;
import com.youyu.cardequity.promotion.vo.req.ClientObtainCouponReq;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.youyu.cardequity.promotion.biz.dal.entity.ClientCouponEntity;
import com.youyu.cardequity.promotion.dto.ClientCouponDto;
import com.youyu.cardequity.promotion.biz.dal.dao.ClientCouponMapper;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.youyu.cardequity.promotion.enums.ResultCode.*;


/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
@Service
public class ClientCouponServiceImpl extends AbstractService<String, ClientCouponDto, ClientCouponEntity, ClientCouponMapper> implements ClientCouponService {

    @Autowired
    private ClientCouponMapper clientCouponMapper;

    @Autowired
    private ProductCouponMapper productCouponMapper;

    @Autowired
    private CouponStageUseAndGetRuleMapper couponStageUseAndGetRuleMapper;

    @Autowired
    private CouponQuotaRuleMapper couponQuotaRuleMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCouponServiceImpl.class);


    /**
     * 获取客户已领取的券,含：已使用(status=1和2)，未使用（status=0且有效期内），已过期（status=0且未在有效期内）
     *
     * @return 返回已领取的券
     * @Param clientId:指定客户号，必填
     */
    public List<ClientCouponDto> FindClientCoupon(String clientId) {


        List<ClientCouponEntity> clientCouponEnts = clientCouponMapper.findClientCoupon(clientId);
        return BeanPropertiesConverter.copyPropertiesOfList(clientCouponEnts, ClientCouponDto.class);

    }

    /**
     * 领取优惠券
     *
     * @return 是否领取成功
     * @Param req:有参数clientId-客户号（必填），couponId-领取的券Id（必填）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ObtainRspDto ObtainCoupon(ClientObtainCouponReq req) {
        ObtainRspDto dto = new ObtainRspDto();
        dto.setSuccess(true);
        //1.根据券id获取优惠券信息
        ProductCouponEntity coupon = productCouponMapper.findProductCouponById(req.getCouponId());
        if (coupon == null) {
            throw new BizException(COUPON_NOT_EXISTS.getCode(), COUPON_NOT_EXISTS.getDesc());
        }

        //2.校验券基本信息是否允许领取：
        dto = CheckCouponBase(coupon, req);
        //校验不通过直接返回
        if (!dto.getSuccess()) {
            return dto;
        }

        //3.校验券的额度限制是否满足
        //检查指定客户的额度信息
        CouponQuotaRuleEntity quota = couponQuotaRuleMapper.findCouponQuotaRuleById(req.getCouponId());
        dto = CheckCouponPersonQuota(quota, req.getClinetId());
        //校验不通过直接返回
        if (!dto.getSuccess()) {
            return dto;
        }

        //检查所有客户领取额度情况
        dto = CheckCouponAllQuota(quota);
        //校验不通过直接返回
        if (!dto.getSuccess()) {
            return dto;
        }

        //4.校验券的领取频率是否满足
        List<ShortCouponDetailDto> couponDetailListByIds = productCouponMapper.findClinetFreqForbidCouponDetailListByIds(req.getClinetId(), req.getCouponId());

        //传入的参数是空时，给一个统一值存到临时变量中方便进行比较
        String t_stageId = req.getStageId();

        CouponStageUseAndGetRuleEntity couponStage = null;
        //获取领取的阶梯
        if (!CommonUtils.isEmptyorNull(req.getStageId())) {
            couponStage = couponStageUseAndGetRuleMapper.findCouponStageById(req.getCouponId(),
                    req.getStageId(),
                    OpCouponType.USERULE.getDictValue());
            //如果找不到阶梯则传入参数有误
            if (couponStage == null) {
                throw new BizException(COUPON_NOT_EXISTS.getCode(), COUPON_NOT_EXISTS.getDesc());
            }
        } else {
            //传入的参数是空时，给一个统一值存到临时变量中方便进行比较
            t_stageId = CommonDict.NULLREPLACE.getCode();
        }

        //逐一进行排除
        for (ShortCouponDetailDto item : couponDetailListByIds) {
            //获取的阶梯id是空时，给一个统一值与临时变量方便进行比较
            if (CommonUtils.isEmptyorNull(item.getStageId()))
                item.setStageId(CommonDict.NULLREPLACE.getCode());
            //该阶梯或券是需要排除的直接返回
            if (item.getCouponId().equals(req.getCouponId()) &&
                    item.getStageId().equals(t_stageId)) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_GET_FREQ.getDesc());
                return dto;
            }
        }

        //5.增加客户已领优惠券
        ClientCouponEntity entity = new ClientCouponEntity();
        entity.setClintId(req.getClinetId());
        entity.setCouponId(req.getCouponId());
        entity.setStageId(req.getStageId());
        //优惠金额以阶段设置为准
        if (couponStage != null)
            entity.setCouponAmout(couponStage.getCouponValue());
        else
            entity.setCouponAmout(coupon.getProfitValue());
        entity.setEntrustWay(req.getEntrustWay());
        entity.setIsEnable(CommonDict.IF_YES.getCode());
        entity.setStatus(CouponStatus.NORMAL.getDictValue());
        entity.setJoinOrderId(req.getActivityId());
        int count = clientCouponMapper.insert(entity);
        if (count <= 0) {
            throw new BizException(COUPON_FAIL_OBTAIN.getCode(), COUPON_FAIL_OBTAIN.getDesc());
        }
        ClientCouponDto rsp = new ClientCouponDto();
        BeanUtils.copyProperties(entity, rsp);
        dto.setData(rsp);

        return dto;
    }

    /**
     * 根据优惠券基本信息校验是否可领取
     *
     * @param coupon 优惠券基本信息
     * @param req    用于校验的商品相关属性、客户相关属性、订单相关属性、支付相关属性值
     * @return
     */
    private ObtainRspDto CheckCouponBase(ProductCouponEntity coupon,
                                         ClientObtainCouponReq req) {
        ObtainRspDto dto = new ObtainRspDto();
        dto.setSuccess(true);
        //是否在允許領取期間
        if (coupon.getAllowGetBeginDate().compareTo(LocalDate.now()) > 0 ||
                coupon.getAllowGetEndDate().compareTo(LocalDate.now()) < 0) {

            dto.setSuccess(false);
            dto.setDesc(COUPON_NOT_ALLOW_DATE.getDesc());
            return dto;
        }

        //a.客户属性校验
        // 客户类型是否允许领取
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(coupon.getClientTypeSet(),
                req.getClinetType())) {

            dto.setSuccess(false);
            dto.setDesc(COUPON_NOT_ALLOW_CLIENTTYPE.getDesc());
            return dto;
        }

        //b.商品属性校验
        //该商品属性是否允许领取该券
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(coupon.getProductSet(),
                req.getProductId())) {

            dto.setSuccess(false);
            dto.setDesc(COUPON_NOT_ALLOW_PRODUCT.getDesc());
            return dto;
        }

        //该商品组是否允许领取该券
        if (!CommonUtils.isEmptyIgnoreOrWildcardOrContains(coupon.getProductGroupSet(),
                req.getGroupId())) {
            dto.setSuccess(false);
            dto.setDesc(COUPON_NOT_ALLOW_PRODUCT.getDesc());
            return dto;
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
     * @return
     */
    private ObtainRspDto CheckCouponPersonQuota(CouponQuotaRuleEntity quota,
                                                String clientId) {
        ObtainRspDto dto = new ObtainRspDto();
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
     * @return
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
     * @return
     */
    private ObtainRspDto CheckCouponAllQuota(CouponQuotaRuleEntity quota) {
        ObtainRspDto dto = new ObtainRspDto();
        dto.setSuccess(true);

        if (quota != null) {
            ClientCoupStatisticsQuotaDto statisticsQuotaDto = null;
            //校验所有客户每天最大优惠额
            int validflag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPersonMaxAmount());
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

            //校验所有客户最大优惠额
            validflag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPersonMaxAmount());
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
            //校验所有客户最大领取数量
            validflag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPersonMaxAmount());
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
     * @return
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




