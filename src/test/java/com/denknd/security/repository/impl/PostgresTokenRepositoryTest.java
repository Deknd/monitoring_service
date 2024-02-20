package com.denknd.security.repository.impl;

import com.denknd.repository.TestContainer;
import com.denknd.security.entity.TokenBlock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostgresTokenRepositoryTest extends TestContainer {

  private PostgresTokenRepository postgresTokenRepository;

  @BeforeEach
  void setUp() {
    this.postgresTokenRepository = new PostgresTokenRepository(postgresContainer.getDataBaseConnection());
  }

  @Test
  @DisplayName("Проверяет, что успешно сохраняется токен")
  void save() throws SQLException {
    var tokenBlock = TokenBlock.builder()
            .expirationTime(OffsetDateTime.now())
            .tokenId(UUID.randomUUID().toString())
            .build();

    var save = this.postgresTokenRepository.save(tokenBlock);

    assertThat(save.tokenBlockId()).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, что не сохраняется токен, когда нет всех данных")
  void save_notRequiredData() {
    var tokenBlock = TokenBlock.builder()
            .expirationTime(OffsetDateTime.now())
            .build();

    assertThatThrownBy(() -> this.postgresTokenRepository.save(tokenBlock));
  }

  @Test
  @DisplayName("Проверяет, что такой токен сохранен")
  void existsByTokenId() throws SQLException {
    var tokenId = "fc9a79e6-b3af-4549-b6d0-35ee2802fb79";

    var existsByTokenId = this.postgresTokenRepository.existsByTokenId(tokenId);

    assertThat(existsByTokenId).isTrue();
  }
  @Test
  @DisplayName("Проверяет, что такой токен сохранен")
  void existsByTokenId_false() throws SQLException {
    var tokenId = "fc9a79e6-b3af-4549-bdss0-35ee2802fb79";

    var existsByTokenId = this.postgresTokenRepository.existsByTokenId(tokenId);

    assertThat(existsByTokenId).isFalse();
  }
}