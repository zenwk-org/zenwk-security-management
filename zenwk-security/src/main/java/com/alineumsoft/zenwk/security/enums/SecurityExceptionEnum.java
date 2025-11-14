package com.alineumsoft.zenwk.security.enums;

import org.hibernate.procedure.NoSuchParameterException;
import com.alineumsoft.zenwk.security.common.constants.CommonMessageConstants;
import com.alineumsoft.zenwk.security.common.exception.enums.CoreExceptionEnum;
import com.alineumsoft.zenwk.security.common.message.component.MessageSourceAccessorComponent;
import lombok.Getter;

/**
 * <p>
 * Enum para los mensajes a mostrar cuando se generan excepciones funcionales predefinidas en el
 * sistema.
 * <p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class ErrorCodeException
 */
@Getter
public enum SecurityExceptionEnum {
  /**
   * Auth
   */
  FUNC_AUTH_TOKEN_JWT_INVALID("FUNC_SEC_AUTH_0001",
      "functional.auth.token.invalid"), FUNC_AUTH_URI_FORBIDDEN("FUNC_SEC_AUTH_0002",
          "functional.auth.uri.forbidden"), FUNC_AUTH_BAD_CREDENTIALS("FUNC_SEC_AUTH_0003",
              "functional.auth.password.bad.credentials"), FUNC_AUTH_TOKEN_EXPIRED(
                  "FUNC_SEC_AUTH_0004",
                  "functional.auth.token.expired"), FUNC_AUTH_TOKEN_NOT_FOUND("FUNC_SEC_AUTH_0005",
                      "functional.auth.token.notfound"), FUNC_AUTH_EMAIL_NOT_MATCH(
                          "FUNC_SEC_AUTH_0006",
                          "functional.auth.token.email.notmatch"), FUNC_AUTH_UUID_NOT_MATCH(
                              "FUNC_SEC_AUTH_0007",
                              "functional.auth.token.uuid.notmatch"), FUNC_AUTH_CODE_NOT_MATCH(
                                  "FUNC_SEC_AUTH_0008", "functional.auth.token.code.notmatch"),
  /**
   * Roles (asociados a usuario)
   */
  FUNC_ROLE_USER_NOT_EXIST("FUNC_SEC_ROLE_0001", "functional.roleuser.not.exist"),

  /**
   * User
   */
  FUNC_USER_CREATE_EMAIL_UNIQUE("FUNC_SEC_USER_0001",
      "functional.user.create.email.unique"), FUNC_USER_NOT_FOUND_ID("FUNC_SEC_USER_0002",
          "functional.user.id.notfound"), FUNC_USER_NOT_FOUND_USERNAME("FUNC_SEC_USER_0003",
              "functional.user.username.notfound"), FUNC_USER_MAIL_EXISTS("FUNC_SEC_USER_0004",
                  "functional.user.email.exists"), FUNC_USER_EXIST("FUNC_SEC_USER_0005",
                      "functional.user.exist"), FUNC_USER_EMAIL_INVALID("FUNC_SEC_USER_0006",
                          "validation.user.email.invalid"), FUNC_ERROR_PASSWORD_REUSE(
                              "FUNC_SEC_USER_007", "functional.user.errorpassword.reused"),

  /**
   * Person
   */
  FUNC_PERSON_NOT_FOUND("FUNC_SEC_PERSON_0001", "functional.person.notfound"), FUNC_PERSON_EXIST(
      "FUNC_SEC_PERSON_0002",
      "functional.person.exist"), FUNC_PERSON_ID_USER_NOT_ASSOCIATE("FUNC_SEC_PERSON_0003",
          "functional.person.iduser.notassociate"), FUNC_PERSON_ID_USER_IS_ASSOCIATE(
              "FUNC_SEC_PERSON_0004", "functional.person.iduser.isassociate"),

  /**
   * Role
   */
  FUNC_ROLE_EXISTS("FUNC_SEC_ROLE_0002", "functional.role.exist"), FUNC_ROLE_NOT_EXISTS(
      "FUNC_SEC_ROLE_0003",
      "common.exception.role.noexists"), TECH_ROLE_NOT_DELETE("TECH_SEC_ROLE_0001",
          "functional.exception.role.nodelete"), FUNC_ROLE_ASSIGNMENT_PERMISSION_EXISTS(
              "FUNC_SEC_ROLE_0004",
              "functional.exception.role.assignment.exists"), FUNC_ROLE_ASSIGNMENT_PERMISSION_NOT_EXISTS(
                  "FUNC_SEC_ROLE_0005", "functional.exception.role.assignment.notexists"),

  /**
   * Permission
   */
  FUNC_PERMISSION_EXIST("FUNC_SEC_PERMISSION_0001",
      "functional.permission.exist"), FUNC_PERMISSION_NOT_EXISTS("FUNC_SEC_PERMISSION_0002",
          "functional.permission.noexists"), TECH_PERMISSION_NOT_DELETE("TECH_SEC_PERMISSION_0001",
              "functional.permission.notdelete"),
  /**
   * Permission
   */
  FUNC_PERSON_SEX_NO_FOUND("FUNC_SEC_PERSON_SEX_0001", "functional.personsex.notfound");

  /**
   * code
   */
  private final String code;
  /**
   * key
   */
  private final String key;

  /**
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param codeException
   * @param keyMessage
   */
  SecurityExceptionEnum(String code, String keyMessage) {
    this.code = code;
    this.key = keyMessage;

  }

  /**
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  public String getMessage() {
    try {
      return MessageSourceAccessorComponent.getMessage(key);
    } catch (Exception e) {
      throw new NoSuchParameterException(
          CoreExceptionEnum.TECH_COMMON_MESSAGE_NOT_FOUND.getCodeMessage(key));
    }
  }

  /**
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  public String getMessage(String... params) {
    try {
      return MessageSourceAccessorComponent.getMessage(key, params);
    } catch (Exception e) {
      throw new NoSuchParameterException(
          CoreExceptionEnum.TECH_COMMON_MESSAGE_NOT_FOUND.getCodeMessage(key));
    }
  }

  /**
   * <p>
   * <b> General User Exception. </b> Recupera el mensaje incluyendo el codigo
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  public String getCodeMessage() {
    return String.format(CommonMessageConstants.FORMAT_EXCEPTION, code, getMessage());
  }

  /**
   * <p>
   * <b> General User Exception. </b> Recupera el mensaje incluyendo el codigo con parametros
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  public String getCodeMessage(String... params) {
    return String.format(CommonMessageConstants.FORMAT_EXCEPTION, code, getMessage(params));
  }

}
