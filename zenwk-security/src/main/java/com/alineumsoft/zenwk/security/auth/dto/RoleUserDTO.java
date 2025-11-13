package com.alineumsoft.zenwk.security.auth.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * DTO para la entidad RoleUser
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class RolUserDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleUserDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * idRole
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Long idRole;

  /**
   * nameRole
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String nameRole;
  /**
   * idUser
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Long idUser;
  /**
   * username
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String username;
}
