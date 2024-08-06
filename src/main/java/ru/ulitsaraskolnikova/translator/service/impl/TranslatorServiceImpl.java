package ru.ulitsaraskolnikova.translator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.ulitsaraskolnikova.translator.model.Request;
import ru.ulitsaraskolnikova.translator.model.Response;
import ru.ulitsaraskolnikova.translator.repo.TranslationRepository;
import ru.ulitsaraskolnikova.translator.service.TranslatorService;
import ru.ulitsaraskolnikova.translator.client.TranslatorClient;

import java.io.IOException;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslatorServiceImpl implements TranslatorService {
    private final TranslatorClient client;
    private final TranslationRepository repository;
    @Override
    public ResponseEntity<Response> service(Request request, String ip) {
        ResponseEntity<Response> responseEntity = client.translate(request);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity;
        }
        try {
            repository.init();
            repository.save(request, responseEntity.getBody(), ip);
        } catch (ClassNotFoundException | SQLException | IOException e) {
            log.error(e.toString());
        }
        return responseEntity;
    }
}
