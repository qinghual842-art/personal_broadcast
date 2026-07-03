package com.blog.security;

import com.blog.util.RedisKeyUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class UserJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                JwtTokenProvider.TokenInfo info = jwtTokenProvider.parseToken(token);
                if ("user".equals(info.type())) {
                    // Verify token exists in Redis (allows server-side revocation)
                    String storedToken = redisTemplate.opsForValue()
                            .get(RedisKeyUtil.userTokenKey(info.id()));
                    if (token.equals(storedToken)) {
                        UserContext.setUserId(info.id());
                    }
                }
            }
        } catch (Exception e) {
            log.debug("User auth filter: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
        UserContext.clear();
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
