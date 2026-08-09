package com.fabioperettig.factory;

import com.fabioperettig.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

public final class ProductTestFactory {

    private static final List<String> NAMES = List.of(
            "Aquário Aventura de Poseidon",
            "Cama Sono Napoleônico",
            "Tripé Dobrável Diamante",
            "Telefone de Parede SCTC Sem Fio",
            "Marco 2.0",
            "Ferrorama DOC SimTrem"
    );

    private ProductTestFactory() {}

    private static String randomName() {
        Random random = new Random();
        int index = random.nextInt(NAMES.size());

        return NAMES.get(index);
    }

    private static BigDecimal priceFor(String name) {

        BigDecimal price;
        
        switch (randomName())  {
            case "Aquário Aventura de Poseidon" -> price = new BigDecimal("199.99");
            case "Cama Sono Napoleônico" -> price = new BigDecimal("1450.49");
            case "Tripé Dobrável Diamante" -> price = new BigDecimal("249.99");
            case "Telefone de Parede SCTC Sem Fio" -> price = new BigDecimal("75.00");
            case "Marco 2.0" -> price = new BigDecimal("6500.00");
            case "Ferrorama DOC SimTrem" -> price = new BigDecimal("955.00");

            default -> price = new BigDecimal("97.99");
        }

        return price;
    }

    public static Product create(String code) {
        String name = randomName();

        Product product = new Product();
        product.setName(name);
        product.setCode(code);
        product.setPrice(priceFor(name));
        product.setStock(50);

        return product;
    }

}
