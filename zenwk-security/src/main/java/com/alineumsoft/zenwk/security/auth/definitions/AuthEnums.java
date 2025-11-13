package com.alineumsoft.zenwk.security.auth.definitions;

import com.alineumsoft.zenwk.security.common.exception.enums.CoreExceptionEnum;
import com.alineumsoft.zenwk.security.common.message.component.MessageSourceAccessorComponent;
import lombok.Getter;

/**
 * <p>
 * enums utilizadas en la autenticacion y autorizacion
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class AuthEnums
 */
@Getter
public enum AuthEnums {
  AUTH_LOGOUT_SUCCES("auth.logout.succes");

  /**
   * key
   */
  private final String key;

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Constructor
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param key
   */
  AuthEnums(String key) {
    this.key = key;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Mensaje
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  public String getMessage() {
    try {
      return MessageSourceAccessorComponent.getMessage(key);
    } catch (Exception e) {
      throw new RuntimeException(
          CoreExceptionEnum.TECH_COMMON_MESSAGE_NOT_FOUND.getCodeMessage(key));
    }
  }

}
