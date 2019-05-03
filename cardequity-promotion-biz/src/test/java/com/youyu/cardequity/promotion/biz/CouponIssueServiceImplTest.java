package com.youyu.cardequity.promotion.biz;

import com.youyu.cardequity.promotion.biz.service.CouponIssueService;
import com.youyu.cardequity.promotion.dto.req.CouponIssueMsgDetailsReq;
import com.youyu.cardequity.promotion.dto.req.CouponIssueReq;
import com.youyu.cardequity.promotion.dto.req.UserInfo4CouponIssueDto;
import io.swagger.annotations.ApiModelProperty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public void processIssue_test(){
        CouponIssueMsgDetailsReq couponIssueMsgDetailsReq=new CouponIssueMsgDetailsReq();
        couponIssueMsgDetailsReq.setCouponIssueId("3175825777902755840");
        couponIssueMsgDetailsReq.setTargetType("1");

        UserInfo4CouponIssueDto userInfo4CouponIssueDto=new UserInfo4CouponIssueDto();
        userInfo4CouponIssueDto.setClientId("1");
        userInfo4CouponIssueDto.setUserType("11");

        UserInfo4CouponIssueDto userInfo4CouponIssueDto2=new UserInfo4CouponIssueDto();
        userInfo4CouponIssueDto2.setClientId("3");
        userInfo4CouponIssueDto2.setUserType("11");

        couponIssueMsgDetailsReq.setUserInfo4CouponIssueDtoList(Arrays.asList(userInfo4CouponIssueDto,userInfo4CouponIssueDto2));

        couponIssueService.processIssue(couponIssueMsgDetailsReq);
    }
}
