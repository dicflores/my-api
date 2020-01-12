package me.dicflores.myapi.booking;

import me.dicflores.myapi.exception.ApiEntityNotFoundException;
import me.dicflores.myapi.exception.ApiIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
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
    public Booking create(@Valid @RequestBody Booking booking) throws ApiIntegrityViolationException {
        return bookingService.createBooking(booking);
        // TODO Retornar 201 (usando HATEOAS, o ResponseEntity
    }

    @PutMapping("/{id}")
    public Booking update(
        @PathVariable @Positive Long id,
        @Valid @RequestBody Booking booking
    ) throws ApiEntityNotFoundException, ApiIntegrityViolationException {
        return bookingService.updateBooking(id, booking);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws ApiEntityNotFoundException {
        bookingService.deleteBooking(id);
    }
}
