package me.dicflores.myapi.calendar;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity(name = "Calendar")
@Table(name = "Calendar")
public class CalendarEntity {
    @Id
    private LocalDate day;
    private Long bookingId;

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }
}
