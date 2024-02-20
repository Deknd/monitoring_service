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
import com.denknd.repository.TestContainer;
import com.denknd.security.service.SecurityService;
import com.denknd.security.service.TokenService;
import com.denknd.services.AddressService;
import com.denknd.services.MeterCountService;
import com.denknd.services.MeterReadingService;
import com.denknd.services.TypeMeterService;
import com.denknd.services.UserService;
import com.nimbusds.jose.KeyLengthException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ManualConfigTest extends TestContainer {

  private ManualConfig manualConfig;
  @BeforeEach
  void setUp() throws FileNotFoundException, ParseException, KeyLengthException {
    this.manualConfig = new ManualConfig("src/test/resources/application-testConfig.yaml", postgresContainer.getDataBaseConnection());
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод UserService экземпляр")
  void getUserService() {
    var userService = this.manualConfig.getUserService();

    assertThat(userService).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод AddressService экземпляр")
  void getAddressService() {
    var addressService = this.manualConfig.getAddressService();

    assertThat(addressService).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод TypeMeterService экземпляр")
  void getTypeMeterService() {
    var typeMeterService = this.manualConfig.getTypeMeterService();

    assertThat(typeMeterService).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод MeterCountService экземпляр")
  void getMeterCountService() {
    var meterCountService = this.manualConfig.getMeterCountService();

    assertThat(meterCountService).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод MeterReadingService экземпля")
  void getMeterReadingService() {
    var meterReadingService = this.manualConfig.getMeterReadingService();

    assertThat(meterReadingService).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод AuditService экземпляр")
  void getAuditService() {
    var auditService = this.manualConfig.getAuditService();

    assertThat(auditService).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод SecurityService экземпляр")
  void getSecurityService() {
    var securityService = this.manualConfig.getSecurityService();

    assertThat(securityService).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод TokenService экземпляр")
  void getTokenService() {
    var tokenService = this.manualConfig.getTokenService();

    assertThat(tokenService).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод AddressMapper экземпляр")
  void getAddressMapper() {
    var addressMapper = this.manualConfig.getAddressMapper();

    assertThat(addressMapper).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод MeterReadingMapper экземпляр")
  void getMeterReadingMapper() {
    var meterReadingMapper = this.manualConfig.getMeterReadingMapper();

    assertThat(meterReadingMapper).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод TypeMeterMapper экземпляр")
  void getTypeMeterMapper() {
    var typeMeterMapper = this.manualConfig.getTypeMeterMapper();

    assertThat(typeMeterMapper).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод UserMapper экземпляр")
  void getUserMapper() {
    var userMapper = this.manualConfig.getUserMapper();

    assertThat(userMapper).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод MeterCountMapper экземпляр")
  void getMeterCountMapper() {
    var meterCountMapper = this.manualConfig.getMeterCountMapper();

    assertThat(meterCountMapper).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод AddressController экземпляр")
  void getAddressController() {
    var addressController = this.manualConfig.getAddressController();

    assertThat(addressController).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод MeterReadingController экземпляр")
  void getMeterReadingController() {
    var meterReadingController = this.manualConfig.getMeterReadingController();

    assertThat(meterReadingController).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод TypeMeterController экземпляр")
  void getTypeMeterController() {
    var typeMeterController = this.manualConfig.getTypeMeterController();

    assertThat(typeMeterController).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод UserController экземпляр")
  void getUserController() {
    var userController = this.manualConfig.getUserController();

    assertThat(userController).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод CounterInfoController экземпляр")
  void getCounterInfoController() {
    var counterInfoController = this.manualConfig.getCounterInfoController();

    assertThat(counterInfoController).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод BasicAuthenticationFilter экземпляр")
  void getBasicAuthenticationFilter() {
    var basicAuthenticationFilter = this.manualConfig.getBasicAuthenticationFilter();

    assertThat(basicAuthenticationFilter).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод CookieAuthenticationFilter экземпляр")
  void getCookieAuthenticationFilter() {
    var cookieAuthenticationFilter = this.manualConfig.getCookieAuthenticationFilter();

    assertThat(cookieAuthenticationFilter).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, возвращает ли метод LogoutFilter экземпляр")
  void getLogoutFilter() {
    var logoutFilter = this.manualConfig.getLogoutFilter();

    assertThat(logoutFilter).isNotNull();
  }
  @Test
  @DisplayName("Проверяет, возвращает ли метод ObjectMapper экземпляр")
  void getObjectMapper(){
    var objectMapper = this.manualConfig.getObjectMapper();

    assertThat(objectMapper).isNotNull();
  }
  @Test
  @DisplayName("Проверяет, возвращает ли метод Validators экземпляр")
  void getValidators(){
    var validator = this.manualConfig.getValidator();

    assertThat(validator).isNotNull();
  }
}