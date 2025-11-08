package com.Wisdom_Nurture_Garden.demo.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.util.Map;

public class HttpClientUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static RestTemplate createUnsafeRestTemplate() {
        try {
            // 创建信任所有证书的 SSLContext
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, (chain, authType) -> true)
                    .build();

            // 创建 SSL Socket Factory
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    NoopHostnameVerifier.INSTANCE
            );

            // 创建连接管理器并设置 SSL Socket Factory
            PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();

            // 构建 HttpClient
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build();

            // 使用原有的超时设置
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setHttpClient(httpClient);
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(5000);

            return new RestTemplate(factory);
        } catch (Exception e) {
            e.printStackTrace();
            return new RestTemplate();
        }
    }

    public static Map<String, Object> getForMap(String url) {
        try {
            RestTemplate restTemplate = createUnsafeRestTemplate();
            String resp = restTemplate.getForObject(url, String.class);
            if (resp == null) return null;
            return objectMapper.readValue(resp, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Object> postForMap(String url, Object body) {
        try {
            RestTemplate restTemplate = createUnsafeRestTemplate();
            String resp = restTemplate.postForObject(url, body, String.class);
            if (resp == null) return null;
            return objectMapper.readValue(resp, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}