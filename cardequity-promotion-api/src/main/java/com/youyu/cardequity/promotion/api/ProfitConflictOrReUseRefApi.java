package com.youyu.cardequity.promotion.api;


import com.youyu.common.api.Result;
import com.youyu.cardequity.promotion.dto.TbProfitConflictOrReUseRefDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2018-12-13
 */
@FeignClient(name = "cardequity-promotion")
@RequestMapping(path = "/tbProfitConflictOrReUseRef")
public interface ProfitConflictOrReUseRefApi {

    /**
     * select one
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "select one")
    @GetMapping("/{id}")
    Result<TbProfitConflictOrReUseRefDto> get(@PathVariable(name = "id") String id);

    /**
     * delete one
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "delete one")
    @DeleteMapping("/{id}")
    Result delete(@PathVariable(name = "id") String id);

    /**
     * save one
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "save one")
    @PostMapping("/")
    Result<TbProfitConflictOrReUseRefDto> save(@RequestBody TbProfitConflictOrReUseRefDto dto);


    /**
     * update one
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "update one")
    @PutMapping("/")
    Result<TbProfitConflictOrReUseRefDto> update(@RequestBody TbProfitConflictOrReUseRefDto dto);


    /**
     * 查询所有
     *
     * @return
     */
    @ApiOperation(value = "find all")
    @GetMapping(path = "/findAll")
    Result<List<TbProfitConflictOrReUseRefDto>> findAll();
}
