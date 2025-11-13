package com.alineumsoft.zenwk.security.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.alineumsoft.zenwk.security.auth.entity.Token;


/**
 * <p>
 * Repositorio de la entidad de token.
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project verification-zenwk
 * @class TokenRepository
 */
public interface TokenRepository extends JpaRepository<Token, Long> {
  /**
   * <p>
   * <b> CU003_Gestionar token de verificación. </b> Buscar un token por email y código.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param email
   * @return
   */
  public Optional<Token> findByEmailAndCode(String email, String code);

  /**
   * 
   * <p>
   * <b> CU003_Gestionar token de verificación. </b> Buscar un token por email.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param email
   * @return
   */
  public Optional<Token> findByEmail(String email);

  /**
   * 
   * <p>
   * <b> CU003_Gestionar token de verificación.</b> Recupera el token por el codigo
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param code
   * @return
   */
  public Optional<Token> findByCode(String code);

  /**
   * 
   * <p>
   * <b> CU003_Gestionar token de verificación.</b> Recupera el token por el codigo UUID
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param uuid
   * @return
   */
  public Optional<Token> findByUuid(String uuid);


}
