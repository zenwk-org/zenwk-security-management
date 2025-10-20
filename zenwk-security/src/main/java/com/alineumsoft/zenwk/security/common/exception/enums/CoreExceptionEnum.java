package com.alineumsoft.zenwk.security.common.exception.enums;

import com.alineumsoft.zenwk.security.common.constants.CommonMessageConstants;
import com.alineumsoft.zenwk.security.common.message.component.MessageSourceAccessorComponent;
import lombok.Getter;

/**
 * <b>Enum que define las excepciones tecnicas y funcionales a nivel general del sistema.</b>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class GeneralCoreExceptionEnum
 */
@Getter
public enum CoreExceptionEnum {
  /**
   * Excepcionoes generales
   */
  TECH_COMMON_HISTORICAL_ENTITY_NOT_FOUND("TECH_GEN_0001",
      "common.exception.error.historical.entity.notfound"), TECH_COMMON_MESSAGE_NOT_FOUND(
          "TECH_GEN_0002",
          "common.exception.error.enum.message.notfound"), TECH_COMMON_ANOTATION_ENTITY_NOT_EXISTS(
              "TECH_GEN_0003", "common.exception.entity.noexists"), FUNC_COMMON_ERROR_GENERAL(
                  "FUNC_COM_0004", "common.exception.error.general"), FUNC_COMMON_ROLE_NOT_EXIST(
                      "FUNC_COM_0005", "common.exception.rol.noexists"),
  /**
   * Token CSRF
   */
  FUNC_VERIFICATION_TOKEN_CSRF_REVOKED("FUNC_SEC_VERIFICATION_CSRF_0001",
      "functional.verification.token.csrf.revoked"), FUNC_VERIFICATION_TOKEN_CSRF_NOT_FOUND(
          "FUNC_SEC_VERIFICATION_CSRF_0002",
          "functional.verification.token.csrf.nofound"), FUNC_VERIFICATION_TOKEN_CSRF_EXPIRATION(
              "FUNC_SEC_VERIFICATION_CSRF_0003", "functional.verification.token.expiration");


  /**
   * code
   */
  private final String code;
  /**
   * messageKey
   */
  private final String messageKey;

  /**
   * @param code
   * @param messageKey
   */
  CoreExceptionEnum(String code, String messageKey) {
    this.code = code;
    this.messageKey = messageKey;
  }

  /**
   * @return
   */
  public String getMessage() {
    try {
      return MessageSourceAccessorComponent.getMessage(messageKey);
    } catch (Exception e) {
      throw new RuntimeException(CoreExceptionEnum.TECH_COMMON_MESSAGE_NOT_FOUND.getCodeMessage());
    }
  }

  /**
   * <p>
   * <b> General core exception: </b> Obtiene un mensaje formateado basado en una clave de mensaje y
   * parámetros opcionales.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param params
   * @return
   */
  public String getMessage(String... params) {
    try {
      return MessageSourceAccessorComponent.getMessage(messageKey, params);
    } catch (Exception e) {
      throw new RuntimeException(
          CoreExceptionEnum.TECH_COMMON_MESSAGE_NOT_FOUND.getCodeMessage(messageKey));
    }
  }

  /**
   * 
   * <p>
   * <b> General core exception: </b> Descripción con codigo de la excepcion generada
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  public String getCodeMessage() {
    return String.format(CommonMessageConstants.FORMAT_EXCEPTION, getCode(), getMessage());
  }

  /**
   * 
   * <p>
   * <b> General core exception: </b> Descripción con codigo de la excepcion generada con parametros
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  public String getCodeMessage(String... params) {
    return String.format(CommonMessageConstants.FORMAT_EXCEPTION, getCode(), getMessage(params));
  }

}
