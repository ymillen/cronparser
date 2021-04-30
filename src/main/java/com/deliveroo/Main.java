package com.deliveroo;

public class Main {
    public static void main(String[] args) {
        CronParser cronParser = new CronParser();
        if (args.length != 1)
            cronParser.printErrorAndTerminate("Invalid number of arguments!");
        else
            cronParser.parseExpression(args[0]);
    }
}