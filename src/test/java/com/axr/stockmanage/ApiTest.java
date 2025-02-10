package com.axr.stockmanage;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.template.QuickConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;


/**
 * @author xinrui.an
 * @date 2025/02/10
 */
@SpringBootTest
class ApiTest {

    @Resource
    private CacheManager cacheManager;

    private Cache<Long, String> strCache;

    @PostConstruct
    public void init() {
        QuickConfig qc = QuickConfig.newBuilder("userCache") // name用于统计信息展示名字
                .expire(Duration.ofSeconds(100))
                //.cacheType(CacheType.BOTH) // 创建一个两级缓存
                //.localLimit(100) // 本地缓存元素个数限制，只对CacheType.LOCAL和CacheType.BOTH有效
                //.syncLocal(true) // 两级缓存的情况下，缓存更新时发消息让其它JVM实例中的缓存失效，需要配置broadcastChannel才生效。
                .build();
        strCache = cacheManager.getOrCreateCache(qc);
    }

    @Test
    void test() {
        strCache.put(1L, "hello");
        System.out.println(strCache.get(1L));
    }

}
