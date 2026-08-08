package com.pb7technologies.razorpay.operations.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class WebhookClientConfig {

    @Bean
    public RestClient webhookRestClient(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // Time to connect to let say merchant , first handshake within this time
        factory.setReadTimeout(5000); // process webhook and return the 2XX response code

        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }
}
