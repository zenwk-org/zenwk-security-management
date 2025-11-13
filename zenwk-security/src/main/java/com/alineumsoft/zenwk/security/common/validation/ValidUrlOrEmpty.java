package com.alineumsoft.zenwk.security.common.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Interfaz
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project zenwk-verification
 * @class ValidUrlOrEmpty
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UrlOrEmptyValidator.class)
@Documented
public @interface ValidUrlOrEmpty {
  String message();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
