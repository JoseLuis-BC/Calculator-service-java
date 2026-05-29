package com.raven.security;

import com.raven.entities.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthenticationEntryPoint authenticationEntryPoint;


    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.startsWith("/auth")) {
            chain.doFilter(req, res);
            return;
        }

        try {
            String header = req.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                chain.doFilter(req, res);
                return;
            }

            String token = header.substring(7);

            if (!token.contains(".")) {
                throw new InsufficientAuthenticationException("Token mal formado");
            }

            var claims = jwtService.extractClaims(token);

            String username = claims.getSubject();
            String userId = claims.get("userId", String.class);

            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                User user = new User();
                user.setUsername(username);
                user.setId(UUID.fromString(userId));

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, List.of());

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            chain.doFilter(req, res);

        } catch (Exception e) {

            SecurityContextHolder.clearContext();

            authenticationEntryPoint.commence(req, res, new InsufficientAuthenticationException("Invalid access token"));
        }
    }

}
