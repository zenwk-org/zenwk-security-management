package com.alineumsoft.zenwk.security.common.validation;

import java.lang.reflect.Method;
import org.springframework.context.ApplicationContext;
import com.alineumsoft.zenwk.security.common.constants.GeneralConstants;
import com.alineumsoft.zenwk.security.common.exception.enums.CoreExceptionEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * <b> Validador generico que indica si una relacion foranea existe o no buscando por id la
 * entidad.</b>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class EntityExistsValidator
 */
public class EntityExistsValidator implements ConstraintValidator<EntityExists, Long> {
  private final ApplicationContext applicationContext;

  private Class<?> serviceClass;

  /**
   * <p>
   * <b> Constructor </b>
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param applicationContext
   */
  public EntityExistsValidator(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * <p>
   * incializa la anotacion personalizada
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param annotation
   * @see jakarta.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
   */
  @Override
  public void initialize(EntityExists annotation) {
    this.serviceClass = annotation.service();
  }

  /**
   * <p>
   * Para que la anotacion funcione debe existir el metodo existsById
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param id
   * @param context
   * @return
   * @see jakarta.validation.ConstraintValidator#isValid(java.lang.Object,
   *      jakarta.validation.ConstraintValidatorContext)
   */
  @Override
  public boolean isValid(Long id, ConstraintValidatorContext context) {
    if (id == null) {
      // Si cumple @NotNull hace efecto.
      return true;
    }

    try {
      Object serviceBean = applicationContext.getBean(serviceClass);
      Method existsByIdMethod =
          serviceClass.getMethod(GeneralConstants.ANOTATION_ENTITY_EXISTS, Long.class);
      return (boolean) existsByIdMethod.invoke(serviceBean, id);
    } catch (Exception e) {
      throw new RuntimeException(
          CoreExceptionEnum.TECH_COMMON_ANOTATION_ENTITY_NOT_EXISTS.getCodeMessage(), e);
    }
  }
}
