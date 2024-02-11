package com.denknd.config;

import com.denknd.controllers.AddressController;
import com.denknd.controllers.MeterReadingController;
import com.denknd.controllers.TypeMeterController;
import com.denknd.controllers.UserController;
import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.Roles;
import com.denknd.entity.User;
import com.denknd.exception.MeterReadingConflictError;
import com.denknd.in.Console;
import com.denknd.in.commands.AddAddressCommand;
import com.denknd.in.commands.AddTypeMeterCommand;
import com.denknd.in.commands.ExitCommand;
import com.denknd.in.commands.GetAddressCommand;
import com.denknd.in.commands.HelpCommand;
import com.denknd.in.commands.HistoryCommand;
import com.denknd.in.commands.LoginCommand;
import com.denknd.in.commands.LogoutCommand;
import com.denknd.in.commands.MeterSendCommand;
import com.denknd.in.commands.MeterValuesCommand;
import com.denknd.in.commands.SignupCommand;
import com.denknd.in.commands.UserCommand;
import com.denknd.mappers.AddressMapper;
import com.denknd.mappers.MeterReadingMapper;
import com.denknd.mappers.TypeMeterMapper;
import com.denknd.mappers.UserMapper;
import com.denknd.out.audit.AuditService;
import com.denknd.out.audit.AuditServiceImpl;
import com.denknd.out.audit.PostgresAuditRepository;
import com.denknd.repository.impl.PostgresAddressRepository;
import com.denknd.repository.impl.PostgresMeterReadingRepository;
import com.denknd.repository.impl.PostgresTypeMeterRepository;
import com.denknd.repository.impl.PostgresUserRepository;
import com.denknd.security.SecurityService;
import com.denknd.security.SecurityServiceImpl;
import com.denknd.security.UserSecurityServiceImpl;
import com.denknd.services.MeterReadingService;
import com.denknd.services.TypeMeterService;
import com.denknd.services.impl.AddressServiceImpl;
import com.denknd.services.impl.MeterReadingServiceImpl;
import com.denknd.services.impl.TypeMeterServiceImpl;
import com.denknd.services.impl.UserServiceImpl;
import com.denknd.util.DataBaseConnection;
import com.denknd.util.impl.DataBaseConnectionImpl;
import com.denknd.util.impl.LiquibaseMigration;
import com.denknd.util.impl.Sha256PasswordEncoderImpl;
import com.denknd.util.impl.YamlParserImpl;
import com.denknd.validator.DataValidatorManager;
import com.denknd.validator.DataValidatorManagerImpl;
import com.denknd.validator.DigitalValidator;
import com.denknd.validator.DoubleDigitalValidator;
import com.denknd.validator.EmailValidator;
import com.denknd.validator.HouseNumberValidator;
import com.denknd.validator.NameValidator;
import com.denknd.validator.PasswordValidator;
import com.denknd.validator.PostalCodeValidator;
import com.denknd.validator.RegionValidator;
import com.denknd.validator.TitleValidator;

import java.io.FileNotFoundException;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.Scanner;

/**
 * Собирает весь контекст приложения.
 */
public class ManualConfig {
  /**
   * Главный класс интерфейса.
   */
  private final Console console;

  /**
   * Собирает весь контекст приложения.
   */
  public ManualConfig(String yamlPath, DataBaseConnection dataBaseConnectionArg) throws FileNotFoundException, ClassNotFoundException {
    var scanner = new Scanner(System.in);
    var passwordEncoder = new Sha256PasswordEncoderImpl();
    var yamlParser = new YamlParserImpl();
    if(yamlPath != null){
      yamlParser.setPathToApplicationYml(yamlPath);
    }
    DataBaseConnection dataBaseConnection;
    if (dataBaseConnectionArg != null){
      dataBaseConnection = dataBaseConnectionArg;
    }else {
      dataBaseConnection = new DataBaseConnectionImpl(yamlParser.dbConfig());
    }
    new LiquibaseMigration(dataBaseConnection, yamlParser.liquibaseConfig()).migration();

    var validators = createValidators(scanner);

    var addressMapper = AddressMapper.INSTANCE;
    var meterReadingMapper = MeterReadingMapper.INSTANCE;
    var typeMeterMapper = TypeMeterMapper.INSTANCE;
    var userMapper = UserMapper.INSTANCE;

    var userService = new UserServiceImpl(new PostgresUserRepository(dataBaseConnection), passwordEncoder);
    var addressService = new AddressServiceImpl(new PostgresAddressRepository(dataBaseConnection));
    var typeMeterService = new TypeMeterServiceImpl(new PostgresTypeMeterRepository(dataBaseConnection));
    var meterReadingService = new MeterReadingServiceImpl(new PostgresMeterReadingRepository(dataBaseConnection), typeMeterService);
    var auditService = new AuditServiceImpl(new PostgresAuditRepository(dataBaseConnection));
    var securityService = new SecurityServiceImpl(new UserSecurityServiceImpl(userService, userMapper), passwordEncoder);

    this.addTestUsers( meterReadingService, typeMeterService);

    var addressController = new AddressController(addressService, userService, addressMapper);
    var meterReadingController = new MeterReadingController(meterReadingService, addressService, typeMeterService, meterReadingMapper);
    var typeMeterController = new TypeMeterController(typeMeterService, typeMeterMapper);
    var userController = new UserController(userService, userMapper);

    this.console = this.createConsole(
            scanner,
            auditService,
            securityService,
            addressController,
            validators,
            typeMeterController,
            meterReadingController,
            userController);

  }

  /**
   * Собирает консоль, добавляет доступные команды
   *
   * @param scanner                сканер, для работы с консолью
   * @param auditService           сервис для отправки логов
   * @param securityService        сервис для аутентификации и авторизации
   * @param addressController      контроллер для управления адресами
   * @param dataValidatorManager   валидатор данных
   * @param typeMeterController    контроллер для работы с типами показаний
   * @param meterReadingController контроллер для работы с показаниями
   * @param userController         контроллер для работы с пользователями
   * @return возвращает настроенную консоль
   */
  private Console createConsole(Scanner scanner, AuditService auditService, SecurityService securityService,
                                AddressController addressController, DataValidatorManager dataValidatorManager,
                                TypeMeterController typeMeterController, MeterReadingController meterReadingController,
                                UserController userController) {
    var console = new Console(scanner, auditService, securityService);

    var addAddressCommand = new AddAddressCommand(addressController, dataValidatorManager);
    var addTypeMeterCommand = new AddTypeMeterCommand(dataValidatorManager, typeMeterController);
    var exitCommand = new ExitCommand(scanner);
    var historyCommand = new HistoryCommand(
            typeMeterController,
            meterReadingController);
    var loginCommand = new LoginCommand(dataValidatorManager, securityService);
    var logoutCommand = new LogoutCommand(securityService);
    var meterSendCommand = new MeterSendCommand(typeMeterController, addressController, meterReadingController, dataValidatorManager);
    var meterValuesCommand = new MeterValuesCommand(typeMeterController, meterReadingController);
    var signupCommand = new SignupCommand(userController, dataValidatorManager);
    var userCommand = new UserCommand(userController);
    var getAddressCommand = new GetAddressCommand(addressController);
    var helpCommand = new HelpCommand(console);

    console.addCommand(signupCommand, exitCommand,
            helpCommand, loginCommand, logoutCommand,
            addAddressCommand, meterSendCommand, meterValuesCommand,
            historyCommand, addTypeMeterCommand, userCommand,
            getAddressCommand);
    return console;
  }

  /**
   * Создает валидатор и добавляет все доступные инструменты валидации
   *
   * @param scanner сканер для работы с данными
   * @return валидатор с инструментами для валидации
   */
  private DataValidatorManager createValidators(Scanner scanner) {
    var digitalValidator = new DigitalValidator();
    var doubleDigitalValidator = new DoubleDigitalValidator();
    var emailValidator = new EmailValidator();
    var houseNumberValidator = new HouseNumberValidator();
    var nameValidator = new NameValidator();
    var passwordValidator = new PasswordValidator();
    var postalCodeValidator = new PostalCodeValidator();
    var regionValidator = new RegionValidator();
    var titleValidator = new TitleValidator();
    var validators = new DataValidatorManagerImpl(scanner);
    validators.addValidators(
            digitalValidator,
            doubleDigitalValidator,
            emailValidator,
            houseNumberValidator,
            nameValidator,
            passwordValidator,
            postalCodeValidator,
            regionValidator,
            titleValidator);
    return validators;
  }

  /**
   * Добавляет тестовые данные для проверки работоспособности
   *
   * @param meterReadingService для добавления показаний
   * @param typeMeterService    для выбора типа показаний
   */
  private void addTestUsers(
          MeterReadingService meterReadingService,
          TypeMeterService typeMeterService) {


      var testUser = User.builder()
              .email("test@mail.com")
              .password("123")
              .firstName("TestName")
              .lastName("TestLastName")
              .role(Roles.USER)
              .build();

      System.out.println("Тестовые данные:\n   "
              + "User:  email - test@mail.com , password - 123\n   "
              + "Admin: email - admin@admin.com , password - 123");
      var testAddress = Address.builder()
              .addressId(1L)
              .owner(testUser)
              .postalCode(184040L)
              .region("Мурманская область")
              .city("Кандалакша")
              .street("Восточная")
              .house("6")
              .apartment("1")
              .build();



      var testData = MeterReading.builder()
              .address(testAddress)
              .meterValue(2133.123)
              .typeMeter(typeMeterService.getTypeMeter().get(0))
              .submissionMonth(YearMonth.now())
              .timeSendMeter(OffsetDateTime.now())
              .build();

      //meterReadingService.addMeterValue(testData);


  }


  /**
   * Для выдачи интерфейса.
   *
   * @return главный класс интерфейса
   */
  public Console console() {
    return this.console;
  }
}
