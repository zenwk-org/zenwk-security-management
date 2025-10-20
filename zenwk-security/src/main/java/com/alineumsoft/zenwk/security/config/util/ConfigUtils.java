package com.alineumsoft.zenwk.security.config.util;

import org.springframework.util.StringUtils;
import com.alineumsoft.zenwk.security.enums.HttpMethodResourceEnum;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project zenwk-security
 * @class ConfigUtils
 */
public final class ConfigUtils {
  /**
   * 
   * <p>
   * <b> Util </b>
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param path
   * @return
   */
  public static String extractBasePath(String path) {
    if (!StringUtils.hasText(path)) {
      return "";
    }

    String cleanPath = path.split("\\?")[0];
    String[] segments = cleanPath.split("/");

    if (segments.length <= 2) {
      return cleanPath.endsWith("/") ? cleanPath : cleanPath + "/";
    }

    String lastSegment = segments[segments.length - 1];

    if (!lastSegment.isBlank()) {
      cleanPath = cleanPath.substring(0, cleanPath.lastIndexOf('/') + 1);
    }

    return cleanPath;
  }

  /**
   * 
   * <p>
   * <b> CU00X_Gestionar protección contra ataques CSRF </b> Define los endpoints que no deben pasar
   * por la validación CSRF. Se agregan los paths del login, refresh o generación de tokens.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param path
   * @return
   */
  public static boolean isPublicEndpoint(String path) {
    String pathResetPassword =
        extractBasePath(HttpMethodResourceEnum.AUTH_RESET_PASSWORD.getResource());

    return path.startsWith(HttpMethodResourceEnum.VERIFICATION_TOKEN.getResource())
        || path.startsWith(HttpMethodResourceEnum.SEX_LIST_OPTIONS.getResource())
        || path.equals(HttpMethodResourceEnum.AUTH_LOGIN.getResource())
        || path.equals(HttpMethodResourceEnum.USER_CREATE.getResource())
        || path.startsWith(HttpMethodResourceEnum.ACTUATOR.getResource())
        || path.startsWith(pathResetPassword);
  }
}
