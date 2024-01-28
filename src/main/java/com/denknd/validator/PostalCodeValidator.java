package com.denknd.validator;

import java.util.regex.Pattern;

public class PostalCodeValidator implements IValidator{

    private static final String POSTAL_CODE_PATTERN = "^\\d{6}$";
    private static final Pattern pattern = Pattern.compile(POSTAL_CODE_PATTERN);


    @Override
    public String nameValidator() {
        return IValidator.POSTAL_CODE_TYPE;
    }

    @Override
    public boolean isValidation(String value) {
        if(value == null){
            return false;
        }
        return pattern.matcher(value).matches();
    }
}
