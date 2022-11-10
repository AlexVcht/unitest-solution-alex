package com.kindredgroup.unibetlivetest.model.exception;

import com.kindredgroup.unibetlivetest.model.types.ExceptionType;
import lombok.Data;

@Data
public class CustomException extends RuntimeException {

    private final ExceptionType exception;
    private final String message;

    public CustomException(String message, ExceptionType exception) {
        this.message = message;
        this.exception = exception;
    }

}
