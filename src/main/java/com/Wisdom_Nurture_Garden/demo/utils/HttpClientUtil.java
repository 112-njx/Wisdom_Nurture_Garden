package com.Wisdom_Nurture_Garden.demo.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class HttpClientUtil {
    private static final RestTemplate restTemplate = new RestTemplate();

    public static Map<String, Object> getForMap(String url) {
        return restTemplate.getForObject(url, Map.class);
    }
}
