package com.alineumsoft.zenwk.security.auth.jwt;

import static com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants.AUTHORIZATION_BEARER;
import static com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants.HEADER_AUTHORIZATION;
import static com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants.INDEX_TOKEN;
import static com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants.JWT_EXPIRATION_TIME;
import static com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants.JWT_ID_USER;
import static com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants.JWT_ROLES;
import static com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants.JWT_SECRET_KEY;
import static com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants.JWT_URLS_ALLOWED_ROL_USER;
import static com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants.JWT_USER_EMAIL;
import static com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants.JWT_USER_STATE;
import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants;
import com.alineumsoft.zenwk.security.common.constants.CommonMessageConstants;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.common.exception.TechnicalException;
import com.alineumsoft.zenwk.security.common.exception.dto.ErrorResponseDTO;
import com.alineumsoft.zenwk.security.common.exception.enums.CoreExceptionEnum;
import com.alineumsoft.zenwk.security.common.helper.ApiRestSecurityHelper;
import com.alineumsoft.zenwk.security.common.util.LocalDateTimeUtil;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.enums.SecurityExceptionEnum;
import com.alineumsoft.zenwk.security.enums.UserStateEnum;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Implementación genreal para autenticación mediante JWT
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class JwtUtil
 */
@Component
@Slf4j
public class JwtProvider extends ApiRestSecurityHelper {
  /**
   * Repositorio para log persistible de modulo
   */
  private final LogSecurityRepository logSecRepo;
  /**
   * Enum para error general
   */
  private static final CoreExceptionEnum generalError = CoreExceptionEnum.FUNC_COMMON_ERROR_GENERAL;
  /**
   * Tiempo de expiracion del token
   */
  @Value(JWT_EXPIRATION_TIME)
  private Long expirationTime;
  /**
   * Clave secreta
   */
  @Value(JWT_SECRET_KEY)
  private String secretKey;
  /**
   * Cache en memoria para los token
   */
  private Map<String, String> cacheToken = new HashMap<>();

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Constructor
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param logSecRepo
   */
  public JwtProvider(LogSecurityRepository logSecRepo) {
    this.logSecRepo = logSecRepo;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Generación del token
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param userDetails
   * @param listAllowedUrlsForUserRole
   * @param idUser
   * @param userState
   * @param email
   * @return
   */
  public String generateToken(UserDetails userDetails, List<String> listAllowedUrlsForUserRole,
      Long idUser, UserStateEnum userState, String email) {
    // Se agrega valor extra al calim, para agregar roles
    Map<String, Object> extraClaim = new HashMap<>();
    List<String> listRoles =
        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    // Roles del usuario
    extraClaim.put(JWT_ROLES, listRoles);
    // Se agregan los permisos por operacion
    extraClaim.put(JWT_URLS_ALLOWED_ROL_USER, listAllowedUrlsForUserRole);
    // Se almacena el id del usuario
    extraClaim.put(JWT_ID_USER, idUser);
    // estado del usuario
    extraClaim.put(JWT_USER_STATE, userState);
    // Email del usuario
    extraClaim.put(JWT_USER_EMAIL, email);

    // Generacion del token.
    String token = Jwts.builder().setClaims(new HashMap<>()).addClaims(extraClaim)
        .setSubject(userDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(getDateExpiration())
        // La firma evita que el token pueda ser modificado sin ser detectado.
        .signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
    // Se actualiza cache.
    cacheToken.put(userDetails.getUsername(), token);
    return token;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Se encarga de generar una clave secreta (Key) para
   * firmar y validar los tokens JWT.
   * </p>
   * Garantiza:
   * <li>La clave para firmar los JWT esté en el formato correcto.</li>
   * <li>Se use un algoritmo seguro (HMAC SHA-256 o superior).</li>
   * <li>El mismo secreto se use para firmar y validar tokens.</li>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  private Key getSignInKey() {
    // JWT requiere una clave binaria para la firma.
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    // Crea una instancia de Key utilizando HMAC con SHA (normalmente SHA-256).
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Extracción del claims a partir de dl token jwt
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param token
   * @return
   */
  public Claims extractAllClaims(String token) {
    // Si el token es invalido, expirado o alterado, lanzará una excepcion
    // (JwtException u otra relacionada)
    return Jwts.parserBuilder().setSigningKey(getSignInKey()).build()
        // Parsea y valida el token
        .parseClaimsJws(token).getBody();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> valida un token JWT comparando el usuario del token
   * con el usuario proporcionado y verificando que el token no haya expirado. Tambien, maneja
   * excepciones para evitar fallos en la ejecucion.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param token
   * @param username
   * @return
   */
  public boolean validateToken(String token, String username) {
    try {
      final Claims claim = extractAllClaims(token);
      String tokenUsername = claim.getSubject();
      Date tokenExpired = claim.getExpiration();
      return username.equals(tokenUsername) && !tokenExpired.before(new Date());
    } catch (JwtException | IllegalArgumentException e) {
      log.error(CommonMessageConstants.LOG_MSG_EXCEPTION, e.getMessage());
      return false;
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Invalida el token de un usuario removiéndolo del
   * caché y agregándolo a una lista de tokens inválidos. Esto previene la reutilización de tokens
   * hasta que expiren.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param token
   */
  public void invalidateToken(String token) {
    try {
      Claims claims = extractAllClaims(token);
      String username = claims.getSubject();
      if (username != null) {
        cacheToken.remove(username);
        log.info("Token invalidado para el usuario: {}", username);
      }
    } catch (JwtException | IllegalArgumentException e) {
      log.warn("Intento de invalidación de un token inválido: {}", e.getMessage());
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Una hora de expiració para el token
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  private Date getDateExpiration() {
    log.info(
        "JwtUtil.getDateExpiration() - " + JWT_EXPIRATION_TIME + ":" + expirationTime / 60 + "m");
    return new Date(System.currentTimeMillis() + expirationTime);
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Se obtiene el userDetails, si no un Optional vacio
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param token
   * @return
   */
  @SuppressWarnings("unchecked")
  public Optional<UserDetails> extractUserDetails(String token) {
    Claims claims = extractAllClaims(token);
    String username = claims.getSubject();
    List<String> roles = (List<String>) claims.get(JWT_ROLES);

    if (username != null && roles != null) {
      Collection<GrantedAuthority> authorities =
          roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
      return Optional.of(new User(username, "", authorities));
    }
    return Optional.empty();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Optiene la lista de roles
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param token
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<String> extractAuthorities(String token) {
    Claims claims = extractAllClaims(token);
    List<String> roles = (List<String>) claims.get(JWT_ROLES);
    return roles != null ? roles : Collections.emptyList();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Optiene la lista de urls permitidas para el rol user
   * con su respectivo id
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param token
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<String> extractUrlsAllowedRolUser(String token) {
    Claims claims = extractAllClaims(token);
    List<String> urlsAllowed = (List<String>) claims.get(JWT_URLS_ALLOWED_ROL_USER);
    return urlsAllowed != null ? urlsAllowed : Collections.emptyList();
  }

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Extrae el token JWT del encabezado Authorization de
   * la solicitud.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @return
   */
  public Optional<String> extractTokenFromAuthorization(HttpServletRequest request) {
    String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
    if (authorizationHeader != null && authorizationHeader.startsWith(AUTHORIZATION_BEARER)) {
      return Optional.of(authorizationHeader.substring(INDEX_TOKEN));
    }
    return Optional.empty();
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
   * @return
   */
  public Optional<String> extractJwtFromCookie(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (AuthConfigConstants.ZENWK_JWT.equals(cookie.getName())) {
          return Optional.of(cookie.getValue());
        }
      }
    }
    return Optional.empty();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Escribe el mensaje de error en el objeto reponse
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param username
   * @param response
   * @param status
   * @param error
   */
  public void sendErrorResponse(String username, HttpServletRequest request,
      HttpServletResponse response, int status, SecurityExceptionEnum error) throws IOException {
    // Se incializa log
    LogSecurity logSecurity = setLogSecurity(username, request, status, error.getCodeMessage());
    // Se inicializa objecto de reponse con error
    ErrorResponseDTO errorDTO = new ErrorResponseDTO(null, error.getCode(),
        generalError.getMessage(), LocalDateTimeUtil.getLocalDateTimeIso86018(LocalDateTime.now()));
    // Se actualiza response de la solicitud
    writeResponse(response, status, errorDTO);
    throw new FunctionalException(error.getCodeMessage(), null, logSecRepo, logSecurity);
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Escribe el mensaje de error en el objeto reponse
   * cuando se presenta un error indesperado
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param username
   * @param response
   * @param status
   * @param exception
   */
  public void sendErrorResponse(String username, HttpServletRequest request,
      HttpServletResponse response, int status, Exception exception) throws IOException {
    // Se incializa log
    LogSecurity logSecurity = setLogSecurity(username, request, status, exception.getMessage());
    // Se inicializa objecto de reponse con error
    ErrorResponseDTO errorDTO = new ErrorResponseDTO(null, generalError.getCode(),
        generalError.getMessage(), LocalDateTimeUtil.getLocalDateTimeIso86018(LocalDateTime.now()));
    // Se actualiza response de la solicitud
    writeResponse(response, status, errorDTO);
    throw new TechnicalException(exception.getMessage(), exception.getCause(), logSecRepo,
        logSecurity);
  }

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Metodo privado reutilizable para escribir JSON en la
   * respuesta HTTP
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param response
   * @param status
   * @param errorDTO
   * @throws IOException
   */
  private void writeResponse(HttpServletResponse response, int status, ErrorResponseDTO errorDTO)
      throws IOException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(status);
    response.getWriter().write(getJson(errorDTO));
    response.getWriter().flush();
  }

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Inicializacion de log persistible
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param username
   * @param request
   * @param status
   * @param error
   * @return
   */
  private LogSecurity setLogSecurity(String username, HttpServletRequest request, int status,
      String error) {
    LogSecurity logSecurity = initializeLog(request, username, CommonMessageConstants.NOT_FOUND,
        CommonMessageConstants.NOT_FOUND, getSecurityActionCodeFromRequest(request));
    logSecurity.setErrorMessage(error);
    logSecurity.setStatusCode(status);
    logSecurity.setExecutionTime(getExecutionTime());
    return logSecurity;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Optiene el id del usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param token
   * @return
   */
  public Long extractIdUser(String token) {
    Claims claims = extractAllClaims(token);
    return Long.parseLong(claims.get(JWT_ID_USER).toString());
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Optiene el estado del usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param token
   * @return
   */
  public UserStateEnum extractUserState(String token) {
    Claims claims = extractAllClaims(token);
    return UserStateEnum.valueOf(claims.get(JWT_USER_STATE).toString());

  }


  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Optiene el email del usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param token
   * @return
   */
  public String extractUserEmail(String token) {
    Claims claims = extractAllClaims(token);
    return claims.get(JWT_USER_EMAIL).toString();

  }

}
