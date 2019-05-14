package com.youyu.cardequity.promotion.api;


import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.other.CouponDetailDto;
import com.youyu.cardequity.promotion.dto.other.ObtainCouponViewDto;
import com.youyu.cardequity.promotion.dto.req.AddCouponReq2;
import com.youyu.cardequity.promotion.dto.req.EditCouponReq2;
import com.youyu.cardequity.promotion.dto.req.MemberProductMaxCouponReq;
import com.youyu.cardequity.promotion.dto.rsp.MemberProductMaxCouponRsp;
import com.youyu.cardequity.promotion.vo.req.*;
import com.youyu.cardequity.promotion.vo.rsp.CouponPageQryRsp;
import com.youyu.cardequity.promotion.vo.rsp.GatherInfoRsp;
import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 * 开发日志
 * V1.0-V1 1004244-徐长焕-20181207 新建代码，findEnableGetCoupon：获取客户可领取的券
 */
@Api(tags = "优惠券信息管理：券的定义信息、使用规则、领取规则、额度设置等")
@FeignClient(name = "cardequity-promotion"/*,url = "127.0.0.1:8084"*/)
@RequestMapping(path = "/productCoupon")
public interface ProductCouponApi {

    /**
     * *********************************【App接口】************************
     * 获取可领取的优惠券
     *
     * @param req：里面的clientType-客户类型如果为空，需要在网关层调用客户信息查询接口，同理groupId-商品组信息
     * @return 开发日志
     * 1004244-徐长焕-20181207 新建
     */
    @ApiOperation(value = "【App-有效期内-上架-有额度】获取可以领取的优惠券")
    @PostMapping(path = "/findEnableGetCoupon")
    Result<List<CouponDetailDto>> findEnableGetCoupon(@RequestBody QryProfitCommonReq req);

    /**
     * 【H5】查询H5首页权益优惠券
     *
     * @param req 查询请求体
     * @return
     */
    @ApiOperation(value = "【H5】查询H5首页会员专享权益优惠券")
    @PostMapping(path = "/findFirstPageVipCoupon")
    Result<List<ObtainCouponViewDto>> findFirstPageVipCoupon(@RequestBody PageQryProfitCommonReq req);

    /**
     * 查询H5指定月可领优惠券
     *
     * @param req 查询请求体
     * @return
     */
    @ApiOperation(value = "【H5】查询H5首页会员专享“更多”权益优惠券")
    @PostMapping(path = "/findEnableObtainCouponByMonth")
    Result<List<ObtainCouponViewDto>> findEnableObtainCouponByMonth(@RequestBody FindEnableObtainCouponByMonthReq req);

    /**
     * *********************************【通用接口】************************
     * 查看商品对应优惠券列表
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "【通用-有效期-上架】查看商品对应优惠券列表")
    @PostMapping(path = "/findCouponListByProduct")
    Result<List<CouponDetailDto>> findCouponListByProduct(@RequestBody FindCouponListByProductReq req);


    /**
     * 查询指定优惠券详情
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "【通用】查询指定优惠券详情")
    @PostMapping(path = "/findCouponById")
    Result<CouponDetailDto> findCouponById(@RequestBody BaseCouponReq req);

    /**
     * 查看指定优惠券id集合对应优惠券列表
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "【通用】查看指定优惠券id集合对应优惠券列表")
    @PostMapping(path = "/findCouponListByIds")
    Result<List<CouponDetailDto>> findCouponListByIds(@RequestBody List<String> req);

    /**
     * *********************************【后台接口】************************
     * 添加优惠券
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "【后台】添加优惠券：添加基本信息、领取频率、使用门槛、关联商品等")
    @PostMapping(path = "/addCoupon")
    Result<CommonBoolDto<CouponDetailDto>> addCoupon(@RequestBody CouponDetailDto req);

    /**
     * 后台添加优惠券
     *
     * @param addCouponReq2
     * @return
     */
    @ApiOperation(value = "【后台】添加优惠券：添加基本信息、领取频率、使用门槛、关联商品等")
    @PostMapping(path = "/addCoupon2")
    Result addCoupon2(@RequestBody AddCouponReq2 addCouponReq2);

    /**
     * 编辑优惠券
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "【后台】编辑优惠券：编辑基本信息、领取频率、使用门槛、关联商品等")
    @PostMapping(path = "/editCoupon")
    Result<CommonBoolDto<CouponDetailDto>> editCoupon(@RequestBody CouponDetailDto req);

    /**
     * 后台编辑优惠券
     *
     * @param editCouponReq2
     * @return
     */
    @ApiOperation(value = "【后台】编辑优惠券：编辑基本信息、领取频率、使用门槛、关联商品等")
    @PostMapping(path = "/editCoupon2")
    Result editCoupon2(@RequestBody EditCouponReq2 editCouponReq2);

    /**
     * 批量删除优惠券
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "【后台】批量删除优惠券：商品对应关系、额度值、频率规则、门槛")
    @PostMapping(path = "/batchDelCoupon")
    Result<CommonBoolDto<Integer>> batchDelCoupon(@RequestBody BatchBaseCouponReq req);

    /**
     * 查询所有优惠券列表
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "【后台-分页】查询所有优惠券列表")
    @PostMapping(path = "/findCouponListByCommon")
    Result<CouponPageQryRsp> findCouponListByCommon(@RequestBody BaseQryCouponReq req);


    /**
     * 模糊查询所有优惠券列表
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "【后台-分页】模糊查询所有优惠券列表")
    @PostMapping(path = "/findCouponList")
    Result<CouponPageQryRsp> findCouponList(@RequestBody BaseQryCouponReq req);

    /**
     * 查询活动汇总信息
     *
     * @param req 普通查询活动请求体
     * @return 活动汇总列表
     */
    @ApiOperation(value = "【后台】查询活动或者信息")
    @PostMapping(path = "/findGatherCouponByCommon")
    Result<List<GatherInfoRsp>> findGatherCouponByCommon(@RequestBody BaseQryCouponReq req);

    /**
     * 【后台】上架优惠券
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "【后台】上架优惠券")
    @PostMapping(path = "/upCoupon")
    Result<CommonBoolDto<Integer>> upCoupon(@RequestBody BatchBaseCouponReq req);


    /**
     * 【后台】下架优惠券
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "【后台】下架优惠券")
    @PostMapping(path = "/downCoupon")
    Result<CommonBoolDto<Integer>> downCoupon(@RequestBody BatchBaseCouponReq req);

    /**
     * 根据请求参数获取最大的优惠券信息
     *
     * @param productMaxCouponReq
     * @return
     */
    @ApiOperation(value = "获取最大的优惠券信息")
    @PostMapping(path = "/getMemberProductMaxCoupon")
    Result<MemberProductMaxCouponRsp> getMemberProductMaxCoupon(@RequestBody MemberProductMaxCouponReq productMaxCouponReq);
}
