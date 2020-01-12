package me.dicflores.myapi.validation.validator;

import me.dicflores.myapi.booking.Booking;
import me.dicflores.myapi.validation.contraints.ValidDateRange;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookingDateRangeValidator implements ConstraintValidator<ValidDateRange, Booking.Dates> {
    @Override
    public boolean isValid(Booking.Dates dates, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate arrival = dates.getArrival();
        LocalDate departure = dates.getDeparture();
        long days = ChronoUnit.DAYS.between(arrival, departure);
        return 0 <= days && days < 3;
    }
}
