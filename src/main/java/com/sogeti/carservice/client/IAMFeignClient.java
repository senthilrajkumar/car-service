package com.sogeti.carservice.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Component
@FeignClient(name = "iam-service", url = "${iam.service.url}") // Specify the Feign client name and the target service URL
@Qualifier("iamFeignClient")
public interface IAMFeignClient {
    @PostMapping("/api/accounts/token-validation") // Define the endpoint path
    ResponseEntity<String> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader);
}
