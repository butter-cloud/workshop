package com.workshop.server.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebResponse<T> {
    private T data;
    private boolean success;
    private String message;

    public static <T> WebResponse<T> success(T data, String message) {
        return new WebResponse<>(data, true, message);
    }

    public static <T> WebResponse<T> success(String message) {
        return new WebResponse<>(null, true, message);
    }

    public static <T> WebResponse<T> failure(String message) {
        return new WebResponse<>(null, false, message);
    }
}

