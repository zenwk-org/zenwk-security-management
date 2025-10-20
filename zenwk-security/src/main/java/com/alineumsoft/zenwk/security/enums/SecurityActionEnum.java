package com.alineumsoft.zenwk.security.enums;

import lombok.Getter;

/**
 * <p>
 * Enum que representa las acciones de seguridad en el sistema.
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class ServiceNameEnum
 */
@Getter
public enum SecurityActionEnum {
  /**
   * User
   */
  USER_CREATE("USER.CREATE"), USER_UPDATE("USER.UPDATE"), USER_DELETE("USER.DELETE"), USER_GET(
      "USER.GET"), USER_ME_JWT("USER_ME_JWT.GET"), USER_LIST(
          "USER.LIST"), USER_LIST_ROLES("USER.LIST_ROLES"), USER_GET_EMAIL("USER.GET_EMAIL"),
  /**
   * Person
   */
  PERSON_CREATE("PERSON.CREATE"), PERSON_UPDATE("PERSON.UPDATE"), PERSON_DELETE(
      "PERSON.DELETE"), PERSON_GET("PERSON.GET"), PERSON_LIST(
          "PERSON.LIST"), PERSON_UPLOAD_PHOTO_PROFILE("PERRSON.UPLOAD_PHOTO_PROFILE"),
  /**
   * Auth
   */
  AUTH_LOGIN("AUTH.LOGIN"), AUTH_LOGOUT("AUTH.LOGOUT"), AUTH_RESET_PASSWORD(
      "AUTH.RESET_PASSWORD"), AUTH_REFRESH_JWT("ATUH.REFRESH_JWT"),
  /**
   * Role
   */
  ROLE_CREATE("ROLE.CREATE"), ROLE_UPDATE("ROLE.UPDATE"), ROLE_DELETE("ROLE.DELETE"), ROLE_GET(
      "ROLE.GET"), ROLE_LIST("ROLE.LIST"), ROLE_ASSIGNMENT_PERMISSION(
          "ROLE.ASSIGNMENT_PERMISSION"), ROLE_REMOVE_PERMISSION(
              "ROLE.REMOVE_PERMISSION"), ROLE_LIST_PERMISSIONS(
                  "ROLE.LIST_PERMISSIONS"), ROLE_ASSIGNMENT_USER(
                      "ROLE.ASSIGNMENT_USER"), ROLE_REMOVE_USER("ROLE.REMOVE_USER"),
  /**
   * Permission
   */
  PERMISSION_CREATE("PERMISSION.CREATE"), PERMISSION_UPDATE("PERMISSION.UPDATE"), PERMISSION_DELETE(
      "PERMISSION.DELETE"), PERMISSION_GET("PERMISSION.GET"), PERMISSION_LIST(
          "PERMISSION.LIST"), PERMISSION_LIST_ROLES("PERMISSION.LIST_ROLES"),
  /**
   * Verificación de token
   */
  VERIFICATION_SEND_TOKEN("VERIFICATION.SEND_TOKEN"), VERIFICATION_VALIDATE_TOKEN(
      "VERIFICATION.VARIFY_TOKEN");



  /**
   * code
   */
  private final String code;

  /**
   * <p>
   * <b> Constructor </b>
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param code
   */
  SecurityActionEnum(String code) {
    this.code = code;
  }

}
