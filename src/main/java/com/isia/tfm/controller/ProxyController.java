package com.isia.tfm.controller;

import com.isia.tfm.api.UserManagementApi;
import com.isia.tfm.model.CreateUser201Response;
import com.isia.tfm.model.GetUsersByGender200Response;
import com.isia.tfm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Proxy controller
 */
@RestController
@RequestMapping({"/auth-rest-application/v1"})
public class ProxyController implements UserManagementApi {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public ResponseEntity<CreateUser201Response> createUser(User user) {
        CreateUser201Response response = webClientBuilder.build()
                .post()
                .uri("http://tfm-rest-container:8080/tfm-rest-application/v1/createUser")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(CreateUser201Response.class)
                .block();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<GetUsersByGender200Response> getUsersByGender(String gender) {
        WebClient webClient = webClientBuilder
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                        .build())
                .build();

        GetUsersByGender200Response response = webClient
                .get()
                .uri("http://tfm-rest-container:8080/tfm-rest-application/v1/getUsersByGender?gender=" + gender)
                .retrieve()
                .bodyToMono(GetUsersByGender200Response.class)
                .block();

        if (response.getData().isEmpty() || response.getData() == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

}
