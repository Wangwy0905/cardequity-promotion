package com.youyu.cardequity.promotion.biz.service.impl;

import com.github.pagehelper.PageInfo;
import com.youyu.cardequity.common.base.uidgenerator.UidGenerator;
import com.youyu.cardequity.common.base.util.LocalDateUtils;
import com.youyu.cardequity.common.spring.service.BatchService;
import com.youyu.cardequity.promotion.biz.constant.ClientCouponStatusConstant;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponIssueHistoryMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponIssueMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponQuotaRuleMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.ProductCouponMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.*;
import com.youyu.cardequity.promotion.biz.enums.ProductCouponGetTypeEnum;
import com.youyu.cardequity.promotion.biz.enums.ProductCouponStatusEnum;
import com.youyu.cardequity.promotion.biz.enums.dict.CouponHistoryQueryStatusMapping;
import com.youyu.cardequity.promotion.biz.service.ClientCouponService;
import com.youyu.cardequity.promotion.biz.service.CouponIssueService;
import com.youyu.cardequity.promotion.constant.CommonConstant;
import com.youyu.cardequity.promotion.dto.CouponIssueHistoryQueryDto;
import com.youyu.cardequity.promotion.dto.req.*;
import com.youyu.cardequity.promotion.dto.rsp.*;
import com.youyu.cardequity.promotion.enums.dict.CouponStatus;
import com.youyu.common.api.PageData;
import com.youyu.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.pagehelper.page.PageMethod.startPage;
import static com.youyu.cardequity.common.base.util.CollectionUtils.isEmpty;
import static com.youyu.cardequity.common.base.util.DateUtil.*;
import static com.youyu.cardequity.common.base.util.EnumUtil.getCardequityEnum;
import static com.youyu.cardequity.common.base.util.PaginationUtils.convert;
import static com.youyu.cardequity.common.base.util.StringUtil.eq;
import static com.youyu.cardequity.promotion.enums.CouponIssueResultEnum.ISSUED_FAILED;
import static com.youyu.cardequity.promotion.enums.CouponIssueResultEnum.ISSUED_SUCCESSED;
import static com.youyu.cardequity.promotion.enums.CouponIssueStatusEnum.*;
import static com.youyu.cardequity.promotion.enums.CouponIssueTargetTypeEnum.ACTIVITY_ID;
import static com.youyu.cardequity.promotion.enums.CouponIssueTriggerTypeEnum.DELAY_JOB_TRIGGER_TYPE;
import static com.youyu.cardequity.promotion.enums.CouponIssueVisibleEnum.INVISIBLE;
import static com.youyu.cardequity.promotion.enums.ResultCode.*;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.time.DateUtils.addHours;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 优惠券发放service 实现
 */
@Service
@Slf4j
public class CouponIssueServiceImpl implements CouponIssueService {
    private static final String IS_INVALID = "1";


    @Autowired
    private CouponIssueMapper couponIssueMapper;
    @Autowired
    private ProductCouponMapper productCouponMapper;
    @Autowired
    private CouponQuotaRuleMapper couponQuotaRuleMapper;

    @Autowired
    private UidGenerator uidGenerator;

    @Autowired
    private BatchService batchService;
    @Autowired
    private ClientCouponService clientCouponService;

    @Autowired
    private CouponIssueHistoryMapper couponIssueHistoryMapper;


    @Override
    @Transactional
    public CouponIssueRsp createIssue(CouponIssueReq couponIssueReq) {
        String couponId = couponIssueReq.getCouponId();
        ProductCouponEntity productCouponEntity = productCouponMapper.selectByPrimaryKey(couponId);

        CouponIssueEntity couponIssueEntity = createCouponIssueEntity(couponIssueReq, productCouponEntity);
        checkCreateCoupon(couponIssueEntity, productCouponEntity);
        couponIssueMapper.insertSelective(couponIssueEntity);
        return getCouponIssueRsp(couponIssueEntity.getCouponIssueId());
    }

    @Override
    @Transactional
    public void processIssue(CouponIssueMsgDetailsReq couponIssueMsgDetailsReq) {

        CouponIssueEntity couponIssueEntity = couponIssueMapper.selectByPrimaryKey(couponIssueMsgDetailsReq.getCouponIssueId());

        ProductCouponEntity productCouponEntity = productCouponMapper.selectByPrimaryKey(couponIssueEntity.getCouponId());

        checkCoupon(couponIssueEntity, productCouponEntity, productCouponEntity.getStatus());

        //更新发放状态为发放中
        couponIssueEntity.setIssueStatus(ISSUING.getCode());
        couponIssueMapper.updateByPrimaryKeySelective(couponIssueEntity);


        //确认最终发券的clientID
        List<UserInfo4CouponIssueDto> resultIssueClientCouponList = confirmIssueClient(couponIssueMsgDetailsReq, productCouponEntity);

        //写入clientCoupon
        List<ClientCouponEntity> clientCouponEntityList = clientCouponService.insertClientCoupon(resultIssueClientCouponList, productCouponEntity, couponIssueMsgDetailsReq.getCouponIssueId());

        //写入发放流水
        insertIssueHistory(couponIssueMsgDetailsReq, clientCouponEntityList);


        //更新状态为：已发券
        couponIssueEntity.setIssueStatus(ISSUED.getCode());
        couponIssueMapper.updateByPrimaryKeySelective(couponIssueEntity);
    }


    @Override
    public PageData<CouponIssueHistoryQueryRep> getCouponIssueHistory(CouponIssueHistoryQueryReq couponIssueHistoryQueryReq) {
        CouponIssueEntity couponIssueEntity = couponIssueMapper.selectByPrimaryKey(couponIssueHistoryQueryReq.getCouponIssueId());
        if (couponIssueEntity == null) {
            throw new BizException(COUPON_ISSUE_RECORD_IS_REMOVED);
        }
        startPage(couponIssueHistoryQueryReq.getPageNo(), couponIssueHistoryQueryReq.getPageSize());

        List<CouponIssueHistoryDetailsEntity> couponIssueEntities = couponIssueHistoryMapper.getCouponIssueHistoryDetails(
                createQueryDto(couponIssueHistoryQueryReq));

        PageInfo<CouponIssueHistoryQueryRep> pageInfo = new PageInfo<>(getCouponIssueHistoryQueryRep(couponIssueEntities));
        return convert(pageInfo, CouponIssueHistoryQueryRep.class);
    }

    private CouponIssueHistoryQueryDto createQueryDto(CouponIssueHistoryQueryReq couponIssueHistoryQueryReq) {
        CouponIssueHistoryQueryDto queryDto = new CouponIssueHistoryQueryDto();
        BeanUtils.copyProperties(couponIssueHistoryQueryReq, queryDto);


        String clientCouponStatusCondition = couponIssueHistoryQueryReq.getClientCouponStatus();

        //状态映射转换
        if (ClientCouponStatusConstant.COUPON_NOT_USED.equals(clientCouponStatusCondition)
                || ClientCouponStatusConstant.COUPON_IS_USED.equals(clientCouponStatusCondition)) {
            queryDto.setClientCouponUseStatus(CouponHistoryQueryStatusMapping.requestCodeToDtoCode(clientCouponStatusCondition));
        }

        if (ClientCouponStatusConstant.ISSUED_FAILED_OR_NOTISSUED.equals(clientCouponStatusCondition)) {
            queryDto.setIssueResult(CouponHistoryQueryStatusMapping.requestCodeToDtoCode(clientCouponStatusCondition));
        }
        //过期
        if (ClientCouponStatusConstant.COUPON_IS_INVALID.equals(clientCouponStatusCondition)) {
            queryDto.setCouponInvalid(IS_INVALID);
        }

        return queryDto;

    }

    private List<CouponIssueHistoryQueryRep> getCouponIssueHistoryQueryRep(
            List<CouponIssueHistoryDetailsEntity> couponIssueHistoryDetailsEntityList) {
        //mapping
        List<CouponIssueHistoryQueryRep> response = new ArrayList<>(couponIssueHistoryDetailsEntityList.size());
        couponIssueHistoryDetailsEntityList.forEach(couponIssueHistoryDetailsEntity -> {
            CouponIssueHistoryQueryRep responseDto = new CouponIssueHistoryQueryRep();
            BeanUtils.copyProperties(couponIssueHistoryDetailsEntity, responseDto);
            clientCouponStatusSetting(couponIssueHistoryDetailsEntity, responseDto);
            response.add(responseDto);
        });
        //暂时保留，数据库内字段sequenceNumber暂时用不到，所以这里也暂时不用
        /*//根据sequenceNumber升序排序
        response.sort((a, b) -> Integer.compare(a.getSequenceNumber().compareTo(b.getSequenceNumber()), 0));
        int sequenceNumber = 1;
        for (CouponIssueHistoryQueryRep entity : response) {
            entity.setSequenceNumber(String.valueOf(sequenceNumber));
            sequenceNumber++;
        }*/
        return response;
    }


    private void clientCouponStatusSetting(CouponIssueHistoryDetailsEntity couponIssueHistoryDetailsEntity, CouponIssueHistoryQueryRep responseDto) {
        //发券：未发券，已发券
        if (StringUtils.isNoneBlank(couponIssueHistoryDetailsEntity.getIssueResult())) {
            responseDto.setClientCouponStatus(
                    CouponHistoryQueryStatusMapping.dtoCodeToResponseCode(
                            couponIssueHistoryDetailsEntity.getIssueResult(), CouponHistoryQueryStatusMapping.ISSUE_RESULT));
        }


        //使用状态：券状态：0-未使用；2-已使用
        if (StringUtils.isNoneBlank(couponIssueHistoryDetailsEntity.getCouponUseStatus())) {
            responseDto.setClientCouponStatus(
                    CouponHistoryQueryStatusMapping.dtoCodeToResponseCode(
                            couponIssueHistoryDetailsEntity.getCouponUseStatus(), CouponHistoryQueryStatusMapping.USE_STATUS));
        }

        //券过期
        Date validEndDate = couponIssueHistoryDetailsEntity.getValidEndDate();
        if (validEndDate != null && now().after(validEndDate)) {
            responseDto.setClientCouponStatus(ClientCouponStatusConstant.COUPON_IS_INVALID);
        }

    }


    /**
     * 确认最终发券的clientID集合
     *
     * @param couponIssueMsgDetailsReq
     * @param productCouponEntity
     * @return
     */
    private List<UserInfo4CouponIssueDto> confirmIssueClient(CouponIssueMsgDetailsReq couponIssueMsgDetailsReq, ProductCouponEntity productCouponEntity) {
        List<UserInfo4CouponIssueDto> eligibleUserList = filterAndGetEligibleClientByClientType(
                couponIssueMsgDetailsReq.getUserInfo4CouponIssueDtoList(), productCouponEntity.getClientTypeSet());

        // 根据用户ID正序sort 并选取前部分用户发券
        return confirmIssueClientAndGetIssueList(eligibleUserList, productCouponEntity.getId());

    }


    /**
     * 插入发放流水
     *
     * @param couponIssueMsgDetailsReq
     * @param clientCouponEntityList
     */
    private void insertIssueHistory(CouponIssueMsgDetailsReq couponIssueMsgDetailsReq, List<ClientCouponEntity> clientCouponEntityList) {
        List<String> preparedIssueClientIdList = couponIssueMsgDetailsReq.getUserInfo4CouponIssueDtoList()
                .stream()
                .map(UserInfo4CouponIssueDto::getClientId)
                .collect(Collectors.toList());

        List<CouponIssueHistoryEntity> couponIssueHistoryEntityList =
                createCouponIssueHistoryEntityList(clientCouponEntityList, preparedIssueClientIdList, couponIssueMsgDetailsReq.getCouponIssueId());
        batchService.batchDispose(couponIssueHistoryEntityList, CouponIssueHistoryMapper.class, "insertSelective");

    }

    private List<CouponIssueHistoryEntity> createCouponIssueHistoryEntityList(
            List<ClientCouponEntity> issuedClientCouponEntityList, List<String> preparedIssueClientIdList, String couponIssueId) {

        //分别筛选出成功发放和未发放券的用户ID
        List<String> issuedClientIdList = issuedClientCouponEntityList
                .stream()
                .map(ClientCouponEntity::getClientId)
                .collect(Collectors.toList());

        List<String> unIssuedClientIdList = preparedIssueClientIdList
                .stream()
                .filter(preparedIssueClientId -> !issuedClientIdList.contains(preparedIssueClientId))
                .collect(Collectors.toList());

        //分别对成功发放和未发放券的用户进行entity拼装
        List<CouponIssueHistoryEntity> result = new ArrayList<>();
        issuedClientIdList.forEach(issuedClientId -> {
            CouponIssueHistoryEntity couponIssueHistoryEntity = new CouponIssueHistoryEntity();
            couponIssueHistoryEntity.setCouponIssueHistoryId(uidGenerator.getUID2());
            couponIssueHistoryEntity.setClientId(issuedClientId);
            couponIssueHistoryEntity.setCouponIssueId(couponIssueId);
            couponIssueHistoryEntity.setIssueResult(ISSUED_SUCCESSED.getCode());

            result.add(couponIssueHistoryEntity);
        });
        unIssuedClientIdList.forEach(unIssuedClientId -> {
            CouponIssueHistoryEntity couponIssueHistoryEntity = new CouponIssueHistoryEntity();
            couponIssueHistoryEntity.setCouponIssueHistoryId(uidGenerator.getUID2());
            couponIssueHistoryEntity.setClientId(unIssuedClientId);
            couponIssueHistoryEntity.setCouponIssueId(couponIssueId);
            couponIssueHistoryEntity.setIssueResult(ISSUED_FAILED.getCode());
            result.add(couponIssueHistoryEntity);
        });

        //由于数据库内字段sequenceNumber暂时不用，所以这里的逻辑也取消
       /* //根据用户ID进行增序排序，并填入此次发券任务的序列号
        result.sort((a, b) -> Integer.compare(a.getClientId().compareTo(b.getClientId()), 0));

        int sequenceNumber = 1;
        //补充序列号ID
        for (CouponIssueHistoryEntity couponIssueHistoryEntity : result) {
            couponIssueHistoryEntity.setSequenceNumber(Integer.toString(sequenceNumber));
            sequenceNumber++;
        }*/
        return result;

    }

    /**
     * 根据用户身份筛选出符合发放条件的用户集合
     *
     * @param userInfo4CouponIssueDtoList
     * @param clientTypeSet
     * @return
     */
    private List<UserInfo4CouponIssueDto> filterAndGetEligibleClientByClientType(List<UserInfo4CouponIssueDto> userInfo4CouponIssueDtoList, String clientTypeSet) {
        List<UserInfo4CouponIssueDto> eligibleUserList = new ArrayList<>();

        //发放类型检验
        userInfo4CouponIssueDtoList.forEach(userInfo4CouponIssueDto -> {
            if (checkClientTypeEligibleIssue(clientTypeSet, userInfo4CouponIssueDto.getUserType())) {
                eligibleUserList.add(userInfo4CouponIssueDto);
            }
        });
        return eligibleUserList;
    }

    /**
     * 检查用户的类型是否符合领券
     *
     * @param couponClientTypeSet
     * @param clientTypeSet
     * @return
     */
    private boolean checkClientTypeEligibleIssue(String couponClientTypeSet, String clientTypeSet) {
        if (couponClientTypeSet.equals(CommonConstant.WILDCARD)
                || StringUtils.isBlank(couponClientTypeSet)
                || StringUtils.isBlank(clientTypeSet)) {
            return true;
        }
        return couponClientTypeSet.contains(clientTypeSet);
    }

    private List<UserInfo4CouponIssueDto> confirmIssueClientAndGetIssueList(List<UserInfo4CouponIssueDto> eligibleUserList, String couponId) {
        CouponQuotaRuleEntity couponQuotaRuleEntity = couponQuotaRuleMapper.findCouponQuotaRuleById(couponId);
        if (couponQuotaRuleEntity == null || couponQuotaRuleEntity.getMaxCount() <= 0) {
            log.info(COUPON_ISSUE_NO_CAPACITY_CANNOT_BE_ISSUED.getDesc());
            throw new BizException(COUPON_ISSUE_NO_CAPACITY_CANNOT_BE_ISSUED);
        }

        //发放顺序按照用户ID进行顺序发放，这里先排序
        eligibleUserList.sort((a, b) -> Integer.compare(a.getClientId().compareTo(b.getClientId()), 0));


        Integer couponMaxIssueCount = couponQuotaRuleEntity.getMaxCount();
        if (couponMaxIssueCount <= 0) {
            log.info("券库存剩余容量为0，无法实施发券操作。券ID为：{},准备发放的用户为：{}", couponId, eligibleUserList);
            throw new BizException(COUPON_ISSUE_NO_CAPACITY_CANNOT_BE_ISSUED);
        }


        if (eligibleUserList.size() < couponMaxIssueCount) {
            return eligibleUserList;
        }
        return eligibleUserList.subList(0, couponMaxIssueCount);
    }

    @Override
    public PageData<CouponIssueQueryRsp> getCouponIssueQuery(CouponIssueQueryReq couponIssueQueryReq) {
        startPage(couponIssueQueryReq.getPageNo(), couponIssueQueryReq.getPageSize());

        List<CouponIssueEntity> couponIssueEntities = couponIssueMapper.getCouponIssueQuery(couponIssueQueryReq);
        PageInfo<CouponIssueEntity> pageInfo = new PageInfo<>(couponIssueEntities);

        PageData<CouponIssueQueryRsp> pageData = convert(pageInfo, CouponIssueQueryRsp.class);
        fillEditDeleteFlag(pageData.getRows());
        return pageData;
    }

    @Override
    public CouponIssueDetailRsp getCouponIssueDetail(CouponIssueDetailReq couponIssueDetailReq) {
        CouponIssueEntity couponIssue = couponIssueMapper.getCouponIssueDetail(couponIssueDetailReq);
        ProductCouponEntity productCoupon = productCouponMapper.selectByPrimaryKey(couponIssue.getCouponId());
        return getCouponIssueDetailRsp(couponIssue, productCoupon);
    }

    @Override
    @Transactional
    public void delete(CouponIssueDeleteReq couponIssueDeleteReq) {
        List<String> couponIssueIds = couponIssueDeleteReq.getCouponIssueIds();

        doDelete(couponIssueIds);
    }

    @Override
    @Transactional
    public void setVisible(CouponIssueVisibleReq couponIssueVisibleReq) {
        List<CouponIssueEntity> couponIssueEntities = new ArrayList<>();

        List<String> couponIssueIds = couponIssueVisibleReq.getCouponIssueIds();
        for (String couponIssueId : couponIssueIds) {
            CouponIssueEntity couponIssueEntity = new CouponIssueEntity();
            couponIssueEntity.setCouponIssueId(couponIssueId);
            couponIssueEntity.setIsVisible(couponIssueVisibleReq.getIsVisible());
            couponIssueEntities.add(couponIssueEntity);
        }
        batchService.batchDispose(couponIssueEntities, CouponIssueMapper.class, "updateByPrimaryKeySelective");
    }

    @Override
    public CouponIssueEditRsp edit(CouponIssueEditReq couponIssueEditReq) {
        CouponIssueEntity originalCouponIssueEntity = couponIssueMapper.selectByPrimaryKey(couponIssueEditReq.getCouponIssueId());
        Date issueDate = string2Date(originalCouponIssueEntity.getIssueTime(), YYYY_MM_DD_HH_MM);
        if (now().after(addHours(issueDate, -1))) {
            throw new BizException(COUPON_ISSUE_TIMEOUT_INVALID_OPERATION);
        }

        return doEdit(couponIssueEditReq, originalCouponIssueEntity);
    }

    /**
     * @param couponIssue
     * @param productCoupon
     * @return
     */
    private CouponIssueDetailRsp getCouponIssueDetailRsp(CouponIssueEntity couponIssue, ProductCouponEntity productCoupon) {
        Date issueDate = string2Date(couponIssue.getIssueTime(), YYYY_MM_DD_HH_MM);

        CouponIssueDetailRsp couponIssueDetailRsp = new CouponIssueDetailRsp();
        couponIssueDetailRsp.setCouponId(couponIssue.getCouponId());
        couponIssueDetailRsp.setCouponName(couponIssue.getCouponName());
        couponIssueDetailRsp.setCouponType(productCoupon.getCouponType());
        couponIssueDetailRsp.setCouponStatus(productCoupon.getStatus());
        couponIssueDetailRsp.setIssueStatus(couponIssue.getIssueStatus());
        couponIssueDetailRsp.setIssueDate(date2String(issueDate, YYYY_MM_DD));
        couponIssueDetailRsp.setIssueTime(date2String(issueDate, HH_MM_SS));
        couponIssueDetailRsp.setTargetType(couponIssue.getTargetType());
        couponIssueDetailRsp.setIssueIds(asList(split(couponIssue.getIssueIds(), ",")));
        return couponIssueDetailRsp;
    }


    /**
     * 发放优惠券规则检验
     *
     * @param couponIssueEntity
     * @param productCouponEntity
     * @param couponIsVisible
     */
    private void checkCoupon(CouponIssueEntity couponIssueEntity, ProductCouponEntity productCouponEntity, String couponIsVisible) {
        checkCoupon(couponIssueEntity, productCouponEntity);
        //检查券的上下架
        if (CouponStatus.NO.getDictValue().equals(couponIsVisible)) {
            throw new BizException(INVISIBLE_COUPON_ISSUE_TASK_CANNOT_BE_ISSUED);
        }
    }

    /**
     * 优惠券发放创建规则检验
     *
     * @param couponIssueEntity
     * @param productCouponEntity
     */
    private void checkCreateCoupon(CouponIssueEntity couponIssueEntity, ProductCouponEntity productCouponEntity) {
        checkCoupon(couponIssueEntity, productCouponEntity);

        Date issueTime = string2Date(couponIssueEntity.getIssueTime(), YYYY_MM_DD_HH_MM);
        Date now = now();
        if (now.after(issueTime)) {
            throw new BizException(ISSUE_TIME_MUST_GREATER_CURRENT_TIME);
        }
    }

    /**
     * 发放优惠券规则检验
     *
     * @param couponIssueEntity
     * @param productCouponEntity
     */
    private void checkCoupon(CouponIssueEntity couponIssueEntity, ProductCouponEntity productCouponEntity) {
        ProductCouponStatusEnum productCouponStatusEnum = getCardequityEnum(ProductCouponStatusEnum.class, productCouponEntity.getStatus());
        if (!productCouponStatusEnum.isVisible()) {
            throw new BizException(INVISIBLE_COUPON_CANNOT_BE_ISSUED);
        }

        LocalDateTime nowTime = LocalDateTime.now();
        boolean isValid = nowTime.isAfter(productCouponEntity.getAllowUseBeginDate()) && nowTime.isBefore(productCouponEntity.getAllowUseEndDate());
        if (!isValid) {
            throw new BizException(COUPON_HAS_EXPIRED);
        }

        ProductCouponGetTypeEnum productCouponGetTypeEnum = getCardequityEnum(ProductCouponGetTypeEnum.class, productCouponEntity.getGetType());
        if (productCouponGetTypeEnum.isHanld()) {
            throw new BizException(MANUAL_COUPON_CANNOT_BE_ISSUED);
        }

        Date issueTime = string2Date(couponIssueEntity.getIssueTime(), YYYY_MM_DD_HH_MM);
        LocalDateTime nowLocalDateTime = LocalDateUtils.date2LocalDateTime(issueTime);
        if (nowLocalDateTime.isAfter(productCouponEntity.getAllowUseEndDate())) {
            throw new BizException(COUPON_END_DATE_MUST_GREATER_CURRENT_DATE);
        }

        CouponQuotaRuleEntity couponQuotaRule = couponQuotaRuleMapper.selectByPrimaryKey(couponIssueEntity.getCouponId());
        Integer issueQuantity = couponQuotaRule.getMaxCount();
        if (nonNull(issueQuantity) && issueQuantity <= 0) {
            throw new BizException(COUPON_ISSUE_QUANTITY_CANNOT_LESS_ZERO);
        }
    }

    /**
     * 创建发放优惠券对象
     *
     * @param couponIssueReq
     * @param productCouponEntity
     * @return
     */
    private CouponIssueEntity createCouponIssueEntity(CouponIssueReq couponIssueReq, ProductCouponEntity productCouponEntity) {
        CouponIssueEntity couponIssueEntity = new CouponIssueEntity();
        couponIssueEntity.setCouponIssueId(uidGenerator.getUID2());
        couponIssueEntity.setCouponId(couponIssueReq.getCouponId());
        couponIssueEntity.setCouponName(productCouponEntity.getCouponName());
        couponIssueEntity.setIssueTime(couponIssueReq.getIssueTime());
        couponIssueEntity.setTargetType(couponIssueReq.getTargetType());
        couponIssueEntity.setIsVisible(INVISIBLE.getCode());
        couponIssueEntity.setIssueStatus(NOT_ISSUE.getCode());
        couponIssueEntity.setTriggerType(DELAY_JOB_TRIGGER_TYPE.getCode());
        couponIssueEntity.setIssueIds(join(couponIssueReq.getIssueIds(), ","));
        couponIssueEntity.setLogicDelete(false);
        return couponIssueEntity;
    }

    /**
     * 删除
     *
     * @param couponIssueIds
     */
    private void doDelete(List<String> couponIssueIds) {
        CouponIssueDetailReq couponIssueDetailReq = null;
        for (String couponIssueId : couponIssueIds) {
            couponIssueDetailReq = new CouponIssueDetailReq();
            couponIssueDetailReq.setCouponIssueId(couponIssueId);

            CouponIssueEntity couponIssueEntity = couponIssueMapper.getCouponIssueDetail(couponIssueDetailReq);
            if (!eq(NOT_ISSUE.getCode(), couponIssueEntity.getIssueStatus())) {
                throw new BizException(COUPON_NOT_ISSUE_STATUS_CAN_DELETE);
            }
            couponIssueEntity.setLogicDelete(true);
            couponIssueMapper.updateByPrimaryKeySelective(couponIssueEntity);
        }
    }

    /**
     * 获取优惠券发放编辑对象
     *
     * @param couponIssueEditReq
     * @param productCouponEntity
     * @return
     */
    private CouponIssueEntity createCouponIssueEntity(CouponIssueEditReq couponIssueEditReq, ProductCouponEntity productCouponEntity) {
        CouponIssueEntity couponIssueEntity = new CouponIssueEntity();
        couponIssueEntity.setCouponIssueId(couponIssueEditReq.getCouponIssueId());
        couponIssueEntity.setCouponId(couponIssueEditReq.getCouponId());
        couponIssueEntity.setCouponName(productCouponEntity.getCouponName());
        couponIssueEntity.setIssueTime(couponIssueEditReq.getIssueTime());
        couponIssueEntity.setTargetType(couponIssueEditReq.getTargetType());
        couponIssueEntity.setIsVisible(couponIssueEditReq.getIsVisible());
        couponIssueEntity.setIssueIds(join(couponIssueEditReq.getIssueIds(), ","));
        couponIssueEntity.setLogicDelete(false);
        return couponIssueEntity;
    }

    /**
     * 填充编辑和删除标志
     *
     * @param couponIssueQueryRsps
     */
    private void fillEditDeleteFlag(List<CouponIssueQueryRsp> couponIssueQueryRsps) {
        if (isEmpty(couponIssueQueryRsps)) {
            return;
        }

        for (CouponIssueQueryRsp couponIssueQueryRsp : couponIssueQueryRsps) {
            couponIssueQueryRsp.setDeleteFlag(!eq(couponIssueQueryRsp.getIssueStatus(), NOT_ISSUE.getCode()));

            Date issueDate = string2Date(couponIssueQueryRsp.getIssueTime(), YYYY_MM_DD_HH_MM);
            couponIssueQueryRsp.setEditFlag(now().after(addHours(issueDate, -1)));
        }
    }

    /**
     * 执行编辑操作
     *
     * @param couponIssueEditReq
     * @param originalCouponIssueEntity
     * @return
     */
    private CouponIssueEditRsp doEdit(CouponIssueEditReq couponIssueEditReq, CouponIssueEntity originalCouponIssueEntity) {
        String couponId = couponIssueEditReq.getCouponId();
        ProductCouponEntity productCouponEntity = productCouponMapper.selectByPrimaryKey(couponId);

        CouponIssueEntity couponIssueEntity = createCouponIssueEntity(couponIssueEditReq, productCouponEntity);
        checkCoupon(couponIssueEntity, productCouponEntity);
        couponIssueMapper.updateByPrimaryKeySelective(couponIssueEntity);
        return getCouponIssueEditRsp(originalCouponIssueEntity, couponIssueEntity);
    }

    /**
     * 获取优惠券编辑返回
     *
     * @param originalCouponIssueEntity
     * @param couponIssueEntity
     * @return
     */
    private CouponIssueEditRsp getCouponIssueEditRsp(CouponIssueEntity originalCouponIssueEntity, CouponIssueEntity couponIssueEntity) {
        CouponIssueEditRsp couponIssueEditRsp = new CouponIssueEditRsp();

        couponIssueEditRsp.setIssueTime(couponIssueEntity.getIssueTime());
        if (eq(couponIssueEntity.getTargetType(), ACTIVITY_ID.getCode())) {
            couponIssueEditRsp.setIssueTimeModifyFlag(false);
            return couponIssueEditRsp;
        }

        couponIssueEditRsp.setIssueTimeModifyFlag(!eq(originalCouponIssueEntity.getIssueTime(), couponIssueEntity.getIssueTime()));
        return couponIssueEditRsp;
    }

    /**
     * 获取创建id
     *
     * @param couponIssueId
     * @return
     */
    private CouponIssueRsp getCouponIssueRsp(String couponIssueId) {
        CouponIssueRsp couponIssueRsp = new CouponIssueRsp();
        couponIssueRsp.setCouponIssueId(couponIssueId);
        return couponIssueRsp;
    }


}
