package com.alineumsoft.zenwk.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import com.alineumsoft.zenwk.security.auth.dto.AuthRequestDTO;
import com.alineumsoft.zenwk.security.auth.dto.AuthResponseDTO;
import com.alineumsoft.zenwk.security.auth.dto.ResetPasswordDTO;
import com.alineumsoft.zenwk.security.auth.entity.Token;
import com.alineumsoft.zenwk.security.auth.jwt.JwtProvider;
import com.alineumsoft.zenwk.security.auth.repository.TokenRepository;
import com.alineumsoft.zenwk.security.auth.service.AuthService;
import com.alineumsoft.zenwk.security.auth.service.PermissionService;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.common.exception.TechnicalException;
import com.alineumsoft.zenwk.security.common.util.CryptoUtil;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.enums.UserStateEnum;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import com.alineumsoft.zenwk.security.user.entity.User;
import com.alineumsoft.zenwk.security.user.entity.UserHist;
import com.alineumsoft.zenwk.security.user.repository.UserHistRepository;
import com.alineumsoft.zenwk.security.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


class AuthServiceTest {

  @Mock
  private LogSecurityRepository logSecRepo;
  @Mock
  private AuthenticationManager authManager;
  @Mock
  private JwtProvider jwtProvider;
  @Mock
  private PermissionService permissionService;
  @Mock
  private UserService userService;
  @Mock
  private UserHistRepository userHistRepository;
  @Mock
  private TokenRepository tokenRepository;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private Authentication authentication;
  @Mock
  private UserDetails userDetails;

  @InjectMocks
  private AuthService authService;


  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    when(userDetails.getUsername()).thenReturn("tester");

    when(request.getRequestURI()).thenReturn("/mock");
    when(request.getMethod()).thenReturn("DELETE");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    when(request.getHeader(anyString())).thenReturn("mock-header");

    when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/mock"));

    when(logSecRepo.save(any(LogSecurity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  @DisplayName("authenticate: autenticación exitosa")
  void authenticate_Success() {
    AuthRequestDTO dto = new AuthRequestDTO("user", "pass");
    User user = new User();
    user.setId(10L);
    user.setEmail("test@mail.com");
    user.setState(UserStateEnum.ACTIVE);

    when(authManager.authenticate(any())).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("user");
    when(userService.findByUsername("user")).thenReturn(user);
    when(permissionService.listAllowedUrlsForUserRole("user")).thenReturn(List.of("/a", "/b"));
    when(jwtProvider.generateToken(eq(userDetails), any(), eq(10L), eq(UserStateEnum.ACTIVE),
        eq("test@mail.com"))).thenReturn("jwt-token");

    AuthResponseDTO result = authService.authenticate(dto, request);

    assertNotNull(result);
    assertEquals(10L, result.getUserId());
    assertEquals("jwt-token", result.getToken());
    verify(logSecRepo).save(any(LogSecurity.class));
  }

  @Test
  @DisplayName("authenticate: credenciales inválidas lanza FunctionalException")
  void authenticate_BadCredentials() {
    AuthRequestDTO dto = new AuthRequestDTO("user", "wrong");
    when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

    assertThrows(FunctionalException.class, () -> authService.authenticate(dto, request));

    verify(logSecRepo).save(any(LogSecurity.class));
  }

  @Test
  @DisplayName("logout: invalida token correctamente")
  void logout_Success() {
    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.of("token-123"));

    authService.logout(request, userDetails);

    verify(jwtProvider).invalidateToken("token-123");
    verify(logSecRepo).save(any(LogSecurity.class));
  }

  @Test
  @DisplayName("logout: error lanza TechnicalException")
  void logout_Error() {
    when(jwtProvider.extractJwtFromCookie(request)).thenThrow(new RuntimeException("ERROR"));
    assertThrows(TechnicalException.class, () -> authService.logout(request, userDetails));
    verify(logSecRepo, never()).save(any(LogSecurity.class));
  }

  @Test
  @DisplayName("refreshJwt: retorna nuevo token")
  void refreshJwt_Success() {
    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.of("old-jwt"));
    when(jwtProvider.extractIdUser("old-jwt")).thenReturn(5L);
    when(jwtProvider.extractUserEmail("old-jwt")).thenReturn("mail@x.com");
    when(jwtProvider.extractUserState("old-jwt")).thenReturn(UserStateEnum.ACTIVE);
    when(permissionService.listAllowedUrlsForUserRole(any())).thenReturn(List.of("/p"));
    when(jwtProvider.generateToken(any(), any(), any(), any(), any())).thenReturn("new-jwt");

    AuthResponseDTO result = authService.refreshJwt(request, userDetails);

    assertEquals("new-jwt", result.getToken());
    verify(jwtProvider).invalidateToken("old-jwt");
  }

  @Test
  @DisplayName("refreshJwt: error lanza TechnicalException")
  void refreshJwt_Error() {
    when(jwtProvider.extractJwtFromCookie(request)).thenThrow(new RuntimeException("Error"));

    assertThrows(TechnicalException.class, () -> authService.refreshJwt(request, userDetails));
  }

  @Test
  @DisplayName("resetPassword: restablecimiento exitoso")
  void resetPassword_Success() {
    ResetPasswordDTO dto = new ResetPasswordDTO();
    dto.setPassword("newPass");
    dto.setUuid("uuid");
    dto.setCodeToken("code");

    User user = new User();
    user.setId(3L);
    user.setEmail("mail@test.com");

    Token token = new Token();
    token.setEmail("mail@test.com");
    token.setUuid(CryptoUtil.encryptPassword("uuid"));
    token.setCode(CryptoUtil.encryptPassword("code"));
    token.setExpirationDate(LocalDateTime.now().plusMinutes(10)); // No vencido

    when(tokenRepository.findByEmail("mail@test.com")).thenReturn(Optional.of(token));
    when(userService.findByEmail("mail@test.com")).thenReturn(user);
    when(userHistRepository.findLast20ByIdUser(3L)).thenReturn(List.of());
    when(userService.updateUser(any(), eq(3L), any(), any(), any())).thenReturn(true);

    Boolean result = authService.resetPassword(request, dto, "mail@test.com");
    assertEquals(true, result);
  }

  @Test
  @DisplayName("resetPassword: token inválido lanza TechnicalException")
  void resetPassword_InvalidToken() {
    ResetPasswordDTO dto = new ResetPasswordDTO();
    dto.setPassword("1234");
    dto.setUuid("bad");
    dto.setCodeToken("bad");

    Token token = new Token();
    token.setEmail("mail@test.com");
    token.setUuid(CryptoUtil.encryptPassword("uuid"));
    token.setCode(CryptoUtil.encryptPassword("code"));
    token.setExpirationDate(LocalDateTime.now().plusMinutes(5));

    when(tokenRepository.findByEmail(any())).thenReturn(Optional.of(token));

    assertThrows(TechnicalException.class,
        () -> authService.resetPassword(request, dto, "mail@test.com"));
  }

  @Test
  @DisplayName("getToken: email no encontrado lanza EntityNotFoundException")
  void getToken_NotFound() {
    when(tokenRepository.findByEmail("x@gmail.com")).thenReturn(Optional.empty());
    assertThrows(TechnicalException.class,
        () -> authService.resetPassword(request, new ResetPasswordDTO(), "x@gmail.com"));
  }

  @Test
  @DisplayName("validatePassword: contraseña reutilizada lanza IllegalArgumentException")
  void validatePassword_Reused() {
    UserHist hist = new UserHist();
    hist.setPassword(CryptoUtil.encryptPassword("testpass")); // Se reutiliza

    when(userHistRepository.findLast20ByIdUser(7L)).thenReturn(List.of(hist));

    ResetPasswordDTO dto = new ResetPasswordDTO();
    dto.setPassword("testpass");
    dto.setUuid("uuid");
    dto.setCodeToken("code");

    Token token = new Token();
    token.setEmail("mail2@test.com");
    token.setUuid(CryptoUtil.encryptPassword("uuid"));
    token.setCode(CryptoUtil.encryptPassword("code"));
    token.setExpirationDate(LocalDateTime.now().plusMinutes(2));

    when(tokenRepository.findByEmail("mail@test.com")).thenReturn(Optional.of(token));
    when(userService.findByEmail("mail@test.com")).thenReturn(new User());

    assertThrows(TechnicalException.class,
        () -> authService.resetPassword(request, dto, "mail@test.com"));
  }

  @Test
  @DisplayName("generateCookieHttpOnlyJwt: agrega cookie correctamente")
  void generateCookieHttpOnlyJwt_Success() {
    HttpServletResponse responseMock = mock(HttpServletResponse.class);
    authService.generateCookieHttpOnlyJwt(responseMock, "jwt123");
    verify(responseMock).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
  }

  @Test
  @DisplayName("disabledCookieHttpOnlyCsrfToken: elimina CSRF cookie")
  void disabledCookieHttpOnlyCsrfToken_Success() {
    HttpServletResponse responseMock = mock(HttpServletResponse.class);
    authService.disabledCookieHttpOnlyCsrfToken(responseMock);
    verify(responseMock).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
  }

}
