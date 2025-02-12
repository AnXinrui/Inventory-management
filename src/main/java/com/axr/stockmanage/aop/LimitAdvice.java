package com.axr.stockmanage.aop;

import com.axr.stockmanage.annotation.Limit;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 对不同接口进行限流
 *
 * @author xinrui.an
 * @date 2025/02/12
 */
@Slf4j
@Aspect
@Component
public class LimitAdvice {

    private final Map<String, RateLimiter> limiterMap = Maps.newConcurrentMap();

    @Around("@annotation(limit)")
    public Object method(ProceedingJoinPoint pjp, Limit limit) throws Throwable {
        if (null != limit) {
            String key = limit.key();
            if (key == null || key.trim().isEmpty()) {
                throw new IllegalArgumentException("Limit key cannot be empty");
            }
            RateLimiter rateLimiter = limiterMap.computeIfAbsent(key, k -> {
                log.info("新建了令牌桶={}，容量={}", k, limit.permitsPerSecond());
                return RateLimiter.create(limit.permitsPerSecond());
            });
            boolean acquire = rateLimiter.tryAcquire(limit.timeout(), limit.timeunit());
            if (!acquire) {
                log.warn("令牌桶={}，获取令牌失败", key);
                return limit.msg();
            }
        }
        return pjp.proceed();
    }
}
