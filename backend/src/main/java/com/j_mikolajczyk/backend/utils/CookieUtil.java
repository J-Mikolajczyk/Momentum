package com.j_mikolajczyk.backend.utils;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CookieUtil {

    public Cookie createCookie(String name, String value, int maxAge, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setDomain("training-momentum.com");
        cookie.setAttribute("SameSite", "Lax");
        return cookie;
    }

    public String extractShortTermJwt(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("shortTermCookie".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public String extractLongTermJwt(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("longTermCookie".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
