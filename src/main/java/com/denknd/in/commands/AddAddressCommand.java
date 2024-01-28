package com.denknd.in.commands;

import com.denknd.entity.Address;
import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.services.AddressService;
import com.denknd.validator.IValidator;
import com.denknd.validator.Validators;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class AddAddressCommand implements ConsoleCommand<String> {

    private final String COMMAND = "add_addr";
    private final AddressService addressService;
    private final Validators validators;
    private final Scanner scanner;


    @Override
    public String getCommand() {
        return this.COMMAND;
    }

    @Override
    public String getMakesAction() {
        return "Добавляет адрес к пользователю";
    }

    @Override
    public String run(String command, User userActive) {
        if (userActive == null) {
            return null;
        }
        if (command.equals(this.COMMAND)) {
            var region = this.validators.isValid("Введите ваш регион: ", IValidator.REGION_TYPE, "Данного региона не найдено", scanner);
            var city = this.validators.isValid("Введите ваш город: ", IValidator.TITLE_TYPE, "Название города введено не корректно", scanner);
            var street = this.validators.isValid("Введите улицу: ", IValidator.TITLE_TYPE, "Название улицы введено не корректно", scanner);
            var house = this.validators.isValid("Введите номер дома: ", IValidator.HOUSE_NUMBER_TYPE, "Номер дома введен не корректно", scanner);
            var apartment = this.validators.isValid("Введите норме квартиры(если номера квартиры нажмите Enter): ", "", "", scanner);
            var postalCodeString = this.validators.isValid("Введите ваш почтовый индекс: ", IValidator.POSTAL_CODE_TYPE, "Почтовый индекс должен быть из 6 цифр", scanner);


            if (!this.validators.notNullValue(region, city, street, house, postalCodeString)) {
                return "Данные введены не корректно";
            }
            var postalCode = Long.parseLong(postalCodeString);
            var address = Address.builder()
                    .region(region)
                    .city(city)
                    .street(street)
                    .house(house)
                    .apartment(apartment)
                    .postalCode(postalCode)
                    .owner(userActive).build();
            var addressByUser = this.addressService.addAddressByUser(address);

            var addresses = userActive.getAddresses();
            List<Address> newAddresses;
            if (addresses == null) {
                newAddresses = new ArrayList<>();
            } else {
                newAddresses = new ArrayList<>(addresses);

            }
            newAddresses.add(addressByUser);
            userActive.setAddresses(List.copyOf(newAddresses));

            return "Адрес добавлен( " + address + ")";

        } else {
            return "Команда не поддерживается: " + command;
        }
    }


    @Override
    public String getHelpCommand(List<Role> roles) {
        if (roles.isEmpty()) {
            return null;
        }
        if (roles.contains(Role.builder().roleName("USER").build()))
            return this.COMMAND + " - используется для добавления адреса подачи показаний";
        return null;
    }
}
