package ru.ulitsaraskolnikova.translator.client;

import org.springframework.http.ResponseEntity;
import ru.ulitsaraskolnikova.translator.model.Request;
import ru.ulitsaraskolnikova.translator.model.Response;

public interface TranslatorClient {
    ResponseEntity<Response> translate(Request request);
}
