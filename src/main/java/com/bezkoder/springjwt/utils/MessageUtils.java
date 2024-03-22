package com.bezkoder.springjwt.utils;

public class MessageUtils {

    public static String OK = "OK";
    public static String ERROR = "ERROR";
    public static String WARNING = "WARNING";
    public static String INFO = "INFO";
    public static String SUCCESS = "SUCCESS";

    public static String buildMessage(String message, String type) {
        return "{\"message\": \""+message+"\", \"type\": \""+type+"\"}";
    }

}
