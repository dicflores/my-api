package me.dicflores.myapi.validation.validator;

import me.dicflores.myapi.validation.contraints.ValidArrivalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BookingArrivalDateValidator implements ConstraintValidator<ValidArrivalDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate arrivalDate, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate now = LocalDate.now();
        LocalDate maxArrivalDate = now.plusMonths(1);
        return arrivalDate.isAfter(now) && arrivalDate.isBefore(maxArrivalDate);
    }
}
