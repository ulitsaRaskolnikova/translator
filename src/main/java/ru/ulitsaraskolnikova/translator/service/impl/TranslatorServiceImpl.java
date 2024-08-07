package ru.ulitsaraskolnikova.translator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.ulitsaraskolnikova.translator.model.Request;
import ru.ulitsaraskolnikova.translator.model.Response;
import ru.ulitsaraskolnikova.translator.repo.TranslationRepository;
import ru.ulitsaraskolnikova.translator.service.TranslatorService;
import ru.ulitsaraskolnikova.translator.client.TranslatorClient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslatorServiceImpl implements TranslatorService {
    private final TranslatorClient client;
    private final TranslationRepository repository;
    private final int MAX_COUNT_OF_THREADS = 10;
    private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_COUNT_OF_THREADS);
    private final Semaphore semaphore = new Semaphore(MAX_COUNT_OF_THREADS);
    @Override
    public ResponseEntity<Response> service(Request request, String ip) {
        String[] words = request.text().strip().split(" +");
        List<Future<ResponseEntity<Response>>> futures = new ArrayList<>();
        for (String word : words) {
            Future<ResponseEntity<Response>> future = executorService.submit(() ->
                    client.translate(new Request(request.sourceLang(), request.targetLang(), word))
            );
            futures.add(future);
        }
        var sb = new StringBuilder();
        for (var future : futures) {
            ResponseEntity<Response> responseEntity;
            try {
                responseEntity = future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.toString());
                responseEntity = new ResponseEntity<>(new Response(e.getMessage()), HttpStatusCode.valueOf(500));
            }
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity;
            }
            sb.append(responseEntity.getBody().message());
            sb.append(" ");
        }
        Response response = new Response(sb.toString().strip());
        try {
            repository.init();
            repository.save(request, response, ip);
        } catch (ClassNotFoundException | SQLException | IOException e) {
            log.error(e.toString());
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }
}
