package com.pb7technologies.razorpay.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String errorCode,
        String errorDescription,
        LocalDateTime timeStamp,
        List<FieldError> fieldErrors
) {
    public record FieldError(String fieldId, String message) { }

    public static ErrorResponse of(String errorCode, String errorDescription){
        return new ErrorResponse(errorCode,errorDescription, LocalDateTime.now(), null);
    }

    public static ErrorResponse of(String errorCode, String errorDescription, List<FieldError> fieldErrors) {
        return new ErrorResponse(errorCode,errorDescription, LocalDateTime.now(), fieldErrors);
    }
}
