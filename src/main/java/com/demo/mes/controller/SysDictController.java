package com.demo.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.mes.common.result.Result;
import com.demo.mes.entity.SysDict;
import com.demo.mes.mapper.SysDictMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "数据字典")
@RestController
@RequestMapping("/system/dict")
public class SysDictController {

    private final SysDictMapper sysDictMapper;

    @Autowired
    public SysDictController(SysDictMapper sysDictMapper) {
        this.sysDictMapper = sysDictMapper;
    }

    @Operation(summary = "字典列表")
    @GetMapping("/list")
    public Result<List<SysDict>> list(@RequestParam(required = false) String dictType) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        if (dictType != null && !dictType.isBlank()) {
            wrapper.eq(SysDict::getDictType, dictType);
        }
        wrapper.orderByAsc(SysDict::getDictType).orderByAsc(SysDict::getSort);
        return Result.success(sysDictMapper.selectList(wrapper));
    }

    @Operation(summary = "按类型查询字典")
    @GetMapping("/type/{dictType}")
    public Result<List<SysDict>> byType(@PathVariable String dictType) {
        return Result.success(sysDictMapper.selectList(
                new LambdaQueryWrapper<SysDict>()
                        .eq(SysDict::getDictType, dictType)
                        .eq(SysDict::getStatus, 1)
                        .orderByAsc(SysDict::getSort)));
    }

    @Operation(summary = "新增字典")
    @PreAuthorize("hasAuthority('system:dict') or hasAuthority('*:*:*')")
    @PostMapping
    public Result<Void> create(@RequestBody SysDict dict) {
        sysDictMapper.insert(dict);
        return Result.success("新增成功", null);
    }

    @Operation(summary = "修改字典")
    @PreAuthorize("hasAuthority('system:dict') or hasAuthority('*:*:*')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysDict dict) {
        dict.setId(id);
        sysDictMapper.updateById(dict);
        return Result.success("修改成功", null);
    }

    @Operation(summary = "删除字典")
    @PreAuthorize("hasAuthority('system:dict') or hasAuthority('*:*:*')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysDictMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
