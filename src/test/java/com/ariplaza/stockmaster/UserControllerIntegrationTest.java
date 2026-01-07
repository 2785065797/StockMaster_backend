package com.ariplaza.stockmaster;

import com.ariplaza.stockmaster.entity.User;
import com.ariplaza.stockmaster.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Objects;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test") // 使用test配置文件
public class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @SpyBean
    private IUserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @ParameterizedTest
    @CsvSource({
            "testUser, password, true, 200, ログイン成功",
            "invalidUser, password, false, 400, ユーザー名またはパスワードが間違いました",
            "testUser, wrongPassword, false, 400, ユーザー名またはパスワードが間違いました",
            ", password, false, 400, ユーザー名またはパスワードが間違いました",
            "testUser, , false, 400, ユーザー名またはパスワードが間違いました"
    })
    void login_WithVariousCredentials(
            String username,
            String password,
            String isValidStr,
            int expectedStatus,
            String expectedMessage) throws Exception {

        boolean isValid = Boolean.parseBoolean(isValidStr);

        // 设置模拟行为
        if (isValid) {
            User user = new User();
            user.setId(1L);
            user.setUsername(username);
            user.setPassword("encodedPassword"); // 任意字符串，因为我们会模拟密码匹配

            when(userService.getUserbyUsername(username)).thenReturn(user);
            when(passwordEncoder.matches(eq(password),anyString())).thenReturn(true);
        } else {
            when(userService.getUserbyUsername(username)).thenReturn(null);
        }

        // 构建请求体
        var credentials = new HashMap<String, String>();
        credentials.put("username", username);
        credentials.put("password", password);
        String jsonContent = objectMapper.writeValueAsString(credentials);

        // 执行测试
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().is(expectedStatus));
    }

    @ParameterizedTest
    @CsvSource({"testuser2, test2@example.com, password123, 200, 登録成功",
            "testuser1, test1@example.com, password123, 400, ユーザーまたはメールは既に存在している",
            ", test@example.com, password123, 400, ユーザー名がありません",
            "testuser3, , password123, 400, メールアドレスがありません",
            "testuser4, test4@example.com, , 400, パスワードがありません"
    })
    void registerUser_Success(String username,
                              String email,
                              String password,
                              int code,
                              String message) throws Exception {
        var registration = new HashMap<String, String>();
        registration.put("username", username);
        registration.put("email", email);
        registration.put("password", password);

        // ✅ 关键修复：正确设置模拟行为
        if(Objects.equals(username, "testuser2")){
            when(userService.getUserbyUsername(username)).thenReturn(null);  // 确保用户名不存在
        }
        else {
            when(userService.getUserbyUsername(username)).thenReturn(new User());  // 确保用户名不存在
        }
        doReturn(true).when(userService).save(any(User.class));

        String jsonContent = objectMapper.writeValueAsString(registration);

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().is(code));
    }

    @Test
    void logout_Success() throws Exception {
        // 先注册用户
        String username = "logoutuser_" + System.currentTimeMillis();
        var registration = new HashMap<String, String>();
        registration.put("username", username);
        registration.put("email", "logout@example.com");
        registration.put("password", "password123");
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration)))
                .andExpect(status().isOk());

        // 登录获取token
        var credentials = new HashMap<String, String>();
        credentials.put("username", username);
        credentials.put("password", "password123");
        MvcResult loginResult = mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andReturn();

        // 获取token
        String token = getCookieValue(loginResult, "token");
        assertNotNull(token);

        // 执行登出
        mockMvc.perform(post("/user/logout")
                        .cookie(new org.springframework.mock.web.MockCookie("token", token)))
                .andExpect(status().isOk());
    }

    private String getCookieValue(MvcResult result, String name) {
        var cookies = result.getResponse().getCookies();
        if (cookies == null) return null;
        for (var cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}