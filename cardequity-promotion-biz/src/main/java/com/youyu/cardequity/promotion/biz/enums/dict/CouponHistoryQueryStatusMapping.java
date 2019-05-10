package com.youyu.cardequity.promotion.biz.enums.dict;

import com.youyu.cardequity.promotion.enums.CouponIssueResultEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @Auther: zjm
 * @Date: 2019-04-28
 * @Description:
 */
@Getter
public class CouponHistoryQueryStatusMapping {
    public static final String ISSUE_RESULT = "1";
    public static final String USE_STATUS = "2";


    /**
     * 请求码映射为对应的dto code
     *
     * @param requestCode
     * @return
     */
    public static String requestCodeToDtoCode(String requestCode) {
        return CouponHistoryQueryStatusMappingEnum.valuesOf(requestCode, "").getDtoCode();
    }


    /**
     * dtoCode to responseCode
     *
     * @param dtoCode
     * @param codeType
     * @return
     */
    public static String dtoCodeToResponseCode(String dtoCode, String codeType) {
        return CouponHistoryQueryStatusMappingEnum.valuesOf(dtoCode, codeType).getRequestCode();
    }


    /**
     * 数据库储存状态：
     * 状态:0-正常 1-使用中 2-已使用
     * 发放结果：1.成功 ；2.失败
     * <p>
     * 前端展示券状态：0-未使用；2-已使用；3-已过期;4-发放失败或未发放"
     */
    @Getter
    enum CouponHistoryQueryStatusMappingEnum {


        /**
         * 发放失败或未发放
         */
        ISSUED_FAILED_OR_NOTISSUED("4", CouponIssueResultEnum.ISSUED_FAILED.getCode(), ISSUE_RESULT),

        /**
         * 发放成功（保留字段，暂时不对外展示）
         */
        ISSUE_SUCCESS("1", CouponIssueResultEnum.ISSUED_SUCCESSED.getCode(), ISSUE_RESULT),

        COUPON_NOT_USED("0", "0", USE_STATUS),
        COUPON_IS_USED("2", "2", USE_STATUS);

        private String requestCode;
        private String dtoCode;
        private String codeType;


        CouponHistoryQueryStatusMappingEnum(String requestCode, String dtoCode, String codeType) {
            this.requestCode = requestCode;
            this.dtoCode = dtoCode;
            this.codeType = codeType;
        }


        /**
         * 通过codeType数据库内的code与前端展示code的映射
         * 这里，不传codeType，则是从request到dto的映射
         *
         * @param code
         * @param codeType
         * @return
         */
        public static CouponHistoryQueryStatusMappingEnum valuesOf(String code, String codeType) {
            if (StringUtils.isBlank(codeType)) {
                return requestToDto(code);
            }

            return dtoToRequest(code, codeType);
        }

        private static CouponHistoryQueryStatusMappingEnum requestToDto(String requestCode) {
            for (CouponHistoryQueryStatusMappingEnum mappingEnum : values()) {
                if (mappingEnum.requestCode.equals(requestCode)) {
                    return mappingEnum;
                }
            }
            throw new IllegalArgumentException("No matching constant for requestCode:[" + requestCode + "]");
        }

        private static CouponHistoryQueryStatusMappingEnum dtoToRequest(String dtoCode, String codeType) {
            for (CouponHistoryQueryStatusMappingEnum mappingEnum : values()) {
                if (mappingEnum.dtoCode.equals(dtoCode) && mappingEnum.codeType.equals(codeType)) {
                    return mappingEnum;
                }
            }
            throw new IllegalArgumentException("No matching constant for dtoCode:[" + dtoCode + "]," +
                    "codeType=[" + codeType + "]");
        }

    }
}