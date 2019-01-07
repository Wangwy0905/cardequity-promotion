package com.youyu.cardequity.promotion.biz.service.impl;

import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.promotion.biz.service.ActivityRefProductService;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.vo.req.BaseActivityReq;
import com.youyu.cardequity.promotion.vo.req.BaseProductReq;
import com.youyu.common.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityRefProductEntity;
import com.youyu.cardequity.promotion.dto.ActivityRefProductDto;
import com.youyu.cardequity.promotion.biz.dal.dao.ActivityRefProductMapper;

import java.util.List;


/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-20
 */
@Service
public class ActivityRefProductServiceImpl extends AbstractService<String, ActivityRefProductDto, ActivityRefProductEntity, ActivityRefProductMapper> implements ActivityRefProductService {
    @Autowired
    private ActivityRefProductMapper activityRefProductMapper;


    /**
     * 指定活动的商品配置范围和其他活动是否冲突,考虑分页的问题
     *
     * @param req
     * @param activity
     * @return
     */
    @Override
    public CommonBoolDto<List<ActivityRefProductEntity>> checkProductReUse(List<BaseProductReq> req, ActivityProfitDto activity) {
        CommonBoolDto<List<ActivityRefProductEntity>> result = new CommonBoolDto(true);
        int perCount = 100, index = 0;
        List<BaseProductReq> listTemp = null;
        do {
            if (req.size() > (index+perCount)) {
                listTemp = req.subList(index, index+perCount);// 分段处理
            } else {
                listTemp = req.subList(index, req.size());//获得[index, req.size())
            }

            List<ActivityRefProductEntity> entities = activityRefProductMapper.findReProductBylist(listTemp, activity);

            if (!entities.isEmpty()) {
                ActivityRefProductEntity entity = entities.get(0);
                result.setSuccess(false);
                result.setDesc("存在商品已经参与了其他活动,如冲突编号=" + entity.getId() + ",商品id=" + entity.getProductId() + "子商品id=" + entity.getSkuId());
                result.setData(entities);
                return result;
            }
            index = index + perCount;
        } while (index <= req.size());

        return result;
    }

    /**
     * 查询已经配置了活动的商品
     * @return
     */
    @Override
    public List<ActivityRefProductDto> findAllProductInValidActivity(BaseActivityReq req){
        List<ActivityRefProductEntity> entities = activityRefProductMapper.findByExcludeActivityId(req.getActivityId());
        return BeanPropertiesConverter.copyPropertiesOfList(entities,ActivityRefProductDto.class);
    }
}




