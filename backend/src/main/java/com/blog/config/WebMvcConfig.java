package com.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${blog.upload.path:./uploads}")
    private String uploadPath;

    @Value("${blog.frontend.dist-path:../frontend/dist}")
    private String frontendDistPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");

        // Serve frontend static assets (JS, CSS, images, etc.) from dist directory
        registry.addResourceHandler("/assets/**", "/favicon.svg", "/icons.svg")
                .addResourceLocations("file:" + frontendDistPath + "/");

        // Serve index.html from dist for the root path and SPA fallback
        registry.addResourceHandler("/index.html")
                .addResourceLocations("file:" + frontendDistPath + "/");
    }
}
