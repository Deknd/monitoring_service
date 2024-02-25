package com.denknd.services.impl;

import com.denknd.entity.Roles;
import com.denknd.entity.TypeMeter;
import com.denknd.exception.TypeMeterAdditionException;
import com.denknd.repository.TypeMeterRepository;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.service.SecurityService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.Mapping;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TypeMeterServiceImplTest {

  @Mock
  private TypeMeterRepository repository;
  @Mock
  private SecurityService securityService;
  private TypeMeterServiceImpl typeMeterService;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.typeMeterService = new TypeMeterServiceImpl(this.repository, this.securityService);
  }
  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }
  @Test
  @DisplayName("Проверяет, что сервис обращается в репозиторий")
  void getTypeMeter() {
    this.typeMeterService.getTypeMeter();

    verify(this.repository, times(1)).findTypeMeter();
  }

  @Test
  @DisplayName("Проверяет, что сервис обращается в репозиторий")
  void addNewTypeMeter() throws SQLException, TypeMeterAdditionException, AccessDeniedException {
    var userSecurity = UserSecurity.builder().role(Roles.ADMIN).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);

    this.typeMeterService.addNewTypeMeter(mock(TypeMeter.class));

    verify(this.repository, times(1)).save(any(TypeMeter.class));
  }
  @Test
  @DisplayName("Проверяет, что если в репозитории выкинется ошибка, то тут ее обработают")
  void addNewTypeMeter_error() throws SQLException {
    var userSecurity = UserSecurity.builder().role(Roles.ADMIN).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.repository.save(any())).thenThrow(SQLException.class);

    assertThatThrownBy(()->this.typeMeterService.addNewTypeMeter(mock(TypeMeter.class))).isInstanceOf(TypeMeterAdditionException.class);

    verify(this.repository, times(1)).save(any(TypeMeter.class));
  }
  @Test
  @DisplayName("Проверяет, что если в репозитории выкинется ошибка, то тут ее обработают")
  void addNewTypeMeter_errorAccessDeniedException() throws SQLException {
    var userSecurity = UserSecurity.builder().role(Roles.USER).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);

    assertThatThrownBy(()->this.typeMeterService.addNewTypeMeter(mock(TypeMeter.class))).isInstanceOf(AccessDeniedException.class);

    verify(this.repository, times(0)).save(any());
  }


  @Test
  @DisplayName("проверяет, что достает данные из репозитория и выбирает из них нужный тип")
  void getTypeMeterByCode() {
    var typeCode = "test2";
    var typeMeter = TypeMeter.builder().typeCode(typeCode).build();
    var typeMeterList = List.of(
            TypeMeter.builder().typeCode("test").build(),
            typeMeter,
            TypeMeter.builder().typeCode("test3").build()
    );
    when(this.repository.findTypeMeter()).thenReturn(typeMeterList);

    var typeMeterByCode = this.typeMeterService.getTypeMeterByCode(typeCode);

    assertThat(typeMeterByCode).isEqualTo(typeMeter);
  }
  @Test
  @DisplayName("проверяет, что достает данные из репозитория и не находит код с данным типом")
  void getTypeMeterByCode_null() {

    var typeMeterList = List.of(
            TypeMeter.builder().typeCode("test").build(),
            TypeMeter.builder().typeCode("test2").build(),
            TypeMeter.builder().typeCode("test3").build()
    );
    when(this.repository.findTypeMeter()).thenReturn(typeMeterList);

    var typeMeterByCode = this.typeMeterService.getTypeMeterByCode("null");

    assertThat(typeMeterByCode).isNull();
  }
}