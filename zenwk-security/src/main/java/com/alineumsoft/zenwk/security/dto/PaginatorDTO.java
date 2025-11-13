package com.alineumsoft.zenwk.security.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * DTO usado para la paginación
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class PaginatorDTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginatorDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * totalElements
   */

  private long totalElements;
  /**
   * totalPages
   */

  private int totalPages;
  /**
   * currentPage
   */

  private int currentPage;
}
