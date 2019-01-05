package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.common.spring.service.BatchService;
import com.youyu.cardequity.promotion.biz.dal.dao.CouponAndActivityLabelMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.CouponAndActivityLabelEntity;
import com.youyu.cardequity.promotion.biz.service.CouponAndActivityLabelService;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.CouponAndActivityLabelDto;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.ActiveOrCouponType;
import com.youyu.cardequity.promotion.vo.req.BaseLabelReq;
import com.youyu.cardequity.promotion.vo.req.BaseQryLabelReq;
import com.youyu.cardequity.promotion.vo.req.BatchBaseLabelReq;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.youyu.cardequity.promotion.enums.ResultCode.PARAM_ERROR;

@Service
public class CouponAndActivityLabelServiceImpl  extends AbstractService<String, CouponAndActivityLabelDto, CouponAndActivityLabelEntity, CouponAndActivityLabelMapper> implements CouponAndActivityLabelService {
    @Autowired
    private CouponAndActivityLabelMapper couponAndActivityLabelMapper;

    @Autowired
    private BatchService batchService;

    /**
     * 添加标签
     *
     * @param req 标签详情
     * @return 处理后的标签详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CouponAndActivityLabelDto add(CouponAndActivityLabelDto req) {
        if (req == null) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定添加数据"));
        }

        if (CommonUtils.isEmptyorNull(req.getLabelName())) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定标签名"));
        }

        if (CommonUtils.isEmptyorNull(req.getLabelType())) {
            req.setLabelType(ActiveOrCouponType.COUPON.getDictValue());
        }
        CouponAndActivityLabelEntity entity = new CouponAndActivityLabelEntity();
        BeanUtils.copyProperties(req, entity);
        entity.setId(CommonUtils.getUUID());
        entity.setIsEnable(CommonDict.IF_YES.getCode());

        int i = couponAndActivityLabelMapper.insertSelective(entity);
        if (i < 1) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("增加签名失败"));
        }

        return req;
    }

    /**
     * 编辑标签
     *
     * @param req 标签详情
     * @return 处理后的标签详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CouponAndActivityLabelDto edit(CouponAndActivityLabelDto req) {

        if (req == null) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定添加数据"));
        }

        if (CommonUtils.isEmptyorNull(req.getLabelName())) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定标签信息"));
        }

        if (CommonUtils.isEmptyorNull(req.getLabelType())) {
            req.setLabelType(ActiveOrCouponType.COUPON.getDictValue());
        }
        if (CommonUtils.isEmptyorNull(req.getId())) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定标签id"));

        }
        CouponAndActivityLabelEntity entity = couponAndActivityLabelMapper.findLabelById(req.getId());
        if (entity == null) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有该标签"));

        }
        BeanUtils.copyProperties(req, entity);
        entity.setIsEnable(CommonDict.IF_YES.getCode());

        int i = couponAndActivityLabelMapper.updateByPrimaryKeySelective(entity);
        if (i < 1) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("更新标签失败"));
        }

        return req;

    }

    /**
     * 编辑标签
     *
     * @param req 标签基本数据
     * @return 处理成功数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer delete(BatchBaseLabelReq req) {

        if (req == null || req.getLabelList()==null || req.getLabelList().isEmpty()) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定添加数据"));
        }
        List<CouponAndActivityLabelEntity> entities=new ArrayList<>();
        for (BaseLabelReq label:req.getLabelList()) {
            CouponAndActivityLabelEntity entity = couponAndActivityLabelMapper.findLabelById(label.getUuid());
            if (entity == null) {
                throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有该标签"));
            }

        }

        //数据库操作
        batchService.batchDispose(entities, CouponAndActivityLabelMapper.class, "deleteByPrimaryKey");

        return req.getLabelList().size();
    }

    /**
     * 查询标签
     *
     * @param req 标签基本查询请求体
     * @return 标签详情列表
     */
    @Override
    public List<CouponAndActivityLabelDto> findByCommon(BaseQryLabelReq req){
        if (req == null) {
            throw new BizException(PARAM_ERROR.getCode(), PARAM_ERROR.getFormatDesc("没有指定参数"));
        }

        List<CouponAndActivityLabelEntity> entities = couponAndActivityLabelMapper.findLabelByCommon(req);
        return BeanPropertiesConverter.copyPropertiesOfList(entities,CouponAndActivityLabelDto.class);
    }
}
