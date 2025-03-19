package org.example.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.example.entity.LogInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

public class LogUtil {

    private static final Logger log = LoggerFactory.getLogger(LogUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final ThreadLocal<LogInfo> logInfoThreadLocal = new ThreadLocal<LogInfo>();

    // 获取当前线程的日志信息
    public static LogInfo getLogInfo() {
        LogInfo logInfo = logInfoThreadLocal.get();
        if (logInfo == null) {
            logInfo = new LogInfo();
            logInfo.setRequestId(UUID.randomUUID().toString().replace("-", ""));
            logInfoThreadLocal.set(logInfo);
        }
        return logInfo;
    }

    // 清除当前线程的日志信息
    public static void clearLogInfo() {
        logInfoThreadLocal.remove();
    }

    // 获取当前请求
    public static HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getClientIp() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 多个代理的情况下第一个IP为真是客户端ip
        if (ip != null && ip.indexOf(",") > 0){
            ip = ip.substring(0, ip.indexOf(","));
        }

        return ip;
    }

    public static void logInfo(LogInfo logInfo) {
        try {
            log.info("{}", objectMapper.writeValueAsString(logInfo));
        } catch (JsonProcessingException e) {
            log.error("日志序列化失败", e);
        }
    }

    public static void logError(String message, Throwable ex) {
        LogInfo logInfo = getLogInfo();
        logInfo.setException(ex.getMessage());
        logInfo.finish();

        log.error("【错误日志】{}", message, ex);
        logInfo(logInfo);
    }
}
