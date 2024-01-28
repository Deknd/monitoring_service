package com.denknd.validator;

import java.util.*;

public class ValidatorsImpl implements Validators{
   private final Map<String, List<IValidator>> validators = new HashMap<>();


    @Override
    public void addValidator(IValidator... iValidator) {
        for (IValidator validator : iValidator){
            var nameValidator = validator.nameValidator();
            if(this.validators.containsKey(nameValidator)){
                var iValidators = this.validators.get(nameValidator);
                iValidators.add(validator);
            } else {
                var iValidators = new ArrayList<IValidator>();
                iValidators.add(validator);
                this.validators.put(nameValidator, iValidators);
            }
        }

    }
    @Override
    public String isValid(String dataPrint, String validator, String exception, Scanner scanner) {
        String value;
        var valid = true;
        var count = 0L;
        do {
            count++;
            if(count > 3){
                return null;
            }
            System.out.print(dataPrint);
            value = scanner.nextLine();
            if(this.validators.containsKey(validator)){
                final var currentValue = value;
                var iValidators = this.validators.get(validator);
                valid = iValidators.stream().allMatch(iValidator -> iValidator.isValidation(currentValue));
            }
            if (!valid) {
                System.out.println(exception);
            }
        } while (!valid );
        return value;
    }

    @Override
    public boolean notNullValue(String... values){
        for(var value : values){
            if(value == null || value.isEmpty()){
                return false;
            }
        }
        return true;
    }
}
