package com.alineumsoft.zenwk.security.common.exception;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class FunctionalException
 */
public class FunctionalException extends CoreException {
  private static final long serialVersionUID = 1L;

  /**
   * <p>
   * <b> General </b> FunctionalException
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param <T>
   * @param message
   * @param code
   * @param cause
   * @param repository
   * @param entity
   */
  public <T> FunctionalException(String message, Throwable cause, JpaRepository<T, ?> repository,
      T entity) {
    super(message, cause, repository, entity);
  }

}
