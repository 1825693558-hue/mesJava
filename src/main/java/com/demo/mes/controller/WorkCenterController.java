package com.demo.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.mes.common.result.PageResult;
import com.demo.mes.common.result.Result;
import com.demo.mes.entity.WorkCenter;
import com.demo.mes.mapper.WorkCenterMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "工作中心")
@RestController
@RequestMapping("/work-center")
public class WorkCenterController {

    private final WorkCenterMapper workCenterMapper;

    @Autowired
    public WorkCenterController(WorkCenterMapper workCenterMapper) {
        this.workCenterMapper = workCenterMapper;
    }

    @Operation(summary = "工作中心分页列表")
    @GetMapping("/list")
    public Result<PageResult<WorkCenter>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        Page<WorkCenter> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<WorkCenter> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(WorkCenter::getCenterCode, keyword).or().like(WorkCenter::getCenterName, keyword));
        }
        wrapper.orderByAsc(WorkCenter::getCenterCode);
        workCenterMapper.selectPage(pageObj, wrapper);
        return Result.success(new PageResult<>(pageObj.getRecords(), pageObj.getTotal(), page, size));
    }

    @Operation(summary = "全部工作中心")
    @GetMapping("/all")
    public Result<List<WorkCenter>> all() {
        return Result.success(workCenterMapper.selectList(new LambdaQueryWrapper<>()));
    }

    @Operation(summary = "新增工作中心")
    @PreAuthorize("hasAuthority('base:workcenter') or hasAuthority('*:*:*')")
    @PostMapping
    public Result<Void> create(@RequestBody WorkCenter workCenter) {
        workCenterMapper.insert(workCenter);
        return Result.success("新增成功", null);
    }

    @Operation(summary = "修改工作中心")
    @PreAuthorize("hasAuthority('base:workcenter') or hasAuthority('*:*:*')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody WorkCenter workCenter) {
        workCenter.setId(id);
        workCenterMapper.updateById(workCenter);
        return Result.success("修改成功", null);
    }

    @Operation(summary = "删除工作中心")
    @PreAuthorize("hasAuthority('base:workcenter') or hasAuthority('*:*:*')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        workCenterMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
