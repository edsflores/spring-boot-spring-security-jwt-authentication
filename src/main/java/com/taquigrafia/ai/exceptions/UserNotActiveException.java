package com.taquigrafia.ai.exceptions;

public class UserNotActiveException extends RuntimeException {
  public UserNotActiveException(String message) {
    super(message);
  }
}
