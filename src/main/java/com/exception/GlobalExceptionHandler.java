package com.exception;

import com.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FlightNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFlightNotFound(
            FlightNotFoundException ex,
            ServerWebExchange exchange) {

        ErrorResponse error = ErrorResponse.builder()
                .success(false)
                .error("Flight Not Found")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .path(exchange.getRequest().getPath().value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFound(
            BookingNotFoundException ex,
            ServerWebExchange exchange) {

        ErrorResponse error = ErrorResponse.builder()
                .success(false)
                .error("Booking Not Found")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .path(exchange.getRequest().getPath().value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(SeatUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleSeatUnavailable(
            SeatUnavailableException ex,
            ServerWebExchange exchange) {

        ErrorResponse error = ErrorResponse.builder()
                .success(false)
                .error("Seat Unavailable")
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .path(exchange.getRequest().getPath().value())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidCancellationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCancellation(
            InvalidCancellationException ex,
            ServerWebExchange exchange) {

        ErrorResponse error = ErrorResponse.builder()
                .success(false)
                .error("Invalid Cancellation")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(exchange.getRequest().getPath().value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex,
            ServerWebExchange exchange) {

        ErrorResponse error = ErrorResponse.builder()
                .success(false)
                .error("Duplicate Resource")
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .path(exchange.getRequest().getPath().value())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(
            InvalidRequestException ex,
            ServerWebExchange exchange) {

        ErrorResponse error = ErrorResponse.builder()
                .success(false)
                .error("Invalid Request")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(exchange.getRequest().getPath().value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AirlineNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAirlineNotFound(
            AirlineNotFoundException ex,
            ServerWebExchange exchange) {

        ErrorResponse error = ErrorResponse.builder()
                .success(false)
                .error("Airline Not Found")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .path(exchange.getRequest().getPath().value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(AirportNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAirportNotFound(
            AirportNotFoundException ex,
            ServerWebExchange exchange) {

        ErrorResponse error = ErrorResponse.builder()
                .success(false)
                .error("Airport Not Found")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .path(exchange.getRequest().getPath().value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            WebExchangeBindException ex,
            ServerWebExchange exchange) {

        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            Object rejectedValue = ((FieldError) error).getRejectedValue();

            validationErrors.add(ErrorResponse.ValidationError.builder()
                    .field(fieldName)
                    .message(errorMessage)
                    .rejectedValue(rejectedValue)
                    .build());
        });

        ErrorResponse error = ErrorResponse.builder()
                .success(false)
                .error("Validation Failed")
                .message("One or more fields have validation errors")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(exchange.getRequest().getPath().value())
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            ServerWebExchange exchange) {

        ErrorResponse error = ErrorResponse.builder()
                .success(false)
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please try again later.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(exchange.getRequest().getPath().value())
                .build();

        // Log the full exception for debugging
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}