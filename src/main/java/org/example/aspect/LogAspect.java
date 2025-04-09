package org.example.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.annotation.LogOperation;
import org.example.entity.LogInfo;
import org.example.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 请求到来  →  检查有没有 @LogOperation 注解
 *    ↓
 * 拦截执行前：记录IP、用户、参数、模块、操作名……
 *    ↓
 * 执行原方法（成功 or 抛异常）
 *    ↓
 * 拦截执行后：记录返回值、耗时、异常信息
 *    ↓
 * 保存日志到数据库
 * **/

@Aspect
@Component
public class LogAspect {
    // final修饰表示这个变量一旦赋值就不能改变
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    private LogService logService;

    @Autowired
    private ObjectMapper objectMapper;

    @Around("@annotation(org.example.annotation.LogOperation)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 每次请求生成一个唯一的ID  方便追踪
        LogInfo logInfo = new LogInfo();
        logInfo.setRequestId(UUID.randomUUID().toString());

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            // 获取请求路径
            logInfo.setUrl(request.getRequestURI());
            // 请求方法
            logInfo.setHttpMethod(request.getMethod());
            // 请求IP地址
            logInfo.setIpAddress(getIpAddress(request));

            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                logInfo.setUsername(authentication.getName());
            }
        }

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogOperation logOperation = method.getAnnotation(LogOperation.class);

        logInfo.setClassName(method.getDeclaringClass().getName());
        logInfo.setMethodName(method.getName());
        // 从注解里面读取模块名以及操作名
        logInfo.setModule(logOperation.module());
        logInfo.setOperation(logOperation.value());

        // 是否记录请求参数
        if (logOperation.logParams()) {
            try {
                logInfo.setRequestParams(objectMapper.writeValueAsString(joinPoint.getArgs()));
            } catch (Exception e) {
                logger.warn("Failed to serialize request params", e);
                logInfo.setRequestParams(String.valueOf(joinPoint.getArgs()));
            }
        }

        Object result;
        try {
            result = joinPoint.proceed();
            // 是否记录结果
            if (logOperation.logResult()) {
                try {
                    logInfo.setResponseData(objectMapper.writeValueAsString(result));
                } catch (Exception e) {
                    logger.warn("Failed to serialize response data", e);
                    logInfo.setResponseData(String.valueOf(result));
                }
            }
        } catch (Throwable e) {
            logInfo.setException(e.getMessage());
            throw e;
        } finally {
            logInfo.finish(); // 计算执行时间

            // 使用日志服务保存到数据库
            try {
                logService.saveLog(logInfo);
            } catch (Exception e) {
                logger.error("Failed to save operation log", e);
            }
        }

        return result;
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理的情况，第一个IP为客户端真实IP
        if (ip != null && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }
}