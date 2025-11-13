package com.alineumsoft.zenwk.security.common.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.alineumsoft.zenwk.security.common.entity.CsrfToken;

/**
 * <p>
 * Repositorio para la entidad CsrfToken
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project zenwk-verification
 * @class CsrfTokenRepository
 */
public interface CsrfTokenRepository extends JpaRepository<CsrfToken, Long> {
  /**
   * 
   * <p>
   * <b> CU00X_Gestionar protección contra ataques CSRF </b> Buscar un token por email.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param email
   * @return
   */
  public Optional<CsrfToken> findByEmail(String email);

  /**
   * <p>
   * <b> CU00X_Gestionar protección contra ataques CSRF </b> Buscar un token por email y código.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param email
   * @return
   */
  public Optional<CsrfToken> findByEmailAndCode(String email, String code);


}
