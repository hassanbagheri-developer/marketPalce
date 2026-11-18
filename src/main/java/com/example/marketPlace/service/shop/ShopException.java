package com.example.marketPlace.service.shop;

import org.springframework.http.HttpStatus;

public class ShopException extends RuntimeException {

    private final HttpStatus status;

    public ShopException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static ShopException badRequest(String message) {
        return new ShopException(HttpStatus.BAD_REQUEST, message);
    }

    public static ShopException notFound(String message) {
        return new ShopException(HttpStatus.NOT_FOUND, message);
    }

    public static ShopException conflict(String message) {
        return new ShopException(HttpStatus.CONFLICT, message);
    }
}
