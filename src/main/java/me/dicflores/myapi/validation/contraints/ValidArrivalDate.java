package me.dicflores.myapi.validation.contraints;

import me.dicflores.myapi.validation.validator.BookingArrivalDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = BookingArrivalDateValidator.class)
@Target({ ElementType.FIELD })
@Retention(RUNTIME)
@Documented
public @interface ValidArrivalDate {
    String message() default "Campsite can be reserved minimum 1 day ahead of arrival and up to 1 month in advance.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
