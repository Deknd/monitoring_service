package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.TypeMeter;
import com.denknd.entity.User;
import com.denknd.services.TypeMeterService;
import com.denknd.validator.IValidator;
import com.denknd.validator.Validators;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class AddTypeMeterCommand implements ConsoleCommand<String> {

    private final String COMMAND = "add_type";
    private final Validators validators;
    private final Scanner scanner;
    private final TypeMeterService typeMeterService;

    @Override
    public String getCommand() {
        return this.COMMAND;
    }

    @Override
    public String getMakesAction() {
        return "Добавляет новый тип показаний в БД";
    }

    @Override
    public String run(String command, User userActive) {
        if (userActive == null) {
            return null;
        }
        if (userActive.getRoles().contains(Role.builder().roleName("ADMIN").build())) {
            var typeCode = this.validators.isValid(
                    "Введите код консоли для нового типа: ",
                    IValidator.TITLE_TYPE,
                    "Код обязателен, служит для добавления данных",
                    this.scanner);
            var description = this.validators.isValid(
                    "Введите описание кратко(будет видно при вызове подсказки): ",
                    IValidator.TITLE_TYPE,
                    "Описание является обязательным",
                    this.scanner
                    );
            var metric = this.validators.isValid(
                    "Введите единицу измерения данного типа: ",
                    IValidator.TITLE_TYPE,
                    "Единица измерения обязательна",
                    this.scanner
            );

            if(!this.validators.notNullValue(typeCode, description, metric)){
                return "Не введены обязательные поля";
            }
            var typeMeter = TypeMeter.builder()
                    .typeCode(typeCode)
                    .typeDescription(description)
                    .metric(metric)
                    .build();
            var newTypeMeter = this.typeMeterService.addNewTypeMeter(typeMeter);

            return "Новый тип показаний добавлен: "+newTypeMeter.getTypeCode()+" - "+newTypeMeter.getTypeDescription();
        }
        return null;
    }

    @Override
    public String getHelpCommand(List<Role> roles) {
        if (roles == null || roles.isEmpty()){
            return null;
        }
        if(roles.contains(Role.builder().roleName("ADMIN").build())){
            return this.COMMAND+" служит для добавления новых типов показаний";
        }
        return null;
    }
}
