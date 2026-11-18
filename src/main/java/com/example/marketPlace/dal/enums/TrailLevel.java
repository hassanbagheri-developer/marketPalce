package com.example.marketPlace.dal.enums;

public enum TrailLevel {
    EASY,
    MEDIUM,
    HARD;

    public static TrailLevel fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("سختی مسیر نامعتبر است");
        }
        try {
            return TrailLevel.valueOf(code.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("سختی مسیر نامعتبر است");
        }
    }
}
