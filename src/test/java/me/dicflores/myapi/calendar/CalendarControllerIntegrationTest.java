package me.dicflores.myapi.calendar;

import me.dicflores.myapi.util.JacksonHelper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CalendarControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private JacksonHelper jacksonHelper;

    @Test
    public void whenNoDatesProvided_thenUseDefaultDateRange() throws Exception {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusMonths(1);
        String expectedContent = jacksonHelper.generateDateArray(from, to);

        String urlString = String.format("http://localhost:%d/calendar", port);
        ResponseEntity<String> response = template.getForEntity(urlString, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedContent, response.getBody());
    }

    @Test
    public void whenValidCustomDateRange_thenUseThisDateRange() throws Exception {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(1);
        String expectedContent = jacksonHelper.generateDateArray(from, to);

        String urlString = String.format("http://localhost:%d/calendar?arrival=%s&departure=%s", port, from.toString(), to.toString());
        ResponseEntity<String> response = template.getForEntity(urlString, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedContent, response.getBody());
    }

    @Test
    public void whenInvalidCustomRange_thenReturnError() throws Exception {
        LocalDate from = LocalDate.now();
        LocalDate to = from.minusDays(10);

        String urlString = String.format("http://localhost:%d/calendar?arrival=%s&departure=%s", port, from.toString(), to.toString());
        ResponseEntity<String> response = template.getForEntity(urlString, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String body = response.getBody();
        assertTrue(body.contains("BAD_REQUEST"));
        assertTrue(body.contains("Invalid date range for calendar"));
    }

    @Test
    public void whenMultipleRequests_thenHandleAllInParallel() throws Exception {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusMonths(1);
        String expectedContent = jacksonHelper.generateDateArray(from, to);

        String urlString = String.format("http://localhost:%d/calendar", port);
        long start = System.currentTimeMillis();
        CompletableFuture<ResponseEntity<String>> [] requests = getMultipleRequests(urlString);
        CompletableFuture.allOf(requests).join(); // Wait for all threads.
        long end = System.currentTimeMillis();

        assertTrue(end - start < 1000, "Should finish in less than a second.");

        for (int i = 0; i < requests.length; i++) {
            ResponseEntity<String> response = requests[i].get();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedContent, response.getBody());
        }
    }

    private CompletableFuture<ResponseEntity<String>> [] getMultipleRequests(String urlString) {
        int n = 100;
        // Define a fixed Thread Pool of n threads.
        Executor executor = Executors.newFixedThreadPool(n);

        // Construct n CompletableFutures.
        CompletableFuture<ResponseEntity<String>> [] array = (CompletableFuture<ResponseEntity<String>> [])Array.newInstance(CompletableFuture.class, n);
        for (int i = 0; i < n; i++) {
            array[i] = CompletableFuture.supplyAsync(() -> template.getForEntity(urlString, String.class), executor);
        }
        return array;
    }
}
