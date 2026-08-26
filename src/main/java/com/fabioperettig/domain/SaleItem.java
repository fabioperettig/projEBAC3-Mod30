package com.fabioperettig.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class SaleItem {

    private final Product product;
    private final BigDecimal unitPrice;
    private int quantity;

    public SaleItem(Product product, int quantity) {
        this(product, quantity, getProductPrice(product));
    }

    public SaleItem(Product product, int quantity, BigDecimal unitPrice) {
        this.product = Objects.requireNonNull(product, "Product is required");
        this.unitPrice = Objects.requireNonNull(unitPrice,"Unit price is required");

        validatePositiveQuantity(quantity);

        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }

    void increaseQuantity(int amount) {
        validatePositiveQuantity(amount);
        quantity = Math.addExact(quantity, amount);
    }

    void decreaseQuantity(int amount) {
        validatePositiveQuantity(amount);

        if (amount >= quantity) {
            throw new IllegalArgumentException(
                    "Amount must be smaller than current quantity"
            );
        }
        quantity -= amount;
    }

    private static BigDecimal getProductPrice(Product product) {
        Objects.requireNonNull(product, "Product is required");

        return Objects.requireNonNull(
                product.getPrice(),
                "Product price is required"
        );
    }

    private static void validatePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }
    }
}
