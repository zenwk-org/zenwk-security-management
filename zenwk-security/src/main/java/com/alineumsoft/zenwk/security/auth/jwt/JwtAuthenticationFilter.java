package com.alineumsoft.zenwk.security.auth.jwt;

import static com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants.WEB_DETAILS;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.alineumsoft.zenwk.security.common.constants.CommonMessageConstants;
import com.alineumsoft.zenwk.security.config.util.ConfigUtils;
import com.alineumsoft.zenwk.security.enums.HttpMethodResourceEnum;
import com.alineumsoft.zenwk.security.enums.RoleEnum;
import com.alineumsoft.zenwk.security.enums.SecurityExceptionEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Este filtro interceptara las peticiones y validara si el JWT es valido.
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class JwtAuthenticationFilter
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  /**
   * Utilidad manejadora del jwt
   */
  private final JwtProvider jwtProvider;

  /**
   * <p>
   * <b> Consructor </b>
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param jwtUtil
   */
  public JwtAuthenticationFilter(JwtProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
  }

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
  public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String username = null;
    try {
      // 1. Enpoints públicos
      if (ConfigUtils.isPublicEndpoint(request.getRequestURI())) {
        filterChain.doFilter(request, response);
        return;
      }
      // 2. EL token es obligatorio
      String token = extractTokenJwt(request, response);
      if (token == null) {
        return;
      }
      // 3. Validación de la autenticación
      username = jwtProvider.extractAllClaims(token).getSubject();
      if (validateAuthenticate(request, response, token, username)) {
        filterChain.doFilter(request, response);
      }
    } catch (Exception e) {
      log.error(CommonMessageConstants.LOG_MSG_EXCEPTION, e);
      jwtProvider.sendErrorResponse(username, request, response,
          HttpServletResponse.SC_UNAUTHORIZED, e);
    }
  }

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Extrae el token y lo valida.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param response
   * @return
   * @throws IOException
   */
  private String extractTokenJwt(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String token = jwtProvider.extractJwtFromCookie(request).orElse(null);

    // Si el token es invalido no continua con el proceso de autorizacion de la
    // sesion
    if (token == null) {
      jwtProvider.sendErrorResponse(null, request, response, HttpServletResponse.SC_UNAUTHORIZED,
          SecurityExceptionEnum.FUNC_AUTH_TOKEN_NOT_FOUND);
    }

    return token;
  }

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Valida los roles del usuario y permisos para acpetar
   * la autentiación.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param response
   * @param token
   * @return
   * @throws IOException
   */
  public boolean validateAuthenticate(HttpServletRequest request, HttpServletResponse response,
      String token, String username) throws IOException {
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = jwtProvider.extractUserDetails(token).orElse(null);

      if (userDetails == null || !jwtProvider.validateToken(token, username)) {
        jwtProvider.sendErrorResponse(username, request, response,
            HttpServletResponse.SC_UNAUTHORIZED, SecurityExceptionEnum.FUNC_AUTH_TOKEN_JWT_INVALID);
        return false;

      } else if (!validateRolUser(token, request)) {
        jwtProvider.sendErrorResponse(username, request, response, HttpServletResponse.SC_FORBIDDEN,
            SecurityExceptionEnum.FUNC_AUTH_URI_FORBIDDEN);
        return false;

      } else {
        authenticateUser(userDetails, request);
      }
    }
    return true;

  }

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Establece la autenticación del usuario en el contexto
   * de seguridad.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param userDetails
   * @param request
   */
  private void authenticateUser(UserDetails userDetails, HttpServletRequest request) {
    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    setAuthenticationDetails(authToken, request);
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Verifica si el usuario con rol USER tiene acceso a la
   * URL solicitada.
   * </p>
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param token
   * @param request
   * @return
   */
  public boolean validateRolUser(String token, HttpServletRequest request) {
    List<String> roles = jwtProvider.extractAuthorities(token);
    String uri = request.getRequestURI();
    // Si el el rol user esta presente se inspeccionan que las uri correpondan a los
    // ids asociados a ese usuario
    if ((roles.contains((RoleEnum.USER.name())) || roles.contains((RoleEnum.NEW_USER.name())))
        && (HttpMethodResourceEnum.USER_CREATE.getResource().equals(uri)
            || HttpMethodResourceEnum.PERSON_CREATE.getResource().equals(uri))) {
      return jwtProvider.extractUrlsAllowedRolUser(token).contains(uri);
    }
    return true;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Actualizacion de UsernamePasswordAuthenticationToken
   * con los permisos y metadatos
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param authentication
   * @param request
   */
  private void setAuthenticationDetails(UsernamePasswordAuthenticationToken authentication,
      HttpServletRequest request) {
    // Se usa cuando necesitas almacenar metadatos adicionales sobre la solicitud,
    // como: dirección IP, tipo de navegador, Información de sesión
    Map<String, Object> details =
        Map.of(WEB_DETAILS, new WebAuthenticationDetailsSource().buildDetails(request));
    authentication.setDetails(details);
  }


}
