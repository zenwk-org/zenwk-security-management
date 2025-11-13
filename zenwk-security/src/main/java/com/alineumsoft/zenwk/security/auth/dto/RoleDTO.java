package com.alineumsoft.zenwk.security.auth.dto;

import com.alineumsoft.zenwk.security.common.constants.RegexConstants;
import com.alineumsoft.zenwk.security.constants.DtoValidationKeys;
import com.alineumsoft.zenwk.security.entity.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * DTO usado para la gestion de los roles en el sistema
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class RoleDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
  /**
   * idRol
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Long id;
  /**
   * rolName
   */
  @Pattern(regexp = RegexConstants.NAME_ROLE_PERMISSION,
      message = DtoValidationKeys.ROLE_NAME_INVALID)
  @Size(max = 100, message = DtoValidationKeys.ROLE_NAME_MAX_LENGTH)
  @NotNull(message = DtoValidationKeys.ROLE_NAME_NOT_NULL)
  private String name;
  /**
   * rolDescription
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Size(max = 255, message = DtoValidationKeys.ROLE_DESCRIPTION_MAX_LENGTH)
  @NotNull(message = DtoValidationKeys.ROLE_DECRIPTION_NOT_NULL)
  private String description;

  /**
   * 
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Constructor a partir del entity
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param role
   */
  public RoleDTO(Role role) {
    this.id = role.getId();
    this.name = role.getName();
    this.description = role.getDescription();
  }

}
