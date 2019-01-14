package com.youyu.cardequity.promotion.biz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.common.base.util.BeanPropertiesUtils;
import com.youyu.cardequity.common.spring.service.BatchService;
import com.youyu.cardequity.promotion.biz.dal.dao.*;
import com.youyu.cardequity.promotion.biz.dal.entity.*;
import com.youyu.cardequity.promotion.biz.service.ClientCouponService;
import com.youyu.cardequity.promotion.biz.service.CouponRefProductService;
import com.youyu.cardequity.promotion.biz.service.ProductCouponService;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.biz.utils.SnowflakeIdWorker;
import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.cardequity.promotion.dto.*;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.other.CouponDetailDto;
import com.youyu.cardequity.promotion.dto.other.ShortCouponDetailDto;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.*;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.CouponPageQryRsp;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
import com.youyu.common.api.PageData;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.youyu.cardequity.common.base.util.PaginationUtils.convert;
import static com.youyu.cardequity.promotion.enums.ResultCode.PARAM_ERROR;


/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 * 开发日志
 * V1.0-V1 1004244-徐长焕-20181207 新建代码，findEnableGetCoupon：获取客户可领取的券
 */
@Service
public class ProductCouponServiceImpl extends AbstractService<String, ProductCouponDto, ProductCouponEntity, ProductCouponMapper> implements ProductCouponService {

    @Autowired
    private ProductCouponMapper productCouponMapper;

    @Autowired
    private CouponStageRuleMapper couponStageRuleMapper;

    @Autowired
    private CouponGetOrUseFreqRuleMapper couponGetOrUseFreqRuleMapper;

    @Autowired
    private CouponAndActivityLabelMapper couponAndActivityLabelMapper;

    @Autowired
    private CouponQuotaRuleMapper couponQuotaRuleMapper;

    @Autowired
    private BatchService batchService;

    @Autowired
    private CouponRefProductService couponRefProductService;

    /**
     * 1004259-徐长焕-20181210 新增
     * 功能：查询指定商品可领取的优惠券
     * @param qryProfitCommonReq 优惠查询请求体
     * @return 优惠券详情列表
     * 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    @Override
    public List<CouponDetailDto> findEnableGetCoupon(QryProfitCommonReq qryProfitCommonReq) {
        List<ProductCouponEntity> productCouponlist =null;

        if(CommonConstant.EXCLUSIONFLAG_ALL.equals(qryProfitCommonReq.getExclusionFlag()) ) {
            //获取满足条件的优惠券：1.满足对应商品属性(指定商品或组)、客户属性(指定客户类型)、订单属性(指定客户类型)；2.满足券额度(券每日领取池，券总金额池，券总量池)
            productCouponlist = productCouponMapper.findEnableGetCouponList(qryProfitCommonReq.getProductId(), qryProfitCommonReq.getEntrustWay(), qryProfitCommonReq.getClinetType());
        }else{
            productCouponlist = productCouponMapper.findEnableGetCouponListByCommon(qryProfitCommonReq.getProductId(), qryProfitCommonReq.getEntrustWay(), qryProfitCommonReq.getClinetType());
        }
        List<CouponDetailDto> result = new ArrayList<>();

        //根据客户对上述券领取情况，以及该券领取频率限制进行排除
        for (ProductCouponEntity item : productCouponlist) {
            //如果非新注册用户,排除掉新用户专享的
            if (!CommonConstant.USENEWREGISTER_YES.equals(qryProfitCommonReq.getNewRegisterFlag())){
                if (UsedStage.Register.getDictValue().equals(item.getGetType())){
                    continue;
                }
            }

            //查询子券信息
            List<CouponStageRuleEntity> stageList = couponStageRuleMapper.findStageByCouponId(item.getId());

            List<ShortCouponDetailDto> shortStageList = new ArrayList<>();
            //获取不满足领取频率的数据
            if(!CommonConstant.EXCLUSIONFLAG_ALL.equals(qryProfitCommonReq.getExclusionFlag()) ) {
                //查询客户受频率限制的券
                if (!CommonUtils.isEmptyorNull(qryProfitCommonReq.getClinetId())) {
                    shortStageList = couponGetOrUseFreqRuleMapper.findClinetFreqForbidCouponDetailListById(qryProfitCommonReq.getClinetId(), item.getId(), "");
                }
            }

            //子券因领取频率受限的
            if (stageList.size() > 0 && shortStageList.size() > 0) {
                List<CouponStageRuleDto> couponStageList = new ArrayList<>();

                for (CouponStageRuleEntity stageItem : stageList) {
                    //排除用户领取频率限制的
                    boolean isExsit = false;
                    for (ShortCouponDetailDto shortItem : shortStageList) {
                        if (stageItem.getUuid().equals(shortItem.getStageId())) {
                            isExsit = true;
                            shortStageList.remove(stageItem);//线程安全
                            break;
                        }
                    }
                    //该阶梯没有领取频率限制则可领取
                    if (!isExsit)
                        couponStageList.add(BeanPropertiesConverter.copyProperties(stageItem, CouponStageRuleDto.class));
                }
                //有阶梯的优惠券，如果有0个阶梯能领取该券可领取，否则该券不能领取
                if (couponStageList.size() > 0) {
                    CouponDetailDto rsp = combinationCoupon(item);
                    rsp.setStageList(couponStageList);//重置可领的子券信息
                    result.add(rsp);
                }
            }
            //没有领取频率受限的
            else if (shortStageList.size() <= 0) {
                CouponDetailDto rsp = combinationCoupon(item);
                result.add(rsp);

            }
            //没有子券，受领取频率限制
            else {
                boolean isLimit = false;
                //正常数据没有阶梯的券，此处只会有一条在shortStageList中，循环是为了容错
                for (ShortCouponDetailDto shortItem : shortStageList) {
                    if (item.getUuid().equals(shortItem.getCouponId()) &&
                            CommonUtils.isEmptyorNull(shortItem.getStageId())) {
                        isLimit = true;
                        break;
                    }
                }
                //该券没有领取频率限制则可领取
                if (!isLimit) {
                    CouponDetailDto rsp = combinationCoupon(item);
                    result.add(rsp);
                }
            }
        }

        return result;

    }

    /**
     * 添加优惠券
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<CouponDetailDto> addCoupon(CouponDetailDto req) {
        //权益中心标志为3，活动表标识为1，ProductCoupon表标识为2
        SnowflakeIdWorker stageWorker = new SnowflakeIdWorker(3, 2);
        CommonBoolDto<CouponDetailDto> result = new CommonBoolDto<>(false);
        List<CouponStageRuleEntity> stageList = new ArrayList<>();
        List<CouponGetOrUseFreqRuleEntity> freqRuleList = new ArrayList<>();
        int sqlresult = 0;

        ProductCouponDto dto = req.getProductCouponDto();
        if (dto == null) {
            result.setDesc("没有指定编辑信息");
            return result;
        }

        if (dto.getAllowGetBeginDate() == null) {
            if (dto.getAllowUseBeginDate() == null) {
                dto.setAllowUseBeginDate(LocalDateTime.now());
            }
            dto.setAllowGetBeginDate(dto.getAllowUseBeginDate());
        } else {
            //参数保护，使用日期为空则等于领取有效起始日
            if (dto.getAllowUseBeginDate() == null) {
                dto.setAllowUseBeginDate(dto.getAllowGetBeginDate());
            }
        }

        if (dto.getAllowGetBeginDate() == null) {
            dto.setAllowGetBeginDate(LocalDateTime.now());
        }
        if (dto.getAllowGetEndDate() == null) {
            dto.setAllowGetEndDate(LocalDateTime.of(2099, 12, 31, 0, 0, 0));
        }
        if (dto.getAllowUseBeginDate() == null) {
            dto.setAllowUseBeginDate(LocalDateTime.now());
        }
        if (dto.getAllowUseEndDate() == null) {
            dto.setAllowUseEndDate(LocalDateTime.of(2099, 12, 31, 0, 0, 0));
        }

        //参数保护，默认使用日期为最大
        if (dto.getAllowUseEndDate() == null) {
            dto.setAllowUseEndDate(LocalDateTime.of(2099, 12, 31, 0, 0, 0));
        }
        if (dto.getAllowGetEndDate() == null) {
            dto.setAllowGetEndDate(dto.getAllowUseEndDate());
        }

        //参数保护，默认期限为无期限
        if (dto.getValIdTerm() == null)
            dto.setValIdTerm(0);

        //参数保护，实际有效日期=领取日+期限
        if (dto.getUseGeEndDateFlag() == null)
            dto.setUseGeEndDateFlag(UseGeEndDateFlag.NO.getDictValue());

        if (dto.getUsedStage() == null)
            dto.setUsedStage(UsedStage.AfterPay.getDictValue());

        if (dto.getGetStage() == null)
            dto.setGetStage(UsedStage.Other.getDictValue());


        if (dto.getAllowUseEndDate().compareTo(dto.getAllowUseBeginDate()) < 0) {
            result.setDesc("优惠券使用日期无效：起始值" + dto.getAllowUseBeginDate() + "；结束值" + dto.getAllowUseEndDate());
            return result;
        }

        if (dto.getAllowGetEndDate().compareTo(dto.getAllowGetBeginDate()) < 0) {
            result.setDesc("优惠券领取日期无效：起始值" + dto.getAllowGetBeginDate() + "；结束值" + dto.getAllowGetEndDate());
            return result;
        }

        if (CouponType.FREETRANSFERFARE.equals(dto.getCouponType()) || CouponType.TRANSFERFARE.equals(dto.getCouponType())) {
            dto.setCouponLevel(CouponActivityLevel.GLOBAL.getDictValue());
        } else if (dto.getCouponLevel() == null) {
            result.setDesc("优惠券等级参数为空：参数值" + dto.getCouponLevel());
            return result;
        }

        if (dto.getCouponStrategyType() == null) {
            result.setDesc("优惠券策略类型没有设置：参数值" + dto.getCouponStrategyType());
            return result;
        }

        if (CouponStrategyType.discount.equals(dto.getCouponStrategyType())) {
            if (BigDecimal.ONE.compareTo(dto.getProfitValue()) <= 0) {
                result.setDesc("折扣优惠券优惠折扣不能高于1，参数值" + dto.getProfitValue());
                return result;
            }
        }
        if (!CommonUtils.isGtZeroDecimal(dto.getProfitValue())) {
            result.setDesc("折扣优惠券优惠折扣不能低于0，参数值" + dto.getProfitValue());
            return result;
        }

        if (dto.getLabelDto() == null) {
            result.setDesc("没有指定标签");
            return result;
        }
        CouponAndActivityLabelEntity labelById = couponAndActivityLabelMapper.findLabelById(dto.getLabelDto().getId());
        if (labelById == null) {
            result.setDesc("指定标签不存在" + dto.getLabelDto().getId());
            return result;
        }

        //如果指定商品集合，默认为自定义配置
        if (req.getProductList() != null || !req.getProductList().isEmpty()) {
            dto.setApplyProductFlag(ApplyProductFlag.APPOINTPRODUCT.getDictValue());
        }
        //生成优惠编号
        dto.setId(stageWorker.nextId() + "");

        //【处理阶梯】
        if (req.getStageList() != null && req.getStageList().size() <= 0) {
            if (!CouponType.TRANSFERFARE.getDictValue().equals(dto.getCouponType()) && !CouponType.FREETRANSFERFARE.getDictValue().equals(dto.getCouponType())) {
                //从普通消费券分化出有门槛和无门槛优惠券
                if (CouponStrategyType.stage.equals(dto.getCouponStrategyType())) {
                    dto.setCouponStrategyType(CouponStrategyType.fix.getDictValue());
                    dto.setCouponType(CouponType.MONEYBAG.getDictValue());
                }
            }
        } else {
            //组装子券信息
            for (CouponStageRuleDto stage : req.getStageList()) {
                if (stage.getBeginValue() == null)
                    stage.setBeginValue(BigDecimal.ZERO);
                if (!CommonUtils.isGtZeroDecimal(stage.getEndValue())) {
                    stage.setEndValue(CommonConstant.IGNOREVALUE);
                }
                if (!CommonUtils.isGtZeroDecimal(stage.getCouponValue()) && CommonUtils.isGtZeroDecimal(dto.getProfitValue())) {
                    stage.setCouponValue(dto.getProfitValue());
                }

                CouponStageRuleEntity stageRuleEntity = BeanPropertiesUtils.copyProperties(stage, CouponStageRuleEntity.class);
                stage.setCouponId(dto.getId());//将先生成的id设置到门槛阶梯
                stageRuleEntity.setIsEnable(CommonDict.IF_YES.getCode());
                //子券id=券id+前补03位
                //stage.setId(stage.getCouponId() + String.format("%03d", Integer.valueOf(req.getStageList().indexOf(stage))));
                stageRuleEntity.setId(CommonUtils.getUUID());
                stage.setId(stageRuleEntity.getId());
                stageList.add(stageRuleEntity);
            }
            batchService.batchDispose(stageList, CouponStageRuleMapper.class, "insert");
        }

        //【处理频率】
        couponGetOrUseFreqRuleMapper.logicDelByCouponId(dto.getId());
        if (req.getFreqRuleList() != null) {
            for (CouponGetOrUseFreqRuleDto item : req.getFreqRuleList()) {
                item.setValue(1);//默认不支持多频率
                item.setCouponId(dto.getId());//将先生成的id设置到门槛阶梯
                if (CommonUtils.isEmptyorNull(item.getOpCouponType()))
                    item.setOpCouponType(OpCouponType.GETRULE.getDictValue());

                if (CommonUtils.isEmptyorNull(item.getStageId())) {
                    if (req.getStageList() != null && req.getStageList().size() == 1) {
                        item.setStageId(req.getStageList().get(0).getId());
                    }
                }

                CouponGetOrUseFreqRuleEntity freqRuleEntity = BeanPropertiesUtils.copyProperties(item, CouponGetOrUseFreqRuleEntity.class);
                freqRuleEntity.setIsEnable(CommonDict.IF_YES.getCode());
                freqRuleEntity.setId(CommonUtils.getUUID());
                item.setId(freqRuleEntity.getId());//返回生成的id
                freqRuleList.add(freqRuleEntity);

            }
            batchService.batchDispose(freqRuleList, CouponGetOrUseFreqRuleMapper.class, "insert");
        }

        //【处理限额】
        if (req.getQuotaRule() != null) {

            req.getQuotaRule().setCouponId(dto.getId());//将先生成的id设置到额度
            CouponQuotaRuleEntity quotaRuleEntity = BeanPropertiesUtils.copyProperties(req.getQuotaRule(), CouponQuotaRuleEntity.class);
            quotaRuleEntity.setIsEnable(CommonDict.IF_YES.getCode());
            sqlresult = couponQuotaRuleMapper.insert(quotaRuleEntity);
            if (sqlresult <= 0) {
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("新增优惠额度信息错误，编号" + quotaRuleEntity.getId()));
            }
        }

        //【配置适用商品】
        if (req.getProductList() != null) {
            BatchRefProductReq refProductReq = new BatchRefProductReq();
            refProductReq.setId(dto.getId());//将先生成的id设置到商品
            refProductReq.setProductList(req.getProductList());
            couponRefProductService.batchAddCouponRefProduct(refProductReq);
        }

        //【基本信息】
        ProductCouponEntity entity = BeanPropertiesUtils.copyProperties(dto, ProductCouponEntity.class);
        entity.setCouponLable(dto.getLabelDto().getId());
        entity.setIsEnable(CommonDict.IF_YES.getCode());
        sqlresult = productCouponMapper.insert(entity);
        if (sqlresult <= 0) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("新增优惠信息错误，编号" + entity.getId()));
        }
        result.setSuccess(true);
        result.setData(req);

        return result;
    }


    /**
     * 编辑优惠券
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<CouponDetailDto> editCoupon(CouponDetailDto req) {
        CommonBoolDto<CouponDetailDto> result = new CommonBoolDto<CouponDetailDto>(false);
        //先删后插需要同步处理CouponGetOrUseFreqRule：因为这表中存有对应字段
        List<CouponStageRuleEntity> stageList = new ArrayList<>();
        List<CouponGetOrUseFreqRuleEntity> freqRuleList = new ArrayList<>();
        int sqlresult = 0;

        ProductCouponDto dto = req.getProductCouponDto();
        if (dto == null) {
            result.setDesc("没有指定编辑信息");
            return result;
        }

        if (dto.getAllowGetBeginDate() == null) {
            if (dto.getAllowUseBeginDate() == null) {
                dto.setAllowUseBeginDate(LocalDateTime.now());
            }
            dto.setAllowGetBeginDate(dto.getAllowUseBeginDate());
        } else {
            //参数保护，使用日期为空则等于领取有效起始日
            if (dto.getAllowUseBeginDate() == null) {
                dto.setAllowUseBeginDate(dto.getAllowGetBeginDate());
            }
        }

        //参数保护，默认使用日期为最大
        if (dto.getAllowUseEndDate() == null) {
            dto.setAllowUseEndDate(LocalDateTime.of(2099, 12, 31, 0, 0, 0));
        }
        if (dto.getAllowGetEndDate() == null) {
            dto.setAllowGetEndDate(dto.getAllowUseEndDate());
        }

        //参数保护，默认期限为无期限
        if (dto.getValIdTerm() == null)
            dto.setValIdTerm(0);

        //参数保护，实际有效日期=领取日+期限
        if (dto.getUseGeEndDateFlag() == null)
            dto.setUseGeEndDateFlag(UseGeEndDateFlag.NO.getDictValue());

        if (dto.getUsedStage() == null)
            dto.setUsedStage(UsedStage.AfterPay.getDictValue());

        if (dto.getGetStage() == null)
            dto.setGetStage(UsedStage.Other.getDictValue());


        if (dto.getAllowUseEndDate().compareTo(dto.getAllowUseBeginDate()) < 0) {
            result.setDesc("优惠券使用日期无效：起始值" + dto.getAllowUseBeginDate() + "；结束值" + dto.getAllowUseEndDate());
            return result;
        }

        if (dto.getAllowGetEndDate().compareTo(dto.getAllowGetBeginDate()) < 0) {
            result.setDesc("优惠券领取日期无效：起始值" + dto.getAllowGetBeginDate() + "；结束值" + dto.getAllowGetEndDate());
            return result;
        }

        //运费券的登记为全局
        if (CouponType.FREETRANSFERFARE.equals(dto.getCouponType()) || CouponType.TRANSFERFARE.equals(dto.getCouponType())) {
            dto.setCouponLevel(CouponActivityLevel.GLOBAL.getDictValue());
        } else {
            if (dto.getCouponLevel() == null) {
                result.setDesc("优惠券等级参数为空：参数值" + dto.getCouponLevel());
                return result;
            }
        }

        //必须要指定为折扣、满减等策略
        if (dto.getCouponStrategyType() == null) {
            result.setDesc("优惠券策略类型没有设置：参数值" + dto.getCouponStrategyType());
            return result;
        }

        //折扣的值必须在(0,1)之间
        if (CouponStrategyType.discount.equals(dto.getCouponStrategyType())) {
            if (BigDecimal.ONE.compareTo(dto.getProfitValue()) <= 0) {
                result.setDesc("折扣优惠券优惠折扣不能高于1，参数值" + dto.getProfitValue());
                return result;
            }
        }

        if (!CommonUtils.isGtZeroDecimal(dto.getProfitValue())) {
            result.setDesc("折扣优惠券优惠折扣不能低于0，参数值" + dto.getProfitValue());
            return result;
        }

        //校验标签是否存在
        if (dto.getLabelDto() == null) {
            result.setDesc("没有指定标签");
            return result;
        }
        CouponAndActivityLabelEntity labelById = couponAndActivityLabelMapper.findLabelById(dto.getLabelDto().getId());
        if (labelById == null) {
            result.setDesc("指定标签不存在" + dto.getLabelDto().getId());
            return result;
        }

        //编辑主体必须存在
        ProductCouponEntity entity = productCouponMapper.findProductCouponById(dto.getId());
        if (entity == null) {
            result.setDesc("指定编辑优惠券不存在");
            return result;
        }

        //如果指定商品集合，默认为自定义配置
        if (req.getProductList() != null || !req.getProductList().isEmpty()) {
            dto.setApplyProductFlag(ApplyProductFlag.APPOINTPRODUCT.getDictValue());
        }

        //【处理阶梯】
        //先逻辑删除门槛信息
        couponStageRuleMapper.logicDelByCouponId(dto.getId());
        if (req.getStageList() != null && req.getStageList().size() <= 0) {
            if (!CouponType.TRANSFERFARE.getDictValue().equals(dto.getCouponType()) && !CouponType.FREETRANSFERFARE.getDictValue().equals(dto.getCouponType())) {
                //从普通消费券分化出有门槛和无门槛优惠券
                if (CouponStrategyType.stage.equals(dto.getCouponStrategyType())) {
                    dto.setCouponStrategyType(CouponStrategyType.fix.getDictValue());
                    dto.setCouponType(CouponType.MONEYBAG.getDictValue());
                }
            }
        } else {
            //组装子券信息
            for (CouponStageRuleDto stage : req.getStageList()) {
                CouponStageRuleEntity stageRuleEntity = new CouponStageRuleEntity();
                if (stage.getBeginValue() == null)
                    stage.setBeginValue(BigDecimal.ZERO);
                if (!CommonUtils.isGtZeroDecimal(stage.getEndValue())) {
                    stage.setEndValue(CommonConstant.IGNOREVALUE);
                }

                if (!CommonUtils.isGtZeroDecimal(stage.getCouponValue()) || CommonUtils.isGtZeroDecimal(dto.getProfitValue())) {
                    stage.setCouponValue(dto.getProfitValue());
                }

                stage.setCouponId(dto.getId());
                BeanPropertiesUtils.copyProperties(stage, stageRuleEntity);
                stageRuleEntity.setIsEnable(CommonDict.IF_YES.getCode());
                if (CommonUtils.isEmptyorNull(stage.getId())) {
                    //子券id=券id+前补03位
                    //stage.setId(stage.getCouponId() + String.format("%03d", Integer.valueOf(req.getStageList().indexOf(stage))));
                    stageRuleEntity.setId(CommonUtils.getUUID());
                    stage.setId(stageRuleEntity.getId());
                    sqlresult = couponStageRuleMapper.insert(stageRuleEntity);
                    if (sqlresult <= 0) {
                        throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("插入优惠门槛错误，子券id" + stageRuleEntity.getId()));
                    }
                } else {
                    sqlresult = couponStageRuleMapper.updateByPrimaryKeySelective(stageRuleEntity);
                    if (sqlresult <= 0) {
                        throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("更新优惠门槛错误，子券id" + stageRuleEntity.getId()));
                    }
                }
                stageList.add(stageRuleEntity);
            }

        }

        //【处理频率】
        couponGetOrUseFreqRuleMapper.deleteByCouponId(dto.getId());
        //couponGetOrUseFreqRuleMapper.logicDelByCouponId(dto.getId());
        if (req.getFreqRuleList() != null) {
            for (CouponGetOrUseFreqRuleDto item : req.getFreqRuleList()) {
                item.setValue(1);//默认不支持多频率
                item.setCouponId(dto.getId());
                if (CommonUtils.isEmptyorNull(item.getOpCouponType()))
                    item.setOpCouponType(OpCouponType.GETRULE.getDictValue());

                if (CommonUtils.isEmptyorNull(item.getStageId())) {
                    if (req.getStageList() != null && req.getStageList().size() == 1) {
                        item.setStageId(req.getStageList().get(0).getId());
                    }
                }

                item.setCouponId(dto.getId());
                CouponGetOrUseFreqRuleEntity freqRuleEntity = BeanPropertiesUtils.copyProperties(item, CouponGetOrUseFreqRuleEntity.class);
                freqRuleEntity.setIsEnable(CommonDict.IF_YES.getCode());
                if (CommonUtils.isEmptyorNull(item.getId())) {
                    freqRuleEntity.setId(CommonUtils.getUUID());
                    item.setId(freqRuleEntity.getId());
                    sqlresult = couponGetOrUseFreqRuleMapper.insert(freqRuleEntity);
                    if (sqlresult <= 0) {
                        throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("插入优惠适用频率错误，编号" + freqRuleEntity.getId()));
                    }
                } else {
                    sqlresult = couponGetOrUseFreqRuleMapper.updateByPrimaryKey(freqRuleEntity);
                    if (sqlresult <= 0) {
                        throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("更新优惠适用频率错误，编号" + freqRuleEntity.getId()));
                    }
                }

                freqRuleList.add(freqRuleEntity);
            }
        }

        //【处理限额】
        sqlresult = couponQuotaRuleMapper.logicDelByCouponId(dto.getId());
        if (req.getQuotaRule() != null) {

            req.getQuotaRule().setCouponId(dto.getId());
            CouponQuotaRuleEntity quotaRuleEntity = BeanPropertiesUtils.copyProperties(req.getQuotaRule(), CouponQuotaRuleEntity.class);
            quotaRuleEntity.setIsEnable(CommonDict.IF_YES.getCode());
            if (sqlresult > 0) {
                sqlresult = couponQuotaRuleMapper.updateByPrimaryKey(quotaRuleEntity);
            } else {
                sqlresult = couponQuotaRuleMapper.insert(quotaRuleEntity);
            }
            if (sqlresult <= 0) {
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("处理优惠额度信息错误，编号" + quotaRuleEntity.getId()));
            }
        }

        //【配置适用商品】:传入了代表着需要更新配置

        if (req.getProductList() != null) {
            BatchRefProductReq refProductReq = new BatchRefProductReq();
            refProductReq.setId(dto.getId());
            refProductReq.setProductList(req.getProductList());
            couponRefProductService.batchAddCouponRefProduct(refProductReq);
        }

        BeanPropertiesUtils.copyProperties(dto, entity);
        entity.setCouponLable(dto.getLabelDto().getId());
        entity.setIsEnable(CommonDict.IF_YES.getCode());

        sqlresult = productCouponMapper.updateByPrimaryKey(entity);
        if (sqlresult <= 0) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("更新优惠信息错误，编号" + entity.getId()));
        }
        result.setSuccess(true);
        result.setData(req);

        return result;
    }

    /**
     * 批量删除优惠券
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<Integer> batchDelCoupon(BatchBaseCouponReq req) {
        CommonBoolDto<Integer> result = new CommonBoolDto<Integer>(false);
        if (req.getBaseCouponList() == null || req.getBaseCouponList().size() <= 0) {
            result.setDesc("没有指定删除的优惠券");
            return result;
        }
        //检查是否有效期外优惠券

        //检查是否有有效的已领取的优惠券
        batchService.batchDispose(req.getBaseCouponList(), ProductCouponMapper.class, "logicDelById");

        //逻辑删除门槛
        batchService.batchDispose(req.getBaseCouponList(), CouponStageRuleMapper.class, "logicDelByCouponId");

        //逻辑删除频率
        batchService.batchDispose(req.getBaseCouponList(), CouponGetOrUseFreqRuleMapper.class, "logicDelByCouponId");

        //逻辑删除适用商品
        batchService.batchDispose(req.getBaseCouponList(), CouponRefProductMapper.class, "logicDelByCouponId");

        //逻辑删除额度
        batchService.batchDispose(req.getBaseCouponList(), CouponQuotaRuleMapper.class, "logicDelByCouponId");

        return result;
    }

    /**
     * 开始发放优惠券
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<ProductCouponDto> startGetCoupon(BaseCouponReq req) {
        CommonBoolDto<ProductCouponDto> result = new CommonBoolDto<ProductCouponDto>(false);
        ProductCouponEntity productCouponById = productCouponMapper.findProductCouponById(req.getCouponId());
        if (productCouponById != null) {
            productCouponById.setAllowGetBeginDate(LocalDateTime.now().minusSeconds(1));
            productCouponById.setRemark("开始发放优惠券");
            ProductCouponDto dto = new ProductCouponDto();
            int updateresult = productCouponMapper.updateByPrimaryKey(productCouponById);
            if (updateresult > 0) {
                result.setSuccess(true);
                result.setData(dto);
            } else {
                result.setDesc("更新数据失败");
                return result;
            }
        } else {
            result.setDesc("找不到该优惠券");
            return result;
        }
        return result;
    }

    /**
     * 停止发放优惠券
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonBoolDto<ProductCouponDto> stopGetCoupon(BaseCouponReq req) {
        CommonBoolDto<ProductCouponDto> result = new CommonBoolDto<ProductCouponDto>(false);
        ProductCouponEntity productCouponById = productCouponMapper.findProductCouponById(req.getCouponId());
        if (productCouponById != null) {
            productCouponById.setAllowGetEndDate(LocalDateTime.now().minusSeconds(1));
            productCouponById.setRemark("停止发放优惠券");
            ProductCouponDto dto = new ProductCouponDto();
            int updateresult = productCouponMapper.updateByPrimaryKey(productCouponById);
            if (updateresult > 0) {
                result.setSuccess(true);
                result.setData(dto);
            } else {
                result.setDesc("更新数据失败");
                return result;
            }
        } else {
            result.setDesc("找不到该优惠券");
            return result;
        }
        return result;
    }

    /**
     * 查看商品对应优惠券列表
     *
     * @param req
     * @return
     */
    @Override
    public List<CouponDetailDto> findCouponListByProduct(BaseProductReq req) {
        BaseQryCouponReq qryReq = new BaseQryCouponReq();
        if (req != null)
            BeanPropertiesUtils.copyProperties(req, qryReq);
        List<ProductCouponEntity> entities = productCouponMapper.findCouponListByCommon(qryReq);
        List<CouponDetailDto> dtoList = new ArrayList<>();
        for (ProductCouponEntity couponEntity : entities) {
            dtoList.add(combinationCoupon(couponEntity));
        }
        return dtoList;
    }

    /**
     * 查询所有优惠券列表
     *
     * @param req
     * @return
     */
    @Override
    public CouponPageQryRsp findCouponListByCommon(BaseQryCouponReq req) {
        if (req == null)
            req = new BaseQryCouponReq();

        CouponPageQryRsp result = new CouponPageQryRsp();
        PageHelper.startPage(req.getPageNo(), req.getPageSize());
        // 获取活动分页信息
        PageInfo<ProductCouponEntity> entitiesPage = new PageInfo<>(productCouponMapper.findCouponListByCommon(req));
        List<CouponDetailDto> dtoList = new ArrayList<>();

        for (ProductCouponEntity couponEntity : entitiesPage.getList()) {
            dtoList.add(combinationCoupon(couponEntity));

        }
        List<GatherInfoRsp> gatherInfoRspList = productCouponMapper.findGatherCouponList(req);
        result.setGatherResult(gatherInfoRspList);

        PageData<CouponDetailDto> pageresult = convert(entitiesPage, dtoList);
        result.setResult(pageresult);
        return result;
    }

    /**
     * 模糊查询所有优惠券列表
     *
     * @param req
     * @return
     */
    @Override
    public CouponPageQryRsp findCouponList(BaseQryCouponReq req) {
        if (req == null)
            req = new BaseQryCouponReq();
        CouponPageQryRsp result = new CouponPageQryRsp();
        // pagination
        PageHelper.startPage(req.getPageNo(), req.getPageSize());
        // 获取活动分页信息
        PageInfo<ProductCouponEntity> entitiesPage = new PageInfo<>(productCouponMapper.findCouponList(req));
        List<CouponDetailDto> dtoList = new ArrayList<>();
        int newi = 0, memberi = 0;
        for (ProductCouponEntity couponEntity : entitiesPage.getList()) {
            dtoList.add(combinationCoupon(couponEntity));
        }

        PageData<CouponDetailDto> pageresult = convert(entitiesPage, dtoList);
        result.setResult(pageresult);

        List<GatherInfoRsp> gatherInfoRspList = productCouponMapper.findGatherCouponList(req);
        result.setGatherResult(gatherInfoRspList);
        return result;
    }

    /**
     * 查询优惠汇总信息
     *
     * @param req 普通优惠活动请求体
     * @return 优惠汇总列表
     */
    @Override
    public List<GatherInfoRsp> findGatherCouponByCommon(BaseQryCouponReq req) {
        if (req==null)
            req=new BaseQryCouponReq();
        return productCouponMapper.findGatherCouponList(req);
    }

    /**
     * 查询所有优惠券列表
     *
     * @param req
     * @return
     */
    @Override
    public CouponDetailDto findCouponById(BaseCouponReq req) {
        CouponDetailDto result = null;

        ProductCouponEntity entity = productCouponMapper.findProductCouponById(req.getCouponId());
        if (entity != null) {
            result = combinationCoupon(entity);
        }
        return result;
    }


    /**
     * 查看商品对应优惠券列表
     *
     * @param req
     * @return
     */
    @Override
    public List<CouponDetailDto> findCouponListByIds(List<String> req) {
        List<CouponDetailDto> dtoList = new ArrayList<>();
        if (req == null || req.isEmpty())
           return dtoList;
        List<ProductCouponEntity> entities = productCouponMapper.findCouponListByIds(req);

        for (ProductCouponEntity couponEntity : entities) {
            dtoList.add(combinationCoupon(couponEntity));
        }
        return dtoList;
    }

    /**
     * 拼装优惠券详情
     *
     * @param entity 优惠券主体
     * @return 优惠券详情：含限额、频率、子券信息
     */
    private CouponDetailDto combinationCoupon(ProductCouponEntity entity) {
        CouponDetailDto result = new CouponDetailDto();

        ProductCouponDto productCouponDto = BeanPropertiesUtils.copyProperties(entity, ProductCouponDto.class);
        if (CommonUtils.isEmptyorNull(entity.getCouponLable())) {
            CouponAndActivityLabelEntity labelEntity = couponAndActivityLabelMapper.findLabelById(entity.getCouponLable());
            if (labelEntity != null) {
                CouponAndActivityLabelDto labelDto = BeanPropertiesUtils.copyProperties(labelEntity, CouponAndActivityLabelDto.class);
                productCouponDto.setLabelDto(labelDto);
            }
        }
        result.setProductCouponDto(productCouponDto);
        //查询限额
        CouponQuotaRuleEntity quotaRuleEntity = couponQuotaRuleMapper.findCouponQuotaRuleById(entity.getId());
        result.setQuotaRule(BeanPropertiesConverter.copyProperties(quotaRuleEntity, CouponQuotaRuleDto.class));

        //查询领取频率
        List<CouponGetOrUseFreqRuleEntity> freqRuleEntities = couponGetOrUseFreqRuleMapper.findByCouponId(entity.getId());
        result.setFreqRuleList(BeanPropertiesConverter.copyPropertiesOfList(freqRuleEntities, CouponGetOrUseFreqRuleDto.class));

        //查询子券信息
        List<CouponStageRuleEntity> stageRuleEntities = couponStageRuleMapper.findStageByCouponId(entity.getId());
        result.setStageList(BeanPropertiesConverter.copyPropertiesOfList(stageRuleEntities, CouponStageRuleDto.class));
        return result;
    }

}




