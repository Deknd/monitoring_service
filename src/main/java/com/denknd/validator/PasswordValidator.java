package com.denknd.validator;

public class PasswordValidator implements IValidator{
    private static final int MIN_LENGTH = 2;


    @Override
    public String nameValidator() {
        return IValidator.PASSWORD_TYPE;
    }

    @Override
    public boolean isValidation(String password) {
        return password!= null && password.length() > MIN_LENGTH;
    }
}
