package com.alineumsoft.zenwk.security.controller;


import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alineumsoft.zenwk.security.auth.Service.AuthService;
import com.alineumsoft.zenwk.security.auth.definitions.AuthEnums;
import com.alineumsoft.zenwk.security.auth.dto.AuthRequestDTO;
import com.alineumsoft.zenwk.security.auth.dto.AuthResponseDTO;
import com.alineumsoft.zenwk.security.auth.dto.LogoutOutDTO;
import com.alineumsoft.zenwk.security.auth.dto.ResetPasswordDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * Controlador para la autenticación del usuario
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class AuthController
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
// @Validated
public class AuthController {
  /**
   * Servicio para auth
   */
  private final AuthService authService;

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Controller para la autenticación
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param servRequest
   * @param principal
   * @return
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponseDTO> login(@RequestBody @Validated AuthRequestDTO request,
      HttpServletRequest servRequest, Principal principal, HttpServletResponse reponse) {
    AuthResponseDTO authDto = authService.authenticate(request, servRequest, principal);
    // Construcción de la cookie http only para no guardar jwt en sesión o localstorage
    authService.generateCookieHttpOnlyJwt(reponse, authDto.getToken());
    return ResponseEntity.ok(authDto);
  }

  /**
   * 
   * <p>
   * <b> CU002_Seguridad_Cierre_Sesion </b> Controller para cerrar sesión
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param userDetails
   * 
   * @return
   */
  @DeleteMapping("/logout")
  public ResponseEntity<LogoutOutDTO> logout(HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
    authService.logout(request, userDetails);
    authService.disabledCookieHttpOnlyJwt(response);
    authService.disabledCookieHttpOnlyCsrfToken(response);
    return ResponseEntity.ok(new LogoutOutDTO(AuthEnums.AUTH_LOGOUT_SUCCES.getMessage()));
  }

  /**
   * 
   * <p>
   * <b> CU002_Seguridad_Cierre_Sesion </b> Controller publico para restablecer el password
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param resetPasswordDTO
   * @param email
   * @return
   */
  @PostMapping("/reset-password/{email}")
  public ResponseEntity<Boolean> resetPassword(@PathVariable String email,
      @RequestBody @Validated ResetPasswordDTO resetPasswordDTO, HttpServletRequest request) {
    return ResponseEntity.ok(authService.ResetPassword(request, resetPasswordDTO, email));

  }

  /**
   * 
   * <p>
   * <b> CU002_Seguridad_Cierre_Sesion </b> Emite un nuevo jwt para que los nuevos roles sean
   * reconocidos en la sesión front del usuario.
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param userDetails
   * @return
   */
  @PostMapping("/refresh-jwt")
  public ResponseEntity<AuthResponseDTO> refreshJwt(HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
    AuthResponseDTO authDto = authService.refreshJwt(request, userDetails);
    authService.generateCookieHttpOnlyJwt(response, authDto.getToken());
    return ResponseEntity.ok(authDto);
  }



}
