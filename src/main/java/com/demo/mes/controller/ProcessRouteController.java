package com.demo.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.mes.common.result.PageResult;
import com.demo.mes.common.result.Result;
import com.demo.mes.entity.ProcessRoute;
import com.demo.mes.entity.ProcessStep;
import com.demo.mes.mapper.ProcessRouteMapper;
import com.demo.mes.mapper.ProcessStepMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "工艺路线")
@RestController
@RequestMapping("/route")
public class ProcessRouteController {

    private final ProcessRouteMapper routeMapper;
    private final ProcessStepMapper stepMapper;

    @Autowired
    public ProcessRouteController(ProcessRouteMapper routeMapper, ProcessStepMapper stepMapper) {
        this.routeMapper = routeMapper;
        this.stepMapper = stepMapper;
    }

    @Operation(summary = "工艺路线分页列表")
    @GetMapping("/list")
    public Result<PageResult<ProcessRoute>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        Page<ProcessRoute> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<ProcessRoute> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(ProcessRoute::getRouteCode, keyword).or().like(ProcessRoute::getRouteName, keyword));
        }
        wrapper.orderByDesc(ProcessRoute::getCreateTime);
        routeMapper.selectPage(pageObj, wrapper);
        return Result.success(new PageResult<>(pageObj.getRecords(), pageObj.getTotal(), page, size));
    }

    @Operation(summary = "全部工艺路线")
    @GetMapping("/all")
    public Result<List<ProcessRoute>> all() {
        return Result.success(routeMapper.selectList(
                new LambdaQueryWrapper<ProcessRoute>().eq(ProcessRoute::getStatus, 1)));
    }

    @Operation(summary = "新增工艺路线")
    @PreAuthorize("hasAuthority('base:route') or hasAuthority('*:*:*')")
    @PostMapping
    public Result<Void> create(@RequestBody ProcessRoute route) {
        routeMapper.insert(route);
        return Result.success("新增成功", null);
    }

    @Operation(summary = "修改工艺路线")
    @PreAuthorize("hasAuthority('base:route') or hasAuthority('*:*:*')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProcessRoute route) {
        route.setId(id);
        routeMapper.updateById(route);
        return Result.success("修改成功", null);
    }

    @Operation(summary = "删除工艺路线")
    @PreAuthorize("hasAuthority('base:route') or hasAuthority('*:*:*')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        routeMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    @Operation(summary = "工序列表")
    @GetMapping("/{id}/steps")
    public Result<List<ProcessStep>> steps(@PathVariable Long id) {
        return Result.success(stepMapper.selectList(
                new LambdaQueryWrapper<ProcessStep>()
                        .eq(ProcessStep::getRouteId, id)
                        .orderByAsc(ProcessStep::getStepNo)));
    }

    @Operation(summary = "新增工序")
    @PreAuthorize("hasAuthority('base:route') or hasAuthority('*:*:*')")
    @PostMapping("/step")
    public Result<Void> createStep(@RequestBody ProcessStep step) {
        stepMapper.insert(step);
        return Result.success("新增成功", null);
    }

    @Operation(summary = "修改工序")
    @PreAuthorize("hasAuthority('base:route') or hasAuthority('*:*:*')")
    @PutMapping("/step/{id}")
    public Result<Void> updateStep(@PathVariable Long id, @RequestBody ProcessStep step) {
        step.setId(id);
        stepMapper.updateById(step);
        return Result.success("修改成功", null);
    }

    @Operation(summary = "删除工序")
    @PreAuthorize("hasAuthority('base:route') or hasAuthority('*:*:*')")
    @DeleteMapping("/step/{id}")
    public Result<Void> deleteStep(@PathVariable Long id) {
        stepMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
