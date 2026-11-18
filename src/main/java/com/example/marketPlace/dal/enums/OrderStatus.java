package com.example.marketPlace.dal.enums;

public enum OrderStatus {
    NEW,
    PREP,
    SENT,
    DONE,
    CANCEL;

    public String code() {
        return name().toLowerCase();
    }

    public static OrderStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("وضعیت سفارش لازم است");
        }
        try {
            return OrderStatus.valueOf(code.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("وضعیت سفارش نامعتبر است");
        }
    }
}
