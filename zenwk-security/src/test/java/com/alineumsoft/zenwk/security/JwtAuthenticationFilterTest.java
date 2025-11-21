package com.alineumsoft.zenwk.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import com.alineumsoft.zenwk.security.auth.jwt.JwtAuthenticationFilter;
import com.alineumsoft.zenwk.security.auth.jwt.JwtProvider;
import com.alineumsoft.zenwk.security.config.util.ConfigUtils;
import com.alineumsoft.zenwk.security.enums.HttpMethodResourceEnum;
import com.alineumsoft.zenwk.security.enums.SecurityExceptionEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class JwtAuthenticationFilterTest {

  private JwtProvider jwtProvider;
  private JwtAuthenticationFilter filter;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private FilterChain filterChain;

  @BeforeEach
  void setUp() throws Exception {
    jwtProvider = mock(JwtProvider.class);
    filter = new JwtAuthenticationFilter(jwtProvider);
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    filterChain = mock(FilterChain.class);

    when(response.getWriter()).thenReturn(new PrintWriter(System.out));
    SecurityContextHolder.clearContext();
  }

  // ---------------------- doFilterInternal tests ----------------------
  @Test
  @DisplayName("doFilterInternal() permite public endpoint sin token")
  void doFilterInternal_publicEndpoint() throws Exception {
    when(request.getRequestURI()).thenReturn("/public/resource");

    try (MockedStatic<ConfigUtils> mocked = mockStatic(ConfigUtils.class)) {
      mocked.when(() -> ConfigUtils.isPublicEndpoint(anyString())).thenReturn(true);
      filter.doFilterInternal(request, response, filterChain);
      verify(filterChain, times(1)).doFilter(request, response);
    }
  }

  @Test
  @DisplayName("doFilterInternal() bloquea si token es null")
  void doFilterInternal_tokenNull() throws Exception {
    when(request.getRequestURI()).thenReturn("/private/resource");
    mockStaticConfigUtils(false);
    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.empty());

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain, never()).doFilter(request, response);
    verify(jwtProvider).sendErrorResponse(eq(null), eq(request), eq(response),
        eq(HttpServletResponse.SC_UNAUTHORIZED),
        eq(SecurityExceptionEnum.FUNC_AUTH_TOKEN_NOT_FOUND));
  }

  @Test
  @DisplayName("doFilterInternal() envía error si token inválido")
  void doFilterInternal_invalidToken() throws Exception {
    String token = "invalid-token";
    when(request.getRequestURI()).thenReturn("/private/resource");
    mockStaticConfigUtils(false);
    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.of(token));
    when(jwtProvider.extractAllClaims(token)).thenThrow(new RuntimeException("Token inválido"));

    filter.doFilterInternal(request, response, filterChain);

    verify(jwtProvider).sendErrorResponse(eq(null), eq(request), eq(response),
        eq(HttpServletResponse.SC_UNAUTHORIZED), any(RuntimeException.class));
    verify(filterChain, never()).doFilter(request, response);
  }

  private void mockStaticConfigUtils(boolean isPublic) {
    try (var mocked = Mockito.mockStatic(ConfigUtils.class)) {
      mocked.when(() -> ConfigUtils.isPublicEndpoint(anyString())).thenReturn(isPublic);
    }
  }

  // ---------------------- validateAuthenticate tests ----------------------
  @Test
  @DisplayName("validateAuthenticate() llama a sendErrorResponse si userDetails es null")
  void validateAuthenticate_userDetailsNull() throws Exception {
    String token = "token1";
    String username = "user1";

    doReturn(Optional.empty()).when(jwtProvider).extractUserDetails(token);

    boolean result = invokeValidateAuthenticate(token, username);

    assertFalse(result);
    verify(jwtProvider).sendErrorResponse(eq(username), eq(request), eq(response),
        eq(HttpServletResponse.SC_UNAUTHORIZED),
        eq(SecurityExceptionEnum.FUNC_AUTH_TOKEN_JWT_INVALID));
  }

  @Test
  @DisplayName("validateAuthenticate() llama a sendErrorResponse si rol no permitido")
  void validateAuthenticate_invalidRole() throws Exception {
    String token = "token2";
    String username = "user2";
    filter = spy(new JwtAuthenticationFilter(jwtProvider));

    UserDetails user = User.withUsername(username).password("pass").roles("USER").build();
    doReturn(Optional.of(user)).when(jwtProvider).extractUserDetails(token);
    doReturn(true).when(jwtProvider).validateToken(token, username);
    doReturn(false).when(filter).validateRolUser(token, request);

    boolean result = invokeValidateAuthenticate(token, username);

    assertFalse(result);
    verify(jwtProvider).sendErrorResponse(eq(username), eq(request), eq(response),
        eq(HttpServletResponse.SC_FORBIDDEN), eq(SecurityExceptionEnum.FUNC_AUTH_URI_FORBIDDEN));
  }

  @Test
  @DisplayName("validateAuthenticate() autentica correctamente si token y rol son válidos")
  void validateAuthenticate_validUser() throws Exception {
    String token = "token3";
    String username = "user3";
    filter = spy(new JwtAuthenticationFilter(jwtProvider));

    UserDetails user = User.withUsername(username).password("pass").roles("USER").build();
    doReturn(Optional.of(user)).when(jwtProvider).extractUserDetails(token);
    doReturn(true).when(jwtProvider).validateToken(token, username);
    doReturn(true).when(filter).validateRolUser(token, request);

    boolean result = invokeValidateAuthenticate(token, username);

    assertTrue(result);
    assertTrue(SecurityContextHolder.getContext().getAuthentication() != null);
  }

  private boolean invokeValidateAuthenticate(String token, String username) throws Exception {
    Method method = JwtAuthenticationFilter.class.getDeclaredMethod("validateAuthenticate",
        HttpServletRequest.class, HttpServletResponse.class, String.class, String.class);
    method.setAccessible(true);
    return (boolean) method.invoke(filter, request, response, token, username);
  }

  // ---------------------- validateRolUser tests ----------------------
  @Test
  @DisplayName("validateRolUser() permite acceso si rol no es USER o NEW_USER")
  void validateRolUser_roleNotUserOrNewUser() {
    String token = "token1";
    when(jwtProvider.extractAuthorities(token)).thenReturn(List.of("ADMIN"));
    when(request.getRequestURI()).thenReturn("/private/resource");

    boolean result = filter.validateRolUser(token, request);

    assertTrue(result);
  }

  @Test
  @DisplayName("validateRolUser() permite acceso si rol USER y URI no restringida")
  void validateRolUser_userRoleUriNotRestricted() {
    String token = "token2";
    when(jwtProvider.extractAuthorities(token)).thenReturn(List.of("USER"));
    when(request.getRequestURI()).thenReturn("/some/other/uri");

    boolean result = filter.validateRolUser(token, request);

    assertTrue(result);
  }

  @Test
  @DisplayName("validateRolUser() permite acceso si rol USER y URI restringida y permitida en JWT")
  void validateRolUser_userRoleUriAllowed() {
    String token = "token3";
    when(jwtProvider.extractAuthorities(token)).thenReturn(List.of("USER"));
    when(request.getRequestURI()).thenReturn(HttpMethodResourceEnum.USER_CREATE.getResource());
    when(jwtProvider.extractUrlsAllowedRolUser(token))
        .thenReturn(List.of(HttpMethodResourceEnum.USER_CREATE.getResource()));

    boolean result = filter.validateRolUser(token, request);

    assertTrue(result);
  }

  @Test
  @DisplayName("validateRolUser() bloquea acceso si rol USER y URI restringida no permitida en JWT")
  void validateRolUser_userRoleUriNotAllowed() {
    String token = "token4";
    when(jwtProvider.extractAuthorities(token)).thenReturn(List.of("USER"));
    when(request.getRequestURI()).thenReturn(HttpMethodResourceEnum.USER_CREATE.getResource());
    when(jwtProvider.extractUrlsAllowedRolUser(token)).thenReturn(List.of("/other/uri"));

    boolean result = filter.validateRolUser(token, request);

    assertFalse(result);
  }

  // ---------------------- doFilterInternal con token válido ----------------------
  @Test
  @DisplayName("doFilterInternal() procesa token y valida autenticación correctamente")
  void doFilterInternal_validToken_callsFilterChain() throws Exception {
    String token = "valid-token";
    String username = "user1";

    when(request.getRequestURI()).thenReturn("/private/resource");

    try (MockedStatic<ConfigUtils> mocked = mockStatic(ConfigUtils.class)) {
      mocked.when(() -> ConfigUtils.isPublicEndpoint(anyString())).thenReturn(false);
      when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.of(token));

      var claims = mock(io.jsonwebtoken.Claims.class);
      when(jwtProvider.extractAllClaims(token)).thenReturn(claims);
      when(claims.getSubject()).thenReturn(username);

      JwtAuthenticationFilter spyFilter = spy(filter);
      doReturn(true).when(spyFilter).validateAuthenticate(request, response, token, username);

      spyFilter.doFilterInternal(request, response, filterChain);

      verify(jwtProvider).extractAllClaims(token);
      verify(spyFilter).validateAuthenticate(request, response, token, username);
      verify(filterChain, times(1)).doFilter(request, response);
    }
  }
}
