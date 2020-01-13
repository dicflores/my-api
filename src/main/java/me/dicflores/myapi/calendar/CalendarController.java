package me.dicflores.myapi.calendar;

import me.dicflores.myapi.exception.ApiInvalidCalendarRangeException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService service;

    public CalendarController(CalendarService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<LocalDate> getAvailableDates(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> arrival,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> departure
    ) throws ApiInvalidCalendarRangeException, ExecutionException, InterruptedException {
        LocalDate from = arrival.orElse(LocalDate.now());
        LocalDate to = departure.orElse(from.plusMonths(1));

        if (from.isAfter(to)) {
            throw new ApiInvalidCalendarRangeException(from, to);
        }
        CompletableFuture<Collection<LocalDate>> availableDates = service.getAvailableDates(from, to);
        return availableDates.get();
    }
}
