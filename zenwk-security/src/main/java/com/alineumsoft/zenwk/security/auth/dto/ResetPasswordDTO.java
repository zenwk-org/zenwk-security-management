package com.alineumsoft.zenwk.security.auth.dto;

import java.io.Serializable;
import com.alineumsoft.zenwk.security.common.constants.RegexConstants;
import com.alineumsoft.zenwk.security.constants.DtoValidationKeys;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * password
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Pattern(regexp = RegexConstants.PASSWORD, message = DtoValidationKeys.USER_PASSWORD_INVALID)
  @Size(max = 64, message = DtoValidationKeys.USER_PASSWORD_MAX_LENGTH)
  @NotNull(message = DtoValidationKeys.USER_PASSWORD_NOT_NULL)
  String password;
  /**
   * uuid
   */
  @NotNull(message = DtoValidationKeys.USER_PASSWORD_NOT_NULL)
  String uuid;
  /**
   * codeToken
   */
  @NotNull(message = DtoValidationKeys.USER_PASSWORD_NOT_NULL)
  @NotNull
  String codeToken;

}
