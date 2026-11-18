package com.example.marketPlace.dal.enums;

public enum ProductStatus {
    LIVE,
    DRAFT;

    public String code() {
        return name().toLowerCase();
    }

    public static ProductStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("وضعیت کالا لازم است");
        }
        try {
            return ProductStatus.valueOf(code.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("وضعیت کالا نامعتبر است");
        }
    }
}
