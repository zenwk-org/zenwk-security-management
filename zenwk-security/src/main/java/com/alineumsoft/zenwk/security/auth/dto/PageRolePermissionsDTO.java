package com.alineumsoft.zenwk.security.auth.dto;

import java.io.Serializable;
import java.util.List;
import com.alineumsoft.zenwk.security.dto.PaginatorDTO;
import com.alineumsoft.zenwk.security.dto.PermissionDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * DTO para listar todos los permisos asociados a un rol
 * <p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class PageRolePermissions
 */
@JsonPropertyOrder({"roleId", "rolName", "permissions"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
@Setter
public class PageRolePermissionsDTO extends PaginatorDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * roleId
   */
  private Long roleId;
  /**
   * rolName
   */
  private String rolName;
  /**
   * permissions
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<PermissionDTO> permissions;

  /**
   * <p>
   * Constructor
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param roleId
   * @param rolName
   * @param permissions
   * @param dto
   */
  public PageRolePermissionsDTO(Long roleId, String rolName, List<PermissionDTO> permissions,
      PaginatorDTO dto) {
    super(dto.getTotalElements(), dto.getTotalPages(), dto.getCurrentPage());
    this.roleId = roleId;
    this.rolName = rolName;
    this.permissions = permissions;
  }

}
