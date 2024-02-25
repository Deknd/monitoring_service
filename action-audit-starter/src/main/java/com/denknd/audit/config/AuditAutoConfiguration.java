package com.denknd.audit.config;

import com.denknd.audit.api.UserIdentificationService;
import com.denknd.audit.aspect.AuditAspect;
import com.denknd.audit.repository.AuditRepository;
import com.denknd.audit.repository.impl.PostgresAuditRepository;
import com.denknd.audit.services.AuditService;
import com.denknd.audit.services.impl.AuditServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Конфигурация модуля audit
 */
@Configuration
public class AuditAutoConfiguration {
  @Bean
  public AuditRepository auditRepository(JdbcTemplate jdbcTemplate) {
    return new PostgresAuditRepository(jdbcTemplate);
  }

  @Bean
  public AuditService auditService(AuditRepository auditRepository) {
    return new AuditServiceImpl(auditRepository);
  }

  @Bean
  @ConditionalOnBean(value = {UserIdentificationService.class})
  public AuditAspect auditAspect(UserIdentificationService userIdentificationService, AuditService auditService) {
    return new AuditAspect(userIdentificationService, auditService);
  }

}
