package com.alineumsoft.zenwk.security.common.entity;

import java.time.LocalDateTime;
import com.alineumsoft.zenwk.security.common.dto.TokenDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * Tabla que almacena los tokens de verficación para proteger la autentiación de ataques crsf
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project zenwk-verification
 * @class CsrfToken
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "sec_csrf_token")
public class CsrfToken {
  /**
   * id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seccsrtokid")
  private Long id;
  /**
   * Codigo del token
   */
  @Column(name = "seccsrtokcode")
  private String code;
  /**
   * Correo del usuario
   */
  @Column(name = "seccsrtokemail")
  private String email;
  /**
   * Estado revocación
   */
  @Column(name = "seccsrtokrevoked")
  private boolean revoked = false;

  @Column(name = "seccsrtokuseragent")
  private String userAgent;
  /**
   * Fecha de creación
   */
  @Column(name = "seccsrtokcreationdate")
  private LocalDateTime creationDate;
  /**
   * Fecha de expiración
   */
  @Column(name = "sectoexpirationdate")
  private LocalDateTime expirationDate;
  /**
   * Usuario de creación
   */
  @Column(name = "seccsrtokcreateuser")
  private String createUser;

  /**
   * 
   * <p>
   * <b> CU00X_Gestionar protección contra ataques CSRF </b> Constructor
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @param username
   * @param userAgent
   */
  public CsrfToken(TokenDTO dto, String username, String userAgent) {
    this.email = dto.getEmail();
    this.code = dto.getCode();
    this.expirationDate = dto.getExpirationDate();
    this.createUser = username;
    this.creationDate = LocalDateTime.now();
    this.userAgent = userAgent;

  }


}
