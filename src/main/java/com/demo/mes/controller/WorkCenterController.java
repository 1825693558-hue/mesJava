package com.demo.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.mes.common.exception.BusinessException;
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
        Page<WorkCenter> pageObj = new Page<>(Math.max(page, 1), Math.min(Math.max(size, 1), 100));
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

    @Operation(summary = "工作中心详情")
    @GetMapping("/{id}")
    public Result<WorkCenter> getById(@PathVariable Long id) {
        WorkCenter wc = workCenterMapper.selectById(id);
        if (wc == null) {
            throw new BusinessException("工作中心不存在");
        }
        return Result.success(wc);
    }

    @Operation(summary = "新增工作中心")
    @PreAuthorize("hasAuthority('base:workcenter') or hasAuthority('*:*:*')")
    @PostMapping
    public Result<Void> create(@RequestBody WorkCenter workCenter) {
        if (workCenter.getCenterCode() == null || workCenter.getCenterCode().isBlank()) {
            throw new BusinessException("工作中心编码不能为空");
        }
        if (workCenter.getCenterName() == null || workCenter.getCenterName().isBlank()) {
            throw new BusinessException("工作中心名称不能为空");
        }
        workCenterMapper.insert(workCenter);
        return Result.success("新增成功", null);
    }

    @Operation(summary = "修改工作中心")
    @PreAuthorize("hasAuthority('base:workcenter') or hasAuthority('*:*:*')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody WorkCenter workCenter) {
        WorkCenter existing = workCenterMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("工作中心不存在");
        }
        workCenter.setId(id);
        workCenterMapper.updateById(workCenter);
        return Result.success("修改成功", null);
    }

    @Operation(summary = "删除工作中心")
    @PreAuthorize("hasAuthority('base:workcenter') or hasAuthority('*:*:*')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        WorkCenter existing = workCenterMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("工作中心不存在");
        }
        workCenterMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
