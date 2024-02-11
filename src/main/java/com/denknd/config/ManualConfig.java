package com.denknd.config;

import com.denknd.controllers.AddressController;
import com.denknd.controllers.CounterInfoController;
import com.denknd.controllers.MeterReadingController;
import com.denknd.controllers.TypeMeterController;
import com.denknd.controllers.UserController;
import com.denknd.in.filters.BasicAuthenticationFilter;
import com.denknd.in.filters.CookieAuthenticationFilter;
import com.denknd.in.filters.LogoutFilter;
import com.denknd.mappers.AddressMapper;
import com.denknd.mappers.MeterCountMapper;
import com.denknd.mappers.MeterReadingMapper;
import com.denknd.mappers.TypeMeterMapper;
import com.denknd.mappers.UserMapper;
import com.denknd.out.audit.AuditService;
import com.denknd.out.audit.AuditServiceImpl;
import com.denknd.out.audit.PostgresAuditRepository;
import com.denknd.repository.impl.PostgresAddressRepository;
import com.denknd.repository.impl.PostgresMeterCountRepository;
import com.denknd.repository.impl.PostgresMeterReadingRepository;
import com.denknd.repository.impl.PostgresTypeMeterRepository;
import com.denknd.repository.impl.PostgresUserRepository;
import com.denknd.security.repository.impl.PostgresTokenRepository;
import com.denknd.security.service.SecurityService;
import com.denknd.security.service.TokenService;
import com.denknd.security.service.impl.SecurityServiceImpl;
import com.denknd.security.service.impl.TokenServiceImpl;
import com.denknd.security.utils.DefaultDeserializerToken;
import com.denknd.security.utils.authenticator.impl.BasicUserAuthenticator;
import com.denknd.security.utils.authenticator.impl.CookieUserAuthenticator;
import com.denknd.security.utils.converter.impl.CookieAuthenticationConverter;
import com.denknd.services.AddressService;
import com.denknd.services.MeterCountService;
import com.denknd.services.MeterReadingService;
import com.denknd.services.TypeMeterService;
import com.denknd.services.UserService;
import com.denknd.services.impl.AddressServiceImpl;
import com.denknd.services.impl.MeterCountServiceImpl;
import com.denknd.services.impl.MeterReadingServiceImpl;
import com.denknd.services.impl.TypeMeterServiceImpl;
import com.denknd.services.impl.UserServiceImpl;
import com.denknd.util.DataBaseConnection;
import com.denknd.util.JwtConfig;
import com.denknd.util.PasswordEncoder;
import com.denknd.util.impl.DataBaseConnectionImpl;
import com.denknd.util.impl.LiquibaseMigration;
import com.denknd.util.impl.Sha256PasswordEncoderImpl;
import com.denknd.util.impl.Validators;
import com.denknd.util.impl.YamlParserImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import jakarta.validation.Validation;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Scanner;

/**
 * Собирает весь контекст приложения.
 */
@Getter
@Log4j2
public class ManualConfig {
  /**
   * Сервис для работы с пользователями
   */
  private UserService userService;
  /**
   * сервис для работы с адресами
   */
  private AddressService addressService;
  /**
   * Сервис для работы с типами показаний
   */
  private TypeMeterService typeMeterService;
  /**
   * Сервис для работы со счетчиками
   */
  private MeterCountService meterCountService;
  /**
   * Сервис для работы с показаниями
   */
  private MeterReadingService meterReadingService;
  /**
   * Сервис для работы аудита
   */
  private AuditService auditService;
  /**
   * Сервис для работы с безопасностью
   */
  private SecurityService securityService;
  /**
   * Сервис для работы с токенами
   */
  private TokenService tokenService;

  /**
   * Маппер для адресов
   */
  private AddressMapper addressMapper;
  /**
   * Маппер для показаний
   */
  private MeterReadingMapper meterReadingMapper;
  /**
   * Маппер для типов показаний
   */
  private TypeMeterMapper typeMeterMapper;
  /**
   * Маппер для объектов пользователя
   */
  private UserMapper userMapper;
  /**
   * Маппер для счетчиков
   */
  private MeterCountMapper meterCountMapper;

  /**
   * Контроллер для работы с адресами
   */
  private AddressController addressController;
  /**
   * Контроллер для работы с показаниями
   */
  private MeterReadingController meterReadingController;
  /**
   * Контроллер для работы с типами показаний
   */
  private TypeMeterController typeMeterController;
  /**
   * Контроллер для работы с пользователями
   */
  private UserController userController;
  /**
   * Контроллер для работы с счетчиками
   */
  private CounterInfoController counterInfoController;

  /**
   * Фильтр для авторизации и получения токена доступа
   */
  private BasicAuthenticationFilter basicAuthenticationFilter;
  /**
   * Фильтр для аунтификации токена доступа
   */
  private CookieAuthenticationFilter cookieAuthenticationFilter;
  /**
   * Фильтр для блокировки токена доступа
   */
  private LogoutFilter logoutFilter;
  /**
   * Маппер для мапинга Json в объект
   */
  private ObjectMapper objectMapper;
  /**
   * Валидатор входящих данных
   */
  private Validators validator;

  /**
   * Собирает весь контекст приложения.
   */
  public ManualConfig(String yamlPath, DataBaseConnection dataBaseConnectionArg) throws FileNotFoundException, KeyLengthException, ParseException {
    try {
      this.objectMapper = new ObjectMapper();
      this.objectMapper.registerModule(new JavaTimeModule());
      var passwordEncoder = new Sha256PasswordEncoderImpl();
      var yamlParser = new YamlParserImpl();
      if (yamlPath != null) {
        yamlParser.setPathToApplicationYml(yamlPath);
      }
      DataBaseConnection dataBaseConnection;
      if (dataBaseConnectionArg != null) {
        dataBaseConnection = dataBaseConnectionArg;
      } else {
        dataBaseConnection = new DataBaseConnectionImpl(yamlParser.dbConfig());
      }
      new LiquibaseMigration(dataBaseConnection, yamlParser.liquibaseConfig()).migration();
      this.validator = new Validators(null);

      var directDecrypter = new DirectDecrypter(OctetSequenceKey.parse(yamlParser.jwtConfig().secretKey()));
      var directEncrypter = new DirectEncrypter(OctetSequenceKey.parse(yamlParser.jwtConfig().secretKey()));


      this.initializerMapper();
      this.initializerService(passwordEncoder, dataBaseConnection, directEncrypter, yamlParser.jwtConfig());
      this.initializerController();
      this.initializerFilter(passwordEncoder, directDecrypter);
    } catch (FileNotFoundException e) {
      log.error("Ошибка получения данных из application.yml. " + e.getMessage());
      throw e;
    } catch (KeyLengthException e) {
      log.error("Ошибка добавления секретного ключа из application.yml. Ключ не совместимой длины. " + e.getMessage());
      throw e;
    } catch (ParseException e) {
      log.error("Ошибка парсинга секретного ключа");
      throw e;
    }


  }

  /**
   * Инициализация всех мапперов
   */
  private void initializerMapper() {
    this.addressMapper = AddressMapper.INSTANCE;
    this.meterReadingMapper = MeterReadingMapper.INSTANCE;
    this.typeMeterMapper = TypeMeterMapper.INSTANCE;
    this.userMapper = UserMapper.INSTANCE;
    this.meterCountMapper = MeterCountMapper.INSTANCE;

  }

  /**
   * Инициализация всех сервисов
   *
   * @param passwordEncoder    шифрование и сравнение паролей
   * @param dataBaseConnection соединение с базой данных
   * @param jweEncrypter       шифрование токена безопасности
   * @param jwtConfig          конфигурация для создания токена
   */
  private void initializerService(PasswordEncoder passwordEncoder, DataBaseConnection dataBaseConnection, JWEEncrypter jweEncrypter, JwtConfig jwtConfig) {
    this.userService = new UserServiceImpl(new PostgresUserRepository(dataBaseConnection, this.userMapper), passwordEncoder);
    this.addressService = new AddressServiceImpl(new PostgresAddressRepository(dataBaseConnection, this.addressMapper));
    this.typeMeterService = new TypeMeterServiceImpl(new PostgresTypeMeterRepository(dataBaseConnection, this.typeMeterMapper));
    this.meterCountService = new MeterCountServiceImpl(new PostgresMeterCountRepository(dataBaseConnection));
    this.meterReadingService = new MeterReadingServiceImpl(new PostgresMeterReadingRepository(dataBaseConnection, this.meterReadingMapper), this.typeMeterService, this.meterCountService);
    this.auditService = new AuditServiceImpl(new PostgresAuditRepository(dataBaseConnection));
    this.tokenService = new TokenServiceImpl(new PostgresTokenRepository(dataBaseConnection));
    this.securityService = new SecurityServiceImpl(jweEncrypter, this.tokenService, jwtConfig);
  }

  /**
   * Инициализация контроллеров
   */
  private void initializerController() {
    this.addressController = new AddressController(this.addressService, this.userService, this.addressMapper);
    this.meterReadingController = new MeterReadingController(this.meterReadingService, this.addressService, this.typeMeterService, this.meterReadingMapper);
    this.typeMeterController = new TypeMeterController(this.typeMeterService, this.typeMeterMapper);
    this.userController = new UserController(this.userService, this.userMapper);
    this.counterInfoController = new CounterInfoController(this.meterCountService, this.meterCountMapper);
  }

  /**
   * Инициализация фильтров безопасности
   *
   * @param passwordEncoder шифрование и сравнение паролей
   * @param jweDecrypter    дешифрование токена
   */
  private void initializerFilter(PasswordEncoder passwordEncoder, JWEDecrypter jweDecrypter) {
    this.basicAuthenticationFilter = new BasicAuthenticationFilter(
            this.objectMapper,
            this.securityService,
            new BasicUserAuthenticator(this.userService, this.userMapper, passwordEncoder));
    this.cookieAuthenticationFilter = new CookieAuthenticationFilter(
            this.objectMapper,
            this.securityService,
            new CookieAuthenticationConverter(
                    new DefaultDeserializerToken(jweDecrypter)
            ),
            new CookieUserAuthenticator(this.tokenService)
    );
    this.logoutFilter = new LogoutFilter(this.securityService);
  }
}
