package com.alineumsoft.zenwk.security.common.exception;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class TechnicalException
 */
public class TechnicalException extends CoreException {
  private static final long serialVersionUID = 1L;

  /**
   * <p>
   * <b> General </b> TechnicalException
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
  public <T> TechnicalException(String message, Throwable cause, JpaRepository<T, ?> repository,
      T entity) {
    super(message, cause, repository, entity);
  }

}
