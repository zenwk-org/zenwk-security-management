package com.alineumsoft.zenwk.security.common.message.component;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class MessageSourceAccessorComponent
 */
@Component
@Data
public class MessageSourceAccessorComponent {

  private final MessageSource messageSource;

  /**
   * <p>
   * <b> Util </b> Constructor inyectado
   * </p>
   *
   * @param messageSource Fuente de mensajes internacionalizados.
   */
  public MessageSourceAccessorComponent(MessageSource messageSource) {
    this.messageSource = messageSource;
    MessageSourceHolder.set(messageSource);
  }

  /**
   * <p>
   * <b> Util </b> Obtener mensaje con internacionalización
   * </p>
   */
  public static String getMessage(String key) {
    try {
      return MessageSourceHolder.get().getMessage(key, null, LocaleContextHolder.getLocale());
    } catch (Exception e) {
      return key; // fallback seguro
    }



  }

  /**
   * <p>
   * <b> Util</b> Obtener mensaje formateado con parámetros opcionales.
   * </p>
   */
  public static String getMessage(String key, String... params) {
    try {
      return MessageSourceHolder.get().getMessage(key, params, LocaleContextHolder.getLocale());
    } catch (Exception e) {
      return key; // fallback seguro
    }

  }

  /**
   * Clase interna estática que mantiene una referencia segura al MessageSource.
   */
  private static final class MessageSourceHolder {
    private static MessageSource source;

    private MessageSourceHolder() {}

    static void set(MessageSource messageSource) {
      if (source == null) {
        source = messageSource;
      }
    }

    static MessageSource get() {
      if (source == null) {
        throw new IllegalStateException("MessageSource not initialized yet.");
      }
      return source;
    }
  }
}
