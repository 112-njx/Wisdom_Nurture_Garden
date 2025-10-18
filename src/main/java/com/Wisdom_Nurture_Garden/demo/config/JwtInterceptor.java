package com.Wisdom_Nurture_Garden.demo.config;

import com.Wisdom_Nurture_Garden.demo.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 允许 OPTIONS 请求（解决跨域预检）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return false;
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = JwtUtil.parseToken(token);

            // 把用户信息存入 request 供 Controller 使用
            request.setAttribute("userName", claims.getSubject());
            request.setAttribute("userRole", claims.get("role"));
            // ⚠️ 建议你在生成 token 时也加上 userId
            request.setAttribute("userId", claims.get("id"));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return false;
        }

        return true;
    }
}
