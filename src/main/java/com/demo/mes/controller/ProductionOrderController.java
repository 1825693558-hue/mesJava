package com.demo.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.mes.common.result.PageResult;
import com.demo.mes.common.result.Result;
import com.demo.mes.dto.ProductionOrderDTO;
import com.demo.mes.entity.ProductionOrder;
import com.demo.mes.service.ProductionOrderService;
import com.demo.mes.vo.OrderProgressVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "生产订单")
@RestController
@RequestMapping("/production-order")
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    @Autowired
    public ProductionOrderController(ProductionOrderService productionOrderService) {
        this.productionOrderService = productionOrderService;
    }

    @Operation(summary = "订单分页列表")
    @GetMapping("/list")
    public Result<PageResult<ProductionOrder>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status) {
        Page<ProductionOrder> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.like(ProductionOrder::getOrderNo, orderNo);
        }
        if (status != null) {
            wrapper.eq(ProductionOrder::getStatus, status);
        }
        wrapper.orderByDesc(ProductionOrder::getCreateTime);
        productionOrderService.page(pageObj, wrapper);
        return Result.success(new PageResult<>(pageObj.getRecords(), pageObj.getTotal(), page, size));
    }

    @Operation(summary = "创建生产订单")
    @PreAuthorize("hasAuthority('order:list') or hasAuthority('*:*:*')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody ProductionOrderDTO dto) {
        productionOrderService.createOrder(dto);
        return Result.success("创建成功", null);
    }

    @Operation(summary = "下发订单（拆解派工单）")
    @PreAuthorize("hasAuthority('order:list') or hasAuthority('*:*:*')")
    @PostMapping("/{id}/release")
    public Result<Void> release(@PathVariable Long id) {
        productionOrderService.releaseOrder(id);
        return Result.success("下发成功", null);
    }

    @Operation(summary = "关闭订单")
    @PreAuthorize("hasAuthority('order:list') or hasAuthority('*:*:*')")
    @PostMapping("/{id}/close")
    public Result<Void> close(@PathVariable Long id) {
        productionOrderService.closeOrder(id);
        return Result.success("关闭成功", null);
    }

    @Operation(summary = "订单进度详情")
    @GetMapping("/{id}/progress")
    public Result<OrderProgressVO> progress(@PathVariable Long id) {
        return Result.success(productionOrderService.getOrderProgress(id));
    }
}
