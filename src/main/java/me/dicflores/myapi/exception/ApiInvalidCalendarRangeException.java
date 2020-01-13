package me.dicflores.myapi.exception;

import java.time.LocalDate;

public class ApiInvalidCalendarRangeException extends Exception {
    public ApiInvalidCalendarRangeException(LocalDate from, LocalDate to) {
        super(String.format("Invalid date range for calendar [%s, %s]", from, to));
    }
}
