package com.alineumsoft.zenwk.security.entity;

import java.time.LocalDateTime;
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
 * Tabla producto de la relacion n:n entre sec_role y sec_permission
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class RolePermission
 */
@Table(name = "sec_role_permission")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RolePermission {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "secrolperid")
  private Long id;

  /**
   * Id que relaciona con la tabla sec_role
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "secrolperidrole")
  private Role role;
  /**
   * Id que referencia la tabla sec_permission
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "secrolperidpermission")
  private Permission permission;

  /**
   * Fecha de creacion
   */
  @Column(name = "secrolpercreationdate")
  private LocalDateTime creationDate;

  /**
   * Usario de creacion
   */
  @Column(name = "secrolpercreationuser")
  private String creationUser;

}
