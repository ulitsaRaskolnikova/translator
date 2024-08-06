package ru.ulitsaraskolnikova.translator.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.ulitsaraskolnikova.translator.client.TranslatorClient;
import ru.ulitsaraskolnikova.translator.model.Request;
import ru.ulitsaraskolnikova.translator.model.Response;


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
        } catch (Exception e) {
            log.error(e.toString());
            return null;
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
        var jsonObject = new JSONObject(json);
        return jsonObject.getString("message");
    }
    private String getTranslationFromJson(String json) {
        var jsonObject = new JSONObject(json);
        var jsonArr = jsonObject.getJSONArray("translations");
        var sb = new StringBuilder();
        for (int i = 0; i < jsonArr.length(); i++) {
            sb.append(jsonArr.getJSONObject(i).getString("text"));
            sb.append(" ");
        }
        return sb.toString();
    }
    private String createYandexJsonRequest(Request userRequest) {
        var translationJson = new JSONObject();
        translationJson.put("sourceLanguageCode", userRequest.sourceLang());
        translationJson.put("targetLanguageCode", userRequest.targetLang());
        translationJson.put("format", "PLAIN_TEXT");
        translationJson.put("texts", userRequest.text().split(" +"));
        translationJson.put("folderId", FOLDER_ID);
        return translationJson.toString();
    }
}
