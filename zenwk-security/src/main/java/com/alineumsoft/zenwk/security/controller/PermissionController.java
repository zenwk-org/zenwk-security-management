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
import com.alineumsoft.zenwk.security.auth.Service.PermissionService;
import com.alineumsoft.zenwk.security.auth.dto.PagePermissionDTO;
import com.alineumsoft.zenwk.security.dto.PermissionDTO;
import com.alineumsoft.zenwk.security.enums.HttpMethodResourceEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * Controlador para gestion de los permisos en el sistema
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class PermissionController
 */
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Validated
public class PermissionController {
  /**
   * PermissionService
   */
  private final PermissionService permissionService;

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Api para la creación del permiso.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @param request
   * @param userDetails
   * @param uriCB
   * @return
   */
  @PostMapping
  public ResponseEntity<Void> createPermission(@Validated @RequestBody PermissionDTO dto,
      HttpServletRequest request, @AuthenticationPrincipal UserDetails userDetails,
      UriComponentsBuilder uriCB) {
    Long idPermission = permissionService.createPemission(dto, request, userDetails);
    URI location = uriCB.path(HttpMethodResourceEnum.PERMISSION_GET.getResource())
        .buildAndExpand(idPermission).toUri();
    return ResponseEntity.created(location).build();
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Api para la actualizacion del permiso
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param id
   * @param dto
   * @param request
   * @param userDetails
   * @return
   */
  @PutMapping("/{id}")
  public ResponseEntity<Void> updatePermission(@PathVariable Long id,
      @Validated @RequestBody PermissionDTO dto, HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    permissionService.updatePermission(id, dto, request, userDetails);
    return ResponseEntity.noContent().build();

  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Api para la busquedad de un permiso por su id
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param id
   * @param request
   * @param userDetails
   * @return
   */
  @GetMapping("/{id}")
  public ResponseEntity<PermissionDTO> findByIdPermission(@PathVariable Long id,
      HttpServletRequest request, @AuthenticationPrincipal UserDetails userDetails) {
    PermissionDTO dto = permissionService.findByIdPermission(id, request, userDetails);
    return ResponseEntity.ok(dto);
  }

  /**
   * <b> CU002_Seguridad_Asignación_Roles </b> Busqueda de todos lo permisos almacenados en el
   * sistema
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pageable
   * @param request
   * @param userDetails
   * @return
   */
  @GetMapping
  public ResponseEntity<PagePermissionDTO> getAllPermissions(Pageable pageable,
      HttpServletRequest request, @AuthenticationPrincipal UserDetails userDetails) {
    PagePermissionDTO pagePermissionDTO =
        permissionService.getAllPermissions(pageable, request, userDetails);
    return ResponseEntity.ok(pagePermissionDTO);

  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Api para al eliminacion de un permiso
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param id
   * @param request
   * @param userDetails
   * @return
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePermission(@PathVariable Long id, HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    permissionService.deletePermission(id, request, userDetails);
    return ResponseEntity.noContent().build();
  }
}
