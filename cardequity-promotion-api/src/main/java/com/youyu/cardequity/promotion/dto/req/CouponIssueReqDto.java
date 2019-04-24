package com.youyu.cardequity.promotion.dto.req;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CouponIssueReqDto implements Serializable {

    private static final long serialVersionUID = 3768737223941098951L;

    private String couponId;

    private String couponName;

    private String issueType;

    private String issueTime;

    private List<String> issueIds = new ArrayList<>();
}
