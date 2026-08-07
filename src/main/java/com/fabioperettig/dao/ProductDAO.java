package com.fabioperettig.dao;

import com.fabioperettig.config.ConnectionFactory;
import com.fabioperettig.domain.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ProductDAO extends AbstractDAO<Product, Long> {
    public ProductDAO(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    ///SQL commands
    private static final String INSERT_SQL =
            "INSERT INTO tb_product (name_product, price_product, stock_product) VALUES (?, ?, ?)";

    private static final String FIND_BY_ID_SQL =
            "SELECT id, name_product, price_product, stock_product FROM tb_product WHERE id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT id, name_product, price_product, stock_product FROM tb_product ORDER BY id";

    private static final String UPDATE_SQL =
            "UPDATE tb_product SET name_product = ?, price_product = ?, stock_product = ? WHERE id = ?";

    private static final String DELETE_BY_ID_SQL =
            "DELETE FROM tb_product WHERE id = ?";


    /// Product-specific implementations
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
    protected void bindInsert(PreparedStatement statement, Product product) throws SQLException {
        statement.setString(1, product.getName());
        statement.setBigDecimal(2, product.getPrice());
        statement.setInt(3, product.getStock());
    }

    @Override
    protected void bindUpdate(PreparedStatement statement, Product product) throws SQLException {
        statement.setString(1, product.getName());
        statement.setBigDecimal(2, product.getPrice());
        statement.setInt(3, product.getStock());
        statement.setLong(4, product.getId());
    }

    @Override
    protected void bindId(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
    }

    @Override
    protected Product mapRow(ResultSet resultSet) throws SQLException {
        Product product = new Product();

        product.setId(resultSet.getLong("id"));
        product.setName(resultSet.getString("name_product"));
        product.setPrice(resultSet.getBigDecimal("price_product"));
        product.setStock(resultSet.getInt("stock_product"));

        return product;
    }

    @Override
    protected Long readGeneratedId(ResultSet generatedKeys) throws SQLException {
        return generatedKeys.getLong(1);
    }

    @Override
    protected void setGeneratedId(Product product, Long generatedId) {
        product.setId(generatedId);
    }
}
