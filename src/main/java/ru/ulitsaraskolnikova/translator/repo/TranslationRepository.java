package ru.ulitsaraskolnikova.translator.repo;

import ru.ulitsaraskolnikova.translator.model.Request;
import ru.ulitsaraskolnikova.translator.model.Response;

import java.io.IOException;
import java.sql.SQLException;

public interface TranslationRepository {
    void init() throws ClassNotFoundException, SQLException, IOException;
    void save(Request request, Response response, String ip) throws SQLException;
}
