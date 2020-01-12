package me.dicflores.myapi.apierror;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class ApiError {
    private HttpStatus httpStatus;
    private LocalDateTime timestamp;
    private String message;
    private String debugMessage;
    private Collection<ApiSubError> subErrors;

    public ApiError(HttpStatus httpStatus) {
        this(httpStatus, "Unexpected Error");
    }
    public ApiError(HttpStatus httpStatus, String message) {
        this(httpStatus, message, null);
    }
    public ApiError(HttpStatus httpStatus, String message, Throwable reason) {
        this.httpStatus = httpStatus;
        this.timestamp = LocalDateTime.now();
        this.message = message;
        if (reason != null) {
            this.debugMessage = reason.getMessage();
        }
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public Collection<ApiSubError> getSubErrors() {
        if (subErrors == null) {
            subErrors = new ArrayList<>();
        }
        return subErrors;
    }

    public void setSubErrors(Collection<ApiSubError> subErrors) {
        this.subErrors = subErrors;
    }
}
