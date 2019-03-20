package com.youyu.cardequity.promotion.biz;

import com.youyu.cardequity.common.base.util.DateUtil;
import com.youyu.cardequity.promotion.biz.controller.ActivityProfitController;
import com.youyu.cardequity.promotion.biz.controller.ClientCouponController;
import com.youyu.cardequity.promotion.biz.controller.ProductCouponController;
import com.youyu.cardequity.promotion.biz.service.ProductCouponService;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.vo.DateParam.DateParam;
import com.youyu.cardequity.promotion.vo.req.ClientObtainCouponReq;
import com.youyu.common.api.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SimpleTimeZone;

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
    @Autowired
    ProductCouponService productCouponService;

    @Test
    public  void test() {

//        QryProfitCommonReq req=new QryProfitCommonReq();
//        req.setClinetId("xuch");
//        req.setClinetType("1");
//        req.setEntrustWay("0");

//        Result<List<CouponDefineRsp>> result= productCouponController.findEnableGetCoupon(req);
//        System.out.println(result.data.size());

//        Result<List<ActivityDefineRsp>> actResult=activityProfitController.findEnableGetActivity(req);
//        System.out.println(actResult.data.size());

        ClientObtainCouponReq req=new ClientObtainCouponReq();
        req.setClientId("5eefIIII");
        req.setCouponId("2");
        req.setStageId("");
        //Result<CommonBoolDto>  obtainRspDtoResult = clientCouponController.obtainCoupon(req);
       // System.out.println(obtainRspDtoResult.data.getSuccess());

       // Result t=new Result();
      //  Result<ClientObtainCouponReq> t1=new Result<ClientObtainCouponReq>();
        //t1=t;
        //Result<List<ClientCouponDto>> rsp=clientCouponController.findEnableUseCoupon(req);
        //System.out.println(rsp.data.size());

    }

    /**
     * 获取月份最后日期
     * @param
     * @return
     * @throws ParseException
     */
    @Test
  public void test2(){

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String firstDay,lastDay;
        Calendar calendar=null;
        //当前月的第一天
        calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH,0);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        firstDay=sdf.format(calendar.getTime());

        //当前月的最后一天
        Calendar cale=Calendar.getInstance();
        cale.add(Calendar.MONTH,1);
        cale.set(Calendar.DAY_OF_MONTH,0);
        lastDay=sdf.format(cale.getTime());
        System.out.println(lastDay);


        System.out.println(firstDay);




    }
    @Test
    public void test3(){
        DateParam maxMonthDate = productCouponService.getMaxMonthDate();

        System.out.println(maxMonthDate);

    }

  /*  public static void main(String[] args) throws ParseException {
        String s = getMaxMonthDate();
        System.out.println(s);
    }

    */
  /*
    public void testGetMaxMonthDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String lastDay;
        Calendar cale=Calendar.getInstance();
        cale.add(Calendar.MONTH,1);
        cale.set(Calendar.DAY_OF_MONTH,0);
        lastDay=dateFormat.format(cale.getTime());
        System.out.println(lastDay);
    }*/

}

