package com.ariplaza.stockmaster.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface IAuthService {

    String generateToken(String username,Long userId);

    boolean validateToken(String token);

    boolean sendPasswordResetEmail(String email);
    String getUsernameFromToken(String token);

    boolean setCookie(String token, HttpServletResponse response);

    String getTokenfromCookie(Cookie[] cookies);

    Long getUserIdFromToken(String token);

    String updateToken(Cookie[] token);

    Boolean deleteToken(Cookie[] cookies);

    Long isValidAndGetUserId(Map<String, String> credentials);
}
