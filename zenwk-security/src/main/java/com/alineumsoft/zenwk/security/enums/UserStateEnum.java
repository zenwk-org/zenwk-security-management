package com.alineumsoft.zenwk.security.enums;

import com.alineumsoft.zenwk.security.common.message.component.MessageSourceAccessorComponent;
import lombok.Getter;

/**
 * <p>
 * Enum con los estados que puede tener un usuario.
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class UserStateEnum
 */
@Getter
public enum UserStateEnum {
  INCOMPLETE_PERFIL("user.state.icompleteperfil"), ACTIVE("user.state.active"), DISABLED(
      "user.state.disabled");

  /**
   * messageKey
   */
  private final String messageKey;

  /**
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param messageKey
   */
  UserStateEnum(String messageKey) {
    this.messageKey = messageKey;
  }

  /**
   * <p>
   * Obtiene la descripción del mensaje usando la clave desde messages.properties.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param messageSource
   * @return
   */
  public String getDescription() {
    return MessageSourceAccessorComponent.getMessage(messageKey);
  }
}
