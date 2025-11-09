package com.Wisdom_Nurture_Garden.demo.utils;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
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

    static {
        restTemplate = createUnsafeRestTemplate();
    }

    public static Map<String, Object> getForMap(String url) {
        try {
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            System.err.println("HTTP 请求失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static RestTemplate createUnsafeRestTemplate() {
        try {
            // 方法1：使用 SimpleClientHttpRequestFactory 并配置 SSL
            return createWithSimpleClientHttpRequestFactory();
        } catch (Exception e) {
            System.err.println("创建自定义 RestTemplate 失败，使用默认配置: " + e.getMessage());
            e.printStackTrace();
            return new RestTemplate();
        }
    }

    private static RestTemplate createWithSimpleClientHttpRequestFactory() throws Exception {
        // 创建信任所有证书的 SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                    @Override
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }
        }, new java.security.SecureRandom());

        // 设置默认的 SSL Socket Factory
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        // 设置不验证主机名
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(10000);

        return new RestTemplate(factory);
    }
}