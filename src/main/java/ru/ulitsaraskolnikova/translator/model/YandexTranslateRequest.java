package ru.ulitsaraskolnikova.translator.model;

public record YandexTranslateRequest(
        String sourceLanguageCode,
        String targetLanguageCode,
        String format,
        String[] texts,
        String folderId
) {}
