package com.blog.config;

import com.blog.security.JwtAuthenticationFilter;
import com.blog.security.JwtTokenProvider;
import com.blog.security.UserJwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public - articles, categories, tags, site
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/articles/**",
                                "/api/v1/categories/**",
                                "/api/v1/tags/**",
                                "/api/v1/site/**",
                                "/api/v1/agents",
                                "/api/v1/agents/*",
                                "/api/v1/health"
                        ).permitAll()
                        // Public - comments (read/write, auth optional)
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/*/comments").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/articles/*/comments").permitAll()
                        // Public - article likes toggle (auth checked in controller)
                        .requestMatchers(HttpMethod.POST, "/api/v1/articles/*/like").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/*/like").permitAll()
                        // Public - agent chat
                        .requestMatchers(HttpMethod.POST, "/api/v1/agents/*/chat").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/agents/*/chat/*").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/agents/*/chat/*").permitAll()
                        // Public - admin login
                        .requestMatchers("/api/v1/admin/auth/login").permitAll()
                        // Public - user auth
                        .requestMatchers("/api/v1/user/register").permitAll()
                        .requestMatchers("/api/v1/user/login").permitAll()
                        // User endpoint routes (auth optional, checked in controller)
                        .requestMatchers("/api/v1/user/**").permitAll()
                        // Admin - requires admin JWT
                        .requestMatchers("/api/v1/admin/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(userJwtFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserJwtAuthenticationFilter userJwtFilter() {
        return new UserJwtAuthenticationFilter(jwtTokenProvider, redisTemplate);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
