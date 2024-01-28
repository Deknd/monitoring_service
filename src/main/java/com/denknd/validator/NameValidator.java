package com.denknd.validator;

public class NameValidator implements IValidator{

    private static final int MIN_LENGTH = 2;


    @Override
    public String nameValidator() {
        return IValidator.NAME_TYPE;
    }

    @Override
    public boolean isValidation(String name) {
        if (name == null) {
            return false;
        }
        if (name.length() < MIN_LENGTH) {
            return false;
        }
        if (!name.matches("[a-zA-Z\\-\\s]+")) {
            return false;
        }
        return true;
    }
}
