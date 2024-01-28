package com.denknd.validator;

public class DoubleDigitalValidator implements IValidator {

    @Override
    public String nameValidator() {
        return IValidator.DOUBLE_TYPE;
    }

    @Override
    public boolean isValidation(String value) {
        return value.matches("\\d+(\\.\\d+)?");

    }
}
