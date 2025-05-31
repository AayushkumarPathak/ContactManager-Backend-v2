package com.amz.scm.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> WHITELISTED_PATHS = Arrays.asList(
            "/api/v2/auth",
                "/v3/api-docs",
                "/v3/api-docs/swagger-config",
                "/swagger-ui",
                "/swagger-ui/",
                "/swagger-ui.html",
                "/swagger-resources",
                "/swagger-resources/",
                "/webjars",
                "/webjars/"
    );

    

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
         String path = request.getRequestURI();

        return path.startsWith("/v3/api-docs") ||
           path.startsWith("/swagger-ui") ||
           path.startsWith("/swagger-resources") ||
           path.startsWith("/webjars") ||
           path.startsWith("/api-docs") || 
           path.startsWith("/api/v2/auth");
    }

    private boolean isWhitelisted(String path) {
        return WHITELISTED_PATHS.stream().anyMatch(path::startsWith);
    }

    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // String path = request.getRequestURI();
        // System.out.println("Request URI: " + path);

        // if (isWhitelisted(path)) {
        //     filterChain.doFilter(request, response);
        //     return;
        // }

        // 1. get token

        String requestToken = request.getHeader("Authorization");
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            System.out.println(headerNames.nextElement());
        }
        // Bearer 2352523sdgsg

        System.out.println("Jwt Auth filter:  Req Token: " + requestToken);

        String username = null;

        String token = null;

        if (requestToken != null && requestToken.startsWith("Bearer ")) {

            token = requestToken.substring(7);

            try {
                username = this.jwtTokenHelper.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get Jwt token");
            } catch (ExpiredJwtException e) {
                System.out.println("Jwt token has expired");
            } catch (MalformedJwtException e) {
                System.out.println("invalid jwt");

            }

        } else {
            logger.error("Error at JWTAuthFilter: ", "Jwt token does not begin with Bearer");
            System.out.println("Jwt token does not begin with Bearer");
        }

        // once we get the token , now validate

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (this.jwtTokenHelper.validateToken(token, userDetails)) {
                // shi chal rha hai
                // authentication karna hai

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            } else {
                System.out.println("Invalid jwt token");
            }

        } else {
            System.out.println("username is null or context is not null");
        }

        filterChain.doFilter(request, response);
    }

}