package ru.ulitsaraskolnikova.translator.model;

public record Request(String sourceLang, String targetLang, String text) {
}
