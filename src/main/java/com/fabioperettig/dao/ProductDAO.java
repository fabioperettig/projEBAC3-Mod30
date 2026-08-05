package com.fabioperettig.dao;

import com.fabioperettig.domain.Product;

import java.util.List;
import java.util.Optional;

public class ProductDAO extends AbstractDAO<Product> implements IGenericDAO<Product, Long> {

    @Override
    public Product create(Product entity) throws Exception {
        return null;
    }

    @Override
    public Optional<Product> findByID(Long aLong) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() throws Exception {
        return List.of();
    }

    @Override
    public boolean update(Product entity) throws Exception {
        return false;
    }

    @Override
    public boolean deleteByID(Long aLong) throws Exception {
        return false;
    }
}
