package com.denknd.security.service.impl;

import com.denknd.security.entity.Token;
import com.denknd.security.entity.TokenBlock;
import com.denknd.security.repository.TokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TokenServiceImplTest {
  private AutoCloseable autoCloseable;
  @Mock
  private TokenRepository tokenRepository;
  private TokenServiceImpl tokenService;

  @BeforeEach
  void setUp() {
    this.autoCloseable = MockitoAnnotations.openMocks(this);
    this.tokenService = new TokenServiceImpl(this.tokenRepository);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.autoCloseable.close();
  }

  @Test
  @DisplayName("Проверяет, что сервис обращается в репозиторий")
  void existsByTokenId() throws SQLException {
    var id ="id";

    this.tokenService.existsByTokenId(id);

    verify(this.tokenRepository, times(1)).existsByTokenId(eq(id));
  }
  @Test
  @DisplayName("Проверяет, что сервис при выкидывание ошибки репозиторием возвращает false")
  void existsByTokenId_null() throws SQLException {
    var id ="id";
    when(this.tokenRepository.existsByTokenId(id)).thenThrow(SQLException.class);

    assertThat(this.tokenService.existsByTokenId(id)).isFalse();

    verify(this.tokenRepository, times(1)).existsByTokenId(eq(id));
  }

  @Test
  @DisplayName("Проверяет, что обращается в репозиторий с нужными аргументами")
  void lockToken() throws SQLException {
    var dateTime = OffsetDateTime.now().plus(3, ChronoUnit.HOURS);
    var token = Token.builder()
            .id(UUID.randomUUID())
            .expiresAt(dateTime.toInstant())
            .build();

    this.tokenService.lockToken(token);

    var tokenCaptor = ArgumentCaptor.forClass(TokenBlock.class);
    verify(this.tokenRepository, times(1)).save(tokenCaptor.capture());
    var tokenBlock = tokenCaptor.getValue();
    assertThat(tokenBlock.tokenId()).isEqualTo(token.id().toString());
    assertThat(tokenBlock.expirationTime()).isEqualTo(dateTime);

  }
  @Test
  @DisplayName("Проверяет, что обращается в репозиторий с нужными аргументами, и ловит ошибку")
  void lockToken_SQLException() throws SQLException {
    var dateTime = OffsetDateTime.now().plus(3, ChronoUnit.HOURS);
    var token = Token.builder()
            .id(UUID.randomUUID())
            .expiresAt(dateTime.toInstant())
            .build();
    when(this.tokenRepository.save(any())).thenThrow(SQLException.class);

    var result = this.tokenService.lockToken(token);

    assertThat(result).isNull();
    var tokenCaptor = ArgumentCaptor.forClass(TokenBlock.class);
    verify(this.tokenRepository, times(1)).save(tokenCaptor.capture());
    var tokenBlock = tokenCaptor.getValue();
    assertThat(tokenBlock.tokenId()).isEqualTo(token.id().toString());
    assertThat(tokenBlock.expirationTime()).isEqualTo(dateTime);

  }
}