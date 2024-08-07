package ru.ulitsaraskolnikova.translator.repo.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ulitsaraskolnikova.translator.model.Request;
import ru.ulitsaraskolnikova.translator.model.Response;
import ru.ulitsaraskolnikova.translator.repo.TranslationRepository;

import java.sql.*;

@Component
@Slf4j
public class TranslationRepositoryImpl implements TranslationRepository {
    @Override
    public void addTranslation(Connection connection, Request request, Response response, String ip) throws SQLException {
        String saveQuery = """
                    insert into translation(ip, source_language, target_language, input_text, output_text)
                    values(?, ?, ?, ?, ?)
                """;
        var saveQuerySt = connection.prepareStatement(saveQuery);
        saveQuerySt.setString(1, ip);
        saveQuerySt.setString(2, request.sourceLang());
        saveQuerySt.setString(3, request.targetLang());
        saveQuerySt.setString(4, request.text());
        saveQuerySt.setString(5, response.message());
        saveQuerySt.executeUpdate();
        log.info("Saved to database: " + request + " " + response + " " + ip);
        saveQuerySt.close();
    }
}
