package com.kindredgroup.unibetlivetest.model.exception;

import com.kindredgroup.unibetlivetest.model.dto.ExceptionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@RequestMapping(produces = "application/vnd.error+json")
public class ExceptionHttpTranslator {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity businessException(HttpServletRequest request, final CustomException e) {
        String message = e.getMessage();
        ExceptionDto exceptionDto = new ExceptionDto().setErrormessage(message).setPath(request.getServletPath());
        Integer statusCode = e.getException().getStatusCode();
        return ResponseEntity.status(statusCode).body(exceptionDto);
    }

}
