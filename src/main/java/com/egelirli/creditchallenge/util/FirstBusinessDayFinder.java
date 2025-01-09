package com.egelirli.creditchallenge.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class FirstBusinessDayFinder {
    
    // Sample holiday list 
    private static Set<LocalDate> getHolidays(int year) {
        Set<LocalDate> holidays = new HashSet<>();
        
        // Add fixed-date holidays
        holidays.add(LocalDate.of(year, 1,  1));    // New Year's Day
        holidays.add(LocalDate.of(year, 4,  23));   // Children's Day 
        holidays.add(LocalDate.of(year, 5,  1));    // Labour Day
        holidays.add(LocalDate.of(year, 5,  19));   // Ataturk Memorial And Youth Day
        holidays.add(LocalDate.of(year, 7,  15));   // National Unity Day
        holidays.add(LocalDate.of(year, 8,  31));   // Victory Day  
        holidays.add(LocalDate.of(year, 10, 29));  // Republic Day
        
        // Add variable-date holiday (e.g., Ramazan, Kurban vs) get it from web service
        //if (year == 2025) { 
        //    holidays.add(LocalDate.of(2023,11,23)); // Thanksgiving in the US - fourth Thursday in November
        //}
        
        return holidays;
    }

    public static LocalDate getFirstBusinessDay(int year, int month) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        
        Set<LocalDate> holidaySet = getHolidays(year);
        
      	// Check if it's a weekend or a holiday
    	while (firstDayOfMonth.getDayOfWeek() == DayOfWeek.SATURDAY ||
                firstDayOfMonth.getDayOfWeek() == DayOfWeek.SUNDAY || 
                holidaySet.contains(firstDayOfMonth)) {
            firstDayOfMonth = firstDayOfMonth.plusDays(1);
    	}
    	
    	return firstDayOfMonth;
    }

    public static void main(String[] args) {
      int year = 2025; 
      int month = 5; 

      LocalDate businessDay = getFirstBusinessDay(year, month);

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      
      System.out.println("The first business day of " + year + "-" + String.format("%02d",month) + " is: " + businessDay.format(formatter));
   }
}
