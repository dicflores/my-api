package me.dicflores.myapi.exception;

import java.time.LocalDate;

public class ApiIntegrityViolationException extends Exception {
    public ApiIntegrityViolationException(LocalDate from , LocalDate to) {
        super(String.format("One or more date(s) of requested range [%s to %s] is(are) no longer available.", from, to));
    }
}
