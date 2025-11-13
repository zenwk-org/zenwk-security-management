package com.alineumsoft.zenwk.security.dto;

import java.io.Serializable;
import com.alineumsoft.zenwk.security.common.constants.RegexConstants;
import com.alineumsoft.zenwk.security.constants.DtoValidationKeys;
import com.alineumsoft.zenwk.security.entity.Permission;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * DTO para el manejo de los permisos en la autorizacion de las api´s del modulo
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class PermissionDTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Long id;
  /**
   * Nombre del rol
   */
  @Pattern(regexp = RegexConstants.NAME_ROLE_PERMISSION,
      message = DtoValidationKeys.PERMISSION_NAME_INVALID)
  @NotNull(message = DtoValidationKeys.PERMISSION_NAME_NOT_NULL)
  @Size(max = 100, message = DtoValidationKeys.PERMISSION_NAME_MAX_LENGTH)
  private String name;
  /**
   * Metodo http
   */
  @NotNull(message = DtoValidationKeys.PERMISSION_DESCRIPTION_NOT_NULL)
  @Size(max = 255, message = DtoValidationKeys.PERMISSION_DESCRIPTION_MAX_LENGTH)
  private String description;

  /**
   * Metodo http
   */
  @Pattern(regexp = RegexConstants.NAME_ROLE_PERMISSION,
      message = DtoValidationKeys.PERMISSION_METHOD_INVALID)
  @NotNull(message = DtoValidationKeys.PERMISSION_METHOD_NOT_NULL)
  @Size(max = 20, message = DtoValidationKeys.PERMISSION_METHOD_MAX_LENGTH)
  private String method;
  /**
   * Recurso
   */
  @Pattern(regexp = RegexConstants.RESOURCE_PERMISSION,
      message = DtoValidationKeys.PERMISSION_RESOURCE_INVALID)
  @NotNull(message = DtoValidationKeys.PERMISSION_RESOURCE_NOT_NULL)
  @Size(max = 200, message = DtoValidationKeys.PERMISSION_RESOURCE_MAX_LENGTH)
  private String resource;
  /**
   * Operacion
   */
  @Pattern(regexp = RegexConstants.OPERATION_PERMISSION,
      message = DtoValidationKeys.PERMISSION_OPERATION_INVALID)
  @NotNull(message = DtoValidationKeys.PERMISSION_OPERATION_NOT_NULL)
  @Size(max = 50, message = DtoValidationKeys.PERMISSION_OPERATION_MAX_LENGTH)
  private String operation;

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Constructor.
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param name
   * @param method
   * @param resource
   */
  public PermissionDTO(String name, String method, String resource) {
    this.name = name;
    this.method = method;
    this.resource = resource;
  }

  /**
   * 
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Constructor a partir del entity
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param role
   */
  public PermissionDTO(Permission permission) {
    this.id = permission.getId();
    this.name = permission.getName();
    this.description = permission.getDescription();
    this.method = permission.getMethod();
    this.resource = permission.getResource();
    this.operation = permission.getOperation().name().toString();
  }

}
