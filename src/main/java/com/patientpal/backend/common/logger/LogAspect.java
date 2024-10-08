package com.patientpal.backend.common.logger;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* com.patientpal.backend..*Controller*.*(..))")
    public void controller() {
    }

    @Around("@annotation(com.patientpal.backend.common.TimeTrace)")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            long timeinMs = end - start;
            log.info("{} | time = {}ms", joinPoint.getSignature(), timeinMs);
        }
    }

    @Around("controller()")
    public Object loggingBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        String controllerName = joinPoint.getSignature().getDeclaringType().getName();

        if (controllerName.equals("com.patientpal.backend.common.controller.PingController")) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String methodName = joinPoint.getSignature().getName();
        Map<String, Object> params = new HashMap<>();

        try {
            String decodedURI = URLDecoder.decode(request.getRequestURI(), "UTF-8");

            params.put("controller", controllerName);
            params.put("method", methodName);
            params.put("params", getParams(request));
            params.put("log_time", System.currentTimeMillis());
            params.put("request_uri", decodedURI);
            params.put("http_method", request.getMethod());
        } catch (Exception e) {
            log.error("LoggerAspect error", e);
        }

        log.info("[{}] {}", params.get("http_method"), params.get("request_uri"));
        log.info("method: {}.{}", params.get("controller") ,params.get("method"));
        log.info("params: {}", params.get("params"));

        return joinPoint.proceed();
    }

    private static JSONObject getParams(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            String replaceParam = param.replaceAll("\\.", "-");
            jsonObject.put(replaceParam, request.getParameter(param));
        }
        return jsonObject;
    }

}
