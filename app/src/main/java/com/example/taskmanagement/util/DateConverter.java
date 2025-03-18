package com.example.taskmanagement.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class DateConverter {
    public static Date convertToDate(String dateString, String timeString) {
        // Parse the date and time strings
        LocalDate date = LocalDate.parse(dateString); // Format: "YYYY-MM-DD"
        LocalTime time = LocalTime.parse(timeString); // Format: "HH:MM"

        // Combine into LocalDateTime
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        // Convert to java.util.Date
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
