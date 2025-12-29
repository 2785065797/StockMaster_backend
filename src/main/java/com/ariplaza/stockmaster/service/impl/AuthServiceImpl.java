package com.ariplaza.stockmaster.service.impl;

import com.ariplaza.stockmaster.entity.User;
import com.ariplaza.stockmaster.service.IAuthService;
import com.ariplaza.stockmaster.service.IUserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class AuthServiceImpl implements IAuthService {

    private static final String TOKEN_PREFIX = "auth:token:"; // Redis key前缀
    //private static final int EXPIRATION_MINUTES = 24 * 60; // 24小时（分钟）

    //private final String secret; // 从application.properties注入

    private final long jwtExpiration; // 从application.properties注入（毫秒）

    private final RedisTemplate<String, String> redisTemplate;

    private final IUserService userService;

    private final SecretKey secretKey;

    private final PasswordEncoder passwordEncoder;
    public AuthServiceImpl(@Value("${jwt.secret-key}")String secret,
                           @Value("${jwt.expiration}")long jwtExpiration,
                           RedisTemplate<String, String> redisTemplate,
                           @Autowired IUserService userService,
                           PasswordEncoder passwordEncoder){
        this.jwtExpiration=jwtExpiration;
        this.redisTemplate=redisTemplate;
        this.userService=userService;
        this.passwordEncoder=passwordEncoder;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 验证用户是否存在
    @Override
    public boolean isValidUser(String username, String password) {
        User user = userService.getUserbyUsername(username);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    //创建token
    @Override
    public String generateToken(String username) {
        // 1. 生成JWT token
        String jti = UUID.randomUUID().toString();
        String token = Jwts.builder()
                .setId(jti)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(secretKey)
                .compact();

        // 2. 存入Redis（使用正确的过期时间）
        redisTemplate.opsForValue().set(
                TOKEN_PREFIX + token,
                username,
                jwtExpiration, // 使用注入的过期时间
                TimeUnit.MILLISECONDS
        );

        return token;
    }

    //查询redis是否存在token
    @Override
    public boolean validateToken(String token) {
        try {
            // 1. 验证JWT签名
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);

            // 2. 检查Redis是否存在
            return Boolean.TRUE.equals(redisTemplate.hasKey(TOKEN_PREFIX + token));
        } catch (ExpiredJwtException e) {
            return false; // 过期token自动失效
        } catch (Exception e) {
            return false; // 无效token
        }
    }

    // 从token获取用户名
    @Override
    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
                    .getBody().getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void setCookie(String token, HttpServletResponse response) {
        // ✅ 重要：设置 HttpOnly Cookie
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);      // 关键：防止 JS 访问
        cookie.setSecure(false);        // 仅 HTTPS 传输
        cookie.setPath("/");           // 所有路径可用
        response.addCookie(cookie);
    }

    @Override
    public String getToken(Cookie[] cookies) {
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        return token;
    }

    //更新token
    @Override
    public String updateToken(String token) {
        String username = getUsernameFromToken(token);
        String tokenNew = generateToken(username);
        //清理旧的token
        String oldRedisKey = TOKEN_PREFIX + token;
        redisTemplate.delete(oldRedisKey);
        return tokenNew;
    }

    @Override
    public boolean sendPasswordResetEmail(String email) {
        return false;
    }
}