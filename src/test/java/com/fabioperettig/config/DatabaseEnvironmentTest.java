package com.fabioperettig.config;

import com.fabioperettig.dao.DaoIntegrationTestSupport;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseEnvironmentTest extends DaoIntegrationTestSupport {

    @Test
    void shouldConnectToPostgreSqlTestDatabase() throws Exception {
        try (Connection connection = connectionFactory.getConnection()) {
            assertTrue(connection.isValid(2));
        }
    }

    @Test
    void shouldInitializeDaoTables() throws Exception {
        String sql = """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name IN ('tb_client', 'tb_product')
                """;

        try (Connection connection = connectionFactory.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            assertTrue(resultSet.next());
            assertEquals(2, resultSet.getInt(1));
        }
    }
}
