package com.ilerna.wr.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

/*
* Clase que capturará los errores producidos.
* Los errores cuya .class no está definida será considerado como una Exception general,
* generando un error del tipo 500.
*/
@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException exception,
            WebRequest request) {
        log.error("1-Failed to find the requested element", exception);
        return buildErrorResponse(exception, HttpStatus.UNAUTHORIZED, request);
    }
//    //Si se utiliza una API, este metodo devuelve un ResponseEntity
//    @ExceptionHandler(ResourceNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException exception,
//            WebRequest request) {
//        log.error("2Failed to find the requested element", exception);
//        return buildErrorResponse(exception, HttpStatus.NOT_FOUND, request);
//    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleResourceNotFoundException(ResourceNotFoundException exception,
            WebRequest request) {
        ModelAndView modelAndView =  new ModelAndView("error");
        buildErrorResponse(exception, exception.getMessage(), HttpStatus.BAD_REQUEST, request);
        return modelAndView;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception,
            WebRequest request) {
        return buildErrorResponse(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException exception,
            WebRequest request) {
        return buildErrorResponse(exception, exception.getLocalizedMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
            WebRequest request) {
        StringBuilder msg = new StringBuilder("Fallo de validación detectado. Compruebe los valores introducidos: ");
        msg.append("[Field=").append(exception.getFieldError().getField())
            .append("] ")
            .append("[RejectedValue=").append(exception.getFieldError().getRejectedValue())
            .append("] ")
            .append("[Error=").append(exception.getFieldError().getDefaultMessage())
            .append("]");
        return buildErrorResponse(exception, msg.toString(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception,
            WebRequest request) {
        return buildErrorResponse(exception, "JSON parse error: Cannot deserialize one of the values (not accepted).", HttpStatus.BAD_REQUEST, request);
    }
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception,
            WebRequest request) {
        ModelAndView modelAndView =  new ModelAndView("error");
        buildErrorResponse(exception, exception.getMessage(), HttpStatus.BAD_REQUEST, request);
        return modelAndView;
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest request) {
        return buildErrorResponse(exception, "Unknown error occurred.", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            HttpStatus httpStatus,
            WebRequest request) {
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            String message,
            HttpStatus httpStatus,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    public ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        return buildErrorResponse(ex, status, request);
    }

}
