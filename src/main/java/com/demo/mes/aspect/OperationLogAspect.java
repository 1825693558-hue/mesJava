package com.demo.mes.aspect;

import com.demo.mes.entity.OperationLog;
import com.demo.mes.mapper.OperationLogMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class OperationLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    // 模块名中文映射
    private static final Map<String, String> MODULE_NAMES = new HashMap<>();
    static {
        MODULE_NAMES.put("Auth", "认证");
        MODULE_NAMES.put("Material", "物料");
        MODULE_NAMES.put("Product", "产品");
        MODULE_NAMES.put("Workcenter", "工作中心");
        MODULE_NAMES.put("Equipment", "设备");
        MODULE_NAMES.put("ProductionOrder", "生产订单");
        MODULE_NAMES.put("Dispatch", "派工管理");
        MODULE_NAMES.put("Report", "报工管理");
        MODULE_NAMES.put("Inspection", "质检管理");
        MODULE_NAMES.put("System", "系统管理");
        MODULE_NAMES.put("User", "用户管理");
        MODULE_NAMES.put("Role", "角色管理");
        MODULE_NAMES.put("Permission", "权限管理");
        MODULE_NAMES.put("OperationLog", "操作日志");
    }

    private final OperationLogMapper operationLogMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OperationLogAspect(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Around("execution(* com.demo.mes.controller..*.*(..))")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String httpMethod = request.getMethod();
                if ("POST".equals(httpMethod) || "PUT".equals(httpMethod) || "DELETE".equals(httpMethod)) {
                    saveLog(joinPoint, request);
                }
            }
        } catch (Exception e) {
            log.warn("记录操作日志失败: {}", e.getMessage());
        }
        return result;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, HttpServletRequest request) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        String httpMethod = request.getMethod();

        OperationLog operationLog = new OperationLog();

        // 当前登录用户
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            operationLog.setUsername(auth.getName());
        } else {
            operationLog.setUsername("匿名");
        }

        // 模块名（中文）
        String moduleEn = className.replace("Controller", "");
        String moduleCn = MODULE_NAMES.getOrDefault(moduleEn, moduleEn);
        operationLog.setModule(moduleCn);

        // 操作描述（友好中文）
        String action = getActionName(httpMethod, methodName);
        operationLog.setOperation(action + moduleCn);

        // 请求参数（截断防止超长）
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0 && !(args[0] instanceof org.springframework.security.core.Authentication)) {
                String params = objectMapper.writeValueAsString(args[0]);
                if (params.length() > 500) params = params.substring(0, 500);
                operationLog.setParams(params);
            }
        } catch (Exception ignored) {}

        // IP
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        operationLog.setIp(ip);

        operationLog.setCreateTime(LocalDateTime.now());
        operationLogMapper.insert(operationLog);
    }

    private String getActionName(String httpMethod, String methodName) {
        if ("POST".equals(httpMethod)) {
            if (methodName.contains("login")) return "登录";
            if (methodName.contains("create") || methodName.contains("add") || methodName.contains("save")) return "新增";
            if (methodName.contains("import")) return "导入";
            return "新增";
        } else if ("PUT".equals(httpMethod) || "PATCH".equals(httpMethod)) {
            if (methodName.contains("update") || methodName.contains("edit") || methodName.contains("modify")) return "修改";
            if (methodName.contains("start")) return "开始";
            if (methodName.contains("pause")) return "暂停";
            if (methodName.contains("complete")) return "完成";
            if (methodName.contains("assign")) return "指派";
            return "修改";
        } else if ("DELETE".equals(httpMethod)) {
            return "删除";
        }
        return httpMethod;
    }
}
