package me.dicflores.myapi.calendar;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Service
public class CalendarService {
    private final CalendarRepository repository;

    public CalendarService(CalendarRepository repository) {
        this.repository = repository;
    }

    @Async
    public CompletableFuture<Collection<LocalDate>> getAvailableDates(LocalDate from, LocalDate to) {
        Collection<LocalDate> availableDates = repository.getAvailableDates(from, to);
        return CompletableFuture.completedFuture(availableDates);
    }
}
