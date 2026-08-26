package com.fabioperettig.domain;

import java.util.Objects;

public final class Stock {

    private final Product product;
    private int availableQuantity;

    public Stock(Product product, int initialQuantity) {
        this.product = requirePersistedProduct(product);
        validateNonNegativeQuantity(initialQuantity);
        this.availableQuantity = initialQuantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public boolean isAvailable() {
        return availableQuantity > 0;
    }

    public boolean hasAvailableQuantity(int requestedQuantity) {
        validatePositiveQuantity(requestedQuantity);
        return availableQuantity >= requestedQuantity;
    }

    public void increaseQuantity(int amount) {
        validatePositiveQuantity(amount);

        availableQuantity = Math.addExact(availableQuantity,amount);
    }

    public void decreaseQuantity(int amount) {
        validatePositiveQuantity(amount);

        if (amount > availableQuantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        availableQuantity -= amount;
    }

    private static Product requirePersistedProduct(Product product) {
        Objects.requireNonNull(product, "Product is required");

        if (product.getId() == null) {
            throw new IllegalArgumentException("Stock product must be persisted");
        }

        return product;
    }

    private static void validateNonNegativeQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Initial stock cannot be negative");
        }
    }

    private static void validatePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }
}
