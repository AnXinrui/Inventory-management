package com.axr.stockmanage.service;

import com.alicp.jetcache.anno.Cached;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Cached(name = "userCache:", key = "#name", expire = 30, timeUnit = TimeUnit.SECONDS)
    public long getUserId() {
        return 1L;
    }

}