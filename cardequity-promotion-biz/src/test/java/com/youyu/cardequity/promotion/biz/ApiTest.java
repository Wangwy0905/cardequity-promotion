package com.youyu.cardequity.promotion.biz;

import com.youyu.cardequity.promotion.biz.controller.ActivityProfitController;
import com.youyu.cardequity.promotion.biz.controller.ClientCouponController;
import com.youyu.cardequity.promotion.biz.controller.ProductCouponController;
import com.youyu.cardequity.promotion.vo.req.ClientObtainCouponReq;
import com.youyu.cardequity.promotion.vo.req.QryProfitCommonReq;
import com.youyu.cardequity.promotion.vo.rsp.ActivityDefineRsp;
import com.youyu.common.api.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by caiyi on 2018/12/11.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {
    @Autowired
    ProductCouponController productCouponController;

    @Autowired
    ActivityProfitController activityProfitController;

    @Autowired
    ClientCouponController clientCouponController;

    @Test
    public  void test() {

        QryProfitCommonReq req=new QryProfitCommonReq();
        req.setClinetId("xuch");
        req.setClinetType("1");
        req.setEntrustWay("0");

//        Result<List<CouponDefineRsp>> result= productCouponController.findEnableGetCoupon(req);
//        System.out.println(result.data.size());

        Result<List<ActivityDefineRsp>> actResult=activityProfitController.findEnableGetActivity(req);
        System.out.println(actResult.data.size());

//        ClientObtainCouponReq req=new ClientObtainCouponReq();
//        req.setClientId("xuch");
        //req.setCouponId("2");
        //req.setStageId("24");
        //Result<ObtainRspDto> obtainRspDtoResult = clientCouponController.obtainCoupon(req);
        //System.out.println(obtainRspDtoResult.data.getSuccess());

//        Result t=new Result();
//        Result<ClientObtainCouponReq> t1=new Result<ClientObtainCouponReq>();
        //t1=t;
        //Result<List<ClientCouponDto>> rsp=clientCouponController.findEnableUseCoupon(req);
        //System.out.println(rsp.data.size());

    }
}
