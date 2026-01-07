package com.ariplaza.stockmaster.service.impl;

import com.ariplaza.stockmaster.entity.User;
import com.ariplaza.stockmaster.service.IAuthService;
import com.ariplaza.stockmaster.service.IUserService;
import com.ariplaza.stockmaster.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements IAuthService {

    private final long jwtExpiration; // 从application.properties注入（毫秒）

    private final RedisUtil redisUtil;
    private final IUserService userService;

    private final SecretKey secretKey;

    private final PasswordEncoder passwordEncoder;
    public AuthServiceImpl(@Value("${jwt.secret-key}")String secret,
                           @Value("${jwt.expiration}")long jwtExpiration,
                           @Autowired IUserService userService,
                           @Autowired RedisUtil redisUtil,
                           PasswordEncoder passwordEncoder){
        this.jwtExpiration=jwtExpiration;
        this.userService=userService;
        this.passwordEncoder=passwordEncoder;
        this.redisUtil=redisUtil;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 验证用户是否存在
    @Override
    public Long isValidAndGetUserId(Map<String,String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        User user = userService.getUserbyUsername(username);
        if(user != null && passwordEncoder.matches(password, user.getPassword())){
            return user.getId();
        }else {
            return null;
        }
    }

    @Override
    public boolean getPasswordEncoder(String password, String encodedPassword) {
        return passwordEncoder.matches(password,encodedPassword);
    }

    //创建token
    @Override
    public String generateToken(String username,Long userId) {
        // 1. 生成JWT token
        String jti = UUID.randomUUID().toString();
        String token = Jwts.builder()
                .setId(jti)
                .setSubject(username)
                .claim("user_id",userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(secretKey)
                .compact();

        // 2. 存入Redis（使用正确的过期时间）
        redisUtil.insertToken(token,username,jwtExpiration);

        return token;
    }

    //查询redis是否存在token
    @Override
    public boolean validateToken(String token) {
        try {
            // 验证JWT签名
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);

            // 检查Redis是否存在
            return redisUtil.equalsToken(token);
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
    // 从token获取用户id
    @Override
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return ((Number) claims.get("user_id")).longValue(); // 安全转换
        } catch (Exception e) {
            return null;
        }
    }

    //设置token到Cookie
    @Override
    public boolean setCookie(String token, HttpServletResponse response) {
        if(token==null){
            return false;
        }
        // 设置 HttpOnly Cookie
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);      // 防止 JS 访问
        cookie.setSecure(false);        // 仅 HTTPS 传输
        cookie.setPath("/");           // 所有路径可用
        cookie.setMaxAge(7 * 24 * 3600000);
        response.addCookie(cookie);
        return true;
    }

    //从Cookie获取token
    @Override
    public String getTokenfromCookie(Cookie[] cookies) {
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
    public String updateToken(Cookie[] cookies) {
        String token = getTokenfromCookie(cookies);
        if (token == null||!validateToken(token)) {
            return null;
        }

        String username = getUsernameFromToken(token);
        Long userId=getUserIdFromToken(token);
        String tokenNew = generateToken(username,userId);
        //清理旧的token
        if(redisUtil.deleteTokenfromRedis(token)){
            return tokenNew;
        }else {
            return null;
        }
    }

    //从redis删除token
    @Override
    public Boolean deleteToken(Cookie[] cookies) {
        String token = getTokenfromCookie(cookies);
        //验证token
        if (token == null||!validateToken(token)) {
            return false;
        }

        //清理token
        return redisUtil.deleteTokenfromRedis(token);
    }

    @Override
    public boolean sendPasswordResetEmail(String email) {
        return false;
    }
}