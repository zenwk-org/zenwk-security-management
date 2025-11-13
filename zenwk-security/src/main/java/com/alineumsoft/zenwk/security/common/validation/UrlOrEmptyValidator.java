package com.alineumsoft.zenwk.security.common.validation;

import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validador
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project zenwk-verification
 * @class UrlOrEmptyValidator
 */
@Component
public class UrlOrEmptyValidator implements ConstraintValidator<ValidUrlOrEmpty, String> {
  /**
   * urlRegex
   */
  @Value("${validation.regex.allowed-url}")
  private String urlRegex;
  /**
   * pattern
   */
  private Pattern pattern;

  /**
   * 
   * <p>
   * <b> @PostConstruct </b>
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   */
  @PostConstruct
  public void init() {
    this.pattern = Pattern.compile(urlRegex);
  }

  /**
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param value
   * @param context
   * @return
   * @see jakarta.validation.ConstraintValidator#isValid(java.lang.Object,
   *      jakarta.validation.ConstraintValidatorContext)
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value == null || pattern.matcher(value).matches();
  }
}
