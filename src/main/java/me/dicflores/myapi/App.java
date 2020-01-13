package me.dicflores.myapi;

import me.dicflores.myapi.booking.BookingService;
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
    public CommandLineRunner initDatabase(CalendarRepository calendarRepository, BookingService bookingService) {
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

/*
            LocalDate date1 = LocalDate.now().plusDays(4);
            LocalDate date2 = date1.plusDays(1);
            LocalDate date3 = date1.plusDays(2);
            LocalDate date4 = date1.plusDays(5);

            Booking.Dates dates1 = new Booking.Dates();
            dates1.setArrival(date1);
            dates1.setDeparture(date3);

            Booking.Dates dates2 = new Booking.Dates();
            dates2.setArrival(date4);
            dates2.setDeparture(date4);

            Booking booking1 = new Booking();
            booking1.setFullName("Damian");
            booking1.setEmail("damian@mail.com");
            booking1.setDates(dates1);

            Booking booking2 = new Booking();
            booking2.setFullName("Ignacio");
            booking2.setEmail("ignacio@mail.com");
            booking2.setDates(dates2);

            bookingService.createBooking(booking1);
            bookingService.createBooking(booking2);
 */
        };
    }
}
