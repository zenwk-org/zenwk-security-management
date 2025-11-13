package com.alineumsoft.zenwk.security.auth.dto;

import java.io.Serializable;
import java.util.List;
import com.alineumsoft.zenwk.security.dto.PaginatorDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * DTO que define la pagina con el resultado de todos los roles asociados a un permiso.
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class RolePermissionDTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"permissionId", "permissionName", "permissionResource", "roles"})
public class PagePermissionRolesDTO extends PaginatorDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * roleId
   */
  private Long permissionId;
  /**
   * rolName
   */
  private String permissionName;
  /**
   * permissionResource
   */
  private String permissionResource;
  /**
   * roles
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<RoleDTO> roles;

  /**
   * <p>
   * Constructor
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param permissionId
   * @param permissionName
   * @param roles
   * @param permissionResource
   * @param dto
   */
  public PagePermissionRolesDTO(Long permissionId, String permissionName, String permissionResource,
      List<RoleDTO> roles, PaginatorDTO dto) {
    super(dto.getTotalElements(), dto.getTotalPages(), dto.getCurrentPage());
    this.permissionId = permissionId;
    this.permissionName = permissionName;
    this.permissionResource = permissionResource;
    this.roles = roles;
  }

}
