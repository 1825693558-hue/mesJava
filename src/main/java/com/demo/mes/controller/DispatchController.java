package com.demo.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.mes.common.result.PageResult;
import com.demo.mes.common.result.Result;
import com.demo.mes.entity.Dispatch;
import com.demo.mes.entity.ProcessStep;
import com.demo.mes.entity.ProductionOrder;
import com.demo.mes.entity.SysUser;
import com.demo.mes.entity.WorkCenter;
import com.demo.mes.mapper.ProcessStepMapper;
import com.demo.mes.mapper.ProductionOrderMapper;
import com.demo.mes.mapper.SysUserMapper;
import com.demo.mes.mapper.WorkCenterMapper;
import com.demo.mes.security.LoginUser;
import com.demo.mes.service.DispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Tag(name = "派工管理")
@RestController
@RequestMapping("/dispatch")
public class DispatchController {

    private final DispatchService dispatchService;
    private final ProductionOrderMapper productionOrderMapper;
    private final ProcessStepMapper processStepMapper;
    private final WorkCenterMapper workCenterMapper;
    private final SysUserMapper sysUserMapper;

    @Autowired
    public DispatchController(DispatchService dispatchService,
                              ProductionOrderMapper productionOrderMapper,
                              ProcessStepMapper processStepMapper,
                              WorkCenterMapper workCenterMapper,
                              SysUserMapper sysUserMapper) {
        this.dispatchService = dispatchService;
        this.productionOrderMapper = productionOrderMapper;
        this.processStepMapper = processStepMapper;
        this.workCenterMapper = workCenterMapper;
        this.sysUserMapper = sysUserMapper;
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
        fillNames(pageObj.getRecords());
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
        fillNames(pageObj.getRecords());
        return Result.success(new PageResult<>(pageObj.getRecords(), pageObj.getTotal(), page, size));
    }

    /** 批量填充订单号、工序名、工作中心名、操作工名，避免前端逐行查接口 */
    private void fillNames(List<Dispatch> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        Set<Long> orderIds = records.stream().map(Dispatch::getOrderId).collect(Collectors.toSet());
        Set<Long> stepIds = records.stream().map(Dispatch::getStepId).collect(Collectors.toSet());
        Set<Long> wcIds = records.stream().map(Dispatch::getWorkCenterId).collect(Collectors.toSet());
        Set<Long> operatorIds = records.stream().map(Dispatch::getOperatorId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());

        Map<Long, ProductionOrder> orderMap = orderIds.isEmpty() ? Collections.emptyMap()
                : productionOrderMapper.selectBatchIds(orderIds).stream()
                .collect(Collectors.toMap(ProductionOrder::getId, Function.identity()));
        Map<Long, ProcessStep> stepMap = stepIds.isEmpty() ? Collections.emptyMap()
                : processStepMapper.selectBatchIds(stepIds).stream()
                .collect(Collectors.toMap(ProcessStep::getId, Function.identity()));
        Map<Long, WorkCenter> wcMap = wcIds.isEmpty() ? Collections.emptyMap()
                : workCenterMapper.selectBatchIds(wcIds).stream()
                .collect(Collectors.toMap(WorkCenter::getId, Function.identity()));
        Map<Long, SysUser> userMap = operatorIds.isEmpty() ? Collections.emptyMap()
                : sysUserMapper.selectBatchIds(operatorIds).stream()
                .collect(Collectors.toMap(SysUser::getId, Function.identity()));

        for (Dispatch d : records) {
            ProductionOrder order = orderMap.get(d.getOrderId());
            if (order != null) d.setOrderNo(order.getOrderNo());
            ProcessStep step = stepMap.get(d.getStepId());
            if (step != null) d.setStepName(step.getStepName());
            WorkCenter wc = wcMap.get(d.getWorkCenterId());
            if (wc != null) d.setWorkCenterName(wc.getCenterName());
            SysUser user = d.getOperatorId() != null ? userMap.get(d.getOperatorId()) : null;
            if (user != null) d.setOperatorName(user.getRealName());
        }
    }
}
