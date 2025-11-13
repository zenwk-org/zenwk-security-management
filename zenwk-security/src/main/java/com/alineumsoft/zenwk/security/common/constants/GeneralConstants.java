package com.alineumsoft.zenwk.security.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class UtilConstants
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GeneralConstants {
  // Ruta definida donde se encuentra los mensajes y la internacionalizacion.
  public static final String CLASSPATH_LANG = "classpath:i18n/messages";
  // Constante que define el metodo estandar para uso de la utilidad historica.
  public static final String METHOD_SAVE_HISTORICAL = "saveHistorical";
  public static final String LEFT_BRACKET = "[";
  public static final String RIGHT_BRACKET = "]";
  // Mascara para datos sensibles
  public static final String VALUE_SENSITY_MASK = "************";
  // Nombre atributo sensible para enmascarar
  public static final String FIELD_PASSWORD = "password";
  // Constante para registro en log cuando una api es invocada por un evento
  public static final String AUTO_GENERATED_EVENT = "AUTO_GENERATED_EVENT";
  // Rerencia la anotacion personalizada @EntityExists
  public static final String ANOTATION_ENTITY_EXISTS = "existsById";
  public static final String TIMER_SEG = "(s)";
  // 2h
  public static final int TOKEN_CSRF_CODE_MINUTES = 120;
  public static final int TOKEN_CSRF_CODE_ZISE = 128;
}
