package com.j_mikolajczyk.backend.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.j_mikolajczyk.backend.utils.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
public class TestCookieUtil {
    
    @Autowired
    private CookieUtil cookieUtil;

    @Test
    public void testCreateCookie() {
        Cookie cookie = cookieUtil.createCookie("testCookie", "testValue", 3600, "/");
        assertEquals("testCookie", cookie.getName());
    }

    @Test
    public void testExtractShortTermJwt() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie shortTermCookie = cookieUtil.createCookie("shortTermCookie", "shortTermValue", 3600, "/");
        when(request.getCookies()).thenReturn(new Cookie[] { shortTermCookie });
        String shortTermJwt = cookieUtil.extractShortTermJwt(request);
        assertEquals("shortTermValue", shortTermJwt);
    }

    @Test
    public void testExtractLongTermJwt() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie longTermCookie = cookieUtil.createCookie("longTermCookie", "longTermValue", 3600, "/");
        when(request.getCookies()).thenReturn(new Cookie[] { longTermCookie });
        String longTermJwt = cookieUtil.extractLongTermJwt(request);
        assertEquals("longTermValue", longTermJwt);
    }

}