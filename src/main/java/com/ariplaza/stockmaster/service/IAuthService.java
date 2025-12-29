package com.ariplaza.stockmaster.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {
    boolean isValidUser(String username, String password);

    String generateToken(String username);

    //String getTokenFromHeader(HttpServletRequest request);

    boolean validateToken(String token);

    boolean sendPasswordResetEmail(String email);
    String getUsernameFromToken(String token);

    void setCookie(String token, HttpServletResponse response);

    String getToken(Cookie[] cookies);

    String updateToken(String token);
}
