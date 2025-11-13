package com.alineumsoft.zenwk.security.common.enums;

/**
 * <p>
 * Representa el check del campo secperoperation de la tabla sec_permission
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class PermissionOperationEnum
 */
public enum PermissionOperationEnum {
  CREATE, UPDATE, DELETE, GET, LIST,
  /**
   * CSRF
   */
  HEAD, OPTIONS, TRACE;
}
