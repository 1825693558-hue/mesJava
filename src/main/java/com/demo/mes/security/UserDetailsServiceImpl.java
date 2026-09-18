package com.demo.mes.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.mes.entity.SysUser;
import com.demo.mes.mapper.SysPermissionMapper;
import com.demo.mes.mapper.SysRoleMapper;
import com.demo.mes.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;

    @Autowired
    public UserDetailsServiceImpl(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper, SysPermissionMapper sysPermissionMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysPermissionMapper = sysPermissionMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(sysUser.getId());
        loginUser.setUsername(sysUser.getUsername());
        loginUser.setPassword(sysUser.getPassword());
        loginUser.setRealName(sysUser.getRealName());
        loginUser.setStatus(sysUser.getStatus());

        List<String> roleCodes = sysRoleMapper.selectRoleCodesByUserId(sysUser.getId());
        loginUser.setRoles(new HashSet<>(roleCodes));

        Set<String> permissionCodes = sysPermissionMapper.selectPermissionCodesByUserId(sysUser.getId());
        if (permissionCodes == null) {
            permissionCodes = new HashSet<>();
        }
        if (roleCodes.contains("ADMIN")) {
            permissionCodes.add("*:*:*");
        }
        loginUser.setPermissions(permissionCodes);

        return loginUser;
    }
}
