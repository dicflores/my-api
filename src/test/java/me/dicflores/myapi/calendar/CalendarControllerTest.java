package me.dicflores.myapi.calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CalendarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper jackson;

    @Test
    public void getAvailableDates() throws Exception {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusMonths(1);

        String expectedContent = generateDateArray(from, to);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/calendar").accept(MediaType.APPLICATION_JSON);
        mvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo(expectedContent)))
        ;
    }

    private String generateDateArray(LocalDate from, LocalDate to) throws Exception {
        Collection<LocalDate> collection = new ArrayList<>();
        LocalDate date = from;
        while (date.isBefore(to) || date.isEqual(to)) {
            collection.add(date);
            date = date.plusDays(1);
        }
        StringWriter writer = new StringWriter();
        jackson.writeValue(writer, collection);
        return writer.toString();
    }
}
