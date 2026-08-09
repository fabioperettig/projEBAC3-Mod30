package com.fabioperettig.dao;

import com.fabioperettig.domain.Product;
import com.fabioperettig.factory.ProductTestFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class ProductDAOTest extends DaoIntegrationTestSupport {

    private ProductDAO productDAO;

    @BeforeEach
    void setup() {
        productDAO = new ProductDAO(connectionFactory);
    }

    @Test
    void shouldCreateAndPersistProduct() {
        Product product = ProductTestFactory.create("TS1obj01");

        Product createdProduct = productDAO.create(product);
        Product persistedProduct = productDAO.findById(createdProduct.getId()).orElseThrow();

        Assertions.assertNotNull(createdProduct.getId());
        Assertions.assertEquals(product.getName(), persistedProduct.getName());
        Assertions.assertEquals(product.getCode(), persistedProduct.getCode());
        Assertions.assertEquals(product.getPrice(), persistedProduct.getPrice());
        Assertions.assertEquals(product.getStock(), persistedProduct.getStock());

    }

    @Test
    void shouldFindProductById() {
        productDAO.create(ProductTestFactory.create("TS1obj1"));
        Product cProduct2 = productDAO.create(ProductTestFactory.create("TS1obj02"));
        productDAO.create(ProductTestFactory.create("TS1obj03"));

        Optional<Product> productResult = productDAO.findById(cProduct2.getId());
        Assertions.assertTrue(productResult.isPresent());

        Product foundProduct = productResult.orElseThrow();

        Assertions.assertEquals(cProduct2.getId(), foundProduct.getId());
        Assertions.assertEquals(cProduct2.getName(), foundProduct.getName());
        Assertions.assertEquals(cProduct2.getCode(), foundProduct.getCode());
        Assertions.assertEquals(cProduct2.getPrice(), foundProduct.getPrice());
        Assertions.assertEquals(cProduct2.getStock(), foundProduct.getStock());

    }

    @Test
    void shouldFindAll(){
        productDAO.create(ProductTestFactory.create("TS1obj1"));
        productDAO.create(ProductTestFactory.create("TS1obj2"));
        productDAO.create(ProductTestFactory.create("TS1obj3"));

        List<Product> productResult = productDAO.findAll();

        Assertions.assertNotNull(productResult);
        Assertions.assertEquals(3, productResult.size());
        Assertions.assertFalse(productResult.isEmpty());
    }

    @Test
    void shouldUpdate() {
        Product createdProduct = productDAO.create(ProductTestFactory.create("NEW00001"));

        String originalCode = createdProduct.getCode();
        String newCode = "TS1obj01";

        Assertions.assertNotEquals(newCode, originalCode);

        createdProduct.setCode(newCode);
        boolean updated = productDAO.update(createdProduct);

        Product persistedProduct = productDAO.findById(createdProduct.getId()).orElseThrow();

        Assertions.assertTrue(updated);
        Assertions.assertEquals(newCode,persistedProduct.getCode());
        Assertions.assertEquals(createdProduct.getName(),persistedProduct.getName());
    }
    @Test
    void shouldDeleteById(){
        Product createdProduct = productDAO.create(ProductTestFactory.create("TS1obj01"));
        Long productId = createdProduct.getId();

        Assertions.assertTrue(productDAO.findById(productId).isPresent());

        boolean deleted = productDAO.deleteById(productId);
        Optional<Product> deletedProduct = productDAO.findById(productId);

        Assertions.assertTrue(deleted);
        Assertions.assertTrue(deletedProduct.isEmpty());
    }


}
