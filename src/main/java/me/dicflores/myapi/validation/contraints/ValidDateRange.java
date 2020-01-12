package me.dicflores.myapi.validation.contraints;

import me.dicflores.myapi.validation.validator.BookingDateRangeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = BookingDateRangeValidator.class)
@Target({ ElementType.FIELD })
@Retention(RUNTIME)
@Documented
public @interface ValidDateRange {
    String message() default "Invalid date range. Maximum is a 3-days period.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
