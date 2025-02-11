package com.axr.stockmanage.common;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Global order ID generation
 * @author xinrui.an
 * @date 2025/02/11
 */
@Component
public class RedisIdWorker {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * Start time stamp
     */
    private static final long BEGIN_TIMESTAMP = 1735689600L;

    /**
     * Serial number digit
     */
    private static final long COUNT_BITS = 32;

    public long nextId(String keyPrefix) {
        LocalDateTime now = LocalDateTime.now();
        // timestamp
        long timestamp = now.toEpochSecond(ZoneOffset.UTC) - BEGIN_TIMESTAMP;
        // serial number
        String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);
         stringRedisTemplate.expire("icr:" + keyPrefix + ":" + date, 24, TimeUnit.HOURS);
        if (count == null) {
            throw new BusinessException("系统错误");
        }
        return timestamp << COUNT_BITS | count;
    }

}
