package ru.ulitsaraskolnikova.translator.repo;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class RepositoryConfiguration {
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
    @Getter
    private Connection connection;
    public void init() throws SQLException, ClassNotFoundException, IOException {
        if (connection != null && !connection.isClosed()) return;
        Class.forName(DRIVER_CLASS_NAME);
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        runSqlScript(SCRIPT_FILE_PATH);
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
