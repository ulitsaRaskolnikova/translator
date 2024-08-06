package ru.ulitsaraskolnikova.translator.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.ulitsaraskolnikova.translator.model.Request;
import ru.ulitsaraskolnikova.translator.model.Response;
import ru.ulitsaraskolnikova.translator.service.TranslatorService;

@RestController
@RequestMapping("${application.endpoint.translate}")
@RequiredArgsConstructor
public class TranslatorController {
    private final TranslatorService translatorService;
    @PostMapping
    public ResponseEntity<Response> translate(@RequestBody Request request,
                                              HttpServletRequest httpRequest) {
         return translatorService.service(request, httpRequest.getRemoteAddr());
    }
}
