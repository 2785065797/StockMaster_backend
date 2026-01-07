package com.ariplaza.stockmaster;

import com.ariplaza.stockmaster.entity.User;
import com.ariplaza.stockmaster.service.IAuthService;
import com.ariplaza.stockmaster.service.IUserService;
import com.ariplaza.stockmaster.service.impl.AuthServiceImpl;
import com.ariplaza.stockmaster.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private IUserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisUtil redisUtil;


    private AuthServiceImpl authService;

    private SecretKey secretKey;
    private final long jwtExpiration = 3600000; // 1小时

    @BeforeEach
    void setUp() {
        // 模拟 secretKey（与测试代码一致）
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        // 重置 authService 实例（避免构造函数依赖配置）
        authService = new AuthServiceImpl(
                "01234567890123456789012345678901",
                jwtExpiration,
                userService,
                redisUtil,
                passwordEncoder
        );
    }

    // ===== 单元测试：isValidAndGetUserId =====
    @Test
    void isValidAndGetUserId_ValidCredentials_ReturnsUserId() {
        User user = new User();
        user.setId(1L);
        user.setPassword("encodedPassword");

        when(userService.getUserbyUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        Map<String, String> credentials = Map.of("username", "testUser", "password", "password");
        Long userId = authService.isValidAndGetUserId(credentials);
        assertEquals(1L, userId);
    }

    @Test
    void isValidAndGetUserId_InvalidPassword_ReturnsNull() {
        User user = new User();
        user.setPassword("encodedPassword");
        when(userService.getUserbyUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);

        Map<String, String> credentials = Map.of("username", "testUser", "password", "wrong");
        assertNull(authService.isValidAndGetUserId(credentials));
    }

    // ===== 单元测试：generateToken =====
    @Test
    void generateToken_StoresTokenInRedis() {
        String username = "testUser";
        Long userId = 1L;
        doNothing().when(redisUtil).insertToken(any(), eq(username), eq(jwtExpiration));

        String token = authService.generateToken(username, userId);

        // 验证 JWT 内容
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(username, claims.getSubject());
        assertEquals(userId, claims.get("user_id"));

        // 验证 Redis 调用
        verify(redisUtil).insertToken(token, username, jwtExpiration);
    }

    // ===== 单元测试：validateToken =====
    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = generateValidToken("testUser", 1L);
        when(redisUtil.equalsToken(token)).thenReturn(true);
        assertTrue(authService.validateToken(token));
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        String token = generateExpiredToken();
        when(redisUtil.equalsToken(token)).thenReturn(true); // Redis 存在，但 JWT 过期
        assertFalse(authService.validateToken(token));
    }

    // ===== 单元测试：setCookie =====
    @Test
    void setCookie_SetsHttpOnlyCookie() {
        String token = "testToken";
        HttpServletResponse response = mock(HttpServletResponse.class);

        authService.setCookie(token, response);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());

        Cookie cookie = cookieCaptor.getValue();
        assertEquals("token", cookie.getName());
        assertTrue(cookie.isHttpOnly());
        assertEquals(7 * 24 * 3600000, cookie.getMaxAge());
    }

    // ===== 辅助方法：生成测试用 token =====
    private String generateValidToken(String username, Long userId) {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(username)
                .claim("user_id", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(secretKey)
                .compact();
    }

    private String generateExpiredToken() {
        return Jwts.builder()
                .setSubject("test")
                .setExpiration(new Date(System.currentTimeMillis() - 10000)) // 10秒前
                .signWith(secretKey)
                .compact();
    }
}