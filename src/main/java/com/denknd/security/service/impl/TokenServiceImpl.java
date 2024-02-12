package com.denknd.security.service.impl;

import com.denknd.security.entity.Token;
import com.denknd.security.entity.TokenBlock;
import com.denknd.security.repository.TokenRepository;
import com.denknd.security.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * Сервис для управления заблокированными токенами
 */
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {
  /**
   * Репозиторий для заблокированных токенов
   */
  private final TokenRepository tokenRepository;
  /**
   * @param id UUID токена доступа, выданный при создании
   * @return true, если токен заблокирован, иначе false
   */
  @Override
  public boolean existsByTokenId(String id) {
    try {
      return this.tokenRepository.existsByTokenId(id);
    } catch (SQLException e) {
      log.error(e.getMessage());
      return false;
    }
  }

  /**
   * Блокирует токен доступа
   * @param token токен доступа, который нужно заблокировать
   * @return заблокированный токен
   */
  @Override
  public TokenBlock lockToken(Token token) {

    var tokenBlock =
            TokenBlock.builder()
                    .tokenId(token.id().toString())
                    .expirationTime(OffsetDateTime.ofInstant(token.expiresAt(), ZoneId.systemDefault()))
                    .build();
    try {
      return this.tokenRepository.save(tokenBlock);
    } catch (SQLException e) {
      log.error(e.getMessage());
      return null;
    }

  }
}
