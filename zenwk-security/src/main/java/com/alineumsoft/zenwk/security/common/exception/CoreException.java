package com.alineumsoft.zenwk.security.common.exception;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

/*
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * 
 * @project SecurityUser
 * 
 * @class BaseException
 */
@Slf4j
@Service
public abstract class CoreException extends RuntimeException {
  static final long serialVersionUID = 1L;
  // Metodo comun por convencion en cualquier tabla de logs
  private static final String METHOD_MESSAGE_ERROR = "setErrorMessage";

  /**
   * <p>
   * <b> General </b> CoreException
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
  public <T> CoreException(String message, Throwable cause, JpaRepository<T, ?> repository,
      T entity) {
    super(message, cause);
    saveLog(repository, entity, message);
  }

  /**
   * <p>
   * <b> General </b> Periste la excepcion
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param <T>
   * @param repository
   * @param entity
   */
  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  private <T> void saveLog(JpaRepository<T, ?> repository, T entity, String message) {
    if (repository != null && entity != null) {
      try {
        entity.getClass().getMethod(METHOD_MESSAGE_ERROR, String.class).invoke(entity, message);
      } catch (Exception e) {
        log.error(e.getMessage());
        throw new RuntimeException(e);
      }
      repository.save(entity);
    }
  }

}
