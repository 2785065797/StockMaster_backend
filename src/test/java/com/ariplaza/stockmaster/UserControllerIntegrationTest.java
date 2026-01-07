package com.ariplaza.stockmaster;

import com.ariplaza.stockmaster.entity.User;
import com.ariplaza.stockmaster.service.IAuthService;
import com.ariplaza.stockmaster.service.IUserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // 使用test配置文件
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private IUserService userService;

    @Autowired
    private IAuthService authService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    @BeforeAll
    static void checkEncoding() {
        System.out.println("System encoding: " + System.getProperty("file.encoding"));
        System.out.println("Default charset: " + System.getProperty("file.encoding"));
    }
    @Test
    @Order(1)
    void registerUser_Success() throws Exception {
        // 准备注册数据
        String username = "testuser_" + System.currentTimeMillis();
        String registrationData = "{\"username\": \""+username+"\", \"email\": \"test@example.com\", \"password\": \"password123\"}";

        // 执行注册
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationData))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        User user = userService.getUserbyUsername(username);
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertTrue(authService.getPasswordEncoder("password123", user.getPassword()));
    }

    @Test
    @Order(2)
    void loginUser_Success() throws Exception {

        // 先注册用户
        String username="testuser_" + System.currentTimeMillis();
        String registrationData = "{\"username\": \""+username+"\", \"email\": \"login@example.com\", \"password\": \"password123\"}";
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationData))
                .andExpect(status().isOk());

        // 准备登录凭证
        String credentialsData = "{\"username\": \""+username+"\", \"password\": \"password123\"}";

        // 执行登录
        MvcResult loginResult = mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsData))
                .andExpect(status().isOk())
                .andReturn();

        // 获取token
        String token = getCookieValue(loginResult, "token");
        assertNotNull(token);

        // 验证token有效
        assertTrue(authService.validateToken(token));

        // 验证从token获取的用户名
        String username2 = authService.getUsernameFromToken(token);
        assertEquals(username, username2);

        // 验证从token获取的用户ID
        Long userId = authService.getUserIdFromToken(token);
        assertNotNull(userId);
    }

    @Test
    @Order(3)
    void autoLogin_Success() throws Exception {
        // 先注册用户
        String registrationData = "{\"username\": \"autologinuser\", \"email\": \"autologin@example.com\", \"password\": \"password123\"}";
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationData))
                .andExpect(status().isOk());

        // 登录获取token
        String credentialsData = "{\"username\": \"autologinuser\", \"password\": \"password123\"}";
        MvcResult loginResult = mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsData))
                .andExpect(status().isOk())
                .andReturn();

        // 获取token
        String token = getCookieValue(loginResult, "token");
        assertNotNull(token);

        // 执行自动登录
        MvcResult autoLoginResult = mockMvc.perform(post("/user/auto-login")
                        .cookie(new org.springframework.mock.web.MockCookie("token", token)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("自動ログイン成功")))
                .andReturn();

        // 获取新的token
        String newToken = getCookieValue(autoLoginResult, "token");
        assertNotNull(newToken);

        // 验证新token有效
        assertTrue(authService.validateToken(newToken));
    }

    @Test
    @Order(4)
    void logout_Success() throws Exception {
        // 先注册用户
        String registrationData = "{\"username\": \"logoutuser\", \"email\": \"logout@example.com\", \"password\": \"password123\"}";
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationData))
                .andExpect(status().isOk());

        // 登录获取token
        String credentialsData = "{\"username\": \"logoutuser\", \"password\": \"password123\"}";
        MvcResult loginResult = mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsData))
                .andExpect(status().isOk())
                .andReturn();

        // 获取token
        String token = getCookieValue(loginResult, "token");
        assertNotNull(token);

        // 执行登出
        mockMvc.perform(post("/user/logout")
                        .cookie(new org.springframework.mock.web.MockCookie("token", token)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ログアウト成功")));

        // 验证token已从Redis中删除
        assertFalse(authService.validateToken(token));
    }

    @ParameterizedTest
    @CsvSource({
            "testuser_1767710050778, password123, true, 1",
            "invalidUser, password, false, 0",
            "testUser, wrongPassword, false, 0",
            ", password123, false, 0",
            "testUser, , false, 0"
    })
    void isValidAndGetUserId_WithVariousCredentials(
            String username,
            String password,
            boolean isValid,
            Long expectedUserId) {

        // 准备测试数据
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser_1767710050778");
        user.setPassword("encodedPassword");

        // 设置模拟行为 - 确保这里使用的是 mock 对象
        doReturn(isValid ? user : null).when(userService).getUserbyUsername(username);

        if (isValid) {
            when(passwordEncoder.matches("encodedPassword", password)).thenReturn(true);
        } else {
            when(passwordEncoder.matches("encodedPassword", password)).thenReturn(false);
        }

        // 执行测试
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        Long userId = authService.isValidAndGetUserId(credentials);

        // 验证结果
        assertEquals(expectedUserId, userId);

        // 验证依赖调用
        verify(userService).getUserbyUsername(username);
        if (isValid) {
            verify(passwordEncoder).matches("encodedPassword", password);
        } else {
            verify(passwordEncoder, never()).matches(any(), any());
        }
    }

    private String getCookieValue(MvcResult result, String name) {
        result.getResponse().getCookies();
        for (var cookie : result.getResponse().getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}