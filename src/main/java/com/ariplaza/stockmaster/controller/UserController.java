package com.ariplaza.stockmaster.controller;

import com.ariplaza.stockmaster.service.IAuthService;
import com.ariplaza.stockmaster.service.IUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
/**
 * <p>
 * 库存系统用户表 前端控制器
 * </p>
 *
 * @author yyj
 * @since 2025-12-26
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    IAuthService authService;
    @Autowired
    IUserService userService;
    /**
     * 用户登录验证接口
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletResponse res) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // 验证用户
        if (authService.isValidUser(username, password)) {
            // 生成JWT token (包含过期时间)
            String token = authService.generateToken(username);
            //将token设置给Cookie
            authService.setCookie(token,res);
            // 返回成功响应
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "登录成功");
            return ResponseEntity.ok(response);
        } else {
            // 返回错误响应
            Map<String, Object> response = new HashMap<>();
            response.put("code", 401);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.status(401).body(response);
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registration) {
        String username = registration.get("username");
        String email = registration.get("email");
        String password = registration.get("password");

        if (userService.registerUser(username, email, password)) {
            return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "message", "注册成功，请登录"
            ));
        } else {
            return ResponseEntity.status(400).body(Map.of(
                    "code", 400,
                    "message", "用户名或邮箱已存在"
            ));
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (authService.sendPasswordResetEmail(email)) {
            return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "message", "重置邮件已发送至您的邮箱"
            ));
        } else {
            return ResponseEntity.status(404).body(Map.of(
                    "code", 404,
                    "message", "邮箱未注册"
            ));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (userService.resetPassword(token, newPassword)) {
            return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "message", "密码重置成功"
            ));
        } else {
            return ResponseEntity.status(400).body(Map.of(
                    "code", 400,
                    "message", "无效的重置链接"
            ));
        }
    }

    @PostMapping("/auto-login")
    public ResponseEntity<?> autoLogin(HttpServletRequest request,HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String token = authService.getToken(cookies);
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "code", 401,
                    "message", "未提供token"
            ));
        }

        // 2. 验证token
        if (!authService.validateToken(token)) {
            return ResponseEntity.status(402).body(Map.of(
                    "code", 402,
                    "message", "无效或过期的token"
            ));
        }
        String tokenNew=authService.updateToken(token);
        //将token设置给Cookie
        authService.setCookie(tokenNew,response);

        // 3. 返回成功响应
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "自动登录成功"
        ));
    }

}
