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
import com.alineumsoft.zenwk.security.auth.Service.RoleAssignmentService;
import com.alineumsoft.zenwk.security.auth.Service.RoleService;
import com.alineumsoft.zenwk.security.auth.dto.PagePermissionRolesDTO;
import com.alineumsoft.zenwk.security.auth.dto.PageRoleDTO;
import com.alineumsoft.zenwk.security.auth.dto.PageRolePermissionsDTO;
import com.alineumsoft.zenwk.security.auth.dto.RoleDTO;
import com.alineumsoft.zenwk.security.enums.HttpMethodResourceEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * API para la gestión de roles en el sistema.
 * </p>
 *
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class RoleController
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Validated
public class RoleController {
  /**
   * Servicio para gestionar el role
   */
  private final RoleService roleService;
  /**
   * Servicio para la gestion del asignamiento / desasignamiento de roles
   */
  private final RoleAssignmentService roleAssignmentService;

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Crea un nuevo rol en el sistema.
   * </p>
   * 
   * @param dto
   * @param request
   * @param uriCB
   * @param userDetails
   * @return
   */
  @PostMapping
  public ResponseEntity<Void> createRole(@Valid @RequestBody RoleDTO dto,
      HttpServletRequest request, UriComponentsBuilder uriCB,
      @AuthenticationPrincipal UserDetails userDetails) {
    Long idRole = roleService.createRole(dto, request, userDetails);
    URI location =
        uriCB.path(HttpMethodResourceEnum.ROLE_GET.getResource()).buildAndExpand(idRole).toUri();
    return ResponseEntity.created(location).build();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Actualiza un rol existente.
   * </p>
   *
   * @param roleId
   * @param dto
   * @param request
   * @param userDetails
   * @return
   */
  @PutMapping("/{roleId}")
  public ResponseEntity<Void> updateRole(@PathVariable Long roleId, @Valid @RequestBody RoleDTO dto,
      HttpServletRequest request, @AuthenticationPrincipal UserDetails userDetails) {
    roleService.updateRole(roleId, dto, request, userDetails);
    return ResponseEntity.noContent().build();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b>Elimina un rol por su ID.
   * </p>
   *
   * @param roleId
   * @param request
   * @param userDetails
   * @return
   */
  @DeleteMapping("/{roleId}")
  public ResponseEntity<Void> deleteRole(@PathVariable Long roleId, HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    roleService.deleteRole(roleId, request, userDetails);
    return ResponseEntity.noContent().build();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Obtiene un rol por su ID.
   * </p>
   *
   * @param roleId
   * @param request
   * @param userDetails
   * @return
   */
  @GetMapping("/{roleId}")
  public ResponseEntity<RoleDTO> findRoleById(@PathVariable Long roleId, HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    RoleDTO dto = roleService.findRoleById(roleId, request, userDetails);
    return ResponseEntity.ok(dto);
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b>Obtiene todos los roles registrados en el sistema.
   * </p>
   *
   * @param pageable
   * @param request
   * @param userDetails
   * @return
   */
  @GetMapping
  public ResponseEntity<PageRoleDTO> findAllRoles(Pageable pageable, HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    PageRoleDTO roles = roleService.findAllRoles(pageable, request, userDetails);
    return ResponseEntity.ok(roles);
  }

  /**
   * <p>
   * <b>CU002_Seguridad_Asignación_Roles</b> - Asigna permisos a un rol.
   * </p>
   * 
   * @param roleId
   * @param dto
   * @param request
   * @param userDetails
   * @return
   */
  @PostMapping("/{roleId}/permissions/{permissionId}")
  public ResponseEntity<Void> assignPermissionsToRole(@PathVariable Long roleId,
      @PathVariable Long permissionId, HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    roleAssignmentService.assignPermissionsToRole(roleId, permissionId, request, userDetails);
    return ResponseEntity.noContent().build();
  }

  /**
   * <p>
   * <b>CU002_Seguridad_Asignación_Roles</b> - Desasigna un permiso de un rol.
   * </p>
   * 
   * @param roleId
   * @param permissionId
   * @param request
   * @param userDetails
   * @return
   */
  @DeleteMapping("/{roleId}/permissions/{permissionId}")
  public ResponseEntity<Void> removePermissionFromRole(@PathVariable Long roleId,
      @PathVariable Long permissionId, HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    roleAssignmentService.removePermissionFromRole(roleId, permissionId, request, userDetails);
    return ResponseEntity.noContent().build();
  }

  /**
   * <p>
   * <b>CU002_Seguridad_Asignación_Roles</b> - Obtiene todos los permisos asociados a un rol.
   * </p>
   * 
   * @param pageable
   * @param roleId
   * @param request
   * @param userDetails
   * @return
   */
  @GetMapping("/{roleId}/permissions")
  public ResponseEntity<PageRolePermissionsDTO> listPermissionsForRole(Pageable pageable,
      @PathVariable Long roleId, HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    PageRolePermissionsDTO pageRolePermissions =
        roleAssignmentService.listPermissionsForRole(pageable, roleId, request, userDetails);
    return ResponseEntity.ok(pageRolePermissions);
  }

  /**
   * <p>
   * <b>CU002_Seguridad_Asignación_Roles</b> - Obtiene todos los roles que tiene asignado un
   * permiso.
   * </p>
   * 
   * @param pageable
   * @param permissionId
   * @param request
   * @param userDetails
   * @return
   */
  @GetMapping("/permissions/{permissionId}")
  public ResponseEntity<PagePermissionRolesDTO> listRolesForPermission(Pageable pageable,
      @PathVariable Long permissionId, HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    PagePermissionRolesDTO pagePermissionRolesDTO =
        roleAssignmentService.listRolesForPermission(pageable, permissionId, request, userDetails);
    return ResponseEntity.ok(pagePermissionRolesDTO);
  }
}
