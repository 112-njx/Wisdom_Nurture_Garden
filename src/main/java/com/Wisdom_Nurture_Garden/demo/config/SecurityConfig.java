package com.Wisdom_Nurture_Garden.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 完全关闭 CSRF，避免 POST 被拦截
                .csrf(csrf -> csrf.disable())

                // 放行登录和注册接口
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/login", "/api/users/register").permitAll()
                        .anyRequest().permitAll() // （调试阶段可先放行所有接口）
                )

                // 禁用表单登录和 HTTP Basic（我们用 JWT）
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}


/* 数据库语句存放在此
CREATE TABLE binding (
    id INT AUTO_INCREMENT PRIMARY KEY,
    child_id INT NOT NULL,
    elder_id INT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_child FOREIGN KEY (child_id) REFERENCES users(id),
//确定外键绑定
    CONSTRAINT fk_elder FOREIGN KEY (elder_id) REFERENCES users(id)
//确定外键绑定
);

ALTER TABLE binding ADD UNIQUE KEY unique_bind (child_id, elder_id);
//绑定关系唯一

CREATE TABLE checkin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    elder_id INT NOT NULL,
    mood TINYINT NOT NULL,
    sleep_quality TINYINT NOT NULL,
    appetite TINYINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_checkin_elder FOREIGN KEY (elder_id) REFERENCES users(id)
);

//增加性别列
alter table users add column gender varchar(10) after name;
 */

//Bearer <上一步获取的token> eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoxLCJpZCI6Niwic3ViIjoiY2hpbGQwMDEiLCJpYXQiOjE3NjA3NjQ5MDUsImV4cCI6MTc2MzM1NjkwNX0.Y_i4b5pNbBUxu79ZQ-meM4CsgfNl-4pVucEsBsFLwAE



//CREATE TABLE help_request (
//    id INT AUTO_INCREMENT PRIMARY KEY,
//    elder_id INT NOT NULL,
//    child_id INT NOT NULL,
//    message VARCHAR(255),
//    create_time DATETIME,
//    FOREIGN KEY (elder_id) REFERENCES users(id),
//    FOREIGN KEY (child_id) REFERENCES users(id)
//);



//用户李四token:eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoyLCJpZCI6OSwic3ViIjoi5p2O5ZubIiwiaWF0IjoxNzYxMzc0Mjk4LCJleHAiOjE3NjM5NjYyOTh9.HGpZQ9fmXaz4jMIhUhcJBlBjba4R0oDve2Zr_2m_MWk
//用户张三token:eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoxLCJpZCI6OCwic3ViIjoi5byg5LiJIiwiaWF0IjoxNzYxMzczODg4LCJleHAiOjE3NjM5NjU4ODh9.DCurvPGS8VnBbysoF5reazPVEz-WajKLDjfhBoFy5xQ