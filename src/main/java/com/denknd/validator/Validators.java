package com.denknd.validator;

import java.util.Scanner;

public interface Validators {

    String isValid(String dataPrint, String validator, String exception, Scanner scanner);

    void addValidator(IValidator... iValidator);

    boolean notNullValue(String... values);
}
