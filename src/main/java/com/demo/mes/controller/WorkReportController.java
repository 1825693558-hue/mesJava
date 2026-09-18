package com.demo.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.mes.common.result.PageResult;
import com.demo.mes.common.result.Result;
import com.demo.mes.dto.WorkReportDTO;
import com.demo.mes.entity.WorkReport;
import com.demo.mes.security.LoginUser;
import com.demo.mes.service.WorkReportService;
import com.demo.mes.vo.WorkReportResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "工序报工")
@RestController
@RequestMapping("/work-report")
public class WorkReportController {

    private final WorkReportService workReportService;

    @Autowired
    public WorkReportController(WorkReportService workReportService) {
        this.workReportService = workReportService;
    }

    @Operation(summary = "提交报工")
    @PreAuthorize("hasAuthority('execution:report') or hasAuthority('*:*:*')")
    @PostMapping
    public Result<WorkReportResultVO> submit(@Valid @RequestBody WorkReportDTO dto) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WorkReportResultVO result = workReportService.submitReport(dto, loginUser.getUserId());
        return Result.success("报工成功", result);
    }

    @Operation(summary = "报工记录列表")
    @GetMapping("/list")
    public Result<PageResult<WorkReport>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long dispatchId,
            @RequestParam(required = false) Long operatorId) {
        Page<WorkReport> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<WorkReport> wrapper = new LambdaQueryWrapper<>();
        if (dispatchId != null) wrapper.eq(WorkReport::getDispatchId, dispatchId);
        if (operatorId != null) wrapper.eq(WorkReport::getOperatorId, operatorId);
        wrapper.orderByDesc(WorkReport::getReportTime);
        workReportService.page(pageObj, wrapper);
        return Result.success(new PageResult<>(pageObj.getRecords(), pageObj.getTotal(), page, size));
    }
}
