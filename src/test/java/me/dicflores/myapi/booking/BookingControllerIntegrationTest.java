package me.dicflores.myapi.booking;

import me.dicflores.myapi.util.JacksonHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookingControllerIntegrationTest {
    @LocalServerPort
    private int port;
    private String base;
    @Autowired
    private TestRestTemplate template;
    @Autowired
    private JacksonHelper jacksonHelper;

    @BeforeEach
    public void setUp() {
        base = String.format("http://localhost:%d", port);
    }

    @Test
    public void whenSave_thenSucess() {
        LocalDate arrival = LocalDate.now().plusDays(5);
        LocalDate departure = arrival.plusDays(2);
        Booking booking = createBookingArray(1, arrival, departure)[0];

        String urlString = base + "/bookings";
        ResponseEntity<Booking> response = template.postForEntity(urlString, booking, Booking.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Booking result = response.getBody();
        assertThat(result.getFullName()).isEqualTo("User0");
        assertThat(result.getEmail()).isEqualTo("user0@email.com");
        assertThat(result.getDates().getArrival()).isEqualTo(arrival);
        assertThat(result.getDates().getDeparture()).isEqualTo(departure);

        // Now, those dates SHOULD NOT be available.
        urlString = base + "/calendar?arrival=" + arrival + "&departure=" + departure;
        ResponseEntity<String> response2 = template.getForEntity(urlString, String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody()).isEqualTo("[]");

        template.delete(base + "/bookings/" + result.getId());
    }

    @Test
    public void whenSaveTwice_thenConflict() {
        LocalDate arrival = LocalDate.now().plusDays(5);
        LocalDate departure = arrival.plusDays(2);
        Booking booking = createBookingArray(1, arrival, departure)[0];

        String urlString = base + "/bookings";
        ResponseEntity<Booking> response = template.postForEntity(urlString, booking, Booking.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());


        ResponseEntity<String> response2 = template.postForEntity(urlString, booking, String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response2.getBody()).contains("One or more date(s) of requested range [" + arrival + " to " + departure + "] is(are) no longer available.");

        template.delete(base + "/bookings/" + response.getBody().getId());
    }

    @Test
    public void whenInvalidArrivalDate_thenValidationFails() {
        LocalDate arrival = LocalDate.now(); // Need 1 day ahead.
        LocalDate departure = arrival.plusDays(2);
        Booking booking = createBookingArray(1, arrival, departure)[0];

        testValidationMessages(booking,
                "Validation error",
                "\"rejectedValue\":\"" + arrival + "\"",
                "Campsite can be reserved minimum 1 day ahead of arrival and up to 1 month in advance"
        );
    }

    @Test
    public void whenInvalidDateRange_thenValidationFails() {
        LocalDate arrival = LocalDate.now().plusDays(3);
        LocalDate departure = arrival.plusDays(10); // Max 3 days
        Booking booking = createBookingArray(1, arrival, departure)[0];

        testValidationMessages(booking,
            "Validation error",
            "\"rejectedValue\":{\"arrival\":\"" + arrival + "\",\"departure\":\"" + departure + "\"}",
            "Invalid date range. Maximum is a 3-days period."
        );
    }

    private void testValidationMessages(Booking booking, String... expectedMessages) {
        ResponseEntity<String> response = template.postForEntity(base + "/bookings", booking, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        String result = response.getBody();
        for (String msg : expectedMessages) {
            assertThat(result).contains(msg);
        }
    }

    @Test
    public void whenConcurrentSaveWithSameDate_thenOnlyOneShouldSuccess() throws Exception {
        LocalDate arrival = LocalDate.now().plusDays(5);
        LocalDate departure = arrival.plusDays(2);

        int count = 10;
        Booking[] bookings = createBookingArray(count, arrival, departure);

        String urlString = String.format("http://localhost:%d/bookings", port);
        CompletableFuture<ResponseEntity<String>> [] requests = getMultipleRequests(urlString, bookings);
        CompletableFuture.allOf(requests).join(); // Wait for all threads.

        // Verify all responses.
        int successRequests = 0, failedRequests = 0;
        Booking successBooking = null;
        for (int i = 0; i < requests.length; i++) {
            ResponseEntity<String> response = requests[i].get();
            String body = response.getBody();
            if (response.getStatusCode() == HttpStatus.CREATED) {
                successRequests++;
                successBooking = jacksonHelper.fromJson(body);
                assertThat(successBooking.getDates().getArrival()).isEqualTo(arrival);
                assertThat(successBooking.getDates().getDeparture()).isEqualTo(departure);
            } else {
                failedRequests++;
                assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
                String expectedSubstring = "One or more date(s) of requested range [" + arrival + " to " + departure + "] is(are) no longer available.";
                assertTrue(body.contains(expectedSubstring));
            }
        }
        assertEquals(1, successRequests); // Only one should success.
        assertEquals(count - 1, failedRequests); // Remaining should fail.

        Long id = successBooking.getId();
        template.delete(base + "/bookings/" + id);
    }

    private Booking[] createBookingArray(int size, LocalDate from, LocalDate to) {
        Booking.Dates dates = new Booking.Dates();
        dates.setArrival(from);
        dates.setDeparture(to);
        Booking[] bookings = new Booking[size];
        for (int i = 0; i < size; i++) {
            Booking booking = new Booking();
            booking.setFullName("User" + i);
            booking.setEmail("user" + i + "@email.com");
            booking.setDates(dates);
            bookings[i] = booking;
        }
        return bookings;
    }

    private CompletableFuture<ResponseEntity<String>> [] getMultipleRequests(String urlString, Booking[] bookings) {
        int n = bookings.length;
        // Define a fixed Thread Pool of n threads.
        Executor executor = Executors.newFixedThreadPool(n);

        // Construct n CompletableFutures.
        CompletableFuture<ResponseEntity<String>> [] array = (CompletableFuture<ResponseEntity<String>> []) Array.newInstance(CompletableFuture.class, n);

        for (int i = 0; i < n; i++) {
            Booking booking = bookings[i];
            array[i] = CompletableFuture.supplyAsync(() -> template.postForEntity(urlString, booking, String.class), executor);
        }
        return array;
    }
}
