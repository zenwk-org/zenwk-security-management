package com.alineumsoft.zenwk.security.common.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class ErrorResponse
 */
@Data
@AllArgsConstructor
public class ErrorResponseDTO {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String field;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String code;

  private String error;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String timestamp;
}
