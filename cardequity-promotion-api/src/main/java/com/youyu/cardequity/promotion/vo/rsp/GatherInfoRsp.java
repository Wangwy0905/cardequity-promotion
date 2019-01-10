package com.youyu.cardequity.promotion.vo.rsp;


import com.youyu.cardequity.promotion.enums.CommonDict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Setter;

/**
 * Created by caiyi on 2019/1/10.
 */
@Setter
public class GatherInfoRsp {
    @ApiModelProperty(value = "统计项")
    private String gatherItem;

    @ApiModelProperty(value = "统计项描述")
    private String gatherName;

    @ApiModelProperty(value = "统计值")
    private int gatherValue;

    public int getGatherValue() {
        return gatherValue;
    }

    public String getGatherItem() {
        return gatherItem;
    }

    public String getGatherName() {
        if (gatherItem != null && !gatherItem.isEmpty()) {
            switch (gatherItem) {
                case "1":
                    gatherName = CommonDict.FRONDEND_NEW.getDesc();
                    break;
                case "2":
                    gatherName = CommonDict.FRONDEND_MEMBER.getDesc();
                    break;
                default:
                    gatherName = CommonDict.FRONDEND_ALL.getDesc();
            }
        }
        return gatherName;
    }
}
