package me.dicflores.myapi.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class JacksonHelper {

    @Autowired
    private ObjectMapper jackson;

    public String generateDateArray(LocalDate from, LocalDate to) throws Exception {
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
