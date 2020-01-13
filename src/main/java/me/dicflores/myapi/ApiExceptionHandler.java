package me.dicflores.myapi;

import me.dicflores.myapi.apierror.ApiError;
import me.dicflores.myapi.apierror.ApiValidationError;
import me.dicflores.myapi.exception.ApiEntityNotFoundException;
import me.dicflores.myapi.exception.ApiIntegrityViolationException;
import me.dicflores.myapi.exception.ApiInvalidCalendarRangeException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiEntityNotFoundException.class)
    protected ResponseEntity<Object> handleApiEntityNotFound(ApiEntityNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), null);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ApiIntegrityViolationException.class)
    protected ResponseEntity<Object> handleApiIntegrityViolation(ApiIntegrityViolationException ex) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT, ex.getMessage(), null);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ApiInvalidCalendarRangeException.class)
    protected ResponseEntity<Object> handleApiInvalidCalendarRange(ApiInvalidCalendarRangeException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
        return buildResponseEntity(apiError);
    }

    /**
     * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation, on Controller.
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Validation error");
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String object = fieldError.getObjectName();
            String message = fieldError.getDefaultMessage();
            String field = fieldError.getField();
            Object rejectedValue = fieldError.getRejectedValue();

            ApiValidationError subError = new ApiValidationError(object, message);
            subError.setField(field);
            subError.setRejectedValue(rejectedValue);

            apiError.getSubErrors().add(subError);
        }
        return buildResponseEntity(apiError);
    }
    /**
     * Handles javax.validation.ConstraintViolationException. Thrown when @Validated fails, on Service.
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Validation error");
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String object = violation.getRootBeanClass().getSimpleName();
            String message = violation.getMessage();
            //String field = ((PathImpl) violation.getPropertyPath()).getLeafNode().asString();
            String field = ((PathImpl) violation.getPropertyPath()).asString();
            Object rejectedValue = violation.getInvalidValue();

            ApiValidationError subError = new ApiValidationError(object, message);
            subError.setField(field);
            subError.setRejectedValue(rejectedValue);

            apiError.getSubErrors().add(subError);
        }

        return buildResponseEntity(apiError);
    }

    /**
     * Handle MissingServletRequestParameterException. Triggered when a 'required' request parameter is missing.
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
        MissingServletRequestParameterException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        String error = String.format("%s parameter is missing.", ex.getParameterName());
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    /**
     * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Malformed JSON request", ex);
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getHttpStatus());
    }
}
