package com.alineumsoft.zenwk.security.entity;

import java.time.LocalDateTime;
import com.alineumsoft.zenwk.security.common.enums.PermissionOperationEnum;
import com.alineumsoft.zenwk.security.dto.PermissionDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sec_permission")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
  /**
   * id (PK)
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "secperidpermission")
  private Long id;

  /**
   * name
   */
  @Column(name = "secpername")
  private String name;

  /**
   * description
   */
  @Column(name = "secperdescription")
  private String description;
  /**
   * resource
   */
  @Column(name = "secperresource")
  private String resource;

  /**
   * method
   */
  @Column(name = "secpermethod")
  private String method;

  /**
   * creationDate
   */
  @Column(name = "secpercreationdate")
  private LocalDateTime creationDate;

  /**
   * modificationDate
   */
  @Column(name = "secpermodificationdate")
  private LocalDateTime modificationDate;

  /**
   * creationUser
   */
  @Column(name = "secpercreationuser")
  private String creationUser;

  /**
   * modificationUser
   */
  @Column(name = "secpermodificationuser")
  private String modificationUser;

  /**
   * operation
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "secperoperation")
  private PermissionOperationEnum operation;

  /**
   * <p>
   * <b> Constructor. </b> Constructor a partir de su DTO
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   */
  public Permission(PermissionDTO dto) {
    this.name = dto.getName().toString();
    this.description = dto.getDescription();
    this.method = dto.getMethod();
    this.resource = dto.getResource();
    this.operation = PermissionOperationEnum.valueOf(dto.getOperation());

  }

}
