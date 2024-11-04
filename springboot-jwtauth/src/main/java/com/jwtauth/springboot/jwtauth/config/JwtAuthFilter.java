package com.jwtauth.springboot.jwtauth.config;

import com.jwtauth.springboot.jwtauth.service.JwtUtils;
import com.jwtauth.springboot.jwtauth.service.OurUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private JwtUtils jwtUtils;
    private OurUserDetailService ourUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token from Authorization header
        String jwtToken = authHeader.substring(7);
        String username = jwtUtils.extractUsername(jwtToken);

        // Check if username is not null and the SecurityContext has no authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details from our custom service
            UserDetails userDetails = ourUserDetailService.loadUserByUsername(username);

            // Validate the JWT token
            if (jwtUtils.isTokenValid(jwtToken, userDetails)) {
                // Create the authentication token
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Set authentication in the SecurityContext
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(usernamePasswordAuthenticationToken);
                SecurityContextHolder.setContext(securityContext);
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);


    }
}
