package com.myprojects.java_bonds_advisor.utils;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@UtilityClass
public class CheckAndParseData {

    @Getter
    private static final ZoneId zoneId = ZoneId.of("Europe/Moscow"); // it's used in the whole app

    // Try to modify this format (e.g., yyyy-MM-dd) from MOEX web page, into (e.g., dd.MM.yyyy)
    private static final SimpleDateFormat necessaryFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static OffsetDateTime parseOffsetDateTime(String date) {
        if (date == null) {
            return null;
        }
        try {
            LocalDateTime localDateTime = null;
            if (date.contains("-")) {
                String[] elemsOfDate = date.split("-");
                if (elemsOfDate[0].length() == 4) { // bring one format to another
                    localDateTime = LocalDateTime.ofInstant(necessaryFormat.parse(
                                    elemsOfDate[2] + "." + elemsOfDate[1] + "." + elemsOfDate[0])
                            .toInstant(), getZoneId());
                }
            } else { // this format (e.g., dd.MM.yyyy) from tBank web page
                localDateTime = LocalDateTime.ofInstant(necessaryFormat.parse(date).toInstant(), ZoneId.systemDefault());
            }
            if (localDateTime != null)
                return OffsetDateTime.of(localDateTime, getZoneId().getRules().getOffset(localDateTime));
        } catch (DateTimeParseException | ParseException ex) {
            log.debug("This date wasn't be modified, {}", date);
        }
        return null;
    }

    public static String dateTypeInMoscowFormat(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null)
            return null;
        // 1. convert OffsetDateTime to LocalDate
        var localDate = offsetDateTime.toLocalDate();
        // 2. define the desired format (dd.MM.yyyy)
        var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        // 3. format the LocalDate (this step is only for representation)
        return localDate.format(formatter);
    }

    public static BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(value.replaceAll("\\s+", "")); // delete all space
        } catch (NumberFormatException e) {
            log.debug("This data cannot be converted: {}", value);
            return null;
        }
    }

    public static Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.replaceAll("\\s+", "")); // delete all space
        } catch (NumberFormatException e) {
            log.debug("This data cannot be converted, {}", value);
            return null;
        }
    }
}
