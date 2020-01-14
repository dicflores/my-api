package me.dicflores.myapi.booking;

import me.dicflores.myapi.exception.ApiEntityNotFoundException;
import me.dicflores.myapi.exception.ApiIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping()
    public Collection<Booking> getAll() {
        return bookingService.getAll();
    }

    @GetMapping("/{id}")
    public Booking get(@PathVariable Long id) throws ApiEntityNotFoundException {
        return bookingService.get(id);
    }

    @PostMapping()
    public ResponseEntity<Booking> create(@Valid @RequestBody Booking booking) throws ApiIntegrityViolationException {
        Booking savedBooking;
        try {
            savedBooking = bookingService.create(booking);
        } catch (RuntimeException rte) {
            throw new ApiIntegrityViolationException(booking.getDates().getArrival(), booking.getDates().getDeparture());
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedBooking.getId()).toUri();

        return ResponseEntity.created(location).body(savedBooking);
    }

    @PutMapping("/{id}")
    public Booking update(
        @PathVariable @Positive Long id,
        @Valid @RequestBody Booking booking
    ) throws ApiEntityNotFoundException, ApiIntegrityViolationException {
        Booking savedBooking;
        try {
            savedBooking = bookingService.update(id, booking);
        } catch (RuntimeException rte) {
            throw new ApiIntegrityViolationException(booking.getDates().getArrival(), booking.getDates().getDeparture());
        }
        return savedBooking;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws ApiEntityNotFoundException {
        bookingService.delete(id);
    }
}
