package com.eyedia.eyedia.config;

import com.eyedia.eyedia.converter.CaseInsensitiveEnumConverterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final CaseInsensitiveEnumConverterFactory enumFactory;
    public WebConfig(CaseInsensitiveEnumConverterFactory enumFactory) {
        this.enumFactory = enumFactory;
    }
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(enumFactory);
    }
}
