package com.alineumsoft.zenwk.security.common.message.component;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class MessageSourceAccessor
 */
@Component
public class MessageSourceAccessorComponent {
  private static MessageSource messageSource;

  /**
   * <p>
   * <b> Util </b> Constructor
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param messageSource
   */
  public MessageSourceAccessorComponent(MessageSource messageSource) {
    MessageSourceAccessorComponent.messageSource = messageSource;
  }

  /**
   * <p>
   * <b> Util </b> Obtener mensaje con internacionalizacion
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param key
   * @return
   */
  public static String getMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }

  /**
   * <p>
   * <b> Util</b> Obtiene un mensaje formateado basado en una clave de mensaje y parámetros
   * opcionales.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param key
   * @param params
   * @return
   */
  public static String getMessage(String key, String... params) {
    return messageSource.getMessage(key, params, LocaleContextHolder.getLocale());
  }
}
