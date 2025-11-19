package com.alineumsoft.zenwk.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetails;
import com.alineumsoft.zenwk.security.auth.dto.PagePermissionDTO;
import com.alineumsoft.zenwk.security.auth.dto.PageRoleDTO;
import com.alineumsoft.zenwk.security.auth.dto.RoleDTO;
import com.alineumsoft.zenwk.security.auth.dto.RoleUserDTO;
import com.alineumsoft.zenwk.security.auth.service.PermissionService;
import com.alineumsoft.zenwk.security.auth.service.RoleAssignmentService;
import com.alineumsoft.zenwk.security.auth.service.RoleService;
import com.alineumsoft.zenwk.security.common.enums.PermissionOperationEnum;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.dto.PaginatorDTO;
import com.alineumsoft.zenwk.security.dto.PermissionDTO;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.entity.Permission;
import com.alineumsoft.zenwk.security.entity.Role;
import com.alineumsoft.zenwk.security.entity.RolePermission;
import com.alineumsoft.zenwk.security.entity.RoleUser;
import com.alineumsoft.zenwk.security.person.repository.RolePermissionRepository;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import com.alineumsoft.zenwk.security.repository.RolUserRepository;
import jakarta.servlet.http.HttpServletRequest;

class RoleAssignmentServiceTest {

  @Mock
  private RoleService roleService;

  @Mock
  private PermissionService permissionService;

  @Mock
  private RolePermissionRepository rolPermissionRepo;

  @Mock
  private RolUserRepository rolUserRepo;

  @Mock
  private LogSecurityRepository logSecRepo;

  @Mock
  private HttpServletRequest request;

  @Mock
  private UserDetails userDetails;

  @InjectMocks
  private RoleAssignmentService roleAssignmentService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    when(userDetails.getUsername()).thenReturn("tester");

    // Mock HttpServletRequest completamente
    when(request.getRequestURI()).thenReturn("/mock");
    when(request.getMethod()).thenReturn("DELETE");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    when(request.getHeader(anyString())).thenReturn("mock-header");

    // MOCK CLAVE → getRequestURL()
    when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/mock"));

    // Log repo devuelve el mismo objeto que recibe
    when(logSecRepo.save(any(LogSecurity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  @DisplayName("Asigna un permiso correctamente a un rol")
  void asignaPermisoCorrectamente() {
    Long roleId = 1L;
    Long permissionId = 2L;

    Role role = new Role();
    role.setId(roleId);
    role.setName("ADMIN");

    Permission permission = new Permission();
    permission.setId(permissionId);
    permission.setName("GET_USER");

    when(roleService.findRoleById(roleId)).thenReturn(role);
    when(permissionService.findPermissionById(permissionId)).thenReturn(permission);
    when(rolPermissionRepo.findByRoleAndPermission(role, permission)).thenReturn(Optional.empty());
    when(rolPermissionRepo.save(any(RolePermission.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    roleAssignmentService.assignPermissionsToRole(roleId, permissionId, request, userDetails);

    verify(rolPermissionRepo, times(1)).save(any(RolePermission.class));
    verify(logSecRepo, times(1)).save(any(LogSecurity.class));
  }

  @Test
  @DisplayName("Lanza excepción si el permiso ya está asignado al rol")
  void lanzaExcepcionSiPermisoYaAsignado() {
    Long roleId = 1L;
    Long permissionId = 2L;

    Role role = new Role();
    role.setId(roleId);

    Permission permission = new Permission();
    permission.setId(permissionId);

    RolePermission existingAssignment =
        new RolePermission(1L, role, permission, LocalDateTime.now(), "tester");

    when(roleService.findRoleById(roleId)).thenReturn(role);
    when(permissionService.findPermissionById(permissionId)).thenReturn(permission);
    when(rolPermissionRepo.findByRoleAndPermission(role, permission))
        .thenReturn(Optional.of(existingAssignment));

    assertThrows(RuntimeException.class, // RoleAssignmentService wraps RuntimeException in
                                         // FunctionalException
        () -> roleAssignmentService.assignPermissionsToRole(roleId, permissionId, request,
            userDetails));

    // No se debe guardar un nuevo RolePermission
    verify(rolPermissionRepo, never()).save(any(RolePermission.class));
    // No verificamos logSecRepo aquí porque la lógica de manejo de excepción puede o no persistir
    // en distintas ramas.
  }

  @Test
  @DisplayName("Lista los permisos correctamente con paginación")
  void listaPermisosPorRol() {
    Long roleId = 1L;
    Pageable pageable = PageRequest.of(0, 10);

    Role role = new Role();
    role.setId(roleId);

    Permission permission = new Permission();
    permission.setId(2L);
    permission.setName("GET_USER");
    permission.setMethod("GET");
    permission.setResource("/users");

    Page<Permission> mockPage = new PageImpl<>(List.of(permission));

    PermissionDTO dto = new PermissionDTO();
    dto.setId(2L);
    dto.setName("GET_USER");
    dto.setMethod("GET");
    dto.setResource("/users");

    PaginatorDTO paginatorDTO = new PaginatorDTO(mockPage.getTotalElements(),
        mockPage.getTotalPages(), mockPage.getNumber());

    PagePermissionDTO dtoResult = new PagePermissionDTO(List.of(dto), paginatorDTO);

    when(roleService.findRoleById(roleId)).thenReturn(role);
    when(permissionService.getPermissionsForRole(pageable, roleId, 0)).thenReturn(mockPage);
    when(permissionService.getPagePermissionDTO(mockPage)).thenReturn(dtoResult);

    var result =
        roleAssignmentService.listPermissionsForRole(pageable, roleId, request, userDetails);

    assertNotNull(result);
    verify(logSecRepo, times(1)).save(any(LogSecurity.class));
  }

  @Test
  @DisplayName("Crea RoleUser y elimina NEW_USER si existe")
  void createRoleUser_eliminaNewUserYguarda() {
    RoleUserDTO dto = new RoleUserDTO();
    dto.setIdUser(10L);
    dto.setNameRole("ADMIN");
    dto.setUsername("tester");

    Role role = new Role();
    role.setId(5L);
    role.setName("ADMIN");

    // Simular que existe un RoleUser NEW_USER que debe eliminarse
    RoleUser existing = new RoleUser();
    existing.setRole(role); // role name mismatch intentionally not NEW_USER (we will also test
                            // branch without delete)
    // Para forzar la eliminación, devolvemos una list con element whose role name equals NEW_USER
    Role newUserRole = new Role();
    newUserRole.setName("NEW_USER");
    RoleUser toDelete = new RoleUser();
    toDelete.setRole(newUserRole);

    when(roleService.findRoleByName("ADMIN")).thenReturn(role);
    // Simular que findByUser devuelve lista que contiene NEW_USER
    when(rolUserRepo.findByUser(any())).thenReturn(List.of(toDelete));
    when(rolUserRepo.save(any(RoleUser.class))).thenAnswer(invocation -> {
      RoleUser ru = invocation.getArgument(0);
      ru.setId(99L);
      return ru;
    });

    var saved = roleAssignmentService.createRoleUser(request, dto);

    assertNotNull(saved);
    assertEquals(99L, saved.getId());
    verify(rolUserRepo, times(1)).delete(any(RoleUser.class)); // deleteRolNewUser debe intentar
                                                               // eliminar
    verify(rolUserRepo, times(1)).save(any(RoleUser.class));
  }

  @Test
  @DisplayName("getRoleNewUser delega correctamente")
  void getRoleNewUser_delega() {
    Role role = new Role();
    role.setId(7L);
    when(roleService.findRoleByName("NEW_USER")).thenReturn(role);

    var roles = roleAssignmentService.getRoleNewUser();

    assertNotNull(roles);
    assertFalse(roles.isEmpty());
    assertEquals(7L, roles.get(0).getId());
  }

  @Test
  @DisplayName("deleteRoleUserByIdUser elimina por user")
  void deleteRoleUserByIdUser() {
    Long idUser = 20L;
    // rolUserRepo.deleteByUser no devuelve nada; solo verificar que no explode
    doNothing().when(rolUserRepo).deleteByUser(any());
    boolean r = roleAssignmentService.deleteRoleUserByIdUser(idUser);
    assertTrue(r);
    verify(rolUserRepo, times(1)).deleteByUser(any());
  }

  @Test
  @DisplayName("findRoleUserByIdUser delega al repo")
  void findRoleUserByIdUser_delega() {
    Long idUser = 20L;
    when(rolUserRepo.findByUser(any())).thenReturn(List.of(new RoleUser()));
    var res = roleAssignmentService.findRoleUserByIdUser(idUser);
    assertNotNull(res);
    verify(rolUserRepo, times(1)).findByUser(any());
  }

  @Test
  @DisplayName("findRoleByIds delega a roleService")
  void findRoleByIds_delega() {
    var ids = List.of(1L, 2L);
    when(roleService.findRoleByIds(ids)).thenReturn(List.of(new Role()));
    var res = roleAssignmentService.findRoleByIds(ids);
    assertNotNull(res);
    verify(roleService, times(1)).findRoleByIds(ids);
  }

  @Test
  @DisplayName("getAssignmentPermissionToRol retorna Optional con valor")
  void getAssignmentPermissionToRol_retorna() {
    Role role = new Role();
    role.setId(1L);
    Permission permission = new Permission();
    permission.setId(2L);

    RolePermission rp = new RolePermission(10L, role, permission, LocalDateTime.now(), "tester");
    when(rolPermissionRepo.findByRoleAndPermission(role, permission)).thenReturn(Optional.of(rp));

    // Llamamos al método público que invoca internamente la búsqueda (assignPermissionsToRole),
    // de forma que se ejecute la rama de lectura del repositorio.
    when(roleService.findRoleById(1L)).thenReturn(role);
    when(permissionService.findPermissionById(2L)).thenReturn(permission);
    when(rolPermissionRepo.findByRoleAndPermission(role, permission)).thenReturn(Optional.of(rp));

    assertThrows(RuntimeException.class,
        () -> roleAssignmentService.assignPermissionsToRole(1L, 2L, request, userDetails));
    verify(rolPermissionRepo, times(1)).findByRoleAndPermission(role, permission);
  }


  @Test
  @DisplayName("Lista roles de un permiso correctamente")
  void listRolesForPermission_correcto() {
    Long permissionId = 5L;
    Pageable pageable = PageRequest.of(0, 10);

    Permission permission = new Permission();
    permission.setName("P1");
    permission.setId(permissionId);
    permission.setMethod(HttpMethod.POST.name());
    permission.setOperation(PermissionOperationEnum.LIST);
    permission.setResource("/api/permissions/{id}");

    Role role = new Role();
    role.setId(1L);
    role.setName("ADMIN");

    Page<Role> mockPage = new PageImpl<>(List.of(role));

    // DTO simulado
    var dto = new PageRoleDTO(List.of(new RoleDTO()), new PaginatorDTO(1, 1, 0));

    when(permissionService.findPermissionById(permissionId)).thenReturn(permission);
    when(roleService.getRolesForPermission(pageable, permissionId, 0)).thenReturn(mockPage);
    when(roleService.getPageRolesDTO(mockPage)).thenReturn(dto);

    var result =
        roleAssignmentService.listRolesForPermission(pageable, permissionId, request, userDetails);

    assertNotNull(result);
    verify(logSecRepo, times(1)).save(any(LogSecurity.class));
    verify(roleService, times(1)).getRolesForPermission(pageable, permissionId, 0);
  }

  @Test
  @DisplayName("removePermissionFromRole elimina asignación si existe")
  void removePermissionFromRole_asignacionPresente() {
    Long roleId = 1L;
    Long permissionId = 2L;

    Role role = new Role();
    role.setId(roleId);
    Permission permission = new Permission();
    permission.setId(permissionId);

    RolePermission rp = new RolePermission(50L, role, permission, LocalDateTime.now(), "tester");

    when(roleService.findRoleById(roleId)).thenReturn(role);
    when(permissionService.findPermissionById(permissionId)).thenReturn(permission);
    when(rolPermissionRepo.findByRoleAndPermission(role, permission)).thenReturn(Optional.of(rp));

    roleAssignmentService.removePermissionFromRole(roleId, permissionId, request, userDetails);

    verify(rolPermissionRepo, times(1)).delete(rp);
    verify(logSecRepo, times(1)).save(any(LogSecurity.class));
  }

  @Test
  @DisplayName("Elimina correctamente un permiso de un rol")
  void removePermissionFromRole_correcto() {
    Long roleId = 1L;
    Long permissionId = 2L;

    Role role = new Role();
    role.setId(roleId);

    Permission permission = new Permission();
    permission.setId(permissionId);
    permission.setName("P_READ"); // necesario para el mensaje de log

    RolePermission existing =
        new RolePermission(10L, role, permission, LocalDateTime.now(), "tester");

    when(roleService.findRoleById(roleId)).thenReturn(role);
    when(permissionService.findPermissionById(permissionId)).thenReturn(permission);
    when(rolPermissionRepo.findByRoleAndPermission(role, permission))
        .thenReturn(Optional.of(existing));

    roleAssignmentService.removePermissionFromRole(roleId, permissionId, request, userDetails);

    verify(rolPermissionRepo, times(1)).delete(existing);
    verify(logSecRepo, times(1)).save(any(LogSecurity.class)); // saveSuccessLog()
  }

  @Test
  @DisplayName("Lanza FunctionalException si el permiso NO está asignado al rol")
  void removePermissionFromRole_sinAsignacion() {
    Long roleId = 1L;
    Long permissionId = 2L;

    Role role = new Role();
    role.setId(roleId);
    role.setName("ADMIN");

    Permission permission = new Permission();
    permission.setId(permissionId);
    permission.setName("P_READ");

    when(roleService.findRoleById(roleId)).thenReturn(role);
    when(permissionService.findPermissionById(permissionId)).thenReturn(permission);
    when(rolPermissionRepo.findByRoleAndPermission(role, permission)).thenReturn(Optional.empty());

    assertThrows(FunctionalException.class, () -> roleAssignmentService
        .removePermissionFromRole(roleId, permissionId, request, userDetails));

    // No debe intentar borrar
    verify(rolPermissionRepo, never()).delete(any());
  }
}
