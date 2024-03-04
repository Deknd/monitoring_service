package com.denknd.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BadCredentialsException extends Exception{
  private final String message;
}
