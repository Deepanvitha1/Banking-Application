package com.banking_portal.security;

import java.io.IOException;

import com.banking_portal.services.JwtService;
import com.banking_portal.services.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.context.ApplicationContext;
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);

    public JwtFilter(JwtService jwtService){
        this.jwtService=jwtService;
    }
    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        LOGGER.info("doFilterInternalsdjbcsdhjbc");
        String authHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String phone_number = null;
        String uid=null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            phone_number = jwtService.extractPhoneNumber(jwtToken);
            uid=jwtService.extractUserId(jwtToken);
        }
        if (phone_number != null && uid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(phone_number);
            if (jwtService.validateAccessToken(jwtToken, phone_number,uid)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
