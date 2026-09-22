package com.demo.mes.controller;

import com.demo.mes.security.LoginUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.mes.common.result.PageResult;
import com.demo.mes.common.result.Result;
import com.demo.mes.entity.QualityRecord;
import com.demo.mes.mapper.QualityRecordMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "质量管理")
@RestController
@RequestMapping("/quality-record")
public class QualityRecordController {

    private final QualityRecordMapper qualityRecordMapper;

    @Autowired
    public QualityRecordController(QualityRecordMapper qualityRecordMapper) {
        this.qualityRecordMapper = qualityRecordMapper;
    }

    @Operation(summary = "质检记录分页列表")
    @GetMapping("/list")
    public Result<PageResult<QualityRecord>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long dispatchId,
            @RequestParam(required = false) Integer checkType,
            @RequestParam(required = false) Integer result) {
        Page<QualityRecord> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<QualityRecord> wrapper = new LambdaQueryWrapper<>();
        if (dispatchId != null) wrapper.eq(QualityRecord::getDispatchId, dispatchId);
        if (checkType != null) wrapper.eq(QualityRecord::getCheckType, checkType);
        if (result != null) wrapper.eq(QualityRecord::getResult, result);
        wrapper.orderByDesc(QualityRecord::getCheckTime);
        qualityRecordMapper.selectPage(pageObj, wrapper);
        return Result.success(new PageResult<>(pageObj.getRecords(), pageObj.getTotal(), page, size));
    }

    @Operation(summary = "提交质检记录")
    @PreAuthorize("hasAuthority('quality:record') or hasAuthority('*:*:*')")
    @PostMapping
    public Result<Void> create(@RequestBody QualityRecord record) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        record.setInspectorId(loginUser.getUserId());
        qualityRecordMapper.insert(record);
        return Result.success("提交成功", null);
    }
}
