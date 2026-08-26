
CREATE TABLE IF NOT EXISTS TB_STOCK (
    product_id BIGINT NOT NULL,
    available_quantity INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT PK_STOCK PRIMARY KEY (product_id),

    CONSTRAINT FK_STOCK_PRODUCT FOREIGN KEY (product_id)
                                REFERENCES TB_PRODUCT (id)
                                ON DELETE CASCADE,

    CONSTRAINT CK_STOCK_QUANTITY CHECK (available_quantity >= 0)
);
