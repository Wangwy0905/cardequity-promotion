package com.youyu.cardequity.promotion.biz;

import com.youyu.cardequity.common.base.converter.OrikaBeanPropertiesConverter;
import com.youyu.cardequity.common.base.uidgenerator.UidGenerator;
import com.youyu.cardequity.common.base.util.DateUtil;
import com.youyu.cardequity.promotion.biz.controller.ActivityProfitController;
import com.youyu.cardequity.promotion.biz.controller.ClientCouponController;
import com.youyu.cardequity.promotion.biz.controller.ProductCouponController;
import com.youyu.cardequity.promotion.biz.service.ProductCouponService;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.vo.DateParam.DateParam;
import com.youyu.cardequity.promotion.vo.req.ClientObtainCouponReq;
import com.youyu.common.api.Result;
import org.apache.tomcat.jni.Local;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 * Created by caiyi on 2018/12/11.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {
  /*  @Autowired
    ProductCouponController productCouponController;
    @Autowired
    ActivityProfitController activityProfitController;

    @Autowired
    ClientCouponController clientCouponController;*/
    @Autowired
    ProductCouponService productCouponService;
    @Autowired
    UidGenerator uidGenerator;

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

      /*  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
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


        System.out.println(firstDay);*/
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar cale=Calendar.getInstance();
        cale.add(Calendar.MONTH,1);
        cale.set(Calendar.DAY_OF_MONTH,0);
        String lastDay=sdf.format(cale.getTime());
        System.out.println(lastDay);





    }

    @Test
    public void testGetMaxMonthDate() {
        Calendar cale = null;
        cale = Calendar.getInstance();
        //int year = cale.get(Calendar.YEAR);
        int month = cale.get(Calendar.MONTH) + 1;
        System.out.println(month);
    }

    /**
     * 测试直接加入优惠券无需领取发放给客户
     * @Param
     */
    @Test
    public  void testCoupon(){
        LocalTime localTime=LocalTime.now();

        System.out.println( LocalDateTime.now());
        System.out.println( LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        System.out.println(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));
        System.out.println(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));

    }
    @Test
    public void testSerialGenerate() {
        // Generate UID
      //  long uid = uidGenerator.getUID();

        // Parse UID into [Timestamp, WorkerId, Sequence]
        // {"UID":"180363646902239241","parsed":{    "timestamp":"2017-01-19 12:15:46",    "workerId":"4",    "sequence":"9"        }}
        //System.out.println(uidGenerator.parseUID(uid));

        String Uid=uidGenerator.getUID2();

        System.out.println(Uid+"----");

    }

    @Test
    public static LocalDateTime testNumToDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        String currdate = format.format(date);
        System.out.println("现在的日期是：" + currdate);
        Calendar cale = null;
        cale = Calendar.getInstance();
        cale.add(Calendar.DATE,18);
        date=cale.getTime();
        String enddate = format.format(date);

        System.out.println("增加天数以后的日期：" + enddate);
        LocalDateTime localDateTime=LocalDateTime.parse(format.format(cale.getTime()),df);
        LocalDateTime localDateTime1 = OrikaBeanPropertiesConverter.copyProperties(localDateTime, LocalDateTime.class);
        LocalDate localDate = localDateTime1.toLocalDate();

        System.out.println(localDate+"======");
        return localDateTime1;

    }
    @Test
    public static LocalDateTime testCurrentMonth(){
/*        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        try {
            Date date=new java.sql.Date(sdf.parse(sdf.format(cale.getTime())).getTime());
            LocalDateTime parse = LocalDateTime.parse(sdf.format(cale.getTime()));

            System.out.println(parse+"1111111111");
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Calendar cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        LocalDateTime localDateTime=LocalDateTime.parse(sdf.format(cale.getTime()),df);
        LocalDateTime localDateTime1 = OrikaBeanPropertiesConverter.copyProperties(localDateTime, LocalDateTime.class);
        LocalDate localDate = localDateTime1.toLocalDate();
        System.out.println(localDateTime1);
        System.out.println(LocalDateTime.now());
        System.out.println(localDate+"555555");
        return localDateTime1;


    }

    public static void main(String[] args) {
        LocalDateTime localDateTime = testCurrentMonth();
        System.out.println(localDateTime+"111");

        LocalDateTime localDateTime1 = testNumToDate();

        System.out.println(localDateTime1+"2222");
    }

}

