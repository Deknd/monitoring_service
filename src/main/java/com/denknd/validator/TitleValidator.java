package com.denknd.validator;

public class TitleValidator implements IValidator{

    private static final int MIN_LENGTH = 2;


    @Override
    public String nameValidator() {
        return IValidator.TITLE_TYPE;
    }

    @Override
    public boolean isValidation(String value) {
        if (value == null) {
            return false;
        }
        return value.length() >= MIN_LENGTH;

    }
}
