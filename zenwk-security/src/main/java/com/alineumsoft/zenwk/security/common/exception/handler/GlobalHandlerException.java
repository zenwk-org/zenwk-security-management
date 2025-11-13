package com.alineumsoft.zenwk.security.common.exception.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.alineumsoft.zenwk.security.common.constants.CommonMessageConstants;
import com.alineumsoft.zenwk.security.common.constants.GeneralConstants;
import com.alineumsoft.zenwk.security.common.constants.RegexConstants;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.common.exception.TechnicalException;
import com.alineumsoft.zenwk.security.common.exception.dto.ErrorResponseDTO;
import com.alineumsoft.zenwk.security.common.exception.enums.CoreExceptionEnum;
import com.alineumsoft.zenwk.security.common.message.component.MessageSourceAccessorComponent;
import com.alineumsoft.zenwk.security.common.util.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class GlobalHandlerException
 */
@RestControllerAdvice
@Slf4j
public class GlobalHandlerException {
  /**
   * Error geneal
   */
  private static final CoreExceptionEnum generalError = CoreExceptionEnum.FUNC_COMMON_ERROR_GENERAL;

  /**
   * /**
   * <p>
   * <b> CommonException: </b> Manejador general
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param e
   * @return
   */
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponseDTO> HandleGeneralException(RuntimeException e) {
    log.error(CommonMessageConstants.LOG_MSG_EXCEPTION, e.getMessage());
    String code = extractCode(e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createNewError(e, code));
  }

  /**
   * <p>
   * <b> CommonException: </b> Manejador para exceptiones tecnicas
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param e
   * @return
   */
  @ExceptionHandler(TechnicalException.class)
  public ResponseEntity<ErrorResponseDTO> HandleTechnicalException(TechnicalException e) {
    log.error(CommonMessageConstants.LOG_MSG_EXCEPTION_TECHNICAL, e.getMessage());
    String code = extractCode(e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createNewError(e, code));
  }

  /**
   * <p>
   * <b> CommonException: </b> Manejador para exceptiones funcionales
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param e
   * @return
   */
  @ExceptionHandler(FunctionalException.class)
  public ResponseEntity<ErrorResponseDTO> HandleFunctionalException(FunctionalException e) {
    log.error(CommonMessageConstants.LOG_MSG_EXCEPTION_FUNCTIONAL, e.getMessage());
    String code = extractCode(e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createNewError(e, code));
  }

  /**
   * <p>
   * <b> Validate: </b> Recupera el mensaje de error en la validacion encontrado en el
   * dto/rquestBody de la solicitud
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param e
   * @return
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<List<ErrorResponseDTO>> handleValidationException(
      MethodArgumentNotValidException e) {
    List<ErrorResponseDTO> listError = e.getBindingResult().getFieldErrors().stream()
        .map(fieldError -> new ErrorResponseDTO(fieldError.getField(),
            MessageSourceAccessorComponent.getMessage(fieldError.getDefaultMessage()), null, null))
        .collect(Collectors.toList());
    return ResponseEntity.badRequest().body(listError);
  }

  /**
   * <p>
   * <b> Util: </b> Crea una nueva instancia para ErrorResponse
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param e
   * @return
   */
  private static ErrorResponseDTO createNewError(RuntimeException e, String code) {
    // Se realiza una ultima comprobacion del codigo de error
    if (code == null) {
      code = extractCode(e.getMessage());
    }

    return new ErrorResponseDTO(null, code == null ? generalError.getCode() : code,
        limpiarCodigoError(e.getMessage()),
        LocalDateTimeUtil.getLocalDateTimeIso86018(LocalDateTime.now()));
  }

  /**
   * <p>
   * <b> Util: </b> Obtiene codigo de la excepcion si existe
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param formattedString
   * @return
   */
  public static String extractCode(String formattedString) {
    int start = formattedString.indexOf(GeneralConstants.LEFT_BRACKET);
    int end = formattedString.indexOf(GeneralConstants.RIGHT_BRACKET);

    if (start < 0 || end < 0 || start >= end) {
      return null;
    }

    String code = formattedString.substring(start + 1, end);

    return (code.startsWith(CommonMessageConstants.FUNCTIONAL_EXCEPTION_PREFIX)
        || code.startsWith(CommonMessageConstants.TECHNICAL_EXCEPTION_PREFIX)) ? code : null;
  }


  /**
   * <p>
   * <b> Util </b> Limpia el el codigo del mensaje
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param mensaje
   * @return
   */
  public static String limpiarCodigoError(String mensaje) {
    return mensaje.replaceFirst(RegexConstants.REGEX_ALL_BRACKETED_BLOCKS, "");
  }

}
