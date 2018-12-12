package com.youyu.cardequity.promotion.biz.controller;


import com.youyu.cardequity.promotion.api.ActivityProfitApi;
import com.youyu.cardequity.promotion.vo.req.QryProfitCommonReq;
import com.youyu.cardequity.promotion.vo.rsp.ActivityDefineRsp;
import com.youyu.cardequity.promotion.vo.rsp.CouponDefineRsp;
import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.biz.service.ActivityProfitService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.youyu.common.enums.BaseResultCode.NO_DATA_FOUND;


/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
@RestController
public class ActivityProfitController implements ActivityProfitApi {

    @Autowired
    private ActivityProfitService activityProfitService;

    @Override
    public Result<ActivityProfitDto> get(@PathVariable(name = "id") String id) {
        ActivityProfitDto dto = activityProfitService.selectByPrimaryKey(id);
        if (dto == null) {
            return Result.fail(NO_DATA_FOUND);
        }
        return Result.ok(dto);
    }

    @Override
    public Result delete(@PathVariable(name = "id") String id) {
        int count = activityProfitService.deleteByPrimaryKey(id);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok();
    }

    @Override
    public Result<ActivityProfitDto> save(@RequestBody ActivityProfitDto dto) {
        int count = activityProfitService.insertSelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<ActivityProfitDto> update(@RequestBody ActivityProfitDto dto) {
        int count = activityProfitService.updateByPrimaryKeySelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);

        }
        return Result.ok(dto);
    }

    @Override
    public Result<List<ActivityProfitDto>> findAll() {
        //默认不实现 findAll
        //Result.ok(tbActivityProfitService.selectAll());
        throw new UnsupportedOperationException();
    }

    @Override
    public Result<List<ActivityDefineRsp>> findEnableGetActivity(QryProfitCommonReq req) {
        List<ActivityDefineRsp> rspList = activityProfitService.findEnableGetActivity(req);
        return Result.ok(rspList);
    }
}
