package com.demo.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.mes.common.result.Result;
import com.demo.mes.entity.SysPermission;
import com.demo.mes.mapper.SysPermissionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "权限管理")
@RestController
@RequestMapping("/system/permission")
public class SysPermissionController {

    private final SysPermissionMapper sysPermissionMapper;

    @Autowired
    public SysPermissionController(SysPermissionMapper sysPermissionMapper) {
        this.sysPermissionMapper = sysPermissionMapper;
    }

    @Operation(summary = "权限树")
    @GetMapping("/tree")
    public Result<List<SysPermission>> tree() {
        List<SysPermission> all = sysPermissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getSort));
        return Result.success(buildTree(all, 0L));
    }

    @Operation(summary = "新增权限")
    @PostMapping
    public Result<Void> create(@RequestBody SysPermission permission) {
        sysPermissionMapper.insert(permission);
        return Result.success("新增成功", null);
    }

    @Operation(summary = "修改权限")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysPermission permission) {
        permission.setId(id);
        sysPermissionMapper.updateById(permission);
        return Result.success("修改成功", null);
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysPermissionMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    private List<SysPermission> buildTree(List<SysPermission> all, Long parentId) {
        return all.stream()
                .filter(p -> p.getParentId().equals(parentId))
                .peek(p -> {
                    List<SysPermission> children = buildTree(all, p.getId());
                    // children can be set via reflection or custom VO; simplified for skeleton
                })
                .collect(Collectors.toList());
    }
}
