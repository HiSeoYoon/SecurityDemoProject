package com.example.securitydemoproject.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);

    public static void requestLogInfo(Class<?> clazz, String requestId, String message) {
        logger.info("[{}] [{}] {}", requestId, clazz.getSimpleName(), message);
    }

    public static void requestLogDebug(Class<?> clazz, String requestId, String message) {
        logger.debug("[{}] [{}] {}", requestId, clazz.getSimpleName(), message);
    }

    public static void requestLogError(Class<?> clazz, String requestId, String message, Throwable throwable) {
        logger.error("[{}] [{}] {}", requestId, clazz.getSimpleName(), message, throwable);
    }

    public static void logInfo(Class<?> clazz, String message) {
        logger.info("[{}] {}", clazz.getSimpleName(), message);
    }

    public static void logDebug(Class<?> clazz, String message) {
        logger.debug("[{}] {}", clazz.getSimpleName(), message);
    }

    public static void logError(Class<?> clazz, String message, Throwable throwable) {
        logger.error("[{}] {}", clazz.getSimpleName(), message, throwable);
    }
}
