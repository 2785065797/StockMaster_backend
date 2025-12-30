package com.ariplaza.stockmaster.controller;

import com.ariplaza.stockmaster.service.IAuthService;
import com.ariplaza.stockmaster.util.ResponseUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    IAuthService authService;
    @GetMapping("/status")
    public ResponseEntity<?> validateToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String token = authService.getTokenfromCookie(cookies);
        if(token!=null){
            if(authService.validateToken(token)){
               return ResponseUtil.success("トークンが存在");
            }
        }
        return ResponseUtil.error(405,"トークンが存在していません");
    }
}