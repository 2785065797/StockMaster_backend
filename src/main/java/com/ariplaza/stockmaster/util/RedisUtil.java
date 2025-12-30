package com.ariplaza.stockmaster.util;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;
    public RedisUtil(RedisTemplate<String,String> redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    private final String TOKEN_PREFIX = "auth:token:"; // Redis key前缀

    public boolean deleteTokenfromRedis(String token){
        String oldRedisKey = TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.delete(oldRedisKey));
    }
    public void insertToken(String token,String username,long jwtExpiration){
        redisTemplate.opsForValue().set(
                TOKEN_PREFIX + token,
                username,
                jwtExpiration, // 使用注入的过期时间
                TimeUnit.MILLISECONDS
        );
    }
    public boolean equalsToken(String token){
        return Boolean.TRUE.equals(redisTemplate.hasKey(TOKEN_PREFIX + token));
    }

}
