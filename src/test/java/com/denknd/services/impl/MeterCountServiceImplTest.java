package com.denknd.services.impl;

import com.denknd.entity.Meter;
import com.denknd.entity.Roles;
import com.denknd.repository.MeterCountRepository;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.service.SecurityService;
import com.denknd.services.MeterCountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MeterCountServiceImplTest {
  private AutoCloseable closeable;
  @Mock
  private MeterCountRepository meterCountRepository;
  @Mock
  private SecurityService securityService;
  private MeterCountService meterCountService;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.meterCountService = new MeterCountServiceImpl(this.meterCountRepository, this.securityService);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает нужный сервис и добавляет время регистрации")
  void saveMeterCount() throws SQLException {
    var meter = mock(Meter.class);

    this.meterCountService.saveMeterCount(meter);

    verify(meter, times(1)).setRegistrationDate(any());
    verify(this.meterCountRepository, times(1)).save(eq(meter));
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает нужный сервис")
  void addInfoForMeterCount() throws SQLException, AccessDeniedException {
    var meter = mock(Meter.class);
    var userSecurity = UserSecurity.builder().role(Roles.ADMIN).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);

    this.meterCountService.addInfoForMeterCount(meter);

    verify(this.meterCountRepository, times(1)).update(eq(meter));
  }


  @Test
  @DisplayName("Проверяет, что с ролью USER выкидывает исключение")
  void addInfoForMeterCount_AccessDeniedException() throws SQLException {
    var meter = mock(Meter.class);
    var userSecurity = UserSecurity.builder().role(Roles.USER).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);

    assertThatThrownBy(() -> this.meterCountService.addInfoForMeterCount(meter));

    verify(this.meterCountRepository, times(0)).update(any());
  }
}