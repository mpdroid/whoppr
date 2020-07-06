package com.whoppr.monolith.config;

import com.whoppr.monolith.exceptions.NoPendingOrders;
import com.whoppr.monolith.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler({NotFoundException.class, NoPendingOrders.class})
  public final ResponseEntity<ApiError> handleException(HttpServletRequest request, Exception ex) {
    ApiError apiError = ApiError.builder()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .message(ex.getMessage())
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(apiError.getStatusCode())
        .body(apiError);
  }
}
