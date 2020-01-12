package me.dicflores.myapi.calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;

public interface CalendarRepository extends JpaRepository<CalendarEntity, LocalDate> {


    @Query("SELECT c.day FROM Calendar c WHERE c.bookingId = -1 AND c.day BETWEEN :from AND :to")
    Collection<LocalDate> getAvailableDates(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("SELECT count(*) FROM Calendar c WHERE c.bookingId = -1 AND c.day BETWEEN :from AND :to")
    Long getAvailableDatesCount(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Modifying
    @Query("UPDATE Calendar c SET c.bookingId = :bookingId WHERE c.day BETWEEN :from AND :to")
    int markReservedDates(
            @Param("bookingId") Long bookingId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Modifying
    @Query("UPDATE Calendar c SET c.bookingId = -1 WHERE c.day BETWEEN :from AND :to")
    int markAvailableDates(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
