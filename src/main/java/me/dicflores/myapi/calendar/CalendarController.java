package me.dicflores.myapi.calendar;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarRepository repository;

    public CalendarController(CalendarRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Collection<LocalDate> getAvailableDates(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> arrival,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> departure
    ) {
        LocalDate from = arrival.orElse(LocalDate.now());
        LocalDate to = departure.orElse(from.plusMonths(1));
        // TODO Validar from < to.
        return repository.getAvailableDates(from, to);
    }
}
