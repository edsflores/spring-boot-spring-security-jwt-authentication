package com.bezkoder.springjwt.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MessageUtils {

    public static String OK = "OK";
    public static String ERROR = "ERROR";
    public static String WARNING = "WARNING";
    public static String INFO = "INFO";
    public static String SUCCESS = "SUCCESS";

    public static ResponseEntity buildErrorMessage(String message, String type) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body("{\"message\": \""+message+"\", \"type\": \""+type+"\"}");
    }


    public static ResponseEntity buildMessage(String message, String type) {
        return ResponseEntity
            .ok()
            .body("{\"message\": \""+message+"\", \"type\": \""+type+"\"}");
    }

}
