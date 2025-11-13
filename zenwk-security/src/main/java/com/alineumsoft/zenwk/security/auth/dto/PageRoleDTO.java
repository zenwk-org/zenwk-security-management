package com.alineumsoft.zenwk.security.auth.dto;

import java.io.Serializable;
import java.util.List;
import com.alineumsoft.zenwk.security.dto.PaginatorDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.NoArgsConstructor;

/**
 * <p>
 * DTO para listar usado en el api de roles
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class PageRoleDTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"roles"})
@NoArgsConstructor
public class PageRoleDTO extends PaginatorDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * roles
   */
  private List<RoleDTO> roles;

  /**
   * <p>
   * Constructor
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param roles
   * @param dto
   */
  public PageRoleDTO(List<RoleDTO> roles, PaginatorDTO dto) {
    super(dto.getTotalElements(), dto.getTotalPages(), dto.getCurrentPage());
    this.roles = roles;
  }

  /**
   * Gets the value of roles.
   * 
   * @return the value of roles.
   */
  public List<RoleDTO> getRoles() {
    return roles;
  }

  /**
   * Sets the value of roles.
   * 
   * @param roles the new value of roles.
   */
  public void setRoles(List<RoleDTO> roles) {
    this.roles = roles;
  }

}
