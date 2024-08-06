package ru.ulitsaraskolnikova.translator.repo.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ulitsaraskolnikova.translator.model.Request;
import ru.ulitsaraskolnikova.translator.model.Response;
import ru.ulitsaraskolnikova.translator.repo.TranslationRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

@Component
public class TranslationRepositoryImpl implements TranslationRepository {
    @Value("${datasource.url}")
    private String URL;
    @Value("${datasource.username}")
    private String USERNAME;
    @Value("${datasource.password}")
    private String PASSWORD;
    @Value("${datasource.driver-class-name}")
    private String DRIVER_CLASS_NAME;
    @Value("${datasource.script-file-path}")
    private String SCRIPT_FILE_PATH;
    private Connection connection;
    @Override
    public void init() throws ClassNotFoundException, SQLException, IOException {
        if (connection != null && !connection.isClosed()) return;
        Class.forName(DRIVER_CLASS_NAME);
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        runSqlScript(SCRIPT_FILE_PATH);
    }
    @Override
    public void save(Request request, Response response, String ip) throws SQLException {
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
        saveQuerySt.close();
    }
    public void runSqlScript(String scriptFilePath) throws IOException, SQLException {
        String script = new String(Files.readAllBytes(Paths.get(scriptFilePath)));
        Statement stmt = connection.createStatement();
        for (String command : script.split(";")) {
            if (!command.trim().isEmpty()) {
                stmt.executeUpdate(command + ";");
            }
        }
    }
}
