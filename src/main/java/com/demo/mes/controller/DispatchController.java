package com.demo.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.mes.common.result.PageResult;
import com.demo.mes.common.result.Result;
import com.demo.mes.entity.Dispatch;
import com.demo.mes.security.LoginUser;
import com.demo.mes.service.DispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "派工管理")
@RestController
@RequestMapping("/dispatch")
public class DispatchController {

    private final DispatchService dispatchService;

    @Autowired
    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @Operation(summary = "派工单分页列表")
    @GetMapping("/list")
    public Result<PageResult<Dispatch>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) Long workCenterId,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(required = false) Integer status) {
        Page<Dispatch> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Dispatch> wrapper = new LambdaQueryWrapper<>();
        if (orderId != null) wrapper.eq(Dispatch::getOrderId, orderId);
        if (workCenterId != null) wrapper.eq(Dispatch::getWorkCenterId, workCenterId);
        if (operatorId != null) wrapper.eq(Dispatch::getOperatorId, operatorId);
        if (status != null) wrapper.eq(Dispatch::getStatus, status);
        wrapper.orderByAsc(Dispatch::getDispatchNo);
        dispatchService.page(pageObj, wrapper);
        return Result.success(new PageResult<>(pageObj.getRecords(), pageObj.getTotal(), page, size));
    }

    @Operation(summary = "开始作业")
    @PreAuthorize("hasAuthority('execution:dispatch') or hasAuthority('*:*:*')")
    @PostMapping("/{id}/start")
    public Result<Void> start(@PathVariable Long id) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dispatchService.startDispatch(id, loginUser.getUserId());
        return Result.success("已开始作业", null);
    }

    @Operation(summary = "暂停作业")
    @PreAuthorize("hasAuthority('execution:dispatch') or hasAuthority('*:*:*')")
    @PostMapping("/{id}/pause")
    public Result<Void> pause(@PathVariable Long id) {
        dispatchService.pauseDispatch(id);
        return Result.success("已暂停作业", null);
    }

    @Operation(summary = "完成派工单")
    @PreAuthorize("hasAuthority('execution:dispatch') or hasAuthority('*:*:*')")
    @PostMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        dispatchService.completeDispatch(id);
        return Result.success("派工单已完成", null);
    }

    @Operation(summary = "指派操作工")
    @PreAuthorize("hasAuthority('execution:dispatch') or hasAuthority('*:*:*')")
    @PostMapping("/{id}/assign")
    public Result<Void> assign(@PathVariable Long id, @RequestParam Long operatorId) {
        dispatchService.assignOperator(id, operatorId);
        return Result.success("指派成功", null);
    }

    @Operation(summary = "我的派工单")
    @GetMapping("/my")
    public Result<PageResult<Dispatch>> myList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<Dispatch> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Dispatch> wrapper = new LambdaQueryWrapper<Dispatch>()
                .eq(Dispatch::getOperatorId, loginUser.getUserId())
                .orderByDesc(Dispatch::getCreateTime);
        dispatchService.page(pageObj, wrapper);
        return Result.success(new PageResult<>(pageObj.getRecords(), pageObj.getTotal(), page, size));
    }
}
