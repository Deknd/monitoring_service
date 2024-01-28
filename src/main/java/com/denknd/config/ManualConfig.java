package com.denknd.config;

import com.denknd.adapter.encoder.Sha256Encode;
import com.denknd.adapter.repository.*;
import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.in.Console;
import com.denknd.in.commands.*;
import com.denknd.in.commands.MeterValuesCommand;
import com.denknd.services.impl.*;
import com.denknd.validator.*;

import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.Scanner;

public class ManualConfig {
    private final Console console;

    public ManualConfig() {
        var testUser = User.builder().email("test@mail.com").password("123").firstName("TestName").userId(1L).lastName("TestLastName").build();
        var roleForTestUser = Role.builder().roleName("USER").build();
        var testAddress = Address.builder().owner(testUser).postalCode(184040L).region("Мурманская область").city("Кандалакша").house("6").apartment("1").build();
        var testData = MeterReading.builder().address(testAddress).meterValue(2133.123).submissionMonth(YearMonth.now()).timeSendMeter(OffsetDateTime.now()).build();

        var testAdmin = User.builder().email("admin@admin.com").password("123").firstName("Admin").userId(2L).lastName("LastName").build();
        var roleAdmin = Role.builder().roleName("ADMIN").build();

        var scanner = new Scanner(System.in);
        var passwordEncoder = new Sha256Encode();

        var digitalValidator = new DigitalValidator();
        var doubleDigitalValidator = new DoubleDigitalValidator();
        var emailValidator = new EmailValidator();
        var houseNumberValidator = new HouseNumberValidator();
        var nameValidator = new NameValidator();
        var passwordValidator = new PasswordValidator();
        var postalCodeValidator = new PostalCodeValidator();
        var regionValidator = new RegionValidator();
        var titleValidator = new TitleValidator();


        var validators = new ValidatorsImpl();
        validators.addValidator(
                digitalValidator,
                doubleDigitalValidator,
                emailValidator,
                houseNumberValidator,
                nameValidator,
                passwordValidator,
                postalCodeValidator,
                regionValidator,
                titleValidator);

        var userRepository = new InMemoryUserRepository();
        var roleRepository = new InMemoryRoleRepository();
        var addressRepository = new InMemoryAddressRepository();
        var meterReadingRepository = new InMemoryMeterRepository();
        var typeMeterRepository = new InMemoryTypeMeterRepository();
        var auditRepository = new InMemoryAuditRepository();

        var userService = new UserServiceImpl(userRepository, passwordEncoder);
        try {
            testUser = userService.registrationUser(testUser);
            testAdmin = userService.registrationUser(testAdmin);
            System.out.println("Тестовые данные:\n   " +
                    "User:  id - "+testUser.getUserId()+", email - "+testUser.getEmail()+"   password - 123\n   " +
                    "Admin: id - "+testAdmin.getUserId()+", email - "+testAdmin.getEmail()+" password - 123");
        } catch (UserAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
        var roleService = new RoleServiceImpl(roleRepository);
        roleService.addRoles(testUser.getUserId(), roleForTestUser);
        roleService.addRoles(testAdmin.getUserId(), roleAdmin);
        var addressService = new AddressServiceImpl(addressRepository);
        addressService.addAddressByUser(testAddress);
        var typeMeterService = new TypeMeterServiceImpl(typeMeterRepository);
        var type = typeMeterService.getTypeMeter().get(0);
        var meterReadingService = new MeterReadingServiceImpl(meterReadingRepository);
        testData.setTypeMeter(type);
        meterReadingService.addMeterValue(testData);
        var auditService = new AuditServiceImpl(auditRepository);

        this.console = new Console(scanner, auditService);

        var addAddressCommand = new AddAddressCommand(addressService, validators, scanner);
        var addTypeMeterCommand = new AddTypeMeterCommand(validators, scanner, typeMeterService);
        var exitCommand = new ExitCommand(scanner);
        var helpCommand = new HelpCommand(this.console);
        var historyCommand = new HistoryCommand(addressService, meterReadingService, typeMeterService, userService);
        var loginCommand = new LoginCommand(userService, roleService, validators, scanner);
        var logoutCommand = new LogoutCommand();
        var meterSendCommand = new MeterSendCommand(typeMeterService, addressService, meterReadingService, validators, scanner);
        var meterValuesCommand = new MeterValuesCommand(addressService, meterReadingService, typeMeterService, userService);
        var signupCommand = new SignupCommand(userService, roleService, validators, scanner );
        var userCommand = new UserCommand(userService);
        var getAddressCommand = new GetAddressCommand(addressService, userService);

        this.console.addCommand(signupCommand, exitCommand,
                helpCommand, loginCommand, logoutCommand,
                addAddressCommand, meterSendCommand, meterValuesCommand,
                historyCommand, addTypeMeterCommand, userCommand,
                getAddressCommand);

    }


    public Console console() {
        return this.console;
    }
}
