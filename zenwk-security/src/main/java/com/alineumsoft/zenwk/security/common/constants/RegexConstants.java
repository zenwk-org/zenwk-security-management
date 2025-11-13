package com.alineumsoft.zenwk.security.common.constants;

/**
 * <p>
 * Expresiones regulares comunes
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class RegexConstants
 */
public final class RegexConstants {
  // Letras, espacios, longitud 1-50.
  public static final String NAME = "^[A-Za-záéíóúÁÉÍÓÚñÑ]{1,30}$";
  // Formato de email.
  public static final String EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
  // Teléfonos con o sin prefijo internacional.
  public static final String PHONE_NUMBER = "^[+]?[0-9]{7,15}$";
  // Minimo 8 caracteres, al menos una letra, un número y un carácter especial
  public static final String PASSWORD =
      "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
  // Fecha en formato YYYY-MM-DD.
  public static final String DATE_ISO_8601 = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}";
  // Solo numeros.
  public static final String NUMERIC = "^\\d+$";
  // Letras y numeros.
  public static final String ALPHANUMERIC = "^[A-Za-z0-9]+$";
  // Alfanumericos y guiones, longitud maxima 30
  public static final String USERNAME = "^[a-zA-Z0-9._-]{3,30}$";
  // Expresion regular para validar un formato de direccion colombiano.
  public static final String COLOMBIA_ADDRESS = "^[a-zA-Z0-9\\s\\-#.,áéíóúÁÉÍÓÚñÑ]+$";
  // Codigo postal de 5 dígitos (o formato extendido).
  public static final String POSTAL_CODE = "^[0-9]{5}(-[0-9]{4})?$";
  // URL basica.
  public static final String URL = "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";
  // Formato nombre del rol.
  public static final String NAME_ROLE_PERMISSION = "^[A-Z]+(_[A-Z]+)*$";
  // Valores permitidos para el campo operación en la entidad permisos.
  public static final String OPERATION_PERMISSION = "^(CREATE|UPDATE|DELETE|GET|LIST)$";
  // Expresión regular para validar los endpoints en el sistema.
  public static final String RESOURCE_PERMISSION = "^\\/api\\/[a-z]+(\\/\\{?[a-zA-Z0-9_-]*\\}?)*$";
  // Métodos http permitidos.
  public static final String RESOURCE_METHOD = "^(GET|POST|PUT|DELETE)$";
  // Elimina todos los bloques [xxx] en cualquier parte del texto
  public static final String REGEX_ALL_BRACKETED_BLOCKS = "^\\[[^\\]]+\\]\\s*";
}
