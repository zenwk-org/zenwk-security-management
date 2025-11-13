package com.alineumsoft.zenwk.security.common.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import com.alineumsoft.zenwk.security.common.constants.CommonMessageConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class ObjectUpdater
 */
@Slf4j
public final class ObjectUpdaterUtil {
  /**
   * Campos descartados
   */
  private static final List<String> DISCARDED_FIELDS =
      Arrays.asList("creationUser", "modificationUser", "userCreation", "modificationDate",
          "creationDate", "userModification", "id");

  /**
   * <p>
   * <b>Util</b> Método para actualizar un objeto del mismo tipo, discriminando por datos nulos o
   * iguales.
   * </p>
   *
   * @param <T> Tipo del objeto.
   * @param source La entidad con los nuevos valores a actualizar.
   * @param target La entidad original recuperada de la BD, que será actualizada.
   * @return Retorna {@code true} si se actualizó algún dato (incluyendo asignaciones a
   *         {@code null}), {@code false} en caso contrario.
   */
  static public <T> boolean updateDataEqualObject(T source, T target) {
    boolean isPersist = false;
    validateObjects(source, target);

    // Campos de la clase del objeto
    Field[] fieldsSource = getFilteredFields(source.getClass());

    for (Field field : fieldsSource) {
      field.setAccessible(true);

      try {
        Object valueTarget = field.get(target);
        Object valueSource = field.get(source);
        if (!Objects.equals(valueTarget, valueSource)) {
          field.set(target, valueSource);
          isPersist = true;
        }
      } catch (IllegalAccessException e) {
        log.warn(CommonMessageConstants.FORMAT_EXCEPTION, field.getName(), e.getMessage());
      }
    }
    return isPersist;
  }

  /**
   * 
   * <p>
   * <b> Util. </b> Obtiene una lista de los atributos de la clase descartando campos de auditoria y
   * pk
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param clazz
   * @return
   */
  public static Field[] getFilteredFields(Class<?> clazz) {
    return Arrays.stream(clazz.getDeclaredFields())
        .filter(field -> !DISCARDED_FIELDS.contains(field.getName())).toArray(Field[]::new);
  }

  /**
   * <p>
   * <b> Util. </b> Valida si un uno de los objectos es nulo
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param <T>
   * @param source
   * @param target
   */
  private static <T> void validateObjects(T source, T target) {
    if (source == null || target == null) {
      throw new IllegalArgumentException(CommonMessageConstants.ILEGAL_ARGUMENT_EXCEPTION);
    }
  }
}
