package com.denknd.validator;

import java.util.regex.Pattern;

public class HouseNumberValidator implements IValidator{
    private static final String HOUSE_NUMBER_PATTERN = "^[\\d]+[a-zA-Z]{0,1}[-/\\w]*$";
    private static final Pattern pattern = Pattern.compile(HOUSE_NUMBER_PATTERN);

    @Override
    public String nameValidator() {
        return IValidator.HOUSE_NUMBER_TYPE;
    }

    @Override
    public boolean isValidation(String value) {
        if (value == null || value.isEmpty()){
            return false;
        }
        return pattern.matcher(value).matches();
    }
}
