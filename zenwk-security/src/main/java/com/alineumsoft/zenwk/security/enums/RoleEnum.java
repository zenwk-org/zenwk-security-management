package com.alineumsoft.zenwk.security.enums;

import com.alineumsoft.zenwk.security.common.message.component.MessageSourceAccessorComponent;
import lombok.Getter;

/**
 * <p>
 * Enum que define los roles predeterminados del sistema. Los roles creados a demanda por el
 * administrador no necesitan ser mapeados aquí, salvo si es requerido mostrar un mensaje con
 * internacionalización.
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class RoleEnum
 */
@Getter
public enum RoleEnum {
  SYSTEM_ADMIN("role.security.systemadmin"), SECURITY_ADMIN(
      "role.security.securityadmin"), APP_ADMIN("role.security.appadmin"), AUDITOR(
          "role.security.auditor"), USER("role.security.user"), NEW_USER("role.security.newuser");

  /**
   * messageKey
   */
  private final String messageKey;

  /**
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param messageKey
   */
  RoleEnum(String messageKey) {
    this.messageKey = messageKey;
  }

  /**
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  public String getDescription() {
    return MessageSourceAccessorComponent.getMessage(messageKey);
  }

}
