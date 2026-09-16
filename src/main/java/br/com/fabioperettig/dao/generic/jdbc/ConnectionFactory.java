package br.com.fabioperettig.dao.generic.jdbc;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.charset.StandardCharsets;

public class ConnectionFactory {
    private static Connection connection;
    private static final Dotenv ENV = Dotenv.configure().ignoreIfMissing().load();
    private static boolean schemaCriado;

    private ConnectionFactory(Connection connection) { }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = initConnection();
        }
        return connection;
    }

    private static Connection initConnection() throws SQLException {
        boolean teste = "teste".equals(System.getProperty("ambiente"));
        String url = obrigatoria(teste ? "TEST_DB_URL" : "DB_URL");
        if (teste && url.equals(ENV.get("DB_URL"))) {
            throw new SQLException("TEST_DB_URL deve ser diferente de DB_URL.");
        }
        Connection nova = DriverManager.getConnection(url,
                obrigatoria("DB_USER"),
                obrigatoria("DB_PASSWORD")
        );

        try {
            if (!schemaCriado) {
                try (var input = ConnectionFactory.class.getResourceAsStream("/database/schema.sql")) {
                    if (input == null) throw new SQLException("Schema não encontrado.");
                    String sql = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                    nova.setAutoCommit(false);
                    try (var stm = nova.createStatement()) {
                        for (String comando : sql.split(";")) {
                            if (!comando.isBlank()) stm.execute(comando);
                        }
                    }
                    nova.commit();
                    nova.setAutoCommit(true);
                    schemaCriado = true;
                } catch (java.io.IOException e) {
                    throw new SQLException("Erro lendo schema.", e);
                }
            }
            return nova;
        } catch (SQLException e) {
            nova.close();
            throw e;
        }
    }

    private static String obrigatoria(String nome) throws SQLException {
        String valor = ENV.get(nome);
        if (valor == null || valor.isBlank()) throw new SQLException("Configure " + nome + ".");
        return valor;
    }
}
