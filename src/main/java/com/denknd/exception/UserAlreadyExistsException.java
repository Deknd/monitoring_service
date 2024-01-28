package com.denknd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserAlreadyExistsException extends Exception {
    private final String message;
}
