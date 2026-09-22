package com.demo.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.mes.common.exception.BusinessException;
import com.demo.mes.common.result.PageResult;
import com.demo.mes.common.result.Result;
import com.demo.mes.entity.Equipment;
import com.demo.mes.mapper.EquipmentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "设备管理")
@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentMapper equipmentMapper;

    @Autowired
    public EquipmentController(EquipmentMapper equipmentMapper) {
        this.equipmentMapper = equipmentMapper;
    }

    @Operation(summary = "设备分页列表")
    @GetMapping("/list")
    public Result<PageResult<Equipment>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer equipmentType,
            @RequestParam(required = false) Integer status) {
        Page<Equipment> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Equipment::getEquipmentCode, keyword).or().like(Equipment::getEquipmentName, keyword));
        }
        if (equipmentType != null) wrapper.eq(Equipment::getEquipmentType, equipmentType);
        if (status != null) wrapper.eq(Equipment::getStatus, status);
        wrapper.orderByAsc(Equipment::getEquipmentCode);
        equipmentMapper.selectPage(pageObj, wrapper);
        return Result.success(new PageResult<>(pageObj.getRecords(), pageObj.getTotal(), page, size));
    }

    @Operation(summary = "新增设备")
    @PreAuthorize("hasAuthority('equipment:list') or hasAuthority('*:*:*')")
    @PostMapping
    public Result<Void> create(@RequestBody Equipment equipment) {
        equipmentMapper.insert(equipment);
        return Result.success("新增成功", null);
    }

    @Operation(summary = "修改设备")
    @PreAuthorize("hasAuthority('equipment:list') or hasAuthority('*:*:*')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Equipment equipment) {
        equipment.setId(id);
        equipmentMapper.updateById(equipment);
        return Result.success("修改成功", null);
    }

    @Operation(summary = "变更设备状态")
    @PreAuthorize("hasAuthority('equipment:list') or hasAuthority('*:*:*')")
    @PostMapping("/{id}/status")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }
        equipment.setStatus(status);
        equipmentMapper.updateById(equipment);
        return Result.success("状态变更成功", null);
    }

    @Operation(summary = "删除设备")
    @PreAuthorize("hasAuthority('equipment:list') or hasAuthority('*:*:*')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }
        equipmentMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
