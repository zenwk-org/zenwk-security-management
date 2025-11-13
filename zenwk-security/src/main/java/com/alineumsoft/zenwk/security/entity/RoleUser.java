package com.alineumsoft.zenwk.security.entity;

import java.time.LocalDateTime;
import com.alineumsoft.zenwk.security.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * Entidad que realciona las tablas sec_role y sec_user
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class RolUser
 */
@Entity
@Table(name = "sec_role_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleUser {
  /**
   * Pk
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "secroluseid")
  private Long id;
  /**
   * Referencia la pk con sec_user
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "secroluseiduser")
  private User user;
  /**
   * Referencia la pk con role
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "secroluseidrole")
  private Role role;
  /**
   * Usuario de creacion
   */
  @Column(name = "secrolusecreationuser")
  private String creationUser;
  /**
   * Fecha de creacion del registro
   */
  @Column(name = "secrolusecreationdate")
  private LocalDateTime creationDate;
}
