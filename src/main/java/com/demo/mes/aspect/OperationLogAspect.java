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

@Aspect
@Component
public class OperationLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

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

        OperationLog operationLog = new OperationLog();

        // 当前登录用户
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            operationLog.setUsername(auth.getName());
        } else {
            operationLog.setUsername("匿名");
        }

        // 模块和操作
        String module = className.replace("Controller", "");
        operationLog.setModule(module);
        operationLog.setOperation(methodName);

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
}
