package com.fabioperettig.domain;

public enum SaleStatus {

    INITIATED,
    COMPLETED,
    CANCELLED;

    public boolean canBeModified() {
        return this ==  INITIATED;
    }

}
