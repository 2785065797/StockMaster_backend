package com.ariplaza.stockmaster.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface IAuthService {

    String generateToken(String username);

    boolean validateToken(String token);

    boolean sendPasswordResetEmail(String email);
    String getUsernameFromToken(String token);

    boolean setCookie(String token, HttpServletResponse response);

    String getTokenfromCookie(Cookie[] cookies);

    String updateToken(Cookie[] token);

    Boolean deleteToken(Cookie[] cookies);

    boolean isValidUser(Map<String, String> credentials);
}
