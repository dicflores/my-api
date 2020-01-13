package me.dicflores.myapi.calendar;

import me.dicflores.myapi.util.JacksonHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URL;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CalendarControllerIntegrationTest {
    private URL base;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private JacksonHelper jacksonHelper;

    @BeforeEach
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/calendar");
    }

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
}
