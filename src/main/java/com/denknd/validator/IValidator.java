package com.denknd.validator;

public interface IValidator {
     String EMAIL_TYPE = "email";
    String NAME_TYPE = "name";
    String PASSWORD_TYPE = "password";
    String REGION_TYPE = "region";
    String TITLE_TYPE = "title";
    String HOUSE_NUMBER_TYPE = "house_number";
    String POSTAL_CODE_TYPE = "postal_code";
    String DIGITAL_TYPE = "numeric";
    String DOUBLE_TYPE = "double";

    String nameValidator();

    boolean isValidation(String value);
}
