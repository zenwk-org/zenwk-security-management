package com.alineumsoft.zenwk.security.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.alineumsoft.zenwk.security.auth.jwt.JwtProvider;
import com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants;
import com.alineumsoft.zenwk.security.common.dto.TokenDTO;
import com.alineumsoft.zenwk.security.common.entity.CsrfToken;
import com.alineumsoft.zenwk.security.common.enums.PermissionOperationEnum;
import com.alineumsoft.zenwk.security.common.exception.enums.CoreExceptionEnum;
import com.alineumsoft.zenwk.security.common.service.CsrfTokenCommonService;
import com.alineumsoft.zenwk.security.config.util.ConfigUtils;
import com.alineumsoft.zenwk.security.config.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Componente para la validación del csrf token y proteger los enpoint senssibles contra estos
 * ataques
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project zenwk-security
 * @class CsrfValidationFilter
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CsrfValidationFilter extends OncePerRequestFilter {
  /**
   * Utilidad manejadora del jwt
   */
  private final JwtProvider jwtProvider;
  /**
   * Servicio común para la gestión del token csrf
   */
  public final CsrfTokenCommonService csrfTokenCommonService;

  /**
   * Utilidad para cookie
   */
  private final CookieUtil cookieUtil;

  /**
   * Metodo exlcuidos del verificación
   */
  public static final Set<String> SAFE_METHODS =
      Set.of(PermissionOperationEnum.GET.name(), PermissionOperationEnum.HEAD.name(),
          PermissionOperationEnum.OPTIONS.name(), PermissionOperationEnum.TRACE.name());

  /**
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param response
   * @param filterChain
   * @throws ServletException
   * @throws IOException
   * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(jakarta.servlet.http.HttpServletRequest,
   *      jakarta.servlet.http.HttpServletResponse, jakarta.servlet.FilterChain)
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (ConfigUtils.isPublicEndpoint(request.getRequestURI())
        || SAFE_METHODS.contains(request.getMethod())) {
      filterChain.doFilter(request, response);
      return;
    }

    String tokenCsrf = extractCookieFromName(request, AuthConfigConstants.XCSRF_TOKEN).orElse(null);
    String tokenJwt = extractCookieFromName(request, AuthConfigConstants.ZENWK_JWT).orElse(null);
    String email = jwtProvider.extractUserEmail(tokenJwt);



    // Se da el acceso si el token csrf es valido
    if (validateCsrfToken(response, tokenCsrf, tokenJwt, email, request)) {
      filterChain.doFilter(request, response);
    }
  }


  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Extrae el token JWT desde la cookie httpOnly desde la
   * solicitud
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param nameCookie
   * @return
   */
  public Optional<String> extractCookieFromName(HttpServletRequest request, String nameCookie) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (nameCookie.equals(cookie.getName())) {
          return Optional.of(cookie.getValue());
        }
      }
    }
    return Optional.empty();
  }

  /**
   * 
   * <p>
   * <b> CU00X_Gestionar protección contra ataques CSRF </b> Validación del token CSRF en caso de
   * error 403 es gestionado por spring security que oculta el error personalizado del respons, para
   * cambiar esta configuración se debe realizar un desarrollo de un excepcion handler y llamarlo en
   * la cadena de filtro de spring security.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param response
   * @param tokenCsrf
   * @param email
   * @param request
   * @return
   * @throws IOException
   */
  private boolean validateCsrfToken(HttpServletResponse response, String tokenCsrf, String tokenJwt,
      String email, HttpServletRequest request) throws IOException {

    if (!ConfigUtils.isActiveLogout(tokenCsrf, tokenJwt, request.getRequestURI())) {
      log.error("CSRF invalid credentials - nulo en el parametro tokenCsrf / tokenJwt");
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
          CoreExceptionEnum.FUNC_VERIFICATION_TOKEN_CSRF_NOT_FOUND.getCodeMessage(email,
              tokenCsrf));
    } else {
      if (tokenCsrf == null) {
        // Sesión activa pero sin token csrf por inactividad
        // debe permitrise el logut
        return true;
      }
      try {
        TokenDTO tokenDto = new TokenDTO();
        tokenDto.setEmail(email);
        tokenDto.setCode(tokenCsrf);
        CsrfToken csrfToken = csrfTokenCommonService.validateCsrfToken(tokenDto);


        if (isNearExpiration(csrfToken.getExpirationDate())) {
          log.debug("Renovando token CSRF automáticamente, expiración próxima.");
          refreshToken(response, request, tokenDto);
        }

        return true;
      } catch (Exception e) {
        log.error(e.getMessage());
        response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
      }

    }
    return false;
  }

  /**
   * 
   * <p>
   * <b> CU001_XX </b> Generar nuevo token
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param response
   * @param request
   * @param tokenDto
   */
  private void refreshToken(HttpServletResponse response, HttpServletRequest request,
      TokenDTO tokenDto) {
    TokenDTO newToken = csrfTokenCommonService.generateCsrfToken(tokenDto, request, null);
    cookieUtil.generateCookieCsrf(response, newToken.getCode());
  }


  /**
   * 
   * <p>
   * <b> CU001_XX </b> Renovación prvia del token CSRF
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param expirationDate
   * @return
   */
  private boolean isNearExpiration(LocalDateTime expirationDate) {
    if (expirationDate == null) {
      return true;
    }

    long secondsUntilExpiration =
        java.time.Duration.between(LocalDateTime.now(), expirationDate).getSeconds();
    return secondsUntilExpiration > 0
        && secondsUntilExpiration <= AuthConfigConstants.TOKEN_RENEW_THRESHOLD_SECONDS;
  }


}
