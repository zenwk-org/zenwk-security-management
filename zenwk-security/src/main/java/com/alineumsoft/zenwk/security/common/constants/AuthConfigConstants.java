package com.alineumsoft.zenwk.security.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <p>
 * Constantes utilizadas en la autenticacion y autorizacion
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class AuthConfig
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthConfigConstants {
  /**
   * class: JwtProvider
   */
  public static final String HEADER_AUTHORIZATION = "Authorization";
  public static final String AUTHORIZATION_BEARER = "Bearer";
  public static final int INDEX_TOKEN = 7;
  public static final String JWT_EXPIRATION_TIME = "${security.jwt.expiration-time}";
  public static final String JWT_SECRET_KEY = "${security.jwt.secret}";
  public static final String JWT_ROLES = "roles";
  public static final String JWT_URLS_ALLOWED_ROL_USER = "urlsRolUser";
  public static final String JWT_ID_USER = "idUser";
  public static final String JWT_USER_STATE = "idState";
  public static final String JWT_USER_EMAIL = "email";
  /**
   * class: PermissionService
   */
  public static final String ID = "{id}";
  public static final String URL_USER = "user";
  public static final String URL_PERSON = "person";
  /**
   * class: JwtAuthenticationFilter
   */
  public static final String PERMISSION = "permissions";
  public static final String WEB_DETAILS = "webDetails";
  /**
   * class: CsrfValidationFilter
   */
  public static final String X_XSRF_TOKEN = "X-XSRF-TOKEN";
  public static final String X_USER_EMAIL = "X-USER-EMAIL";
  /**
   * class: CsrfController
   */
  public static final String XCSRF_TOKEN = "XCSRF-TOKEN";
  public static final String ZENWK_JWT = "ZENWK_JWT";
  // 10m
  public static final long TOKEN_RENEW_THRESHOLD_SECONDS = 600;


}
