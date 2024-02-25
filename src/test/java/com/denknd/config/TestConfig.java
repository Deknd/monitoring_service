package com.denknd.config;

import com.denknd.exception.BadCredentialsException;
import com.denknd.security.entity.PreAuthenticatedAuthenticationToken;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.service.SecurityService;
import com.denknd.security.utils.authenticator.UserAuthenticator;
import com.denknd.security.utils.converter.AuthenticationConverter;
import com.denknd.services.TypeMeterService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestConfig {
  @Bean(name = "basicUserAuthenticator")
  public UserAuthenticator userAuthenticator(){
    var authenticator = mock(UserAuthenticator.class);
    when(authenticator.authentication(any())).thenReturn(mock(UserSecurity.class));
    return authenticator;
  };
  @Bean
  public AuthenticationConverter authenticationConverter() throws BadCredentialsException {
    var authenticationConverter = mock(AuthenticationConverter.class);
    when(authenticationConverter.convert(any())).thenReturn(mock(PreAuthenticatedAuthenticationToken.class));
    return authenticationConverter;
  }
  @MockBean
  private SecurityService securityService;

  @MockBean
  public TypeMeterService typeMeterService;

}
