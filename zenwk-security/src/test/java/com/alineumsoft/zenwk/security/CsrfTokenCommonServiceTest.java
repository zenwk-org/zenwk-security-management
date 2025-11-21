package com.alineumsoft.zenwk.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.alineumsoft.zenwk.security.common.dto.TokenDTO;
import com.alineumsoft.zenwk.security.common.entity.CsrfToken;
import com.alineumsoft.zenwk.security.common.exception.enums.CoreExceptionEnum;
import com.alineumsoft.zenwk.security.common.repository.CsrfTokenRepository;
import com.alineumsoft.zenwk.security.common.service.CsrfTokenCommonService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;


/**
 * Pruebas unitarias para {@link CsrfTokenCommonService} Valida la generación, almacenamiento y
 * validación de tokens CSRF.
 */
class CsrfTokenCommonServiceTest {

  private CsrfTokenRepository csrfTokenRepository;
  private CsrfTokenCommonService service;
  private HttpServletRequest request;

  @BeforeEach
  void setUp() {
    csrfTokenRepository = mock(CsrfTokenRepository.class);
    service = new CsrfTokenCommonService(csrfTokenRepository);
    request = mock(HttpServletRequest.class);
  }

  @Test
  @DisplayName("Debe generar y guardar correctamente el token CSRF asociado al usuario")
  void testGenerateCsrfToken() {
    TokenDTO dto = new TokenDTO();
    dto.setEmail("test@alineumsoft.com");

    when(request.getHeader("User-Agent")).thenReturn("JUnit-Agent");
    when(csrfTokenRepository.save(any(CsrfToken.class))).thenAnswer(i -> i.getArgument(0));

    TokenDTO out = service.generateCsrfToken(dto, request, "user123");

    assertNotNull(out.getCode());
    assertEquals("test@alineumsoft.com", out.getEmail());
    assertTrue(out.getExpirationDate().isAfter(LocalDateTime.now()));
    verify(csrfTokenRepository, times(1)).save(any(CsrfToken.class));
  }

  @Test
  @DisplayName("Debe validar correctamente un token CSRF válido y vigente")
  void testValidateCsrfTokenValid() {
    TokenDTO dto = new TokenDTO();
    dto.setEmail("test@alineumsoft.com");
    dto.setCode("ABC123");

    CsrfToken token = mock(CsrfToken.class);
    when(token.getExpirationDate()).thenReturn(LocalDateTime.now().plusMinutes(5));
    when(token.isRevoked()).thenReturn(false);

    when(csrfTokenRepository.findByEmailAndCode(dto.getEmail(), dto.getCode()))
        .thenReturn(Optional.of(token));

    assertDoesNotThrow(() -> service.validateCsrfToken(dto));
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando el token CSRF ha expirado")
  void testValidateCsrfTokenExpired() {
    TokenDTO dto = new TokenDTO();
    dto.setEmail("test@alineumsoft.com");
    dto.setCode("ABC123");

    CsrfToken token = mock(CsrfToken.class);
    when(token.getExpirationDate()).thenReturn(LocalDateTime.now().minusMinutes(5));
    when(token.isRevoked()).thenReturn(false);

    when(csrfTokenRepository.findByEmailAndCode(dto.getEmail(), dto.getCode()))
        .thenReturn(Optional.of(token));

    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> service.validateCsrfToken(dto));
    assertTrue(ex.getMessage()
        .contains(CoreExceptionEnum.FUNC_VERIFICATION_TOKEN_CSRF_EXPIRATION.getCodeMessage()));
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando el token CSRF ha sido revocado")
  void testValidateCsrfTokenRevoked() {
    TokenDTO dto = new TokenDTO();
    dto.setEmail("test@alineumsoft.com");
    dto.setCode("ABC123");

    CsrfToken token = mock(CsrfToken.class);
    when(token.getExpirationDate()).thenReturn(LocalDateTime.now().plusMinutes(10));
    when(token.isRevoked()).thenReturn(true);

    when(csrfTokenRepository.findByEmailAndCode(dto.getEmail(), dto.getCode()))
        .thenReturn(Optional.of(token));

    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> service.validateCsrfToken(dto));
    assertTrue(ex.getMessage()
        .contains(CoreExceptionEnum.FUNC_VERIFICATION_TOKEN_CSRF_REVOKED.getCodeMessage()));
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando no se encuentra el token CSRF para el correo y código indicados")
  void testValidateCsrfTokenNotFound() {
    TokenDTO dto = new TokenDTO();
    dto.setEmail("notfound@alineumsoft.com");
    dto.setCode("XXX999");

    when(csrfTokenRepository.findByEmailAndCode(dto.getEmail(), dto.getCode()))
        .thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> service.validateCsrfToken(dto));
  }
}
