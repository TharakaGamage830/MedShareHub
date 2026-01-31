package com.medshare.hub.config;

import com.medshare.hub.security.AbacInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AbacInterceptor abacInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(abacInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**");
    }
}
