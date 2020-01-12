package me.dicflores.myapi.booking;

import me.dicflores.myapi.validation.contraints.ValidArrivalDate;
import me.dicflores.myapi.validation.contraints.ValidDateRange;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

public class Booking {
    private Long id;

    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String fullName;

    @Valid
    @ValidDateRange
    private Dates dates;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Dates getDates() {
        return dates;
    }

    public void setDates(Dates dates) {
        this.dates = dates;
    }

    public static class Dates {
        @Future
        @ValidArrivalDate
        private LocalDate arrival;

        @Future
        private LocalDate departure;

        public LocalDate getArrival() {
            return arrival;
        }

        public void setArrival(LocalDate arrival) {
            this.arrival = arrival;
        }

        public LocalDate getDeparture() {
            return departure;
        }

        public void setDeparture(LocalDate departure) {
            this.departure = departure;
        }
    }
}
