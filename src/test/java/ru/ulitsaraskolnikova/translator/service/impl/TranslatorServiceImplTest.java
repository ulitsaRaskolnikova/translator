package ru.ulitsaraskolnikova.translator.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import ru.ulitsaraskolnikova.translator.client.TranslatorClient;
import ru.ulitsaraskolnikova.translator.model.Request;
import ru.ulitsaraskolnikova.translator.model.Response;
import ru.ulitsaraskolnikova.translator.repo.RepositoryConfiguration;
import ru.ulitsaraskolnikova.translator.repo.TranslationRepository;

@ExtendWith(MockitoExtension.class)
public class TranslatorServiceImplTest {
    private final String ip = "0:0:0:0:0:0:0:1";
    @InjectMocks
    private TranslatorServiceImpl translatorService;
    @Mock
    private TranslatorClient client;
    @Mock
    private RepositoryConfiguration repositoryConfiguration;
    @Mock
    private TranslationRepository repository;
    @Test
    void service_regularText() {
        Request request = new Request("en", "ru", "I love you");
        Response response = new Response("Я любовь ты");
        HttpStatusCode goodStatusCode = HttpStatusCode.valueOf(200);
        ResponseEntity<Response> expectedResponseEntity = new ResponseEntity<>(response, goodStatusCode);
        Mockito.when(client.translate(new Request("en", "ru", "I")))
                .thenReturn(new ResponseEntity<>(new Response("Я"), goodStatusCode));
        Mockito.when(client.translate(new Request("en", "ru", "love")))
                .thenReturn(new ResponseEntity<>(new Response("любовь"), goodStatusCode));
        Mockito.when(client.translate(new Request("en", "ru", "you")))
                .thenReturn(new ResponseEntity<>(new Response("ты"), goodStatusCode));
        ResponseEntity<Response> responseEntity = translatorService.service(request, ip);
        Assertions.assertEquals(expectedResponseEntity, responseEntity);
    }
    @Test
    void service_emptyText() {
        Request request = new Request("ru", "en", "");
        Response response = new Response("texts are empty");
        ResponseEntity<Response> expectedResponseEntity = new ResponseEntity<>(response, HttpStatusCode.valueOf(400));
        Mockito.when(client.translate(request)).thenReturn(expectedResponseEntity);
        ResponseEntity<Response> responseEntity = translatorService.service(request, ip);
        Assertions.assertEquals(expectedResponseEntity, responseEntity);
    }
    @Test
    void service_onlySpaces() {
        Request request = new Request("ru", "en", "     ");
        Response response = new Response("texts are empty");
        ResponseEntity<Response> expectedResponseEntity = new ResponseEntity<>(response, HttpStatusCode.valueOf(400));
        Mockito.when(client.translate(new Request("ru", "en", "")))
                .thenReturn(expectedResponseEntity);
        ResponseEntity<Response> responseEntity = translatorService.service(request, ip);
        Assertions.assertEquals(expectedResponseEntity, responseEntity);
    }
    @Test
    void service_spacesOnSides() {
        Request request = new Request("en", "ru", " I love you ");
        Response response = new Response("Я любовь ты");
        HttpStatusCode goodStatusCode = HttpStatusCode.valueOf(200);
        ResponseEntity<Response> expectedResponseEntity = new ResponseEntity<>(response, goodStatusCode);
        Mockito.when(client.translate(new Request("en", "ru", "I")))
                .thenReturn(new ResponseEntity<>(new Response("Я"), goodStatusCode));
        Mockito.when(client.translate(new Request("en", "ru", "love")))
                .thenReturn(new ResponseEntity<>(new Response("любовь"), goodStatusCode));
        Mockito.when(client.translate(new Request("en", "ru", "you")))
                .thenReturn(new ResponseEntity<>(new Response("ты"), goodStatusCode));
        ResponseEntity<Response> responseEntity = translatorService.service(request, ip);
        Assertions.assertEquals(expectedResponseEntity, responseEntity);
    }
    @Test
    void service_wrongSourceLanguage() {
        Request request = new Request("end", "ru", "I love you");
        Response response = new Response("unsupported source_language_code: end");
        HttpStatusCode badStatusCode = HttpStatusCode.valueOf(400);
        ResponseEntity<Response> expectedResponseEntity = new ResponseEntity<>(response, badStatusCode);
        Mockito.when(client.translate(new Request("end", "ru", "I")))
                .thenReturn(new ResponseEntity<>(response, badStatusCode));
        Mockito.when(client.translate(new Request("end", "ru", "love")))
                .thenReturn(new ResponseEntity<>(response, badStatusCode));
        Mockito.when(client.translate(new Request("end", "ru", "you")))
                .thenReturn(new ResponseEntity<>(response, badStatusCode));
        ResponseEntity<Response> responseEntity = translatorService.service(request, ip);
        Assertions.assertEquals(expectedResponseEntity, responseEntity);
    }
    @Test
    void service_wrongTargetLanguage() {
        Request request = new Request("en", "rus", "I love you");
        Response response = new Response("unsupported source_language_code: rus");
        HttpStatusCode badStatusCode = HttpStatusCode.valueOf(400);
        ResponseEntity<Response> expectedResponseEntity = new ResponseEntity<>(response, badStatusCode);
        Mockito.when(client.translate(new Request("en", "rus", "I")))
                .thenReturn(new ResponseEntity<>(response, badStatusCode));
        Mockito.when(client.translate(new Request("en", "rus", "love")))
                .thenReturn(new ResponseEntity<>(response, badStatusCode));
        Mockito.when(client.translate(new Request("en", "rus", "you")))
                .thenReturn(new ResponseEntity<>(response, badStatusCode));
        ResponseEntity<Response> responseEntity = translatorService.service(request, ip);
        Assertions.assertEquals(expectedResponseEntity, responseEntity);
    }
}
