package com.alineumsoft.zenwk.security.controller;

import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.alineumsoft.zenwk.security.enums.HttpMethodResourceEnum;
import com.alineumsoft.zenwk.security.user.dto.PageUserDTO;
import com.alineumsoft.zenwk.security.user.dto.UserDTO;
import com.alineumsoft.zenwk.security.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Controlador para el api de usuarios
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class UserController
 */
@RestController
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
  /**
   * Servicio de controlador
   */
  private final UserService userService;

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Creacion usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @param uriCB
   * @param request
   * @return
   */
  @PostMapping
  public ResponseEntity<Void> createUser(@Validated @RequestBody UserDTO dto,
      UriComponentsBuilder uriCB, HttpServletRequest request) {
    Long idUser = userService.createUser(dto, request);
    URI location =
        uriCB.path(HttpMethodResourceEnum.USER_GET.getResource()).buildAndExpand(idUser).toUri();
    return ResponseEntity.created(location).build();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Actualizacion de usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @param dto
   * @return
   */
  @PutMapping("{idUser}")
  public ResponseEntity<Void> updateUser(@PathVariable Long idUser, @RequestBody UserDTO dto,
      HttpServletRequest request, @AuthenticationPrincipal UserDetails userDetails) {
    userService.updateUser(request, idUser, dto, userDetails, null);
    return ResponseEntity.noContent().build();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Eliminacion de usuario de la bd
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @param request
   * @return
   * @throws JsonProcessingException
   */
  @DeleteMapping("/{idUser}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long idUser, HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    userService.deleteUser(idUser, request, userDetails);
    return ResponseEntity.noContent().build();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Busqueda por id
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @param request
   * @param userDetails
   * @return
   */
  @GetMapping("/{idUser}")
  public ResponseEntity<UserDTO> findByIdUser(@PathVariable Long idUser, HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    return ResponseEntity.ok(userService.findByIdUser(idUser, request, userDetails));
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Recupera los datos del usuario actualmente
   * autenticado en el sistema
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @param request
   * @param userDetails
   * @return
   */
  @GetMapping("/me")
  public ResponseEntity<UserDTO> getCurrentUser(HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    return ResponseEntity.ok(userService.getCurrentUser(request, userDetails));
  }


  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Busqueda de todos los usuarios en el sistema en caso
   * de error excepciona
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pageable
   * @param request
   * @return
   */
  @GetMapping
  public ResponseEntity<PageUserDTO> getAllUsers(Pageable pageable, HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    return ResponseEntity.ok(userService.getAllUsers(pageable, request, userDetails));
  }


  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Valida si un email ya esta en uso previo el registro.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param email
   * @param request
   * @param uriCB
   * @return
   */
  @GetMapping("/email/{email}")
  public ResponseEntity<Boolean> findUserByEmail(@PathVariable String email,
      HttpServletRequest request, UriComponentsBuilder uriCB,
      @AuthenticationPrincipal UserDetails userDetails) {
    return ResponseEntity.ok(userService.findByEmail(email, request));
  }
}
