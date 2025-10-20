package com.alineumsoft.zenwk.security.constants;

/**
 * <p>
 * Contiene las claves de los mensajes de validación utilizados en los DTOs para anotaciones de
 * validación como {@code @Valid}, {@code @NotNull}, {@code @Size}, entre otras. Estas claves se
 * utilizan en archivos de internacionalización (i18n) para proporcionar mensajes de error
 * personalizados en las validaciones.
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class PersonValidationEnum
 */
public final class DtoValidationKeys {
  /**
   * PersonDTO
   */
  public static final String PERSON_ID_USER_NOT_NULL = "validation.person.iduser.notnull";
  public static final String PERSON_ID_SEX_NOT_NULL = "validation.person.idsex.notnull";
  public static final String PERSON_AGE_NOT_NULL = "validation.person.age.notnull";
  public static final String PERSON_ID_USER_NOT_FOUND = "validation.person.iduser.notfound";
  public static final String PERSON_FIRST_NAME_NOT_NULL = "validation.person.firstname.notnull";
  public static final String PERSON_LAST_NAME_NOT_NULL = "validation.person.lastname.notnull";
  public static final String PERSON_NAME_INVALID = "validation.person.firstname.invalid";
  public static final String PERSON_LAST_NAME_INVALID = "validation.person.lastname.invalid";
  public static final String PERSON_PHONE_NUMBER_INVALID = "validation.person.phonenumber.invalid";
  public static final String PERSON_DATE_INVALID = "validation.person.date.invalidformat";
  public static final String PERSON_NUMERIC_INVALID = "validation.person.numeric.invalid";
  public static final String PERSON_ALPHANUMERIC_INVALID = "validation.person.alphanumeric.invalid";
  public static final String PERSON_COLOMBIA_ADDRESS = "validation.address.invalidformat";
  public static final String PERSON_URL_INVALID = "validation.person.url.invalid";
  /**
   * UserDTO
   */
  public static final String USER_PASSWORD_INVALID = "validation.user.password.invalid";
  public static final String USER_USERNAME_INVALID = "validation.user.username.invalid";
  public static final String USER_EMAIL_INVALID = "validation.user.email.invalid";
  public static final String USER_EMAIL_NOT_NULL = "validation.user.email.notnull";
  public static final String USER_USERNAME_NOT_NULL = "validation.user.username.notnull";
  public static final String USER_PASSWORD_NOT_NULL = "validation.user.password.notnull";
  public static final String USER_EMAIL_MAX_LENGTH = "validation.user.email.maxlength";
  public static final String USER_PASSWORD_MAX_LENGTH = "validation.user.password.maxlength";
  public static final String USER_USERNAME_MAX_LENGTH = "validation.user.username.maxlength";
  /**
   * RoleDTO
   */
  public static final String ROLE_NAME_INVALID = "validation.role.name.invalid";
  public static final String ROLE_NAME_MAX_LENGTH = "validation.role.name.invalid.maxlength";
  public static final String ROLE_NAME_NOT_NULL = "validation.role.name.notnull";
  public static final String ROLE_DECRIPTION_NOT_NULL = "validation.role.description.notnull";
  public static final String ROLE_DESCRIPTION_MAX_LENGTH = "validation.role.description.maxlength";
  /**
   * PermissionDTO
   */
  public static final String PERMISSION_NAME_INVALID = "validation.permission.name.invalid";
  public static final String PERMISSION_NAME_MAX_LENGTH =
      "validation.permission.name.invalid.maxlength";
  public static final String PERMISSION_NAME_NOT_NULL = "validation.permission.name.notnull";
  public static final String PERMISSION_DESCRIPTION_MAX_LENGTH =
      "validation.permission.description.invalid.maxlength";
  public static final String PERMISSION_DESCRIPTION_NOT_NULL =
      "validation.permission.description.notnull";
  public static final String PERMISSION_RESOURCE_INVALID = "validation.permission.resource.invalid";
  public static final String PERMISSION_RESOURCE_MAX_LENGTH =
      "validation.permission.resource.invalid.maxlength";
  public static final String PERMISSION_RESOURCE_NOT_NULL =
      "validation.permission.resource.notnull";
  public static final String PERMISSION_METHOD_INVALID = "validation.permission.method.invalid";
  public static final String PERMISSION_METHOD_MAX_LENGTH =
      "validation.permission.method.invalid.maxlength";
  public static final String PERMISSION_METHOD_NOT_NULL = "validation.permission.method.notnull";
  public static final String PERMISSION_OPERATION_INVALID =
      "validation.permission.operation.invalid";
  public static final String PERMISSION_OPERATION_MAX_LENGTH =
      "validation.permission.operation.invalid.maxlength";
  public static final String PERMISSION_OPERATION_NOT_NULL =
      "validation.permission.operation.notnull";

  /**
   * ResetPasswordDTO
   */
  public static final String RESET_PASSWORD_CODE_TOKEN_NOT_NULL =
      "validation.resetpassword.codetoken.notnull";
  public static final String RESET_PASSWORD_CODE_UUID_NOT_NULL =
      "validation.resetpassword.codeuuid.notnull";
  /**
   * TokenDTO
   */
  public static final String URL_OR_EMPTY_REGEX = "validation.resetpassaord.url.not.valid";


}
