package com.alineumsoft.zenwk.security.config;

import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import com.alineumsoft.zenwk.security.auth.jwt.JwtProvider;
import com.alineumsoft.zenwk.security.enums.SecurityExceptionEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Clase encargada de manejar los errores de acceso denegado en la aplicación. Implementa la
 * interfaz {@link AccessDeniedHandler} para personalizar la respuesta cuando un usuario no tiene
 * los permisos necesarios para acceder a un recurso.
 * </p>
 * <p>
 * Al interceptar una excepción de acceso denegado, esta clase envía una respuesta personalizada con
 * un código HTTP 403 (Forbidden) y un mensaje en formato JSON indicando que el acceso al recurso
 * está prohibido.
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class CustomAccessDeniedHandler
 */
@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
  /**
   * Utilidad manejadora del jwt
   */
  private final JwtProvider jwtProvider;

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Constructor
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param jwtProvider
   */
  public CustomAccessDeniedHandler(JwtProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
  }

  /**
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param response
   * @param accessDeniedException
   * @throws IOException
   * @see org.springframework.security.web.access.AccessDeniedHandler#handle(jakarta.servlet.http.HttpServletRequest,
   *      jakarta.servlet.http.HttpServletResponse,
   *      org.springframework.security.access.AccessDeniedException)
   */
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {
    String token = jwtProvider.extractTokenFromAuthorization(request).orElse(null);
    String username = jwtProvider.extractAllClaims(token).getSubject();
    jwtProvider.sendErrorResponse(username, request, response, HttpServletResponse.SC_FORBIDDEN,
        SecurityExceptionEnum.FUNC_AUTH_URI_FORBIDDEN);
  }
}
