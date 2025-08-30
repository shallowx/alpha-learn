package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class DateTimeTests {

    @Test
    public void testDayOfWeek(){
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(DayOfWeek.SATURDAY.name());
        log.info("dayOfWeek: {}", dayOfWeek.plus(1));
        log.info("dayOfWeek: {}", dayOfWeek.minus(1));

        LocalDate localDate = LocalDate.now();
        log.info("localDate: {}", localDate);
        log.info("today is dayOfWeek: {}", localDate.getDayOfWeek());
        log.info("value: {}", localDate.getDayOfWeek().getValue());

        String displayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINA);
        log.info("displayName: {}", displayName);
    }

    @Test
    void testLocalDateCreation() {
        LocalDate date = LocalDate.of(2025, 8, 14);
        assertEquals(2025, date.getYear());
        assertEquals(8, date.getMonthValue());
        assertEquals(14, date.getDayOfMonth());

        LocalDate parsedDate = LocalDate.parse("2025-08-14");
        assertEquals(date, parsedDate);
    }

    @Test
    void testLocalTimeCreation() {
        LocalTime time = LocalTime.of(15, 30, 45);
        assertEquals(15, time.getHour());
        assertEquals(30, time.getMinute());
        assertEquals(45, time.getSecond());

        LocalTime parsedTime = LocalTime.parse("15:30:45");
        assertEquals(time, parsedTime);
    }

    @Test
    void testLocalDateTimeCreation() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 8, 14, 15, 30, 45);
        assertEquals(2025, dateTime.getYear());
        assertEquals(8, dateTime.getMonthValue());
        assertEquals(14, dateTime.getDayOfMonth());
        assertEquals(15, dateTime.getHour());
        assertEquals(30, dateTime.getMinute());
        assertEquals(45, dateTime.getSecond());

        LocalDateTime parsedDateTime = LocalDateTime.parse("2025-08-14T15:30:45");
        assertEquals(dateTime, parsedDateTime);
    }

    @Test
    void testZonedDateTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(
                LocalDateTime.of(2025, 8, 14, 15, 30),
                ZoneId.of("America/New_York")
        );
        assertEquals("America/New_York", zonedDateTime.getZone().toString());

        ZonedDateTime parsedZdt = ZonedDateTime.parse("2025-08-14T15:30-04:00[America/New_York]");
        assertEquals(zonedDateTime, parsedZdt);
    }

    @Test
    void testDuration() {
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(12, 30);
        Duration duration = Duration.between(start, end);
        assertEquals(2 * 60 + 30, duration.toMinutes());
    }

    @Test
    void testPeriod() {
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2025, 8, 14);
        Period period = Period.between(start, end);
        assertEquals(5, period.getYears());
        assertEquals(7, period.getMonths());
        assertEquals(13, period.getDays());

        long daysBetween = ChronoUnit.DAYS.between(start, end);
        System.out.println("total days: " + daysBetween);
    }

    @Test
    void testDateTimeFormatter() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 8, 14, 15, 30, 56, 567 * 1_000_000);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
        String formatted = dateTime.format(formatter);
        assertEquals("2025/08/14 15:30:56.567", formatted);

        LocalDateTime parsed = LocalDateTime.parse("2025/08/14 15:30:56.567", formatter);
        assertEquals(dateTime, parsed);

        Instant start = Instant.parse("2025-08-14T10:00:00Z");
        Instant end = Instant.parse("2025-08-14T12:30:45Z");

        Duration duration = Duration.between(start, end);
        long seconds = duration.getSeconds();
        long millis = duration.toMillis();
        System.out.println("total seconds: " + seconds);
        System.out.println("total millis: " + millis);
    }

    @Test
    public void testUTC() {
        Instant instant = Instant.parse("2025-08-14T10:00:00Z");
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatted = zonedDateTime.format(formatter);
        log.info("2025-08-14 10:00:00: {}", formatted);

        Instant now = Instant.now();
        LocalDateTime now1 = LocalDateTime.now();
        log.info("now: {}, now1: {}", now, now1);

        ZoneId.getAvailableZoneIds().forEach(System.out::println);

        MonthDay monthDay = MonthDay.now();
        log.info("monthDay: {}", monthDay);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM", Locale.CHINA);
        String format = dateTimeFormatter.format(monthDay);
        log.info("format: {}", format);

        YearMonth yearMonth = YearMonth.now();
        log.info("now2: {}", yearMonth);
        DateTimeFormatter ymFormatter = DateTimeFormatter.ofPattern("MM/yyyy", Locale.CHINA);
        String s = ymFormatter.format(yearMonth);
        log.info("s: {}", s);
    }
}
