package io.github.stackpan.tasque.http.controller;

import io.github.stackpan.tasque.http.resource.ErrorResource;
import org.springframework.beans.TypeMismatchException;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var errors = ex.getBindingResult().getAllErrors();

        var errorMap = new HashMap<String, List<String>>();
        errors.forEach(error -> errorMap.put(
                ((FieldError) error).getField(),
                List.of(Objects.requireNonNull(error.getDefaultMessage()))
        ));

        var resource = new ErrorResource("Invalid payload.");
        var model = HalModelBuilder.halModelOf(resource)
                .embed(errorMap, LinkRelation.of("payloadErrors"))
                .build();

        return ResponseEntity.badRequest().body(model);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex instanceof MethodArgumentTypeMismatchException subEx) {
            if (subEx.getParameter().getParameterType() == UUID.class) {
                var e = new ResponseStatusException(HttpStatus.NOT_FOUND);
                return super.handleErrorResponseException(e, e.getHeaders(), e.getStatusCode(), request);
            }
        }

        return super.handleTypeMismatch(ex, headers, status, request);
    }
}
