package me.dicflores.myapi.calendar;

import me.dicflores.myapi.util.JacksonHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CalendarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonHelper jacksonHelper;

    @Test
    public void whenNoDatesProvided_thenUseDefaultDateRange() throws Exception {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusMonths(1);

        String expectedContent = jacksonHelper.generateDateArray(from, to);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/calendar")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo(expectedContent)))
        ;
    }

    @Test
    public void whenValidCustomDateRange_thenUseThisDateRange() throws Exception {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(1);

        String expectedContent = jacksonHelper.generateDateArray(from, to);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/calendar")
                .queryParam("arrival", from.toString())
                .queryParam("departure", to.toString())
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(expectedContent)))
        ;
    }

    @Test
    public void whenInvalidCustomRange_thenReturnError() throws Exception {
        LocalDate from = LocalDate.now();
        LocalDate to = from.minusDays(10);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/calendar")
                .queryParam("arrival", from.toString())
                .queryParam("departure", to.toString())
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("BAD_REQUEST")))
                .andExpect(content().string(containsString("Invalid date range for calendar")))
        ;
    }
}
