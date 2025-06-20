package com.j_mikolajczyk.backend.filters;

import com.j_mikolajczyk.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


class TestJwtAuthenticationFilter {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Claims claims;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new JwtAuthenticationFilter(jwtUtil);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testNoCookies() throws Exception {
        when(request.getCookies()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void testNoShortTermCookie() throws Exception {
        Cookie[] cookies = { new Cookie("otherCookie", "value") };
        when(request.getCookies()).thenReturn(cookies);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void testValidShortTermCookie() throws Exception {
        Cookie[] cookies = { new Cookie("shortTermCookie", "token123") };
        when(request.getCookies()).thenReturn(cookies);
        when(jwtUtil.validateToken("token123")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("userId123");

        filter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth instanceof UsernamePasswordAuthenticationToken);
        assertEquals("userId123", auth.getPrincipal());
        verify(jwtUtil).validateToken("token123");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testInvalidShortTermCookie() throws Exception {
        Cookie[] cookies = { new Cookie("shortTermCookie", "badtoken") };
        when(request.getCookies()).thenReturn(cookies);
        when(jwtUtil.validateToken("badtoken")).thenThrow(new RuntimeException("Invalid token"));

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtil).validateToken("badtoken");
        verify(filterChain).doFilter(request, response);
    }
}