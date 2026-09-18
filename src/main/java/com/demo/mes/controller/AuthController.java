package com.demo.mes.controller;

import com.demo.mes.common.result.Result;
import com.demo.mes.dto.LoginDTO;
import com.demo.mes.entity.SysPermission;
import com.demo.mes.mapper.SysPermissionMapper;
import com.demo.mes.security.JwtUtils;
import com.demo.mes.security.LoginUser;
import com.demo.mes.vo.LoginVO;
import com.demo.mes.vo.MenuVO;
import com.demo.mes.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final SysPermissionMapper sysPermissionMapper;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, SysPermissionMapper sysPermissionMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.sysPermissionMapper = sysPermissionMapper;
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        String token = jwtUtils.generateToken(loginUser.getUserId(), loginUser.getUsername());

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUsername(loginUser.getUsername());
        loginVO.setRealName(loginUser.getRealName());
        loginVO.setRoles(loginUser.getRoles().toArray(new String[0]));

        return Result.success("登录成功", loginVO);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> info() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(loginUser.getUserId());
        vo.setUsername(loginUser.getUsername());
        vo.setRealName(loginUser.getRealName());
        vo.setRoles(loginUser.getRoles());
        vo.setPermissions(loginUser.getPermissions());

        return Result.success(vo);
    }

    @Operation(summary = "获取当前用户菜单树")
    @GetMapping("/menus")
    public Result<List<MenuVO>> menus() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<SysPermission> permissions = sysPermissionMapper.selectMenusByUserId(loginUser.getUserId());
        List<MenuVO> tree = buildMenuTree(permissions, 0L);
        return Result.success(tree);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        SecurityContextHolder.clearContext();
        return Result.success("退出成功", null);
    }

    private List<MenuVO> buildMenuTree(List<SysPermission> permissions, Long parentId) {
        return permissions.stream()
                .filter(p -> p.getParentId().equals(parentId))
                .map(p -> {
                    MenuVO vo = new MenuVO();
                    vo.setId(p.getId());
                    vo.setParentId(p.getParentId());
                    vo.setPermissionCode(p.getPermissionCode());
                    vo.setPermissionName(p.getPermissionName());
                    vo.setType(p.getType());
                    vo.setPath(p.getPath());
                    vo.setComponent(p.getComponent());
                    vo.setIcon(p.getIcon());
                    vo.setSort(p.getSort());
                    vo.setChildren(buildMenuTree(permissions, p.getId()));
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
