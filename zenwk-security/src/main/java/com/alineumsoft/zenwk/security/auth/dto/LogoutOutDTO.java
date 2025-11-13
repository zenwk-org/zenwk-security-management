package com.alineumsoft.zenwk.security.auth.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * DTO de salidad para logout
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class AuthRequestDTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogoutOutDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * message
   */
  private String message;
}
