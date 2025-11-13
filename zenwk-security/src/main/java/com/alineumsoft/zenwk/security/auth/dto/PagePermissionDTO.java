package com.alineumsoft.zenwk.security.auth.dto;

import java.io.Serializable;
import java.util.List;
import com.alineumsoft.zenwk.security.dto.PaginatorDTO;
import com.alineumsoft.zenwk.security.dto.PermissionDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * DTO para listar los permisos
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class PagePemissions
 */
@JsonPropertyOrder({"permissions"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class PagePermissionDTO extends PaginatorDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * Lista de permisos
   */
  private List<PermissionDTO> permissions;

  /**
   * <p>
   * Constructor
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param users
   * @param dto
   */
  public PagePermissionDTO(List<PermissionDTO> permissions, PaginatorDTO dto) {
    super(dto.getTotalElements(), dto.getTotalPages(), dto.getCurrentPage());
    this.permissions = permissions;
  }

}
