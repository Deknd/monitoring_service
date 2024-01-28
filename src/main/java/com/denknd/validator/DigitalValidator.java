package com.denknd.validator;

public class DigitalValidator implements IValidator{
    @Override
    public String nameValidator() {
        return IValidator.DIGITAL_TYPE;
    }

    @Override
    public boolean isValidation(String value) {
        return value.matches("\\d+");
    }
}
