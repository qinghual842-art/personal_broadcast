package com.blog.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Integer.MAX_VALUE)
public class SpaFallbackFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Only handle non-API requests that could be SPA routes
        if (!path.startsWith("/api/") && !path.startsWith("/uploads/")) {
            // Let static file requests through (has file extension)
            String filename = path.substring(path.lastIndexOf('/') + 1);
            if (!filename.contains(".")) {
                request.getRequestDispatcher("/index.html").forward(request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
