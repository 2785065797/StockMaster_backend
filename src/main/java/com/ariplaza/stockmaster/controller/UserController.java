package com.ariplaza.stockmaster.controller;

import com.ariplaza.stockmaster.service.IAuthService;
import com.ariplaza.stockmaster.service.IUserService;
import com.ariplaza.stockmaster.util.ResponseUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        Long userId=authService.isValidAndGetUserId(credentials);
        // 验证用户
        if (userId==null){
            return ResponseUtil.error(400,"ユーザー名またはパスワードが間違いました");
        }
        // 生成JWT token (包含过期时间)
        String token = authService.generateToken(username,userId);
        //将token设置给Cookie
        if(authService.setCookie(token,res)){
            // 返回成功响应
            return ResponseUtil.success("ログイン成功");
        } else {
            // 返回错误响应
            return ResponseUtil.error(400,"ユーザー名またはパスワードが間違いました");
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registration) {
        if (userService.registerUser(registration)) {
            return ResponseUtil.success("登録成功、ログインしてください");
        } else {
            return ResponseUtil.error(400,"ユーザーまたはメールは既に存在している");
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (authService.sendPasswordResetEmail(email)) {
            return ResponseUtil.success("リセットメールは送信されました");
        } else {
            return ResponseUtil.error(402,"メールは登録されません");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (userService.resetPassword(token, newPassword)) {
            return ResponseUtil.success("パスワードのリセットに成功しました");
        } else {
            return ResponseUtil.error(400,"無効なリセットリンク");
        }
    }

    @PostMapping("/auto-login")
    public ResponseEntity<?> autoLogin(HttpServletRequest request,HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String tokenNew=authService.updateToken(cookies);
        //将token设置给Cookie
        if(authService.setCookie(tokenNew,response)){
            // 返回成功响应
            return ResponseUtil.success("自動ログイン成功");
        } else {
            return ResponseUtil.error(401, "無効または期限切れのトークン");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> exitLogin(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(authService.deleteToken(cookies)){
            return ResponseUtil.success("ログアウト成功");
        }else {
            return ResponseUtil.error(401,"無効または期限切れのトークン");
        }
    }


}
