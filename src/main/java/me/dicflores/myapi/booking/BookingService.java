package me.dicflores.myapi.booking;


import me.dicflores.myapi.calendar.CalendarRepository;
import me.dicflores.myapi.exception.ApiEntityNotFoundException;
import me.dicflores.myapi.exception.ApiIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.stream.Collectors;


@Service
@Validated
public class BookingService {
    private final BookingRepository bookingRepository;
    private final CalendarRepository calendarRepository;

    public BookingService(BookingRepository bookingRepository, CalendarRepository calendarRepository) {
        this.bookingRepository = bookingRepository;
        this.calendarRepository = calendarRepository;
    }

    public Collection<Booking> getAll() {
        return bookingRepository.findAll().stream()
                .filter(e-> e.getId() != -1)
                .map(this::toResource)
                .collect(Collectors.toList());
    }

    public Booking get(@NotNull Long id) throws ApiEntityNotFoundException {
        return toResource(getEntity(id));
    }

    private BookingEntity getEntity(Long id) throws ApiEntityNotFoundException {
        return bookingRepository.findById(id).orElseThrow(() -> new ApiEntityNotFoundException(BookingEntity.class, "id", String.valueOf(id)));
    }

    @Transactional(rollbackFor = Exception.class)
    public Booking createBooking(@Valid Booking booking) throws ApiIntegrityViolationException {
        Booking.Dates dates = booking.getDates();
        LocalDate from = dates.getArrival();
        LocalDate to = dates.getDeparture();

        Long availableCount = calendarRepository.getAvailableDatesCount(from, to);
        if (availableCount != daysCount(from, to)) {
            throw new ApiIntegrityViolationException(from, to);
        }

        BookingEntity entity = bookingRepository.save(toEntity(booking));

        calendarRepository.markReservedDates(entity.getId(), entity.getArrivalDate(), entity.getDepartureDate());

        return toResource(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public Booking updateBooking(@NotNull Long id, @Valid Booking booking) throws ApiEntityNotFoundException, ApiIntegrityViolationException {
        BookingEntity entity = getEntity(id);
        LocalDate oldFrom = entity.getArrivalDate();
        LocalDate oldTo = entity.getDepartureDate();
        calendarRepository.markAvailableDates(oldFrom, oldTo);

        Booking.Dates dates = booking.getDates();
        LocalDate newFrom = dates.getArrival();
        LocalDate newTo = dates.getDeparture();

        Long availableCount = calendarRepository.getAvailableDatesCount(newFrom, newTo);
        if (availableCount != daysCount(newFrom, newTo)) {
            throw new ApiIntegrityViolationException(newFrom, newTo);
        }

        entity.setEmail(booking.getEmail());
        entity.setFullName(booking.getFullName());
        entity.setArrivalDate(newFrom);
        entity.setDepartureDate(newTo);

        entity = bookingRepository.save(entity);
        calendarRepository.markReservedDates(entity.getId(), newFrom, newTo);

        return toResource(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBooking(@NotNull Long id) throws ApiEntityNotFoundException {
        BookingEntity entity = getEntity(id);

        LocalDate from = entity.getArrivalDate();
        LocalDate to = entity.getDepartureDate();
        calendarRepository.markAvailableDates(from, to);
        bookingRepository.deleteById(id);
    }

    private long daysCount(LocalDate from, LocalDate to) {
        return ChronoUnit.DAYS.between(from, to) + 1;
    }

    //TODO Use a mapping library.
    private BookingEntity toEntity(Booking resource) {
        Booking.Dates dates = resource.getDates();
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setId(resource.getId());
        bookingEntity.setFullName(resource.getFullName());
        bookingEntity.setEmail(resource.getEmail());
        bookingEntity.setArrivalDate(dates.getArrival());
        bookingEntity.setDepartureDate(dates.getDeparture());
        return bookingEntity;
    }
    //TODO Use a mapping library.
    private Booking toResource(BookingEntity bookingEntity) {
        Booking.Dates dates = new Booking.Dates();
        dates.setArrival(bookingEntity.getArrivalDate());
        dates.setDeparture(bookingEntity.getDepartureDate());

        Booking resource = new Booking();
        resource.setId(bookingEntity.getId());
        resource.setFullName(bookingEntity.getFullName());
        resource.setEmail(bookingEntity.getEmail());
        resource.setDates(dates);
        return resource;
    }
}
