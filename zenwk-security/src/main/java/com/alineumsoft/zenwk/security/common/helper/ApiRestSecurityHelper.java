package com.alineumsoft.zenwk.security.common.helper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants;
import com.alineumsoft.zenwk.security.common.constants.CommonMessageConstants;
import com.alineumsoft.zenwk.security.common.constants.GeneralConstants;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.enums.HttpMethodResourceEnum;
import com.alineumsoft.zenwk.security.enums.SecurityActionEnum;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class ApiRestSecurityHelper
 */
@Slf4j
public class ApiRestSecurityHelper extends ApiRestHelper {
  protected static final String ID = "id";
  protected static String notBody = CommonMessageConstants.NOT_APPLICABLE_BODY;

  /**
   * <p>
   * <b> General </b> Fija el valor para los atributos: creationDate, userCreation y url
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param response
   * @param request
   * @param principal
   * @param serviceName
   * @return
   */
  public LogSecurity initializeLog(HttpServletRequest httpRequest, String userName, String request,
      String response, String serviceName) {
    Optional<String> urlOptional =
        Optional.ofNullable(httpRequest).map(req -> req.getRequestURL().toString());
    Optional<String> methodOptional =
        Optional.ofNullable(httpRequest).map(HttpServletRequest::getMethod);
    LogSecurity regLog = new LogSecurity();
    regLog.setCreationDate(LocalDateTime.now());
    regLog.setUserCreation(null);
    regLog.setUrl(urlOptional.orElse(GeneralConstants.AUTO_GENERATED_EVENT));
    regLog.setUserCreation(userName);
    regLog.setMethod(methodOptional.orElse(GeneralConstants.AUTO_GENERATED_EVENT));
    regLog.setRequest(request);
    regLog.setResponse(response);
    regLog.setIpAddress(getClientIp(httpRequest));
    regLog.setUserAgent(getUserAgent(httpRequest));
    regLog.setServiceName(serviceName);
    return regLog;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Actualizacion generica en caso de error
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param e
   * @param logSecUser
   */
  public void setLogSecurityError(RuntimeException e, LogSecurity logSecUser) {
    log.error(CommonMessageConstants.LOG_MSG_EXCEPTION, e);
    logSecUser.setErrorMessage(e.getMessage());
    logSecUser.setExecutionTime(getExecutionTime());
    if (e instanceof FunctionalException || e instanceof EntityNotFoundException) {
      logSecUser.setStatusCode(HttpStatus.NOT_FOUND.value());
    } else {
      logSecUser.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Actualizacion generica en caso de exito
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param e
   * @param logSecUser
   */
  public void setLogSecuritySuccesfull(int httpStatusCode, LogSecurity logSecUser) {
    log.info(CommonMessageConstants.REQUEST_SUCCESSFUL);
    logSecUser.setStatusCode(httpStatusCode);
    logSecUser.setErrorMessage(CommonMessageConstants.REQUEST_SUCCESSFUL);
    // Calculo tiempo de la solicitud
    if (GeneralConstants.AUTO_GENERATED_EVENT.equals(logSecUser.getIpAddress())) {
      logSecUser.setExecutionTime(GeneralConstants.AUTO_GENERATED_EVENT);
    } else {
      logSecUser.setExecutionTime(getExecutionTime());
    }
  }

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Obtiene el nombre el servicio (SecurityActionEnum) a
   * desde del HttpServletRequest, si no lo cuengra retorna NOT_FOUND
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @return
   */
  public String getSecurityActionCodeFromRequest(HttpServletRequest request) {
    HttpMethod method = HttpMethod.valueOf(request.getMethod());
    String uri = request.getRequestURI();
    String param = Optional.ofNullable(request.getParameter(AuthConfigConstants.ID)).orElse("");
    for (HttpMethodResourceEnum resourceEnum : HttpMethodResourceEnum.values()) {
      if (resourceEnum.getMethod().equals(method)
          && uri.matches(resourceEnum.getResource().replace(AuthConfigConstants.ID, param))) {
        String actionName = resourceEnum.name();
        SecurityActionEnum securityActionEnum = SecurityActionEnum.valueOf(actionName);
        return securityActionEnum.getCode();
      }
    }
    return CommonMessageConstants.NOT_FOUND;
  }

  /**
   * <p>
   * <b> Util </b> Persistencia de log para una solicitud exitosa.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param httpEstatus
   * @param logSec
   * @param logSecurityRepo
   */
  public void saveSuccessLog(int httpEstatus, LogSecurity logSec,
      LogSecurityRepository logSecurityRepo) {
    setLogSecuritySuccesfull(httpEstatus, logSec);
    logSecurityRepo.save(logSec);
  }

  /**
   * <p>
   * <b> General: Paginación. </b> Obtiene el numero de pagina.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pageable
   * @return
   */
  public int getPageNumber(Pageable pageable) {
    return pageable.getPageNumber() > 0 ? pageable.getPageNumber() - 1 : 0;
  }

  /**
   * 
   * <p>
   * <b> General </b> Sanitizar valore ingresados y enviados por el usuario (bug sonar).
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param value
   * @return
   */
  public String sanitize(String value) {
    return value == null ? null : value.replaceAll("[\n\r|\r|\t]", "");
  }

  /**
   * 
   * <p>
   * <b> General </b> Sanitizar userDetails.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param details
   * @return
   */
  public UserDetails sanitizeUserDetails(UserDetails userDetails) {
    if (userDetails == null) {
      return null;
    }

    String safeUsername = sanitize(userDetails.getUsername());

    Collection<? extends GrantedAuthority> safeAuthorities = userDetails.getAuthorities().stream()
        .map(a -> (GrantedAuthority) () -> sanitize(a.getAuthority())).toList();

    return new UserDetails() {
      private static final long serialVersionUID = 1L;

      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return safeAuthorities;
      }

      @Override
      public String getPassword() {
        return null;
      }

      @Override
      public String getUsername() {
        return safeUsername;
      }

      @Override
      public boolean isAccountNonExpired() {
        return userDetails.isAccountNonExpired();
      }

      @Override
      public boolean isAccountNonLocked() {
        return userDetails.isAccountNonLocked();
      }

      @Override
      public boolean isCredentialsNonExpired() {
        return userDetails.isCredentialsNonExpired();
      }

      @Override
      public boolean isEnabled() {
        return userDetails.isEnabled();
      }
    };
  }


}
