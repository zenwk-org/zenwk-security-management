package com.alineumsoft.zenwk.security;


import static com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants.JWT_URLS_ALLOWED_ROL_USER;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import com.alineumsoft.zenwk.security.auth.jwt.JwtProvider;
import com.alineumsoft.zenwk.security.common.constants.CommonMessageConstants;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.common.exception.TechnicalException;
import com.alineumsoft.zenwk.security.common.exception.dto.ErrorResponseDTO;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.enums.SecurityExceptionEnum;
import com.alineumsoft.zenwk.security.enums.UserStateEnum;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


class JwtProviderTest {

  @Mock
  private LogSecurityRepository logSecRepo;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @InjectMocks
  private JwtProvider jwtProvider;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);

    // Generar una clave verdadera de 256 bits:
    Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
    String strongSecretKey = Base64.getEncoder().encodeToString(key.getEncoded());

    ReflectionTestUtils.setField(jwtProvider, "secretKey", strongSecretKey);
    ReflectionTestUtils.setField(jwtProvider, "expirationTime", 3600000L);
  }


  // @Test
  // @DisplayName("generateToken() debe generar un token válido y almacenarlo en cache")
  // void generateToken_success() {
  // UserDetails user = new User("testUser", "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
  // List<String> urls = List.of("/api/v1/resource");
  // String token = jwtProvider.generateToken(user, urls, 1L, UserStateEnum.ACTIVE,
  // "test@mail.com");
  //
  // assertNotNull(token);
  // Claims claims = jwtProvider.extractAllClaims(token);
  // assertEquals("testUser", claims.getSubject());
  // assertEquals("test@mail.com", claims.get("JWT_USER_EMAIL"));
  // }

  // --------------------------------------------
  // validateToken()
  // --------------------------------------------
  @Test
  @DisplayName("validateToken() debe retornar true cuando el token es válido")
  void validateToken_valid() {
    UserDetails user = new User("admin", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    String token =
        jwtProvider.generateToken(user, List.of(), 2L, UserStateEnum.ACTIVE, "admin@mail.com");

    assertTrue(jwtProvider.validateToken(token, "admin"));
  }

  @Test
  @DisplayName("validateToken() debe retornar false cuando el token no coincide con el usuario")
  void validateToken_invalidUser() {
    UserDetails user = new User("john", "", List.of());
    String token =
        jwtProvider.generateToken(user, List.of(), 3L, UserStateEnum.ACTIVE, "john@mail.com");

    assertFalse(jwtProvider.validateToken(token, "otherUser"));
  }

  @Test
  @DisplayName("validateToken() debe retornar false cuando extractAllClaims lanza excepción")
  void validateToken_extractAllClaimsThrowsException_returnsFalse() {
    // Arrange
    String token = "invalidToken";
    String username = "user123";

    JwtProvider spyService = Mockito.spy(jwtProvider); // spy sobre el servicio real

    // Simular que el método privado extractAllClaims lance excepción
    doThrow(new JwtException("Token inválido")).when(spyService).extractAllClaims(token);

    // Act
    boolean result = spyService.validateToken(token, username);

    // Assert
    assertFalse(result); // debe entrar al catch y retornar false
    verify(spyService, times(1)).validateToken(token, username);
  }

  // --------------------------------------------
  // invalidateToken()
  // --------------------------------------------
  @Test
  @DisplayName("invalidateToken() debe remover token del cache sin lanzar excepción")
  void invalidateToken_success() {
    UserDetails user = new User("userTest", "", List.of());
    String token =
        jwtProvider.generateToken(user, List.of(), 1L, UserStateEnum.ACTIVE, "test@mail.com");
    assertDoesNotThrow(() -> jwtProvider.invalidateToken(token));
  }

  @Test
  @DisplayName("invalidateToken() debe capturar excepción y registrar advertencia cuando el token es inválido")
  void invalidateToken_invalidToken_logsWarning() {
    String token = "invalidToken";
    JwtProvider spyService = Mockito.spy(jwtProvider);

    doThrow(new JwtException("Token malformado")).when(spyService).extractAllClaims(token);
    spyService.invalidateToken(token);

    verify(spyService, times(1)).invalidateToken(token);
  }

  // --------------------------------------------
  // extractUserDetails()
  // --------------------------------------------
  @Test
  @DisplayName("extractUserDetails() debe extraer UserDetails desde el token")
  void extractUserDetails_success() {
    UserDetails user = new User("alex", "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    String token =
        jwtProvider.generateToken(user, List.of(), 10L, UserStateEnum.ACTIVE, "alex@mail.com");

    Optional<UserDetails> result = jwtProvider.extractUserDetails(token);
    assertTrue(result.isPresent());
    assertEquals("alex", result.get().getUsername());
  }

  @Test
  @DisplayName("extractAuthorities() debe retornar lista de roles del token")
  void extractAuthorities_success() {
    UserDetails user =
        new User("userRoleTest", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    String token =
        jwtProvider.generateToken(user, List.of(), 10L, UserStateEnum.ACTIVE, "mail@mail.com");

    List<String> roles = jwtProvider.extractAuthorities(token);
    assertEquals(1, roles.size());
    assertEquals("ROLE_ADMIN", roles.get(0));
  }

  @Test
  @DisplayName("extractTokenFromAuthorization() debe retornar token si el header es correcto")
  void extractTokenFromAuthorization_success() {
    when(request.getHeader("Authorization")).thenReturn("Bearer myToken123");
    Optional<String> result = jwtProvider.extractTokenFromAuthorization(request);

    assertTrue(result.isPresent());
    assertEquals("myToken123", result.get());
  }

  @Test
  @DisplayName("extractJwtFromCookie() debe extraer token desde cookie válida")
  void extractJwtFromCookie_success() {
    Cookie cookie = new Cookie("ZENWK_JWT", "jwtCookieValue");
    when(request.getCookies()).thenReturn(new Cookie[] {cookie});
    Optional<String> result = jwtProvider.extractJwtFromCookie(request);

    assertTrue(result.isPresent());
    assertEquals("jwtCookieValue", result.get());
  }

  @Test
  @DisplayName("extractIdUser() debe retornar el ID correcto del token")
  void extractIdUser_success() {
    UserDetails user = new User("pepe", "", List.of());
    String token =
        jwtProvider.generateToken(user, List.of(), 777L, UserStateEnum.ACTIVE, "test@mail.com");
    assertEquals(777L, jwtProvider.extractIdUser(token));
  }

  @Test
  @DisplayName("extractUserState() debe retornar estado correcto del token")
  void extractUserState_success() {
    UserDetails user = new User("carlos", "", List.of());
    String token =
        jwtProvider.generateToken(user, List.of(), 1L, UserStateEnum.DISABLED, "carlos@mail.com");
    assertEquals(UserStateEnum.DISABLED, jwtProvider.extractUserState(token));
  }

  @Test
  @DisplayName("extractUserEmail() debe retornar email embebido en el token")
  void extractUserEmail_success() {
    UserDetails user = new User("test", "", List.of());
    String token =
        jwtProvider.generateToken(user, List.of(), 1L, UserStateEnum.ACTIVE, "mail@mail.com");
    assertEquals("mail@mail.com", jwtProvider.extractUserEmail(token));
  }

  // --------------------------------------------
  // extractUrlsAllowedRolUser()
  // --------------------------------------------
  @Test
  @DisplayName("extractUrlsAllowedRolUser() debe retornar las URLs cuando el claim existe")
  void extractUrlsAllowedRolUser_claimPresent() {
    // Arrange
    String token = "tokenValido";
    List<String> expectedUrls = Arrays.asList("/api/user", "/api/admin");

    Claims mockClaims = Mockito.mock(Claims.class);
    when(mockClaims.get(JWT_URLS_ALLOWED_ROL_USER)).thenReturn(expectedUrls);

    JwtProvider spyService = Mockito.spy(jwtProvider);
    doReturn(mockClaims).when(spyService).extractAllClaims(token);

    List<String> result = spyService.extractUrlsAllowedRolUser(token);
    assertEquals(expectedUrls, result);
  }

  // --------------------------------------------
  // sendErrorResponse()
  // --------------------------------------------
  @Test
  @DisplayName("sendErrorResponse() debe lanzar FunctionalException y llamar métodos auxiliares")
  void sendErrorResponse_shouldThrowFunctionalException() throws IOException {
    // Arrange
    String username = "testuser";
    int status = HttpServletResponse.SC_BAD_REQUEST;
    SecurityExceptionEnum errorEnum = SecurityExceptionEnum.FUNC_USER_NOT_FOUND_ID;

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    LogSecurity mockLogSecurity = mock(LogSecurity.class);

    JwtProvider spyService = Mockito.spy(jwtProvider);

    doReturn(mockLogSecurity).when(spyService).setLogSecurity(anyString(),
        any(HttpServletRequest.class), anyInt(), anyString());
    doNothing().when(spyService).writeResponse(any(), anyInt(), any());

    FunctionalException exception = assertThrows(FunctionalException.class,
        () -> spyService.sendErrorResponse(username, mockRequest, mockResponse, status, errorEnum));

    assertEquals(errorEnum.getCodeMessage(), exception.getMessage());

    verify(spyService, times(1)).setLogSecurity(eq(username), eq(mockRequest), eq(status),
        anyString());
    verify(spyService, times(1)).writeResponse(eq(mockResponse), eq(status),
        any(ErrorResponseDTO.class));
  }

  // --------------------------------------------
  // sendErrorResponse(Exception)
  // --------------------------------------------
  @Test
  @DisplayName("sendErrorResponse(Exception) debe lanzar TechnicalException y ejecutar métodos auxiliares")
  void sendErrorResponse_withException_shouldThrowTechnicalException() throws IOException {
    // Arrange
    String username = "testuser";
    int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    Exception genericException = new Exception("Fatal error");

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    LogSecurity mockLogSecurity = mock(LogSecurity.class);

    JwtProvider spyProvider = Mockito.spy(jwtProvider);

    doReturn(mockLogSecurity).when(spyProvider).setLogSecurity(anyString(),
        any(HttpServletRequest.class), anyInt(), anyString());
    doNothing().when(spyProvider).writeResponse(any(), anyInt(), any());

    TechnicalException ex = assertThrows(TechnicalException.class, () -> spyProvider
        .sendErrorResponse(username, mockRequest, mockResponse, status, genericException));

    assertEquals("Fatal error", ex.getMessage());
    verify(spyProvider, times(1)).setLogSecurity(eq(username), eq(mockRequest), eq(status),
        anyString());
    verify(spyProvider, times(1)).writeResponse(eq(mockResponse), eq(status),
        any(ErrorResponseDTO.class));
  }

  // --------------------------------------------
  // sendErrorResponse(Exception)
  // --------------------------------------------
  @Test
  @DisplayName("writeResponse() debe escribir el JSON en el response y ejecutar flush correctamente")
  void writeResponse_success() throws IOException {
    // Arrange
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    PrintWriter mockWriter = mock(PrintWriter.class);
    ErrorResponseDTO errorDTO =
        new ErrorResponseDTO("id", "400", "Bad Request", "2025-03-10T12:00:00");

    JwtProvider spyProvider = Mockito.spy(jwtProvider);

    // Mock dependencias necesarias
    when(mockResponse.getWriter()).thenReturn(mockWriter);
    doReturn("{\"json\":true}").when(spyProvider).getJson(errorDTO);

    int status = 400;

    // Act
    spyProvider.writeResponse(mockResponse, status, errorDTO);

    // Assert
    verify(mockResponse).setContentType(MediaType.APPLICATION_JSON_VALUE);
    verify(mockResponse).setStatus(status);

    verify(mockWriter).write("{\"json\":true}");
    verify(mockWriter).flush();
  }

  // --------------------------------------------
  // setLogSecurity()
  // --------------------------------------------
  @Test
  @DisplayName("setLogSecurity() debe inicializar y configurar correctamente el LogSecurity")
  void setLogSecurity_success() {
    String username = "testUser";
    int status = 404;
    String errorMessage = "User not found";

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    JwtProvider spyProvider = mock(JwtProvider.class, Mockito.CALLS_REAL_METHODS);
    doReturn(new LogSecurity()).when(spyProvider).initializeLog(any(), anyString(), anyString(),
        anyString(), any());

    doReturn("ACTION_CODE_TEST").when(spyProvider).getSecurityActionCodeFromRequest(any());

    LogSecurity result = spyProvider.setLogSecurity(username, mockRequest, status, errorMessage);


    assertNotNull(result);
    assertEquals(status, result.getStatusCode());
    assertEquals(errorMessage, result.getErrorMessage());

    verify(spyProvider, times(1)).initializeLog(eq(mockRequest), eq(username),
        eq(CommonMessageConstants.NOT_FOUND), eq(CommonMessageConstants.NOT_FOUND), any());
  }

}
