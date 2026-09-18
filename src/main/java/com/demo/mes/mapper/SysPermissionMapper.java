package com.demo.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.mes.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Set;

@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    @Select("SELECT DISTINCT p.permission_code FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.deleted = 0 AND p.status = 1")
    Set<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    @Select("SELECT DISTINCT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.deleted = 0 AND p.status = 1 AND p.type = 1 " +
            "ORDER BY p.sort")
    List<SysPermission> selectMenusByUserId(@Param("userId") Long userId);
}
