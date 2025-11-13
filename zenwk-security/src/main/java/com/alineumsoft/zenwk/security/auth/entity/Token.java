package com.alineumsoft.zenwk.security.auth.entity;

import java.time.LocalDateTime;
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
 * Tabla para alamcenar los tokens de verificación.
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project verification-zenwk
 * @class Token
 */
@Entity
@Data
@Table(name = "sec_token")
@NoArgsConstructor
public class Token {
  /**
   * Id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sectokid")
  private Long id;
  /**
   * Código
   */
  @Column(name = "sectokcode")
  private String code;
  /**
   * UUID
   */
  @Column(name = "sectokuuid")
  private String uuid;
  /**
   * Email
   */
  @Column(name = "sectokemail")
  private String email;
  /**
   * Fecha expiración token
   */
  @Column(name = "sectoexpirationdate")
  private LocalDateTime expirationDate;
  /**
   * Usuario de creación
   */
  @Column(name = "sectokcreateuser")
  private String createUser;
  /**
   * Fecha de creación
   */
  @Column(name = "sectokcreationdate")
  private LocalDateTime creationDate;

  /**
   * <p>
   * <b> CU003_Gestionar token de verificación. </b> Constructor.
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param code
   * @param email
   * @param username
   * @param uuid
   */
  public Token(String code, String email, String username, String uuid) {
    this.code = code;
    this.email = email;
    this.createUser = username;
    this.creationDate = LocalDateTime.now();
    this.uuid = uuid;
  }
}
