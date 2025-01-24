package com.seeyoungryu.connecti.config.filter;

import com.seeyoungryu.connecti.service.util.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            log.error("Invalid Authorization header. Header is null or doesn't start with Bearer");
            response.setStatus(401);
            response.getWriter().write("Invalid or missing token");
            return;
        }

        try {
            final String token = header.substring(7).trim();

            if (JwtTokenUtils.isTokenExpired(token, secretKey)) {
                log.error("JWT token is expired");
                response.setStatus(401);
                response.getWriter().write("Expired token");
                return;
            }

            String userName = JwtTokenUtils.getUsernameFromToken(token, secretKey);

            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            log.error("Error while processing JWT token", e);
            response.setStatus(401);
            response.getWriter().write("Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
