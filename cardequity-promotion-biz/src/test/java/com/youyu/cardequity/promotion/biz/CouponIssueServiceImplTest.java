package com.youyu.cardequity.promotion.biz;

import com.youyu.cardequity.promotion.biz.service.CouponIssueService;
import com.youyu.cardequity.promotion.dto.req.CouponIssueMsgDetailsReq;
import com.youyu.cardequity.promotion.dto.req.CouponIssueReq;
import com.youyu.cardequity.promotion.dto.req.UserInfo4CouponIssueDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static com.youyu.cardequity.promotion.constant.CommonConstant.USENEWREGISTER_NO;
import static com.youyu.cardequity.promotion.constant.CommonConstant.USENEWREGISTER_YES;

/**
 * @Auther: zjm
 * @Date: 2019-04-25
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CouponIssueServiceImplTest {

    @Autowired
    private CouponIssueService couponIssueService;

    @Test
    public void createIssue_Test() {

        CouponIssueReq couponIssueReq = new CouponIssueReq();
        couponIssueReq.setCouponId("3149027277917069312");
        couponIssueReq.setIssueIds(Arrays.asList("1", "2", "3", "4"));
        couponIssueReq.setIssueTime("2019-09-9 14:00");
        couponIssueReq.setTargetType("1");

        couponIssueService.createIssue(couponIssueReq);
    }


    @Test
    public void processIssue_test() {

        CouponIssueMsgDetailsReq couponIssueMsgDetailsReq = new CouponIssueMsgDetailsReq();
        couponIssueMsgDetailsReq.setCouponIssueId("3217151953229676554");

        UserInfo4CouponIssueDto userInfo4CouponIssueDto = new UserInfo4CouponIssueDto();
        userInfo4CouponIssueDto.setClientId("3cee34fcab0f40a584c4006bd225ed09");
        userInfo4CouponIssueDto.setUserType("10");
        userInfo4CouponIssueDto.setNewUserFlag(USENEWREGISTER_YES);

        UserInfo4CouponIssueDto userInfo4CouponIssueDto2 = new UserInfo4CouponIssueDto();
        userInfo4CouponIssueDto2.setClientId("4b472051538a485c86619e809b4e70a9");
        userInfo4CouponIssueDto2.setUserType("11");
        userInfo4CouponIssueDto2.setNewUserFlag(USENEWREGISTER_YES);


        UserInfo4CouponIssueDto userInfo4CouponIssueDto3 = new UserInfo4CouponIssueDto();
        userInfo4CouponIssueDto3.setClientId("b48d6d4e23f840a899d7f7a53a84804a");
        userInfo4CouponIssueDto3.setUserType("12");
        userInfo4CouponIssueDto3.setNewUserFlag(USENEWREGISTER_NO);


        UserInfo4CouponIssueDto userInfo4CouponIssueDto4 = new UserInfo4CouponIssueDto();
        userInfo4CouponIssueDto4.setClientId("b48d6d4e23f840a899d7f7a53a84804a");
        userInfo4CouponIssueDto4.setUserType("10");
        userInfo4CouponIssueDto4.setNewUserFlag(USENEWREGISTER_NO);


        couponIssueMsgDetailsReq.setUserInfo4CouponIssueDtoList(Arrays.asList(userInfo4CouponIssueDto, userInfo4CouponIssueDto2, userInfo4CouponIssueDto3,userInfo4CouponIssueDto4));

        couponIssueService.processIssue(couponIssueMsgDetailsReq);
    }

    @Test
    public void getCouponIssueCompensateTest() {
        couponIssueService.getCouponIssueCompensate();
    }


    /**
     * 用于测试用户类型是否符合领券条件
     */
    /*@Test
    public void filterAndGetEligibleClientByClientTypeTest() {
        CouponIssueServiceImpl couponIssueService = new CouponIssueServiceImpl();

        UserInfo4CouponIssueDto userInfo4CouponIssueDto = new UserInfo4CouponIssueDto();
        userInfo4CouponIssueDto.setClientId("3cee34fcab0f40a584c4006bd225ed09");
        userInfo4CouponIssueDto.setUserType("10");
        userInfo4CouponIssueDto.setNewUserFlag(USENEWREGISTER_YES);

        UserInfo4CouponIssueDto userInfo4CouponIssueDto2 = new UserInfo4CouponIssueDto();
        userInfo4CouponIssueDto2.setClientId("4b472051538a485c86619e809b4e70a9");
        userInfo4CouponIssueDto2.setUserType("11");
        userInfo4CouponIssueDto2.setNewUserFlag(USENEWREGISTER_YES);


        UserInfo4CouponIssueDto userInfo4CouponIssueDto3 = new UserInfo4CouponIssueDto();
        userInfo4CouponIssueDto3.setClientId("b48d6d4e23f840a899d7f7a53a84804a");
        userInfo4CouponIssueDto3.setUserType("12");
        userInfo4CouponIssueDto3.setNewUserFlag(USENEWREGISTER_NO);


        UserInfo4CouponIssueDto userInfo4CouponIssueDto4 = new UserInfo4CouponIssueDto();
        userInfo4CouponIssueDto4.setClientId("b48d6d4e23f840a899d7f7a53a84804a");
        userInfo4CouponIssueDto4.setUserType("10");
        userInfo4CouponIssueDto4.setNewUserFlag(USENEWREGISTER_NO);


//        String couponUserTypeSet = "*";
        //注册
        String couponUserTypeSet = "10";
        couponIssueService.filterAndGetEligibleClientByClientType(
                asList(userInfo4CouponIssueDto, userInfo4CouponIssueDto2, userInfo4CouponIssueDto3, userInfo4CouponIssueDto4),
                couponUserTypeSet);

        //注册 会员
        couponUserTypeSet = "10,11";

        couponIssueService.filterAndGetEligibleClientByClientType(
                asList(userInfo4CouponIssueDto, userInfo4CouponIssueDto2, userInfo4CouponIssueDto3, userInfo4CouponIssueDto4),
                couponUserTypeSet);

        //会员
        couponUserTypeSet = "11";
        couponIssueService.filterAndGetEligibleClientByClientType(
                asList(userInfo4CouponIssueDto, userInfo4CouponIssueDto2, userInfo4CouponIssueDto3, userInfo4CouponIssueDto4),
                couponUserTypeSet);

    }*/

}
