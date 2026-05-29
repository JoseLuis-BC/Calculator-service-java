package com.raven.security;

import com.raven.entities.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Mock
    private FilterChain chain;

    private JwtAuthFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthFilter(jwtService, authenticationEntryPoint);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        clearInvocations(jwtService, authenticationEntryPoint, chain);
    }

    @Test
    void doFilter_skipsSwaggerAndDocsAndAuthPaths() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setServletPath("/swagger-ui/index.html");

        filter.doFilter(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);
        verifyNoInteractions(jwtService, authenticationEntryPoint);
    }

    @Test
    void doFilter_noAuthorizationHeader_callsChain() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setServletPath("/api/secure");

        filter.doFilter(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);
        verifyNoInteractions(jwtService, authenticationEntryPoint);
    }

    @Test
    void doFilter_headerNotBearer_callsChain() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setServletPath("/api/secure");
        req.addHeader("Authorization", "Token abcdef");

        filter.doFilter(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);
        verifyNoInteractions(jwtService, authenticationEntryPoint);
    }

    @Test
    void doFilter_malformedToken_invokesAuthenticationEntryPoint() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setServletPath("/api/secure");
        req.addHeader("Authorization", "Bearer malformedtoken"); // no '.'
        filter.doFilter(req, res, chain);
        verify(chain, never()).doFilter(any(), any());

        ArgumentCaptor<HttpServletRequest> reqCap = ArgumentCaptor.forClass(HttpServletRequest.class);
        ArgumentCaptor<HttpServletResponse> resCap = ArgumentCaptor.forClass(HttpServletResponse.class);
        ArgumentCaptor<org.springframework.security.core.AuthenticationException> exCap =
                ArgumentCaptor.forClass(org.springframework.security.core.AuthenticationException.class);

        verify(authenticationEntryPoint, times(1)).commence(reqCap.capture(), resCap.capture(), exCap.capture());

        assertTrue(exCap.getValue() instanceof InsufficientAuthenticationException);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_validToken_setsSecurityContextAndContinues() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setServletPath("/api/secure");
        String token = "aaa.bbb.ccc";
        req.addHeader("Authorization", "Bearer " + token);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("john.doe");
        UUID userId = UUID.randomUUID();
        when(claims.get("userId", String.class)).thenReturn(userId.toString());

        when(jwtService.extractClaims(token)).thenReturn(claims);

        filter.doFilter(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "Authentication should be set in SecurityContext");
        assertTrue(auth.getPrincipal() instanceof User);
        User principal = (User) auth.getPrincipal();
        assertEquals("john.doe", principal.getUsername());
        assertEquals(userId, principal.getId());
    }
    @Test
    void doFilter_skipsV3ApiDocsPath() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setServletPath("/v3/api-docs/swagger-config");

        filter.doFilter(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);
        verifyNoInteractions(jwtService, authenticationEntryPoint);
    }

    @Test
    void doFilter_skipsAuthPath() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setServletPath("/auth/login");

        filter.doFilter(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);
        verifyNoInteractions(jwtService, authenticationEntryPoint);
    }

    @Test
    void doFilter_validToken_butUsernameIsNull_doesNotSetAuthentication() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setServletPath("/api/secure");
        String token = "aaa.bbb.ccc";
        req.addHeader("Authorization", "Bearer " + token);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(null); // username is null
        when(claims.get("userId", String.class)).thenReturn(UUID.randomUUID().toString());

        when(jwtService.extractClaims(token)).thenReturn(claims);

        filter.doFilter(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth, "Authentication should NOT be set when username is null");
    }

    @Test
    void doFilter_existingAuthentication_notOverwritten() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setServletPath("/api/secure");
        String token = "aaa.bbb.ccc";
        req.addHeader("Authorization", "Bearer " + token);

        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setUsername("existing.user");
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken existingAuth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        existingUser, null, java.util.List.of()
                );
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("new.user");
        UUID newUserId = UUID.randomUUID();
        when(claims.get("userId", String.class)).thenReturn(newUserId.toString());

        when(jwtService.extractClaims(token)).thenReturn(claims);

        filter.doFilter(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        User principal = (User) auth.getPrincipal();
        assertEquals("existing.user", principal.getUsername(), "Existing authentication should not be overwritten");
    }
}