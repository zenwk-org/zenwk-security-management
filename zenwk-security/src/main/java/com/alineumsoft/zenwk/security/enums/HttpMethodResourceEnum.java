package com.alineumsoft.zenwk.security.enums;

import org.springframework.http.HttpMethod;
import lombok.Getter;

/**
 * <p>
 * Enum que define los recursos expuestos y sus metodos http para el api de seguridad a ser usado en
 * RBAC definido.
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class HttpMethodResourceEnum
 */
@Getter
public enum HttpMethodResourceEnum {
  /**
   * Recursos par ala entidad usuarios
   */
  USER_CREATE(HttpMethod.POST, "/api/users"), USER_UPDATE(HttpMethod.PUT,
      "/api/users/{id}"), USER_DELETE(HttpMethod.DELETE, "/api/users/{id}"), USER_LIST(
          HttpMethod.GET,
          "/api/users"), USER_GET(HttpMethod.GET, "/api/users/{id}"), USER_GET_EMAIL(HttpMethod.GET,
              "/api/users/email/{email}"), USER_JWT_ME(HttpMethod.GET, "/api/users/me"),
  /**
   * Recursos para la entidad persona
   */
  PERSON_CREATE(HttpMethod.POST, "/api/persons"), PERSON_UPDATE(HttpMethod.PUT,
      "/api/persons/{id}"), PERSON_DELETE(HttpMethod.DELETE, "/api/persons/{id}"), PERSON_LIST(
          HttpMethod.GET, "/api/persons"), PERSON_GET(HttpMethod.GET,
              "/api/persons/{id}"), PERSON_UPLOAD_PHOTO_PROFILE(HttpMethod.POST,
                  "/profile/upload-photo"),
  /**
   * Permisos
   */
  PERMISSION_CREATE(HttpMethod.POST, "/api/permissions"), PERMISSION_LIST(HttpMethod.GET,
      "/api/permissions"), PERMISSION_GET(HttpMethod.GET,
          "api/permissions/{id}"), PERMISSION_UPDATE(HttpMethod.PUT,
              "/api/permissions/{id}"), PERMISSION_DELETE(HttpMethod.DELETE,
                  "/api/permissions/{id}"),
  /**
   * Roles
   */
  ROLE_CREATE(HttpMethod.POST, "/api/roles"), ROLE_UPDATE(HttpMethod.PUT,
      "/api/roles/{id}"), ROLE_DELETE(HttpMethod.DELETE, "/api/roles/{id}"), ROLE_GET(
          HttpMethod.GET, "/api/roles/{id}"), ROLE_LIST(HttpMethod.GET, "/api/roles"),
  /**
   * Auth
   */
  AUTH_LOGIN(HttpMethod.POST, "/api/auth/login"), AUTH_LOGOUT(HttpMethod.DELETE,
      "/api/auth/logout"), AUTH_RESET_PASSWORD(HttpMethod.POST,
          "/api/auth/reset-password/{email}"), AUTH_REFRESH_JWT(HttpMethod.POST,
              "/api/auth/refresh-jwt"),
  /**
   * Verification (publicas)
   */
  VERIFICATION_TOKEN(HttpMethod.POST, "/api/verification/**"),
  /**
   * Recursos para la entidad sexo.
   */
  SEX_LIST_OPTIONS(HttpMethod.GET, "/api/person-sex"),
  /**
   * actuator
   */
  ACTUATOR(null, "/actuator/**");


  /**
   * Metodo http
   */
  private final HttpMethod method;
  /**
   * recurso
   */
  private final String resource;

  /**
   * <p>
   * <b> Constructor </b>
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param method
   * @param resource
   */
  HttpMethodResourceEnum(HttpMethod method, String resource) {
    this.method = method;
    this.resource = resource;
  }
}
