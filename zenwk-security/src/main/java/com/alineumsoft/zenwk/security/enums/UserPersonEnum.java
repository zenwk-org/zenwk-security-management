package com.alineumsoft.zenwk.security.enums;

import com.alineumsoft.zenwk.security.common.message.component.MessageSourceAccessorComponent;
import lombok.Getter;

/**
 * <p>
 * Enum para los campos de la entidad persona.
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class UserEnum
 */
@Getter
public enum UserPersonEnum {
  USER_USERNAME("username"), USER_EMAIL("email"), PERSON_FIRST_NAME("firstName"), PERSON_LAST_NAME(
      "lastName");

  /**
   * messageKey
   */
  private final String messageKey;

  /**
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param messageKey
   */
  UserPersonEnum(String messageKey) {
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
