package ru.ulitsaraskolnikova.translator.model;

import java.util.List;

public record YandexTranslateResponse(List<Translations> translations) {
}
