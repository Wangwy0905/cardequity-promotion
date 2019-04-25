package com.youyu.cardequity.promotion.biz;

import com.youyu.cardequity.promotion.biz.service.CouponIssueService;
import com.youyu.cardequity.promotion.dto.req.CouponIssueReq;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

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
        couponIssueReq.setObjectType("1");

        couponIssueService.createIssue(couponIssueReq);
    }
}
