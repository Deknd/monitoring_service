package com.denknd.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements IValidator{

    private final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private final Pattern EMAIL_PATTERN = Pattern.compile(this.EMAIL_REGEX);


    @Override
    public String nameValidator() {
        return IValidator.EMAIL_TYPE;
    }

    @Override
    public boolean isValidation(String email) {
        if (email == null){
            return false;
        }
        Matcher matcher = this.EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
}
