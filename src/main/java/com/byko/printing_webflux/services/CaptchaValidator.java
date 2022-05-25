package com.byko.printing_webflux.services;

import com.byko.printing_webflux.model.RecaptchaResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CaptchaValidator {

    @Value("${captcha.secrect.key}")
    private String captchaSecretKey;

    public Mono<Boolean> captchaValidator(String captchaResponse){
        String url = "https://www.google.com/recaptcha/api/siteverify?secret="
                        + captchaSecretKey + "&response=" + captchaResponse;
        Mono<RecaptchaResult> resultMono = WebClient.create()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(RecaptchaResult.class);

        return resultMono.flatMap(s -> Mono.just(s.isSuccess()));
    }
}
