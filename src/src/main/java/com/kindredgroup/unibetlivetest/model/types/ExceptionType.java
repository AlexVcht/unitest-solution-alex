package com.kindredgroup.unibetlivetest.model.types;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ExceptionType {

    // Customer exceptions
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND.value()),

    // Event exceptions
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND.value()),

    // Selection exceptions
    SELECTION_NOT_FOUND(HttpStatus.NOT_FOUND.value()),
    CLOSED_SELECTION(600),

    // Bet exceptions
    BET_ALREAD_PLACED(HttpStatus.CONFLICT.value()),
    NOT_ENOUGH_BALANCE(600),
    UPDATED_ODD(601);

    @Getter
    final Integer statusCode;

    ExceptionType(Integer statusCode) {
        this.statusCode = statusCode;
    }

}
