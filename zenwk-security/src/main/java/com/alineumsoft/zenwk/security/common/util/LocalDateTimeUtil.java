package com.alineumsoft.zenwk.security.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import com.alineumsoft.zenwk.security.common.message.component.MessageSourceAccessorComponent;
import com.alineumsoft.zenwk.security.constants.DtoValidationKeys;

/**
 * <p>
 * Clase utilitaria que proporciona métodos transversales para el manejo y manipulación de tipos de
 * datos relacionados con fechas y tiempos, tales como {@link java.util.Calendar},
 * {@link java.time.LocalDateTime}, y {@link java.util.Date}.
 * </p>
 * <p>
 * Los métodos de esta clase permiten convertir, formatear y realizar operaciones comunes sobre
 * estos tipos de datos para asegurar consistencia en la manipulación de fechas en el proyecto.
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class LocalDateTimeUtil
 */
public final class LocalDateTimeUtil {
  /**
   * Constante para el formato ISO-8601
   */
  private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER =
      DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  /**
   * <p>
   * <b> General </b> Convierte una cadena a un LocalDateTime usando el formato ISO-8601
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dateString
   * @return
   */
  public static LocalDateTime getLocalDateTimeIso8601(String dateString) {
    if (dateString == null) {
      return null;
    }
    try {
      return LocalDateTime.parse(dateString, ISO_DATE_TIME_FORMATTER);
    } catch (DateTimeParseException e) {
      String msgError =
          MessageSourceAccessorComponent.getMessage(DtoValidationKeys.PERSON_DATE_INVALID);
      throw new IllegalArgumentException(msgError);
    }
  }

  /**
   * <p>
   * <b> General </b>Convierte un LocalDateTime a un string usando el formato ISO-8601
   * </p>
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param date
   * @return
   */
  public static String getLocalDateTimeIso86018(LocalDateTime date) {
    return ISO_DATE_TIME_FORMATTER.format(date);
  }

}
