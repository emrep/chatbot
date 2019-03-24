package com.udemy.chatbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@EnableScheduling
@Configuration
@EnableAsync
public class SpringConfig implements AsyncConfigurer {

    @Value("${api.request.proxy.url}")
    private String proxyUrl;

    @Value("${api.request.proxy.port}")
    private Integer proxyPort;

    @Bean
    public RestTemplate restTemplate() {
        if(proxyUrl.isEmpty()) {
            return new RestTemplate();
        } else {
            return getRestTemplateWithProxy();
        }
    }

    private RestTemplate getRestTemplateWithProxy() {
        SimpleClientHttpRequestFactory clientHttpReq = new SimpleClientHttpRequestFactory();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl, proxyPort));
        clientHttpReq.setProxy(proxy);
        return new RestTemplate(clientHttpReq);
    }
}
