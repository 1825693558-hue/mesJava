package com.demo.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.mes.common.result.PageResult;
import com.demo.mes.common.result.Result;
import com.demo.mes.entity.SysRole;
import com.demo.mes.entity.SysRolePermission;
import com.demo.mes.mapper.SysRoleMapper;
import com.demo.mes.mapper.SysRolePermissionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Autowired
    public SysRoleController(SysRoleMapper sysRoleMapper, SysRolePermissionMapper sysRolePermissionMapper) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysRolePermissionMapper = sysRolePermissionMapper;
    }

    @Operation(summary = "角色分页列表")
    @GetMapping("/list")
    public Result<PageResult<SysRole>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SysRole> pageObj = new Page<>(page, size);
        sysRoleMapper.selectPage(pageObj, new LambdaQueryWrapper<>());
        return Result.success(new PageResult<>(pageObj.getRecords(), pageObj.getTotal(), page, size));
    }

    @Operation(summary = "全部角色")
    @GetMapping("/all")
    public Result<List<SysRole>> all() {
        return Result.success(sysRoleMapper.selectList(new LambdaQueryWrapper<>()));
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public Result<Void> create(@RequestBody SysRole role) {
        sysRoleMapper.insert(role);
        return Result.success("新增成功", null);
    }

    @Operation(summary = "修改角色")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        sysRoleMapper.updateById(role);
        return Result.success("修改成功", null);
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    @Operation(summary = "角色权限列表")
    @GetMapping("/{id}/permissions")
    public Result<List<Long>> permissions(@PathVariable Long id) {
        List<SysRolePermission> list = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, id));
        return Result.success(list.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList()));
    }

    @Operation(summary = "分配角色权限")
    @PostMapping("/{id}/permissions")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, id));
        for (Long permId : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(id);
            rp.setPermissionId(permId);
            sysRolePermissionMapper.insert(rp);
        }
        return Result.success("分配成功", null);
    }
}
