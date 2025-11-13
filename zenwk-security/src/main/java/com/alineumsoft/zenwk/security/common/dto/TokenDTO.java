package com.alineumsoft.zenwk.security.common.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.alineumsoft.zenwk.security.common.constants.RegexConstants;
import com.alineumsoft.zenwk.security.common.validation.ValidUrlOrEmpty;
import com.alineumsoft.zenwk.security.constants.DtoValidationKeys;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * DTO para la gestión de la solicitud de token de verificación.
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project verification-zenwk
 * @class TokenDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * Email (max 254).
   */
  @Pattern(regexp = RegexConstants.EMAIL, message = DtoValidationKeys.USER_EMAIL_INVALID)
  @NotNull(message = DtoValidationKeys.USER_EMAIL_NOT_NULL)
  @Size(max = 254, message = DtoValidationKeys.USER_EMAIL_MAX_LENGTH)
  private String email;
  /**
   * Url enviada desde el front
   */
  @ValidUrlOrEmpty(message = DtoValidationKeys.URL_OR_EMPTY_REGEX)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String pathUrl;
  /**
   * Codigo del token.
   */
  private String code;
  /**
   * UUID asociado al objecto.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String uuid;
  /**
   * Hash del token
   */
  @JsonIgnore
  private String hashCode;
  /**
   * Hash de UUID
   */
  @JsonIgnore
  private String hashUuid;
  /**
   * Fecha expiración token
   */
  @JsonIgnore
  private LocalDateTime expirationDate;

}
