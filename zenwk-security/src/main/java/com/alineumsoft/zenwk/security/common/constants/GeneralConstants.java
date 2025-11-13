package com.alineumsoft.zenwk.security.common.constants;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class UtilConstants
 */
public final class GeneralConstants {
  // Ruta definida donde se encuentra los mensajes y la internacionalizacion.
  public static final String CLASSPATH_LANG = "classpath:i18n/messages";
  // Constante que define el metodo estandar para uso de la utilidad historica.
  public static final String METHOD_SAVE_HISTORICAL = "saveHistorical";
  public static final String LEFT_BRACKET = "[";
  public static final String RIGHT_BRACKET = "]";
  // Mascara para datos sensibles
  public final static String VALUE_SENSITY_MASK = "************";
  // Nombre atributo sensible para enmascarar
  public final static String FIELD_PASSWORD = "password";
  // Constante para registro en log cuando una api es invocada por un evento
  public static final String AUTO_GENERATED_EVENT = "AUTO_GENERATED_EVENT";
  // Rerencia la anotacion personalizada @EntityExists
  public final static String ANOTATION_ENTITY_EXISTS = "existsById";
  public final static String TIMER_SEG = "(s)";
  // 2h
  public static final int TOKEN_CSRF_CODE_MINUTES = 120;
  public static final int TOKEN_CSRF_CODE_ZISE = 128;
}
