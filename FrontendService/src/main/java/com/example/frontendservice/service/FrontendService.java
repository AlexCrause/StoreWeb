package com.example.frontendservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FrontendService {

    private final RestTemplate restTemplate;

    @Autowired
    public FrontendService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAuthServiceData() {
        return restTemplate.getForObject("http://localhost:8765/serviceAuth/someEndpoint", String.class);
    }

    public String getFrontendData() {
        return restTemplate.getForObject("http://localhost:8765/serviceFrontend/anotherEndpoint", String.class);
    }
}
