package com.alineumsoft.zenwk.security.common.helper;

import java.math.BigDecimal;
import java.util.Map;
import com.alineumsoft.zenwk.security.common.component.RequestTimingFilter;
import com.alineumsoft.zenwk.security.common.constants.CommonMessageConstants;
import com.alineumsoft.zenwk.security.common.constants.GeneralConstants;
import com.alineumsoft.zenwk.security.common.exception.handler.GlobalHandlerException;
import com.alineumsoft.zenwk.security.constants.ServiceControllerConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class ServiceCommons
 */
@Slf4j
public class ApiRestHelper {
  /**
   * <p>
   * <b> General </b> Escribe el log de la peticion cuando existe un request body
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param inDTO
   * @param request
   * @throws JsonProcessingException
   */
  public void logRequest(Object inDTO, HttpServletRequest request) throws JsonProcessingException {
    writeLogRequest(request);
    log.info(getJson(inDTO));
  }

  /**
   * <p>
   * <b>General</b> Obtener Json, oculta datos sensibles con el password
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param inDTO
   * @param objectMapper
   * @return
   * @throws JsonProcessingException
   */
  public String getJson(Object inDTO) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      String json = objectMapper.writeValueAsString(inDTO);

      if (json != null) {
        Map<String, Object> jsonMap =
            objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        if (jsonMap.containsKey(GeneralConstants.FIELD_PASSWORD)) {
          jsonMap.put(GeneralConstants.FIELD_PASSWORD, GeneralConstants.VALUE_SENSITY_MASK);

        }
        json = objectMapper.writeValueAsString(jsonMap);
      }
      return json;

    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * <p>
   * <b> General </b> Escribe el log de la peticion cuando NO existe un request body
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param modUserInDTO
   * @param request
   * @throws JsonProcessingException
   */
  public static void logRequest(HttpServletRequest request) throws JsonProcessingException {
    writeLogRequest(request);
    log.info(CommonMessageConstants.NOT_APPLICABLE_BODY);
  }

  /**
   * <p>
   * <b> Performance </b> this.logRequest()
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   */
  private static void writeLogRequest(HttpServletRequest request) {
    log.info(request.getMethod());
    log.info(request.getRequestURL().toString());
  }

  /**
   * <p>
   * <b> General </b> maneja de acuerdo a una runtimeException que que excpecion custom corresponde:
   * tecnica o funcional
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param <T>
   * @param e
   * @param repository
   * @param logSecUser
   */
  public boolean isFunctionalException(RuntimeException e) {
    String code = GlobalHandlerException.extractCode(e.getMessage());
    if (code != null && code.contains(CommonMessageConstants.FUNCTIONAL_EXCEPTION_PREFIX)) {
      return true;
    }
    return false;
  }

  /**
   * <p>
   * <b> General </b> Encabezado HTTP estandar no oficial utilizado para identificar la dirección IP
   * del cliente original que realiza una solicitud a traves de un servidor proxy o balanceador de
   * carga.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @return
   */
  public String getClientIp(HttpServletRequest request) {
    if (request == null) {
      return GeneralConstants.AUTO_GENERATED_EVENT;
    }
    String ipAddress = request.getHeader(ServiceControllerConstants.HEADER_X_FORWARDED_FOR);
    if (ipAddress == null || ipAddress.isEmpty()
        || ServiceControllerConstants.IP_UNKNOWN.equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getRemoteAddr();
    }
    return ipAddress;
  }

  /**
   * <p>
   * <b> General </b> Encabezado HTTP para identificar informacion sobre el agente que envia la
   * solicitud
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @return
   */
  public String getUserAgent(HttpServletRequest request) {
    return request != null ? request.getHeader(ServiceControllerConstants.HEADER_USER_AGENT)
        : GeneralConstants.AUTO_GENERATED_EVENT;
  }

  /**
   * <p>
   * <b> General </b> Tiempo de duración de la solicitud en segundos formateado a dos decimales
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  public String getExecutionTime() {
    Long startTime = RequestTimingFilter.getStartTime();
    if (startTime == null) {
      return GeneralConstants.AUTO_GENERATED_EVENT;
    }
    long timeMillis = System.currentTimeMillis() - startTime;
    BigDecimal timeSeconds = BigDecimal.valueOf(timeMillis).divide(BigDecimal.valueOf(1000.00));
    RequestTimingFilter.destroyStartTime();
    return timeSeconds.toString().concat(GeneralConstants.TIMER_SEG);
  }
}
