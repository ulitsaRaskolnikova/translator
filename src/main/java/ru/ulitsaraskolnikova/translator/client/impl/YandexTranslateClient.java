package ru.ulitsaraskolnikova.translator.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.ulitsaraskolnikova.translator.client.TranslatorClient;
import ru.ulitsaraskolnikova.translator.model.Request;
import ru.ulitsaraskolnikova.translator.model.Response;
import ru.ulitsaraskolnikova.translator.model.YandexTranslateRequest;
import ru.ulitsaraskolnikova.translator.model.YandexTranslateResponse;


@Component
@RequiredArgsConstructor
@Slf4j
public class YandexTranslateClient implements TranslatorClient {
    @Value("${yandex.cloud.url}")
    private String URL;
    @Value("${yandex.cloud.token}")
    private String TOKEN;
    @Value("${yandex.cloud.folder.id}")
    private String FOLDER_ID;
    private final ObjectMapper objectMapper;
    @Override
    public ResponseEntity<Response> translate(Request request) {
        var restTemplate = new RestTemplate();
        HttpEntity<String> yandexHttpEntity = createYandexHttpEntity(request);

        ResponseEntity<String> yandexResponseEntity;
        Response response;
        ResponseEntity<Response> responseEntity;
        try {
            yandexResponseEntity = restTemplate
                    .postForEntity(URL, yandexHttpEntity, String.class);
            response = new Response(getTranslationFromJson(yandexResponseEntity.getBody()));
            responseEntity = new ResponseEntity<>(response, yandexResponseEntity.getStatusCode());
        } catch (HttpClientErrorException e) {
            log.error(e.toString());
            String message;
            if (e.getStatusCode().value() == 400) {
                message = getMessageFromJson(e.getResponseBodyAsString());
            } else {
                message = e.getStatusText();
            }
            response = new Response(message);
            responseEntity = new ResponseEntity<>(response, e.getStatusCode());
        }
        return responseEntity;
    }
    private HttpEntity<String> createYandexHttpEntity(Request request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + TOKEN);
        return new HttpEntity<>(
                createYandexJsonRequest(request),
                headers);
    }
    private String getMessageFromJson(String json) {
        try {
            var jsonNode = objectMapper.readTree(json);
            return jsonNode.get("message").asText();
        } catch (JsonProcessingException e) {
            log.error(e.toString());
            return e.getMessage();
        }
    }
    private String getTranslationFromJson(String json) {
        YandexTranslateResponse response;
        try {
            response = objectMapper.readValue(json, YandexTranslateResponse.class);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
        var sb = new StringBuilder();
        for (var translation : response.translations()) {
            sb.append(translation.text());
            sb.append(" ");
        }
        return sb.toString();
    }
    private String createYandexJsonRequest(Request userRequest) {
        try {
            return objectMapper.writeValueAsString(new YandexTranslateRequest(
                    userRequest.sourceLang(),
                    userRequest.targetLang(),
                    "PLAIN_TEXT",
                    new String[]{userRequest.text()},
                    FOLDER_ID
            ));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }
}
