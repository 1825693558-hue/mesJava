package com.demo.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.mes.common.result.PageResult;
import com.demo.mes.common.result.Result;
import com.demo.mes.entity.OperationLog;
import com.demo.mes.mapper.OperationLogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "操作日志")
@RestController
@RequestMapping("/system/log")
public class OperationLogController {

    private final OperationLogMapper operationLogMapper;

    @Autowired
    public OperationLogController(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Operation(summary = "操作日志分页列表")
    @GetMapping("/list")
    public Result<PageResult<OperationLog>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String username) {
        Page<OperationLog> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (module != null && !module.isBlank()) wrapper.eq(OperationLog::getModule, module);
        if (username != null && !username.isBlank()) wrapper.like(OperationLog::getUsername, username);
        wrapper.orderByDesc(OperationLog::getCreateTime);
        operationLogMapper.selectPage(pageObj, wrapper);
        return Result.success(new PageResult<>(pageObj.getRecords(), pageObj.getTotal(), page, size));
    }
}
