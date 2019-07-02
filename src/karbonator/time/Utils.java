package karbonator.time;

public class Utils {
    public static final int DAY_COUNT_TO_YEAR1970 = getDayCountToYear(1970);
    
    public static final int getLeapYearCountFromYear(int year) {
        return (int)Math.floor(LEAP_YEAR_COUNT_PER_YEAR_FACTOR * year);
    };
    
    public static final int getYearOfDayCount(int dayCount) {
        return (int)Math.floor((YEAR_COUNT_PER_YEAR_CYCLE * dayCount) / DAY_COUNT_PER_YEAR_CYCLE) + 1;
    };
    
    public static final int getDayCountToYear(int year) {
        --year;
        
        return DAY_COUNT_PER_YEAR * year + (year / 400) - (year / 100) + (year >> 2);
    };
    
    public static boolean isLeapYear(int year) {
        return (((year & 0x03) == 0) && (year % 100 != 0)) || (year % 400 == 0);
    };
    
    public static int getDayCountOfMonth(int year, int month) {
        month = assertIsMonthValid(month);
        
        return DAY_COUNTS_PER_MONTH[month - 1] + ((month == 2 && isLeapYear(year)) ? 1 : 0);
    };
    
    public static int getDayOfWeekIndex(int year, int month, int day) {
        month = assertIsMonthValid(month);
        day = assertIsDayValid(day);
        
        switch(month) {
        case 1:
        case 2:
            month += 12;
            --year;
        break;
        }
        
        int j = (int)Math.floor(year / 100);
        int k = (int)(year % 100);
        
        int dowIndex = day + ((int)Math.floor((13 * (month + 1)) / 5.0) + k + (k >> 2) + (j >> 2) + (5 * j));
        
        return (dowIndex - 1) % 7;
    };
    
    private static final int[] DAY_COUNTS_PER_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    
    private static final int DAY_COUNT_PER_YEAR = 365;
    
    private static final int YEAR_COUNT_PER_YEAR_CYCLE = 400;
    
    private static final int LEAP_YEAR_COUNT_PER_YEAR_CYCLE = 97;
    
    private static final int DAY_COUNT_PER_YEAR_CYCLE = DAY_COUNT_PER_YEAR * YEAR_COUNT_PER_YEAR_CYCLE + LEAP_YEAR_COUNT_PER_YEAR_CYCLE;
    
    private static final double LEAP_YEAR_COUNT_PER_YEAR_FACTOR = LEAP_YEAR_COUNT_PER_YEAR_CYCLE / (double)YEAR_COUNT_PER_YEAR_CYCLE;
    
    private static final int assertIsMonthValid(int month) {
        if(month < 1 || month > 12) {
            throw new RuntimeException("'month' must be in the range [1, 12].");
        }
        
        return month;
    };
    
    private static final int assertIsDayValid(int day) {
        return assertIsDayValid(day, 1970, 12);
    }
    
    private static final int assertIsDayValid(int day, int year, int month) {
        if(day < 0) {
            throw new RuntimeException("'day' must be a non-negative safe integer.");
        }
        
        final int dayCount = getDayCountOfMonth(year, assertIsMonthValid(month));
        if(day < 1 || day > dayCount) {
            throw new RuntimeException("'day' must be in the range [1, " + dayCount + "].");
        }
        
        return day;
    };
    
    private Utils() {}
}
