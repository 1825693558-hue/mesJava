package com.demo.mes.common.exception;

import com.demo.mes.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 唯一约束名 -> 字段中文名 */
    private static final Map<String, String> UNIQUE_KEY_NAMES = Map.of(
            "uk_username", "用户名",
            "uk_role_code", "角色编码",
            "uk_permission_code", "权限编码",
            "uk_product_code", "产品编码",
            "uk_material_code", "物料编码",
            "uk_center_code", "工作中心编码",
            "uk_route_code", "工艺路线编码",
            "uk_order_no", "订单编号",
            "uk_dispatch_no", "派工单号"
    );

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        return Result.error(403, "权限不足");
    }

    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证异常: {}", e.getMessage());
        return Result.error(401, "未登录或登录已过期");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.error(400, message);
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.error(400, message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        return Result.error(400, "缺少必填参数: " + e.getParameterName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return Result.error(400, "请求体格式错误或缺失");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return Result.error(405, "不支持的请求方法: " + e.getMethod());
    }

    /**
     * 唯一约束冲突（编码重复），返回友好提示而非"系统内部错误"
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKey(DuplicateKeyException e, HttpServletRequest request) {
        log.warn("唯一约束冲突: {} - {}", request.getRequestURI(), e.getMessage());
        String detail = e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : e.getMessage();
        String fieldName = "编码";
        String duplicateValue = "";

        // MySQL 错误格式: Duplicate entry 'ADMIN' for key 'uk_role_code'
        if (detail != null) {
            for (Map.Entry<String, String> entry : UNIQUE_KEY_NAMES.entrySet()) {
                if (detail.contains(entry.getKey())) {
                    fieldName = entry.getValue();
                    break;
                }
            }
            // 提取重复值
            int start = detail.indexOf("'");
            int end = detail.indexOf("'", start + 1);
            if (start >= 0 && end > start) {
                duplicateValue = detail.substring(start + 1, end);
            }
        }

        String message = duplicateValue.isEmpty()
                ? fieldName + "已存在，请勿重复"
                : fieldName + "「" + duplicateValue + "」已存在，请勿重复";
        return Result.error(400, message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error("系统内部错误");
    }
}
