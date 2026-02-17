package com.alineumsoft.zenwk.security.config.util;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Utilidad centralizada para la gestión de cookies CSRF
 *
 * Responsabilidad única: - Resolver dominio - Construir cookies CSRF correctamente
 *
 * Sin dependencia de lógica de negocio.
 */
@Component
public class CookieUtil {

  @Value("${security.csrf.cookie-domain:}")
  private String cookieDomain;

  /**
   * Genera una cookie CSRF HttpOnly
   *
   * @param response HttpServletResponse
   * @param token token CSRF
   */
  public void generateCookieJwt(HttpServletResponse response, String token) {
    ResponseCookie.ResponseCookieBuilder builder =
        ResponseCookie.from(AuthConfigConstants.ZENWK_JWT, token).httpOnly(true).path("/")
            .secure(true).sameSite("None").maxAge(Duration.ofHours(2));

    String domain = resolveDomain();
    if (!domain.isBlank()) {
      builder.domain(domain);
    }

    ResponseCookie cookie = builder.build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  /**
   * Inactiva la cookie CSRF
   *
   * @param response HttpServletResponse
   */
  public void disableCookieJwt(HttpServletResponse response) {
    ResponseCookie.ResponseCookieBuilder builder =
        ResponseCookie.from(AuthConfigConstants.ZENWK_JWT, "").httpOnly(true).path("/").secure(true)
            .sameSite("None").maxAge(Duration.ZERO);

    String domain = resolveDomain();
    if (!domain.isBlank()) {
      builder.domain(domain);
    }

    ResponseCookie cookie = builder.maxAge(Duration.ofHours(0)).build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  /**
   * 
   * <p>
   * <b> CU001_XX </b> Generar nueva cookie
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param response
   * @param token
   * @param domain
   */
  public void generateCookieCsrf(HttpServletResponse response, String token) {
    ResponseCookie.ResponseCookieBuilder builder =
        ResponseCookie.from(AuthConfigConstants.XCSRF_TOKEN, token).httpOnly(true).path("/")
            .secure(true).sameSite("None").maxAge(Duration.ofHours(2));
    ResponseCookie csrfCookie = builder.build();
    response.addHeader(HttpHeaders.SET_COOKIE, csrfCookie.toString());
  }

  /**
   * 
   * <p>
   * <b> CU001_XX </b> Método general que inactiva la cookie httpOnly para el token CSRF FROM
   * eclipse-temurin:17 WORKDIR /app COPY build/libs/*.jar app.jar EXPOSE 8086 # Agregar opciones
   * para permitir acceso a métricas (actuator/health) del sistema ENV JAVA_OPTS="--add-opens
   * java.base/jdk.internal.platform=ALL-UNNAMED" ENTRYPOINT ["java", "-jar", "app.jar"]
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param token
   */
  public void disabledCookieCsrf(HttpServletResponse response) {
    ResponseCookie cookie = ResponseCookie.from(AuthConfigConstants.XCSRF_TOKEN, "").httpOnly(true)
        .secure(true).sameSite("None").path("/").maxAge(Duration.ofHours(0)).build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }


  /**
   * Resuelve el dominio configurado para cookies
   */
  private String resolveDomain() {
    return cookieDomain != null ? cookieDomain.trim() : "";
  }
}
