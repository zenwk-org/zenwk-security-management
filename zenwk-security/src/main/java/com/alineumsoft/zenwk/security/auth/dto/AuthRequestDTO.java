package com.alineumsoft.zenwk.security.auth.dto;

import java.io.Serializable;
import com.alineumsoft.zenwk.security.constants.DtoValidationKeys;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * DTO para el request requerido para la autenticación en el sistema
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class AuthRequestDTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * username
   */
  @NotNull(message = DtoValidationKeys.USER_USERNAME_NOT_NULL)
  private String username;

  /**
   * password
   */
  @NotNull(message = DtoValidationKeys.USER_PASSWORD_NOT_NULL)
  private String password;

}
