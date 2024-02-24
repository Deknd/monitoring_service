//package com.denknd.security;
//
//import com.denknd.security.entity.Token;
//import com.denknd.security.entity.UserSecurity;
//import com.denknd.security.service.TokenService;
//import com.denknd.security.service.impl.SecurityServiceImpl;
//import com.nimbusds.jose.JWEEncrypter;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletResponse;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.function.Function;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//class SecurityServiceImplTest {
//
//  @Mock
//  private TokenService tokenService;
//  @Mock
//  private Function<UserSecurity, Token> createToken;
//  @Mock
//  private Function<Token, String> serializerToken;
//  @Mock
//  private JWEEncrypter jweEncrypter;
//  @Mock
//  private JwtConfig jwtConfig;
//  private SecurityServiceImpl securityService;
//  private AutoCloseable closeable;
//
//  @BeforeEach
//  void setUp() {
//    this.closeable = MockitoAnnotations.openMocks(this);
//    this.securityService = new SecurityServiceImpl(this.jweEncrypter, this.tokenService, this.jwtConfig);
//    this.securityService.setCreateToken(this.createToken);
//    this.securityService.setSerializerToken(this.serializerToken);
//  }
//  @AfterEach
//  void tearDown() throws Exception {
//    this.closeable.close();
//  }
// @Test
// @DisplayName("Проверяет, что добавляется и достается пользователь")
// void addPrincipal(){
//   var userSecurity = mock(UserSecurity.class);
//
//   this.securityService.addPrincipal(userSecurity);
//   var security = this.securityService.getUserSecurity();
//
//   assertThat(security).isEqualTo(userSecurity);
// }
//
//  @Test
//  @DisplayName("Проверяет, что если пользователь авторизован, будет возвращать true")
//  void isAuthentication() {
//    var userSecurity = mock(UserSecurity.class);
//
//    assertThat(this.securityService.isAuthentication()).isFalse();
//
//    this.securityService.addPrincipal(userSecurity);
//
//    assertThat(this.securityService.isAuthentication()).isTrue();
//  }
//
//  @Test
//  @DisplayName("Проверяет, что если пользователь не авторизован, будет возвращать false")
//  void isAuthentication_notAuthentication() {
//    var security = this.securityService.isAuthentication();
//
//    assertThat(security).isEqualTo(false);
//  }
//
//
//  @Test
//  @DisplayName("Проверяет, что при аутентифицированного пользователе, при вызове логаута сохраняется пустая кука")
//  void logout(){
//    var userSecurity = mock(UserSecurity.class);
//    this.securityService.addPrincipal(userSecurity);
//    var response = mock(HttpServletResponse.class);
//
//    var logout = this.securityService.logout(response);
//
//    assertThat(logout).isTrue();
//    assertThat(this.securityService.isAuthentication()).isFalse();
//    var cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
//    verify(response, times(1)).addCookie(cookieCaptor.capture());
//    var value = cookieCaptor.getValue();
//    assertThat(value.getValue()).isNull();
//    assertThat(value.getName()).isEqualTo("__Host-auth-token");
//  }
//  @Test
//  @DisplayName("Проверяет, что при не аутентифицированного пользователе при вызове логаута куки не трогаются")
//  void logout_notUser(){
//    var response = mock(HttpServletResponse.class);
//
//    var logout = this.securityService.logout(response);
//
//    assertThat(logout).isTrue();
//    assertThat(this.securityService.isAuthentication()).isFalse();
//    verify(response, times(0)).addCookie(any());
//  }
//
//  @Test
//  @DisplayName("Проверяет, что создается куки с полученными данными")
//  void onAuthentication(){
//    var userSecurity = mock(UserSecurity.class);
//    this.securityService.addPrincipal(userSecurity);
//    var response = mock(HttpServletResponse.class);
//    var testToken = "test token";
//    var token = mock(Token.class);
//    when(token.expiresAt()).thenReturn(Instant.now().plus(40, ChronoUnit.SECONDS));
//    when(this.createToken.apply(eq(userSecurity))).thenReturn(token);
//    when(this.serializerToken.apply(eq(token))).thenReturn(testToken);
//
//    this.securityService.onAuthentication(response);
//
//    var cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
//    verify(response, times(1)).addCookie(cookieCaptor.capture());
//    var value = cookieCaptor.getValue();
//    assertThat(value.getValue()).isEqualTo(testToken);
//    assertThat(value.getName()).isEqualTo("__Host-auth-token");
//  }
//  @Test
//  @DisplayName("Проверяет, что сли пользователь не аутентифицирован, то куки не создаются")
//  void onAuthentication_notAuthentication(){
//    var response = mock(HttpServletResponse.class);
//
//    this.securityService.onAuthentication(response);
//
//    verify(response, times(0)).addCookie(any());
//
//  }
//}