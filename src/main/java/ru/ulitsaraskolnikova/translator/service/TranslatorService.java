package ru.ulitsaraskolnikova.translator.service;

import org.springframework.http.ResponseEntity;
import ru.ulitsaraskolnikova.translator.model.Request;
import ru.ulitsaraskolnikova.translator.model.Response;

public interface TranslatorService {
    ResponseEntity<Response> service(Request request, String ip);
}
