package com.alineumsoft.zenwk.security.auth.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.alineumsoft.zenwk.security.auth.dto.AuthRequestDTO;
import com.alineumsoft.zenwk.security.auth.dto.AuthResponseDTO;
import com.alineumsoft.zenwk.security.auth.dto.ResetPasswordDTO;
import com.alineumsoft.zenwk.security.auth.entity.Token;
import com.alineumsoft.zenwk.security.auth.jwt.JwtProvider;
import com.alineumsoft.zenwk.security.auth.repository.TokenRepository;
import com.alineumsoft.zenwk.security.common.constants.CommonMessageConstants;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.common.exception.TechnicalException;
import com.alineumsoft.zenwk.security.common.helper.ApiRestSecurityHelper;
import com.alineumsoft.zenwk.security.common.util.CryptoUtil;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.enums.SecurityActionEnum;
import com.alineumsoft.zenwk.security.enums.SecurityExceptionEnum;
import com.alineumsoft.zenwk.security.enums.UserStateEnum;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import com.alineumsoft.zenwk.security.user.dto.UserDTO;
import com.alineumsoft.zenwk.security.user.entity.User;
import com.alineumsoft.zenwk.security.user.entity.UserHist;
import com.alineumsoft.zenwk.security.user.repository.UserHistRepository;
import com.alineumsoft.zenwk.security.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Servicio usado para la autenticacion en el sistema
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class AuthService
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService extends ApiRestSecurityHelper {
  /**
   * Repositorio para log persistible de modulo
   */
  private final LogSecurityRepository logSecRepo;

  /**
   * Manejador de autenticacion para spring security
   */
  private final AuthenticationManager authManager;

  /**
   * Manejador de jwt
   */
  private final JwtProvider jwtProvider;

  /**
   * Servicio para la carga de los permisos
   */
  private final PermissionService permissionService;
  /**
   * Servicio para la gestion de user
   */
  private final UserService userService;
  /**
   * Tabla historica de usuarios.
   */
  private final UserHistRepository userHistRepository;
  /**
   * Servicio para gestión del token
   */
  private final TokenRepository tokenRepository;

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Servicio usado para la autenticación en el sistema
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param inDTO
   * @param request
   * @return
   */
  public AuthResponseDTO authenticate(AuthRequestDTO inDTO, HttpServletRequest request) {
    AuthResponseDTO outDTO = new AuthResponseDTO();
    String username = null;
    LogSecurity logSec = initializeLog(request, inDTO.getUsername(), getJson(inDTO),
        CommonMessageConstants.NOT_APPLICABLE_BODY, SecurityActionEnum.AUTH_LOGIN.getCode());

    try {
      Authentication authentication = authManager.authenticate(
          new UsernamePasswordAuthenticationToken(inDTO.getUsername(), inDTO.getPassword()));
      // La constrasea se borra por seguridad cuando la autentiacion es exitosa
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      username = userDetails.getUsername();

      User user = userService.findByUsername(username);
      List<String> roles = permissionService.listAllowedUrlsForUserRole(username);
      // Se genera el token con los permisos
      outDTO.setToken(jwtProvider.generateToken(userDetails, roles, user.getId(), user.getState(),
          user.getEmail()));
      outDTO.setUserId(user.getId());

      setLogSecuritySuccesfull(HttpStatus.OK.value(), logSec);
      logSecRepo.save(logSec);
      return outDTO;
    } catch (RuntimeException e) {
      log.error(CommonMessageConstants.LOG_MSG_EXCEPTION, e);
      setLogSecurityError(e, logSec);
      throw new FunctionalException(getMessageError(e), e.getCause(), logSecRepo, logSec);
    }
  }

  /***
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Inhabilita token
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   */
  public void logout(HttpServletRequest request, UserDetails userDetails) {
    LogSecurity logSec = initializeLog(request, userDetails.getUsername(),
        CommonMessageConstants.NOT_APPLICABLE_BODY, CommonMessageConstants.NOT_APPLICABLE_BODY,
        SecurityActionEnum.AUTH_LOGOUT.getCode());
    try {
      String token = jwtProvider.extractJwtFromCookie(request).orElseThrow();
      jwtProvider.invalidateToken(token);
      setLogSecuritySuccesfull(HttpStatus.OK.value(), logSec);
      logSecRepo.save(logSec);
    } catch (RuntimeException e) {
      log.error(CommonMessageConstants.LOG_MSG_EXCEPTION, e);
      setLogSecurityError(e, logSec);
      throw new TechnicalException(e.getMessage(), e.getCause(), logSecRepo, logSec);
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Recupera el mensaje del error
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param e
   * @return
   */
  private String getMessageError(RuntimeException e) {
    if (e instanceof BadCredentialsException) {
      return SecurityExceptionEnum.FUNC_AUTH_BAD_CREDENTIALS.getCodeMessage();
    }
    return e.getMessage();
  }


  /**
   * <p>
   * <b> CU004_Reestablecer contraseña </b> Reestablece la contraseña
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param dto
   * @param email
   * @return
   */
  public Boolean resetPassword(HttpServletRequest request, ResetPasswordDTO dto, String email) {
    LogSecurity logSecurity = initializeLog(request, email, getJson(dto), Boolean.class.getName(),
        SecurityActionEnum.AUTH_RESET_PASSWORD.getCode());
    try {
      String password = dto.getPassword();
      Token token = getToken(email);

      String messageError = validateToken(email, dto, token);
      // Validacion del token
      if (!messageError.isEmpty()) {
        throw new IllegalArgumentException(messageError);
      }

      // Se crea la nueva contraseña.
      User user = userService.findByEmail(email);
      UserDTO userDTO = new UserDTO(user);
      userDTO.setPassword(password);

      // Si el password coincide con uno anterior excepciona.
      validatePassword(password, user.getId());

      // Se invoca el api que actualiza el usuario
      boolean isUpdateUser = userService.updateUser(request, user.getId(), userDTO, null, null);

      setLogSecuritySuccesfull(HttpStatus.OK.value(), logSecurity);
      logSecRepo.save(logSecurity);

      return isUpdateUser;
    } catch (RuntimeException e) {
      log.error(CommonMessageConstants.LOG_MSG_EXCEPTION, e);
      setLogSecurityError(e, logSecurity);
      throw new TechnicalException(e.getMessage(), e.getCause(), logSecRepo, logSecurity);
    }
  }



  /**
   * 
   * <p>
   * <b> CU004_Reestablecer contraseña. </b> Validacion del token
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param email
   * @param dto
   * @param token
   * @return
   */
  private String validateToken(String email, ResetPasswordDTO dto, Token token) {
    String messageError = "";
    if (!token.getEmail().equals(email)) {
      messageError = SecurityExceptionEnum.FUNC_AUTH_EMAIL_NOT_MATCH.getCodeMessage();
    } else if (!CryptoUtil.matchesPassword(dto.getUuid(), token.getUuid())) {
      messageError = SecurityExceptionEnum.FUNC_AUTH_UUID_NOT_MATCH.getCodeMessage();
    } else if (!CryptoUtil.matchesPassword(dto.getCodeToken(), token.getCode())) {
      messageError = SecurityExceptionEnum.FUNC_AUTH_CODE_NOT_MATCH.getCodeMessage();
    } else if (token.getExpirationDate().isBefore(LocalDateTime.now())) {
      messageError = SecurityExceptionEnum.FUNC_AUTH_TOKEN_EXPIRED.getCodeMessage();
    }
    return messageError;
  }

  /**
   * <p>
   * <b> U003_Gestionar token de verificación. </b> Recupera el token con todos sus datos
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param email
   * @return
   */
  private Token getToken(String email) {
    return tokenRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(
        SecurityExceptionEnum.FUNC_AUTH_TOKEN_NOT_FOUND.getCodeMessage()));
  }

  /**
   * 
   * <p>
   * <b> CU004_Reestablecer contraseña </b> Valida que la nueva contraseña no hayas sido usada
   * recientemente.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param password
   * @param userId
   */
  private void validatePassword(String password, Long userId) {
    // Validar que el password no sea el anterior ingresado
    List<UserHist> listUserHist = userHistRepository.findLast20ByIdUser(userId);
    if (!listUserHist.isEmpty()) {
      // NOSONAR java:S6204
      listUserHist = listUserHist.stream()
          .filter(hist -> CryptoUtil.matchesPassword(password, hist.getPassword())).toList();
      // Si la constraseña se encuentra presente
      if (!listUserHist.isEmpty()) {
        throw new IllegalArgumentException(
            SecurityExceptionEnum.FUNC_ERROR_PASSWORD_REUSE.getCodeMessage());
      }
    }
  }


  /***
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Emite un nuevo jwt para que los nuevos roles sean
   * reconocidos en la sesión front del usuario.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   */
  public AuthResponseDTO refreshJwt(HttpServletRequest request, UserDetails userDetails) {
    String username = userDetails.getUsername();
    LogSecurity logSec = initializeLog(request, username,
        CommonMessageConstants.NOT_APPLICABLE_BODY, CommonMessageConstants.NOT_APPLICABLE_BODY,
        SecurityActionEnum.AUTH_REFRESH_JWT.getCode());


    try {
      AuthResponseDTO outDTO = new AuthResponseDTO();
      String token = jwtProvider.extractJwtFromCookie(request).orElseThrow();
      Long idUser = jwtProvider.extractIdUser(token);
      String email = jwtProvider.extractUserEmail(token);
      UserStateEnum state = jwtProvider.extractUserState(token);
      List<String> roles = permissionService.listAllowedUrlsForUserRole(username);

      // Si invalida token jwt actual cuando se emite uno nuevo.
      jwtProvider.invalidateToken(token);

      outDTO.setToken(jwtProvider.generateToken(userDetails, roles, idUser, state, email));
      outDTO.setUserId(idUser);

      setLogSecuritySuccesfull(HttpStatus.OK.value(), logSec);
      logSecRepo.save(logSec);
      // Se retorna el nuevo jwt
      return outDTO;
    } catch (RuntimeException e) {
      log.error(CommonMessageConstants.LOG_MSG_EXCEPTION, e);
      setLogSecurityError(e, logSec);
      throw new TechnicalException(e.getMessage(), e.getCause(), logSecRepo, logSec);
    }
  }

}
