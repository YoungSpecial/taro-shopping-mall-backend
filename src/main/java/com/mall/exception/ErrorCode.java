package com.mall.exception;

public enum ErrorCode {

    SUCCESS(200, "Success"),

    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not found"),
    METHOD_NOT_ALLOWED(405, "Method not allowed"),
    CONFLICT(409, "Conflict"),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),

    USER_NOT_FOUND(1001, "User not found"),
    USER_ALREADY_EXISTS(1002, "User already exists"),
    INVALID_TOKEN(1003, "Invalid token"),
    TOKEN_EXPIRED(1004, "Token expired"),

    PRODUCT_NOT_FOUND(2001, "Product not found"),
    INSUFFICIENT_STOCK(2002, "Insufficient stock"),
    PRODUCT_OUT_OF_STOCK(2003, "Product out of stock"),

    CART_NOT_FOUND(3001, "Cart not found"),
    CART_ITEM_NOT_FOUND(3002, "Cart item not found"),

    ORDER_NOT_FOUND(4001, "Order not found"),
    ORDER_CANNOT_BE_CANCELLED(4002, "Order cannot be cancelled"),
    ORDER_CANNOT_BE_REFUNDED(4003, "Order cannot be refunded"),

    PAYMENT_FAILED(5001, "Payment failed"),
    PAYMENT_NOT_FOUND(5002, "Payment not found"),

    WECHAT_AUTH_FAILED(6001, "WeChat authentication failed"),
    WECHAT_OAUTH_ERROR(6002, "WeChat OAuth error");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
