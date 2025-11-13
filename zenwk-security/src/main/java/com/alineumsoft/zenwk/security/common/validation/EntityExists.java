package com.alineumsoft.zenwk.security.common.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * <p>
 * Interfaz para validador generico que indica si una relacion foranea existe o no buscando por id
 * la entidad. Estandar de validación de Bean Validation (JSR 380)
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class EntityExists
 */
@Documented
@Constraint(validatedBy = EntityExistsValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityExists {
  String message();

  Class<?> service();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
