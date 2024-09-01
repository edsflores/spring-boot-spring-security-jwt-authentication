package com.taquigrafia.ai.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.taquigrafia.ai.payload.response.MessageResponse;

public class MessageUtils {

    public static final String OK = "OK";
    public static final String ERROR = "ERROR";
    public static final String WARNING = "WARNING";
    public static final String INFO = "INFO";
    public static final String SUCCESS = "SUCCESS";

    public static ResponseEntity<MessageResponse> buildErrorMessage(String message, String type) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(message, type));
    }

    public static ResponseEntity<MessageResponse> buildMessage(String message, String type) {
        return ResponseEntity.ok().body(new MessageResponse(message, type));
    }

}
