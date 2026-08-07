package com.fabioperettig.dao;

import com.fabioperettig.config.ConnectionFactory;
import com.fabioperettig.domain.Client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientDAO extends AbstractDAO<Client, Long> {
    public ClientDAO(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    /// SQL commands
    private static final String INSERT_SQL =
            "INSERT INTO tb_client (name_client, cpf_client, contact_client) VALUES (?, ?, ?)";

    private static final String FIND_BY_ID_SQL =
            "SELECT id, name_client, cpf_client, contact_client FROM tb_client WHERE id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT id, name_client, cpf_client, contact_client FROM tb_client ORDER BY id";

    private static final String UPDATE_SQL =
            "UPDATE tb_client SET name_client = ?, cpf_client = ?, contact_client = ? WHERE id = ?";

    private static final String DELETE_BY_ID_SQL =
            "DELETE FROM tb_client WHERE id = ?";


    /// Client-specific implementations
    @Override
    protected String getInsertSql() {
        return INSERT_SQL;
    }

    @Override
    protected String getFindByIdSql() {
        return FIND_BY_ID_SQL;
    }

    @Override
    protected String getFindAllSql() {
        return FIND_ALL_SQL;
    }

    @Override
    protected String getUpdateSql() {
        return UPDATE_SQL;
    }

    @Override
    protected String getDeleteByIdSql() {
        return DELETE_BY_ID_SQL;
    }

    @Override
    protected void bindInsert(PreparedStatement statement, Client client) throws SQLException {
        statement.setString(1, client.getName());
        statement.setString(2, client.getCpf());
        statement.setString(3, client.getContact());
    }

    @Override
    protected void bindUpdate(PreparedStatement statement, Client client) throws SQLException {
        statement.setString(1, client.getName());
        statement.setString(2, client.getCpf());
        statement.setString(3, client.getContact());
        statement.setLong(4, client.getId());
    }

    @Override
    protected void bindId(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
    }

    @Override
    protected Client mapRow(ResultSet resultSet) throws SQLException {
        Client client = new Client();

        client.setId(resultSet.getLong("id"));
        client.setName(resultSet.getString("name_client"));
        client.setCpf(resultSet.getString("cpf_client"));
        client.setContact(resultSet.getString("contact_client"));

        return client;
    }

    @Override
    protected Long readGeneratedId(ResultSet generatedKeys) throws SQLException {
        return generatedKeys.getLong(1);
    }

    @Override
    protected void setGeneratedId(Client client, Long generatedId) {
        client.setId(generatedId);
    }
}
