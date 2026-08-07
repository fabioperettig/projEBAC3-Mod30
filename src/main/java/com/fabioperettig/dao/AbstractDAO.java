package com.fabioperettig.dao;

import com.fabioperettig.config.ConnectionFactory;
import com.fabioperettig.exception.DataAccessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractDAO<T, ID> implements IGenericDAO<T, ID> {

    private final ConnectionFactory connectionFactory;

    protected AbstractDAO(ConnectionFactory connectionFactory) {
        this.connectionFactory = Objects.requireNonNull(connectionFactory,"ConnectionFactory is required");
    }


    ///Specific points for ClientDAO and ProductDAO
    protected abstract String getInsertSql();
    protected abstract String getFindByIdSql();
    protected abstract String getFindAllSql();
    protected  abstract String getUpdateSql();
    protected abstract String getDeleteByIdSql();

    protected abstract void bindInsert(PreparedStatement statement, T entity) throws SQLException;
    protected abstract void bindUpdate(PreparedStatement statement, T entity) throws SQLException;
    protected abstract void bindId(PreparedStatement statement, ID id) throws SQLException;
    protected abstract T mapRow(ResultSet resultSet) throws SQLException;
    protected abstract ID readGeneratedId(ResultSet generatedId) throws SQLException;
    protected abstract void setGeneratedId(T entity, ID generatedId);

    ///Generic CRUD
    @Override
    public T create(T entity) {
        Objects.requireNonNull(entity, "Entity is required");

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     getInsertSql(),
                     Statement.RETURN_GENERATED_KEYS))
        {
            bindInsert(statement, entity);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new DataAccessException("Expected one insert row, but got: " + affectedRows);
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if(!generatedKeys.next()) {
                    throw new DataAccessException("Database did not return the generated ID");
                }

                ID generatedId = readGeneratedId(generatedKeys);
                setGeneratedId(entity, generatedId);
            }

            return entity;

        } catch (SQLException exception) {
            throw new DataAccessException("Failed to create entity", exception);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        Objects.requireNonNull(id, "ID is required");

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                    getFindByIdSql())) {

            bindId(statement, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(mapRow(resultSet));
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Failed to find entity by ID", exception);
        }
    }
    @Override
    public List<T> findAll() {
        List<T> entities = new ArrayList<>();

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        getFindAllSql()
                );
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                entities.add(mapRow(resultSet));
            }

            return entities;
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Failed to find all entities",
                    exception
            );
        }
    }

    @Override
    public boolean update(T entity) {
        Objects.requireNonNull(entity, "Entity is required");

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        getUpdateSql()
                )
        ) {
            bindUpdate(statement, entity);

            int affectedRows = statement.executeUpdate();

            return affectedRows == 1;
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Failed to update entity",
                    exception
            );
        }
    }

    @Override
    public boolean deleteById(ID id) {
        Objects.requireNonNull(id, "ID is required");

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        getDeleteByIdSql()
                )
        ) {
            bindId(statement, id);

            int affectedRows = statement.executeUpdate();

            return affectedRows == 1;
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Failed to delete entity",
                    exception
            );
        }
    }
}