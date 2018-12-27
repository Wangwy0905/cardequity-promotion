package com.youyu.cardequity.promotion.biz.strategy.activity;

import com.youyu.cardequity.common.base.annotation.StatusAndStrategyNum;
import com.youyu.cardequity.promotion.biz.dal.dao.ActivityRefProductMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.ActivityStageCouponMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityProfitEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityRefProductEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityStageCouponEntity;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.dto.ActivityStageCouponDto;
import com.youyu.cardequity.promotion.dto.OrderProductDetailDto;
import com.youyu.cardequity.promotion.enums.dict.ApplyProductFlag;
import com.youyu.cardequity.promotion.enums.dict.TriggerByType;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by caiyi on 2018/12/26.
 */
@Slf4j
@StatusAndStrategyNum(superClass = ActivityStrategy.class, number = "3", describe = "现金立减")
@Component
public class CashStrategy extends ActivityStrategy {
    @Autowired
    ActivityRefProductMapper activityRefProductMapper;

    @Autowired
    ActivityStageCouponMapper activityStageCouponMapper;

    @Override
    public UseActivityRsp applyActivity(ActivityProfitEntity item, List<OrderProductDetailDto> productList) {

        //装箱返回数据
        UseActivityRsp rsp = new UseActivityRsp();
        ActivityProfitDto dto = new ActivityProfitDto();
        BeanUtils.copyProperties(item, dto);
        rsp.setActivity(dto);
        rsp.setProfitAmount(item.getProfitValue());

        //获取活动阶梯
        List<ActivityStageCouponEntity> activityProfitDetail = activityStageCouponMapper.findActivityProfitDetail(item.getId());

        activityProfitDetail.sort(new Comparator<ActivityStageCouponEntity>() {
            @Override
            public int compare(ActivityStageCouponEntity entity1, ActivityStageCouponEntity entity2) {//如果是折扣、任选、优惠价从小到大
                return entity1.getProfitValue().compareTo(entity2.getProfitValue());
            }
        });

        BigDecimal countCondition = BigDecimal.ZERO;
        BigDecimal amountCondition = BigDecimal.ZERO;
        BigDecimal diff = BigDecimal.ZERO;
        BigDecimal applyNum = BigDecimal.ZERO;
        List<OrderProductDetailDto> temproductLsit = new ArrayList<>();

        //所有活动在定义适用商品时都不会重叠
        for (OrderProductDetailDto productItem : productList) {
            //1.该商品是否适用于此活动
            if (!ApplyProductFlag.ALL.getDictValue().equals(item.getApplyProductFlag())) {
                //该商品属性是否允许参与活动
                ActivityRefProductEntity entity = activityRefProductMapper.findByBothId(item.getId(), productItem.getProductId());
                if (entity == null) {
                    continue;
                }
            }
            //2.后续会多次设置product对象相关值，需要拷贝出来避免污染
            OrderProductDetailDto product = new OrderProductDetailDto();
            BeanUtils.copyProperties(productItem, product);
            //总额做保护
            product.setTotalAmount(product.getAppCount().multiply(product.getPrice()));

            //3.有门槛的活动处理
            for (ActivityStageCouponEntity stage : activityProfitDetail) {
                //3-1.折扣活动只取优惠力度最大的
                if (rsp.getStage() != null &&
                        rsp.getStage().getProfitValue().compareTo(stage.getProfitValue()) >= 0)
                    continue;

                //3-2.满减活动只适用于满足条件的个数和商品，则后面商品都不算入该阶梯适用商品
                if (rsp.getStage() != null &&
                        rsp.getStage().getId().equals(stage.getId())) {
                    continue;
                }

                //3-3.按金额统计门槛
                if (TriggerByType.CAPITAL.getDictValue().equals(stage.getTriggerByType())) {
                    diff = stage.getBeginValue().subtract(amountCondition);

                    //满足门槛条件情况下：将原适用详情temproductLsit替换为最新满足的活动的
                    if (product.getTotalAmount().compareTo(diff) >= 0) {

                        //适用范围=向上取整(门槛差额/价格)，后续循环会被3-2限制住
                        applyNum = diff.divide(product.getPrice()).setScale(0, BigDecimal.ROUND_UP);

                        //计算：该商品适用优惠金额、该活动总优惠金额、及适用商品和数量
                        temproductLsit = calculationProfitAmount(product, applyNum, stage, rsp);
                    }

                } else {//按数量统计门槛
                    diff = stage.getBeginValue().subtract(countCondition);

                    //满足门槛条件情况下：将原适用详情temproductLsit替换为最新满足的活动的
                    if (product.getAppCount().subtract(diff).compareTo(BigDecimal.ZERO) >= 0) {
                        applyNum = diff;
                        temproductLsit = calculationProfitAmount(product, applyNum, stage, rsp);
                    }

                }
            }

            //记录活动适用的商品，但是没有计算对应优惠值，对应优惠值是在满足门槛后再calculationProfitAmount中计算
            rsp.getProductLsit().add(product);
            countCondition = countCondition.add(product.getAppCount());
            amountCondition = amountCondition.add(product.getAppCount().multiply(product.getPrice()));
        }

        //找到最终适用的阶梯后，将对应最终适用商品情况赋值
        if (activityProfitDetail.size() > 0) {
            if (rsp.getStage() != null) {
                rsp.setProductLsit(temproductLsit);
                BigDecimal totalRealAmount = rsp.getProductLsit().stream().map(OrderProductDetailDto::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                //每种商品优惠的金额是按适用金额比例来的
                if (totalRealAmount.compareTo(BigDecimal.ZERO)>0) {
                    for (OrderProductDetailDto product : rsp.getProductLsit()) {
                        product.setProfitAmount(rsp.getProfitAmount().multiply(product.getTotalAmount().divide(totalRealAmount)));
                    }
                }
                return rsp;
            }
            //无适用门槛立减活动需要将每个商品的优惠值进行再次计算；每个数量单独适用该立减
        } else {
            for (OrderProductDetailDto product : rsp.getProductLsit()) {
                product.setProfitAmount(product.getAppCount().multiply(item.getProfitValue()));
            }
            return rsp;
        }
        return null;
    }

    /**
     * 计算活动对应商品适用数量，该活动总优惠金额，返回适用的商品范围及数量
     *
     * @param product
     * @param applyNum
     * @param stage
     * @param rsp
     * @return
     */
    private List<OrderProductDetailDto> calculationProfitAmount(OrderProductDetailDto product,
                                                                BigDecimal applyNum,
                                                                ActivityStageCouponEntity stage,
                                                                UseActivityRsp rsp) {
        List<OrderProductDetailDto> temproductLsit = new ArrayList<>();
        OrderProductDetailDto cyproduct = new OrderProductDetailDto();
        BeanUtils.copyProperties(product, cyproduct);
        //涉及适用的数量
        cyproduct.setAppCount(applyNum);
        //涉及适用的总金额
        cyproduct.setTotalAmount(applyNum.multiply(cyproduct.getPrice()));

        //优惠金额=涉及适用的总金额-指定限制的总额
        rsp.setProfitAmount(stage.getProfitValue());

        //得到当前适用的阶梯
        ActivityStageCouponDto stageDto = new ActivityStageCouponDto();
        BeanUtils.copyProperties(stage, stageDto);
        rsp.setStage(stageDto);

        //适用商品列表=之前已匹配商品+本次达到门槛适用商品及数量
        temproductLsit.addAll(rsp.getProductLsit());
        temproductLsit.add(cyproduct);
        return temproductLsit;
    }

}
