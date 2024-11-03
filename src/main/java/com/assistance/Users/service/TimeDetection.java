package com.assistance.Users.service;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeDetection {

    private static final Map<String, LocalDateTimeAdjuster> TIME_ADJUSTMENTS = new HashMap<>();
// Improved regex pattern to handle optional minutes and AM/PM markers
private static final String TIME_REGEX = "(?i)\\b(1[0-2]|[1-9])(?::([0-5][0-9]))?\\s*(am|pm|a\\.m\\.|p\\.m\\.|o'clock|oclock)\\b";
    // Constructor that initializes time adjustments in memory
    public TimeDetection() {
        initializeTimeAdjustments();
    }

    public static void main(String[] args) {
        TimeDetection timeDetection = new TimeDetection();
        String input = "remind me that tomorrow at 2:30 am";
        LocalDateTime extractedDateTime = timeDetection.extractDateTime(input);

        if (extractedDateTime != null) {
            // Convert LocalDateTime to Timestamp
            Timestamp extractedTimestamp = Timestamp.valueOf(extractedDateTime);
            System.out.println("Extracted Timestamp: " + extractedTimestamp);
        } else {
            System.out.println("No valid time detected.");
        }
    }



    public LocalDateTime extractDateTime(String input) {
        LocalDateTime date = LocalDateTime.now(); // Start with the current date and time
        String originalInput = input.replace("." , ""); // Keep the original input for time extraction

        // Normalize input for easier keyword matching
        input = input.toLowerCase(); // Convert the input string to lowercase

        System.out.println("processed date : " +  input );
        boolean hasKeywords = true; // Flag to control the keyword detection loop
        boolean timeOverride = false; // Flag to override time if specific time is detected

        // Loop through keywords and handle explicit time detection
        while (hasKeywords) {
            hasKeywords = false; // Reset the flag for the next iteration
            for (String keyword : TIME_ADJUSTMENTS.keySet()) { // Check each keyword for a match
                if (input.contains(keyword)) { // If the keyword is found in the input
                    hasKeywords = true; // Indicate a keyword was found
                    System.out.println("Keyword Detected: " + keyword); // Log the detected keyword
                    date = handleKeyword(keyword, date); // Apply the time adjustment based on the keyword
                    input = input.replace(keyword, "").trim(); // Remove the keyword from the input string
                    System.out.println("Adjusted date after handling keyword: " + date);
                }
            }
        }

        // Check for explicit time matches regardless of keyword presence
        Matcher matcher = Pattern.compile(TIME_REGEX).matcher(originalInput);
        while (matcher.find()) { // If any time match is found
            String timeStr = matcher.group(); // Extract the matched time string
            System.out.println("Matched time string: " + timeStr); // Log the matched time string

            // Extract hour and minute from the regex groups
            int hour = Integer.parseInt(matcher.group(1)); // Get the hour from the first group
            int minute = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0; // Get minute or default to 0 if not present
            System.out.println("Hour detected: " + hour + ", Minute detected: " + minute); // Log detected hour and minute

            // Handle AM/PM or other time format
            if (matcher.group(3) != null) { // If there's an AM/PM or other designation
                String amPm = matcher.group(3).toLowerCase();
                if (amPm.contains("pm") && hour < 12) {
                    hour += 12; // Convert PM hour to 24-hour format, except for 12 PM
                } else if (amPm.contains("am") && hour == 12) {
                    hour = 0; // Convert 12 AM to 0 hours for midnight
                }
            }

            System.out.println("Final hour after adjustment: " + hour + ", Final minute: " + minute); // Log final hour and minute
            date = date.withHour(hour).withMinute(minute); // Set the hour and minute in the date
            System.out.println("Date after explicit time detection: " + date); // Log date after explicit time detection
            timeOverride = true; // Set the override flag since a specific time was found
        }

        if (timeOverride) {
            // If time was explicitly set, ensure no conflicting adjustments from keywords
            System.out.println("Time was explicitly set, returning adjusted date.");
            return date; // Return the date with the explicitly set time
        }

        System.out.println("Returning adjusted date based on keywords." + date);
        return date; // Return the adjusted date based on keywords
    }


    // Method to handle keyword adjustments
    static LocalDateTime handleKeyword(String keyword, LocalDateTime now) {
        return TIME_ADJUSTMENTS.get(keyword).adjust(now); // Apply the corresponding time adjustment for the keyword
    }

    // Functional interface for date/time adjustments
    @FunctionalInterface
    interface LocalDateTimeAdjuster {
        LocalDateTime adjust(LocalDateTime now); // Method to adjust the current date/time
    }




    // Method to initialize time adjustments
// Method to initialize time adjustments
    private void initializeTimeAdjustments() {
        // Phrases sorted by length (number of words) in descending order
        TIME_ADJUSTMENTS.put("beginning of next month", now -> now.with(TemporalAdjusters.firstDayOfNextMonth()).withHour(9).withMinute(0)); // 9 AM on the first day of next month
        TIME_ADJUSTMENTS.put("end of next month", now -> now.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth())); // Last day of next month
        TIME_ADJUSTMENTS.put("after the holidays", now -> now.plusDays(10)); // Ten days after holidays
        TIME_ADJUSTMENTS.put("early next week", now -> now.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(9).withMinute(0)); // 9 AM next Monday
        TIME_ADJUSTMENTS.put("later this year", now -> now.plusMonths(6)); // Six months later in the year
        TIME_ADJUSTMENTS.put("later this week", now -> now.plusDays(3)); // Three days later in the week
        TIME_ADJUSTMENTS.put("next quarter", now -> now.plusMonths(3)); // Move to the next quarter
        TIME_ADJUSTMENTS.put("at the end of the week", now -> now.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).withHour(23).withMinute(59)); // End of the week
        TIME_ADJUSTMENTS.put("this weekend", now -> now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))); // Set to this weekend
        TIME_ADJUSTMENTS.put("next Tuesday", now -> now.with(TemporalAdjusters.next(DayOfWeek.TUESDAY))); // Set to next Tuesday
        TIME_ADJUSTMENTS.put("tomorrow night", now -> now.plusDays(1).withHour(20).withMinute(0)); // 8 PM tomorrow
        TIME_ADJUSTMENTS.put("at noon tomorrow", now -> now.plusDays(1).withHour(12).withMinute(0)); // 12 PM tomorrow
        TIME_ADJUSTMENTS.put("in a couple of days", now -> now.plusDays(2)); // Two days later
        TIME_ADJUSTMENTS.put("this afternoon", now -> now.withHour(15).withMinute(0)); // 3 PM today
        TIME_ADJUSTMENTS.put("this evening", now -> now.withHour(18).withMinute(0)); // 6 PM today
        TIME_ADJUSTMENTS.put("this morning", now -> now.withHour(9).withMinute(0)); // 9 AM today
        TIME_ADJUSTMENTS.put("around noon", now -> now.withHour(12).withMinute(0)); // 12 PM today
        TIME_ADJUSTMENTS.put("after work", now -> now.withHour(17).withMinute(0)); // 5 PM today
        TIME_ADJUSTMENTS.put("at midnight", now -> now.withHour(0).withMinute(0)); // Midnight today
        TIME_ADJUSTMENTS.put("at dawn", now -> now.withHour(6).withMinute(0)); // 6 AM today
        TIME_ADJUSTMENTS.put("at dusk", now -> now.withHour(18).withMinute(0)); // 6 PM today
        TIME_ADJUSTMENTS.put("next month", now -> now.plusMonths(1)); // Move to next month
        TIME_ADJUSTMENTS.put("in a month", now -> now.plusMonths(1)); // One month later
        TIME_ADJUSTMENTS.put("in a week", now -> now.plusWeeks(1)); // Move to next week
        TIME_ADJUSTMENTS.put("in two days", now -> now.plusDays(2)); // Two days later
        TIME_ADJUSTMENTS.put("tonight", now -> now.withHour(19).withMinute(0)); // 7 PM today
        TIME_ADJUSTMENTS.put("shortly", now -> now.plusMinutes(5)); // 5 minutes later
        TIME_ADJUSTMENTS.put("in a minute", now -> now.plusMinutes(1)); // 1 minute later
        TIME_ADJUSTMENTS.put("in an hour", now -> now.plusHours(1)); // 1 hour later
        TIME_ADJUSTMENTS.put("in a few hours", now -> now.plusHours(2)); // 2 hours later
        TIME_ADJUSTMENTS.put("in a few minutes", now -> now.plusMinutes(5)); // 5 minutes later
        TIME_ADJUSTMENTS.put("later", now -> now.plusHours(1)); // 1 hour later
        TIME_ADJUSTMENTS.put("soon", now -> now.plusMinutes(10)); // 10 minutes laterTIME_ADJUSTMENTS.put("at the beginning of next year", now -> now.plusYears(1).withDayOfYear(1).withHour(9).withMinute(0)); // Start of next year at 9 AM
        TIME_ADJUSTMENTS.put("at the beginning of this year", now -> now.withDayOfYear(1).withHour(9).withMinute(0)); // Start of this year at 9 AM
        TIME_ADJUSTMENTS.put("at the beginning of next decade", now -> now.plusYears(10 - (now.getYear() % 10)).withDayOfYear(1).withHour(9).withMinute(0)); // Start of next decade at 9 AM
        TIME_ADJUSTMENTS.put("at the beginning of this decade", now -> now.withYear(now.getYear() - (now.getYear() % 10)).withDayOfYear(1).withHour(9).withMinute(0)); // Start of this decade at 9 AM
        TIME_ADJUSTMENTS.put("next week", now -> now.plusWeeks(1)); // Move to next week
        TIME_ADJUSTMENTS.put("in a decade", now -> now.plusYears(10)); // Move to next decade
        TIME_ADJUSTMENTS.put("in a century", now -> now.plusYears(100)); // Move to next century
        TIME_ADJUSTMENTS.put("next year", now -> now.plusYears(1)); // One year later
        TIME_ADJUSTMENTS.put("at the stroke of midnight", now -> now.withHour(0).withMinute(0).withSecond(0)); // Midnight today
        TIME_ADJUSTMENTS.put("in the foreseeable future", now -> now.plusWeeks(1)); // Near future, within next week
        TIME_ADJUSTMENTS.put("in the distant future", now -> now.plusYears(5)); // Distant future, several years ahead
        TIME_ADJUSTMENTS.put("at the crack of dawn", now -> now.withHour(5).withMinute(0)); // Early morning, around sunrise
        TIME_ADJUSTMENTS.put("a couple of hours from now", now -> now.plusHours(2)); // Current time plus 2 hours
        TIME_ADJUSTMENTS.put("at the last minute", now -> now.plusMinutes(1)); // Just before an event
        TIME_ADJUSTMENTS.put("at the beginning of the month", now -> now.withDayOfMonth(1).withHour(0).withMinute(0)); // First of the month
        TIME_ADJUSTMENTS.put("at the end of the month", now -> now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59)); // Last day of the month
        TIME_ADJUSTMENTS.put("by the end of the month", now -> now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59)); // By last day of month
        TIME_ADJUSTMENTS.put("by the end of the week", now -> now.with(java.time.DayOfWeek.SUNDAY).withHour(23).withMinute(59)); // End of current week
        TIME_ADJUSTMENTS.put("by the end of the year", now -> now.withMonth(12).withDayOfMonth(31).withHour(23).withMinute(59)); // End of the year
        TIME_ADJUSTMENTS.put("during the holidays", now -> now.withMonth(12).withDayOfMonth(25)); // Holiday season, December 25th
        TIME_ADJUSTMENTS.put("in the early hours of the morning", now -> now.withHour(3)); // Between midnight and dawn
        TIME_ADJUSTMENTS.put("a year from now", now -> now.plusYears(1)); // Exactly one year later
        TIME_ADJUSTMENTS.put("in the next few days", now -> now.plusDays(3)); // 3 days from now
        TIME_ADJUSTMENTS.put("the following month", now -> now.plusMonths(1).withDayOfMonth(1)); // Start of next month
        TIME_ADJUSTMENTS.put("the following week", now -> now.plusWeeks(1).with(java.time.DayOfWeek.MONDAY)); // Start of next week
        TIME_ADJUSTMENTS.put("the following day", now -> now.plusDays(1)); // Next day
        TIME_ADJUSTMENTS.put("in a few weeks", now -> now.plusWeeks(3)); // Three weeks from now
        TIME_ADJUSTMENTS.put("in a few days", now -> now.plusDays(3)); // Three days from now
        TIME_ADJUSTMENTS.put("three months ago", now -> now.minusMonths(3)); // Three months in the past
        TIME_ADJUSTMENTS.put("in the long term", now -> now.plusYears(5)); // Long term, several years from now
        TIME_ADJUSTMENTS.put("in the coming days", now -> now.plusDays(3)); // Next few days
        TIME_ADJUSTMENTS.put("for the next few days", now -> now.plusDays(3)); // Duration of next few days
        TIME_ADJUSTMENTS.put("once a year", now -> now.plusYears(1)); // Every year, from current date
        TIME_ADJUSTMENTS.put("in the past year", now -> now.minusYears(1)); // One year prior to today
        TIME_ADJUSTMENTS.put("at the end of the day", now -> now.withHour(23).withMinute(59)); // End of today
        TIME_ADJUSTMENTS.put("last Friday", now -> now.with(java.time.DayOfWeek.FRIDAY).minusWeeks(1)); // Friday of previous week
        TIME_ADJUSTMENTS.put("last month", now -> now.minusMonths(1).withDayOfMonth(1)); // Start of previous month
        TIME_ADJUSTMENTS.put("last year", now -> now.minusYears(1)); // Previous year
        TIME_ADJUSTMENTS.put("next business day", now -> now.plusDays(1).with(java.time.temporal.TemporalAdjusters.next(java.time.DayOfWeek.MONDAY))); // Following working day
        TIME_ADJUSTMENTS.put("on Thanksgiving Day", now -> now.withMonth(11).with(java.time.temporal.TemporalAdjusters.dayOfWeekInMonth(4, java.time.DayOfWeek.THURSDAY))); // Fourth Thursday in November
        TIME_ADJUSTMENTS.put("at this moment", now -> now); // Current moment
        TIME_ADJUSTMENTS.put("during the week", now -> now.with(java.time.DayOfWeek.MONDAY)); // Start of the current week
        TIME_ADJUSTMENTS.put("in the summer", now -> now.withMonth(6).withDayOfMonth(21)); // Start of summer (June 21)
        TIME_ADJUSTMENTS.put("in the winter", now -> now.withMonth(12).withDayOfMonth(21)); // Start of winter (December 21)
        TIME_ADJUSTMENTS.put("in the spring", now -> now.withMonth(3).withDayOfMonth(21)); // Start of spring (March 21)
        TIME_ADJUSTMENTS.put("in the fall", now -> now.withMonth(9).withDayOfMonth(21)); // Start of fall (September 21)
        TIME_ADJUSTMENTS.put("in January", now -> now.withMonth(1).withDayOfMonth(1)); // Start of January
        TIME_ADJUSTMENTS.put("in February", now -> now.withMonth(2).withDayOfMonth(1)); // Start of February
        TIME_ADJUSTMENTS.put("in March", now -> now.withMonth(3).withDayOfMonth(1)); // Start of March
        TIME_ADJUSTMENTS.put("in April", now -> now.withMonth(4).withDayOfMonth(1)); // Start of April
        TIME_ADJUSTMENTS.put("in May", now -> now.withMonth(5).withDayOfMonth(1)); // Start of May
        TIME_ADJUSTMENTS.put("in June", now -> now.withMonth(6).withDayOfMonth(1)); // Start of June
        TIME_ADJUSTMENTS.put("in July", now -> now.withMonth(7).withDayOfMonth(1)); // Start of July
        TIME_ADJUSTMENTS.put("in August", now -> now.withMonth(8).withDayOfMonth(1)); // Start of August
        TIME_ADJUSTMENTS.put("in September", now -> now.withMonth(9).withDayOfMonth(1)); // Start of September
        TIME_ADJUSTMENTS.put("in October", now -> now.withMonth(10).withDayOfMonth(1)); // Start of October
        TIME_ADJUSTMENTS.put("in November", now -> now.withMonth(11).withDayOfMonth(1)); // Start of November
        TIME_ADJUSTMENTS.put("in December", now -> now.withMonth(12).withDayOfMonth(1)); // Start of December
        TIME_ADJUSTMENTS.put("a little while ago", now -> now.minusMinutes(10)); // Ten minutes before
        TIME_ADJUSTMENTS.put("a minute from now", now -> now.plusMinutes(1)); // One minute later
        TIME_ADJUSTMENTS.put("in three days", now -> now.plusDays(3)); // Three days from today
        TIME_ADJUSTMENTS.put("in a year", now -> now.plusYears(1)); // 365 days from today
        TIME_ADJUSTMENTS.put("at this time", now -> now); // Current time
        TIME_ADJUSTMENTS.put("every day", now -> now.plusDays(1)); // Daily, repeats
        TIME_ADJUSTMENTS.put("today", now -> now.withHour(0).withMinute(0).withSecond(0)); // Start of today
        TIME_ADJUSTMENTS.put("day after tomorrow", now -> now.plusDays(2).withHour(0).withMinute(0).withSecond(0)); // Two days from today
        TIME_ADJUSTMENTS.put("day before yesterday", now -> now.minusDays(2).withHour(0).withMinute(0).withSecond(0)); // Two days ago
        TIME_ADJUSTMENTS.put("tomorrow", now -> now.plusDays(1)); // Next day
        TIME_ADJUSTMENTS.put("yesterday", now -> now.minusDays(1)); // Previous day

        // Upcoming weekday adjustments, starting from today
        TIME_ADJUSTMENTS.put("coming monday", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0)); // Next or same Monday
        TIME_ADJUSTMENTS.put("coming tuesday", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.TUESDAY)).withHour(0).withMinute(0).withSecond(0)); // Next or same Tuesday
        TIME_ADJUSTMENTS.put("coming wednesday", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.WEDNESDAY)).withHour(0).withMinute(0).withSecond(0)); // Next or same Wednesday
        TIME_ADJUSTMENTS.put("coming thursday", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.THURSDAY)).withHour(0).withMinute(0).withSecond(0)); // Next or same Thursday
        TIME_ADJUSTMENTS.put("coming friday", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.FRIDAY)).withHour(0).withMinute(0).withSecond(0)); // Next or same Friday
        TIME_ADJUSTMENTS.put("coming saturday", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SATURDAY)).withHour(0).withMinute(0).withSecond(0)); // Next or same Saturday
        TIME_ADJUSTMENTS.put("coming sunday", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).withHour(0).withMinute(0).withSecond(0)); // Next or same Sunday

// "Next" weekday adjustments, ensuring the day is always after today
        TIME_ADJUSTMENTS.put("next monday", now -> now.with(java.time.temporal.TemporalAdjusters.next(java.time.DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0)); // Always the Monday after today
        TIME_ADJUSTMENTS.put("next wednesday", now -> now.with(java.time.temporal.TemporalAdjusters.next(java.time.DayOfWeek.WEDNESDAY)).withHour(0).withMinute(0).withSecond(0)); // Always the Wednesday after today
        TIME_ADJUSTMENTS.put("next thursday", now -> now.with(java.time.temporal.TemporalAdjusters.next(java.time.DayOfWeek.THURSDAY)).withHour(0).withMinute(0).withSecond(0)); // Always the Thursday after today
        TIME_ADJUSTMENTS.put("next friday", now -> now.with(java.time.temporal.TemporalAdjusters.next(java.time.DayOfWeek.FRIDAY)).withHour(0).withMinute(0).withSecond(0)); // Always the Friday after today
        TIME_ADJUSTMENTS.put("next saturday", now -> now.with(java.time.temporal.TemporalAdjusters.next(java.time.DayOfWeek.SATURDAY)).withHour(0).withMinute(0).withSecond(0)); // Always the Saturday after today
        TIME_ADJUSTMENTS.put("next sunday", now -> now.with(java.time.temporal.TemporalAdjusters.next(java.time.DayOfWeek.SUNDAY)).withHour(0).withMinute(0).withSecond(0)); // Always the Sunday after today


        // Specific to Indian conversational context
        TIME_ADJUSTMENTS.put("this coming Monday", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0)); // Next or same Monday
        TIME_ADJUSTMENTS.put("next to next Monday", now -> now.plusWeeks(2).with(java.time.DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0)); // The Monday after the upcoming Monday
        TIME_ADJUSTMENTS.put("end of this week", now -> now.with(java.time.DayOfWeek.SUNDAY).withHour(23).withMinute(59).withSecond(59)); // End of this week
        TIME_ADJUSTMENTS.put("next weekend", now -> now.plusWeeks(1).with(java.time.DayOfWeek.SATURDAY).withHour(0).withMinute(0).withSecond(0)); // Start of the next weekend

// Common Indian phrases for relative times
        TIME_ADJUSTMENTS.put("this Sunday evening", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).withHour(18).withMinute(0)); // Sunday evening this week
        TIME_ADJUSTMENTS.put("next Saturday morning", now -> now.plusWeeks(1).with(java.time.DayOfWeek.SATURDAY).withHour(8).withMinute(0)); // Next Saturday at 8 AM
        TIME_ADJUSTMENTS.put("coming Wednesday afternoon", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.WEDNESDAY)).withHour(15).withMinute(0)); // Coming Wednesday at 3 PM
        TIME_ADJUSTMENTS.put("coming Friday night", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.FRIDAY)).withHour(20).withMinute(0)); // Coming Friday night at 8 PM

// Vague but commonly used expressions in Indian English
        TIME_ADJUSTMENTS.put("in a few moments", now -> now.plusMinutes(10)); // In the next few minutes
        TIME_ADJUSTMENTS.put("in a little while", now -> now.plusMinutes(30)); // Within the next half hour
        TIME_ADJUSTMENTS.put("after some time", now -> now.plusHours(1)); // After about an hour
        TIME_ADJUSTMENTS.put("by noon", now -> now.withHour(12).withMinute(0).withSecond(0)); // By 12 PM today
        TIME_ADJUSTMENTS.put("by evening", now -> now.withHour(18).withMinute(0).withSecond(0)); // By 6 PM today
        TIME_ADJUSTMENTS.put("by tonight", now -> now.withHour(21).withMinute(0).withSecond(0)); // By 9 PM today

// Indian cultural references to time
        TIME_ADJUSTMENTS.put("during Diwali", now -> now.withMonth(10).with(java.time.temporal.TemporalAdjusters.lastInMonth(java.time.DayOfWeek.MONDAY)).withHour(18).withMinute(0)); // Last Monday of October as an approximation for Diwali
        TIME_ADJUSTMENTS.put("in the festive season", now -> now.withMonth(10).withDayOfMonth(1)); // Start of October, approximate festive season
        TIME_ADJUSTMENTS.put("after the monsoon", now -> now.withMonth(10).withDayOfMonth(1)); // October as monsoon season end
        TIME_ADJUSTMENTS.put("before Holi", now -> now.withMonth(3).with(java.time.temporal.TemporalAdjusters.dayOfWeekInMonth(2, java.time.DayOfWeek.MONDAY)).withHour(0).withMinute(0)); // Approximate date before Holi in March
        TIME_ADJUSTMENTS.put("around Navratri", now -> now.withMonth(10).withDayOfMonth(7)); // October, around Navratri festival time

// Regional phrases for urgency or immediacy
        TIME_ADJUSTMENTS.put("jaldi se", now -> now.plusMinutes(1)); // "Quickly" in Hindi; adds one minute
        TIME_ADJUSTMENTS.put("abhi ke abhi", now -> now); // "Right now" in Hindi
        TIME_ADJUSTMENTS.put("thodi der mein", now -> now.plusMinutes(10)); // "In a little while" in Hindi
        TIME_ADJUSTMENTS.put("thodi der pehle", now -> now.minusMinutes(10)); // "A little while ago" in Hindi
        TIME_ADJUSTMENTS.put("raat ko", now -> now.withHour(22).withMinute(0).withSecond(0)); // "At night" in Hindi; 10 PM
        TIME_ADJUSTMENTS.put("subah", now -> now.withHour(6).withMinute(0)); // "In the morning" in Hindi; 6 AM
        TIME_ADJUSTMENTS.put("dopehar mein", now -> now.withHour(12)); // "At noon" in Hindi
        TIME_ADJUSTMENTS.put("shaam ko", now -> now.withHour(18)); // "In the evening" in Hindi; 6 PM


        // Upcoming weekday adjustments, starting from today
        TIME_ADJUSTMENTS.put("monday", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0)); // Next or same Monday
        TIME_ADJUSTMENTS.put("tuesday", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.TUESDAY)).withHour(0).withMinute(0).withSecond(0)); // Next or same Tuesday
        TIME_ADJUSTMENTS.put("wednesday", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.WEDNESDAY)).withHour(0).withMinute(0).withSecond(0)); // Next or same Wednesday
        TIME_ADJUSTMENTS.put("thursday", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.THURSDAY)).withHour(0).withMinute(0).withSecond(0)); // Next or same Thursday
        TIME_ADJUSTMENTS.put("friday", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.FRIDAY)).withHour(0).withMinute(0).withSecond(0)); // Next or same Friday
        TIME_ADJUSTMENTS.put("saturday", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SATURDAY)).withHour(0).withMinute(0).withSecond(0)); // Next or same Saturday
        TIME_ADJUSTMENTS.put("sunday", now -> now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).withHour(0).withMinute(0).withSecond(0)); // Next or same Sunday
        TIME_ADJUSTMENTS.put("afternoon", now -> now.withHour(15).withMinute(0)); // 3 PM today
        TIME_ADJUSTMENTS.put("evening", now -> now.withHour(18).withMinute(0)); // 6 PM today
        TIME_ADJUSTMENTS.put("morning", now -> now.withHour(9).withMinute(0)); // 9 AM today



    }

    // Method to remove time-related phrases and specific times from the input sentence
    public String removeTimePhrases(String input) {
        // Convert the input to lowercase for case-insensitive keyword matching
        String modifiedInput = input.toLowerCase();

        // Remove all time-related keywords from the input
        for (String keyword : TIME_ADJUSTMENTS.keySet()) {
            if (modifiedInput.contains(keyword)) {
                modifiedInput = modifiedInput.replace(keyword, "").trim(); // Remove keyword and trim spaces
            }
        }

        modifiedInput = modifiedInput.replace("." , "").replace("at" , "");
        // Remove any specific times matching the TIME_REGEX pattern
        modifiedInput = modifiedInput.replaceAll(TIME_REGEX, "").trim(); // Remove matched times and trim spaces

        // Return the cleaned input without time-related phrases and specific times
        System.out.println("removeTimePhrases :  "+ modifiedInput);
        return modifiedInput;
    }

}



/*
this is code where i am  getting time and date by the user told times like
tomorroe tofay etc but now i want tocreate one method in it where i want
 sentence pu by user but without that phrase of time like tomorrow today etc
 */
