package com.Wisdom_Nurture_Garden.demo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.Map;

@Component
public class HttpClientUtil {

    private static final RestTemplate restTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        configureGlobalSSL();
        restTemplate = new RestTemplate();
    }

    public static Map<String, Object> getForMap(String url) {
        System.out.println("🔗 HttpClientUtil 开始请求: " + url);
        try {
            // 直接获取字符串响应，然后手动解析 JSON
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("HttpClientUtil 原始响应: " + response);

            if (response == null || response.trim().isEmpty()) {
                System.err.println("响应为空");
                return null;
            }

            // 手动解析 JSON
            Map<String, Object> result = objectMapper.readValue(response, Map.class);
            System.out.println("HttpClientUtil 解析后的响应: " + result);
            return result;

        } catch (Exception e) {
            System.err.println( "HttpClientUtil请求失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static void configureGlobalSSL() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        @Override public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        @Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            System.out.println("SSL 全局配置完成");
        } catch (Exception e) {
            System.err.println("SSL 全局配置失败: " + e.getMessage());
        }
    }
}