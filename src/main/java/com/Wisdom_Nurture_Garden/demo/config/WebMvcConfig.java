//此代码从来没有被调用过，是废稿代码，用于存储前端传来的图片，供后续应用更新使用
package com.Wisdom_Nurture_Garden.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保路径以 / 结尾
        String fullPath = "file:" + uploadDir;
        if (!uploadDir.endsWith("/")) {
            fullPath = "file:" + uploadDir + "/";
        }

        System.out.println("静态资源映射路径: " + fullPath);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(fullPath);
    }
}
