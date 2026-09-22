package com.demo.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.mes.common.exception.BusinessException;
import com.demo.mes.common.result.PageResult;
import com.demo.mes.common.result.Result;
import com.demo.mes.entity.SysUser;
import com.demo.mes.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SysUserController(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "用户分页列表")
    @GetMapping("/list")
    public Result<PageResult<SysUser>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        Page<SysUser> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword).or().like(SysUser::getRealName, keyword));
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        sysUserMapper.selectPage(pageObj, wrapper);
        pageObj.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(new PageResult<>(pageObj.getRecords(), pageObj.getTotal(), page, size));
    }

    @Operation(summary = "新增用户")
    @PreAuthorize("hasAuthority('system:user:add') or hasAuthority('*:*:*')")
    @PostMapping
    public Result<Void> create(@RequestBody SysUser user) {
        SysUser existing = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, user.getUsername()));
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword() != null ? user.getPassword() : "123456"));
        sysUserMapper.insert(user);
        return Result.success("新增成功", null);
    }

    @Operation(summary = "修改用户")
    @PreAuthorize("hasAuthority('system:user:edit') or hasAuthority('*:*:*')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysUser user) {
        SysUser existing = sysUserMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        user.setId(id);
        user.setPassword(null);
        sysUserMapper.updateById(user);
        return Result.success("修改成功", null);
    }

    @Operation(summary = "重置密码")
    @PreAuthorize("hasAuthority('system:user:edit') or hasAuthority('*:*:*')")
    @PostMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserMapper.updateById(user);
        return Result.success("重置成功", null);
    }

    @Operation(summary = "删除用户")
    @PreAuthorize("hasAuthority('system:user:del') or hasAuthority('*:*:*')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        sysUserMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
