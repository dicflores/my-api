package me.dicflores.myapi;

import me.dicflores.myapi.calendar.CalendarEntity;
import me.dicflores.myapi.calendar.CalendarRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@SpringBootApplication
@EnableAsync
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(CalendarRepository calendarRepository) {
        return args -> {
            // Lleno la tabla Calendar.
            LocalDate date = LocalDate.now();
            Collection<CalendarEntity> all = new ArrayList<>();
            CalendarEntity c;
            for (int i = 0; i < 60; i++) {
                c = new CalendarEntity();
                c.setDay(date);
                c.setBookingId((long)-1);
                all.add(c);
                date = date.plusDays(1);
            }
            calendarRepository.saveAll(all);
        };
    }
}
