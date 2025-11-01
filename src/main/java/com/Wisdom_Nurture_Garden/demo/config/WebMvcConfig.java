package com.Wisdom_Nurture_Garden.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /uploads/** 映射到本地文件目录
        registry.addResourceHandler("/Wisdom_Nurture_Garden/demo/demo/Pictures/**")
                .addResourceLocations("file:D:/Wisdom_Nurture_Garden/demo/demo/Pictures");
    }
}
