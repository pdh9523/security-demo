package com.example.server.util.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate redisTemplate;

    public static long REDIS_EMAIL_EXPIRE = 60 * 5;          // 60sec * 5min
    public static long REDIS_VIEW_EXPIRE = 60 * 60 * 24;     // 60sec * 60min * 24hour

    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setData(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setDataWithExpire(String key, String value, long duration) {
        Duration expireDuration = Duration.ofSeconds(duration);
        redisTemplate.opsForValue().set(key, value, expireDuration);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

}
