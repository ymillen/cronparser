package com.deliveroo;

import java.util.*;

public class CronParser {
    public void parseExpression(String input) {
        //Tokenize
        String[] tokenizedInput = input.trim().split(" ");

        //Validate
        /*Number of arguments*/
        if(tokenizedInput.length < 6)
            printErrorAndTerminate("Invalid number of arguments!");

        //Parse
        StringBuilder resultSB = new StringBuilder();
        /* Minutes */
        String minutes_raw = tokenizedInput[0];
        try {
            resultSB.append("minute        " + parseRawTimeUnit(minutes_raw, 0, 59));
        } catch (Exception e) {
            printErrorAndTerminate(e.getMessage() + " in MINUTES parameter!");
        }
        resultSB.append(System.getProperty("line.separator"));

        /* Hours */
        String hours_raw = tokenizedInput[1];
        try {
            resultSB.append("hour          " + parseRawTimeUnit(hours_raw, 0, 23));
        } catch (Exception e) {
            printErrorAndTerminate(e.getMessage() + " in HOURS parameter!");
        }
        resultSB.append(System.getProperty("line.separator"));

        /* Days of month */
        String daysOfMonths_raw = tokenizedInput[2];
        try {
            resultSB.append("day of month  " + parseRawTimeUnit(daysOfMonths_raw, 1, 31));
        } catch (Exception e) {
            printErrorAndTerminate(e.getMessage() + " in DAY-OF-MONTH parameter!");
        }
        resultSB.append(System.getProperty("line.separator"));

        /* Months */
        String months_raw = tokenizedInput[3];
        try {
            resultSB.append("month         " + parseMonths(months_raw));
        } catch (Exception e) {
            printErrorAndTerminate(e.getMessage() + " in MONTH parameter!");
        }
        resultSB.append(System.getProperty("line.separator"));

        /* Days of week */
        String daysOfWeek_raw = tokenizedInput[4];
        try {
            resultSB.append("day of week   " + parseDaysOfWeek(daysOfWeek_raw));
        } catch (Exception e) {
            printErrorAndTerminate(e.getMessage() + " in DAY-OF-WEEK parameter!");
        }
        resultSB.append(System.getProperty("line.separator"));

        /* Command */
        String command = tokenizedInput[5];
        for (int arg=6; arg<tokenizedInput.length; arg++) {
            command = command + " " + tokenizedInput[arg];
        }
        resultSB.append("command       " + command);
        resultSB.append(System.getProperty("line.separator"));

        //Print result
        System.out.println(resultSB);
    }

    public String parseMonths(String input) {
        input = input.replaceAll("JAN", "1");
        input = input.replaceAll("FEB", "2");
        input = input.replaceAll("MAR", "3");
        input = input.replaceAll("APR", "4");
        input = input.replaceAll("MAY", "5");
        input = input.replaceAll("JUN", "6");
        input = input.replaceAll("JUL", "7");
        input = input.replaceAll("AUG", "8");
        input = input.replaceAll("SEP", "9");
        input = input.replaceAll("OCT", "10");
        input = input.replaceAll("NOV", "11");
        input = input.replaceAll("DEC", "12");

        return parseRawTimeUnit(input, 1, 12);
    }

    public String parseDaysOfWeek(String input) {
        input = input.replaceAll("SON", "0");
        input = input.replaceAll("MON", "1");
        input = input.replaceAll("TUE", "2");
        input = input.replaceAll("WED", "3");
        input = input.replaceAll("THU", "4");
        input = input.replaceAll("FRI", "5");
        input = input.replaceAll("SAT", "6");

        return parseRawTimeUnit(input, 0, 6);
    }

    public String parseRawTimeUnit(String input, int minValue, int maxValue){
        //Tokenize
        String[] intervals = input.split(",");

        //Validate
        if(intervals.length == 0)
            throw new RuntimeException("Invalid value");

        //Generate all cron initiation times for every interval
        Set<Integer> setOfInitiationTimes = new HashSet<>();
        for (String interval : intervals) {
            List<Integer> listOfValuesForInterval = generateCronValuesForInterval(interval, minValue, maxValue);
            setOfInitiationTimes.addAll(listOfValuesForInterval);
        }

        //Covert to list and sort
        List<Integer> finalValuesList = new ArrayList<>(setOfInitiationTimes);
        finalValuesList.sort((x1, x2) -> x1 - x2);

        //Covert to string
        StringBuilder sb = new StringBuilder();
        for (Integer value : finalValuesList) {
            sb.append(" " + value);
        }

        //Return result
        return sb.toString().trim();
    }

    private List<Integer> generateCronValuesForInterval(String input, int minValue, int maxValue) {
        /*Single value*/
        if(input.matches("\\d+")) {
            int singleValue = Integer.valueOf(input);
            if(isValidNumericalValue(singleValue, minValue, maxValue))
                return generateCronValuesForRange(Integer.valueOf(input), Integer.valueOf(input), 1);
            else
                throw new RuntimeException("Invalid single value");
        }

        /*All values*/
        if(input.matches("\\*"))
            return generateCronValuesForRange(minValue, maxValue, 1);

        /*All values with step*/
        if(input.matches("\\*/\\d+")) {
            return generateCronValuesForRange(minValue, maxValue, Integer.valueOf(input.split("/")[1]));
        }

        /*Range*/
        if(input.matches("\\d+-\\d+")) {
            int range_from = Integer.valueOf(input.split("-")[0]);
            int range_to = Integer.valueOf(input.split("-")[1]);

            if(isValidNumericalValue(range_from, minValue, maxValue) &&
               isValidNumericalValue(range_to, minValue, maxValue) &&
               range_from <= range_to
            )
                return generateCronValuesForRange(range_from, range_to, 1);
            else
                throw new RuntimeException("Invalid value in range");
        }

        /*Range with step*/
        if(input.matches("\\d+-\\d+/\\d+")) {
            String interval = input.split("/")[0];
            int range_from = Integer.valueOf(interval.split("-")[0]);
            int range_to = Integer.valueOf(interval.split("-")[1]);
            int step = Integer.valueOf(input.split("/")[1]);

            if(isValidNumericalValue(range_from, minValue, maxValue) &&
               isValidNumericalValue(range_to, minValue, maxValue) &&
               range_from <= range_to
            )
                return generateCronValuesForRange(range_from, range_to, step);
            else
                throw new RuntimeException("Invalid value in range");
        }

        /*Value with step*/
        if(input.matches("\\d+/\\d+")) {
            int range_from = Integer.valueOf(input.split("/")[0]);
            int step = Integer.valueOf(input.split("/")[1]);

            if(isValidNumericalValue(range_from, minValue, maxValue))
                return generateCronValuesForRange(range_from, maxValue, step);
            else
                throw new RuntimeException("Invalid value with step");
        }

        throw new RuntimeException("Invalid expression");
    }

    private List<Integer> generateCronValuesForRange(int from, int to, int step) {
        List<Integer> listOfValues = new ArrayList<>();

        for (int i=from; i<=to; i+=step)
            listOfValues.add(i);

        return listOfValues;
    }


    /*********************************
     *  Validators
     */
    private boolean isValidNumericalValue(int value, int minValue, int maxValue){
        return (value >= minValue && value <= maxValue);
    }

    /*********************************
     *  Error handlers
     */
    public void printErrorAndTerminate(String errorMessage){
        System.out.println(errorMessage);
        printUsage();
        System.exit(0);
    }

    private void printUsage(){
        System.out.println("""
        =====================
        USAGE
        =====================
        Valid operators:
        *	    any value
        ,	    value list separator
        -	    range of values
        /	    step values
        ---------------------
        Valid ranges:
        0-59              minutes
        0-23              hours
        1-31              days of month
        1-12 or JAN-DEC   months
        0-6 or SUN-SAT    day of week (0 = Sunday)
        """);
    }
}
