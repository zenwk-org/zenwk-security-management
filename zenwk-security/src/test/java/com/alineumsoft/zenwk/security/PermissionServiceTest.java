package com.alineumsoft.zenwk.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import com.alineumsoft.zenwk.security.auth.dto.PagePermissionDTO;
import com.alineumsoft.zenwk.security.auth.service.PermissionService;
import com.alineumsoft.zenwk.security.common.enums.PermissionOperationEnum;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.common.exception.TechnicalException;
import com.alineumsoft.zenwk.security.dto.PermissionDTO;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.entity.Permission;
import com.alineumsoft.zenwk.security.entity.Role;
import com.alineumsoft.zenwk.security.entity.RoleUser;
import com.alineumsoft.zenwk.security.enums.RoleEnum;
import com.alineumsoft.zenwk.security.enums.SecurityActionEnum;
import com.alineumsoft.zenwk.security.person.repository.PermissionRepository;
import com.alineumsoft.zenwk.security.person.repository.RolePermissionRepository;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import com.alineumsoft.zenwk.security.user.entity.User;
import com.alineumsoft.zenwk.security.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

class PermissionServiceTest {

  @Mock
  private RolePermissionRepository rolePermRepo;
  @Mock
  private UserService userService;
  @Mock
  private LogSecurityRepository logSecRepo;
  @Mock
  private PermissionRepository permissionRepo;

  @Mock
  private HttpServletRequest request;
  @Mock
  private UserDetails userDetails;

  @InjectMocks
  private PermissionService service;

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

    when(logSecRepo.save(any(LogSecurity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  @DisplayName("Verifica que el enum sea correcto")
  void testEnumValue() {
    assertEquals("PERMISSION_DELETE", SecurityActionEnum.PERMISSION_DELETE.name());
    assertEquals("PERMISSION.DELETE", SecurityActionEnum.PERMISSION_DELETE.getCode());
  }

  @Test
  @DisplayName("Obtiene permisos por operación correctamente cuando existen registros")
  void testGetOperationPermission_success() {
    List<Object[]> mockData = List.of(new Object[] {"CREATE", "P1", "R1", "DESC1"},
        new Object[] {"UPDATE", "P2", "R2", "DESC2"});

    when(rolePermRepo.findAllRolePermissions()).thenReturn(mockData);

    Map<PermissionOperationEnum, List<PermissionDTO>> result = service.getOperationPermission();

    assertEquals(2, result.size());
    assertEquals("P1", result.get(PermissionOperationEnum.CREATE).get(0).getName());
  }

  @Test
  @DisplayName("Retorna mapa vacío cuando ocurre una excepción al obtener permisos")
  void testGetOperationPermission_exception_returnsEmptyMap() {
    when(rolePermRepo.findAllRolePermissions()).thenThrow(new RuntimeException());
    assertTrue(service.getOperationPermission().isEmpty());
  }

  @Test
  @DisplayName("Genera URLs permitidas dinámicas para un usuario con rol válido")
  void testListAllowedUrlsForUserRole_userWithDynamicUrls() {
    User user = new User();
    user.setId(10L);

    RoleUser role = new RoleUser();
    Role roleEntity = new Role();
    roleEntity.setName(RoleEnum.USER.name());
    role.setRole(roleEntity);

    when(userService.findByUsername("tester")).thenReturn(user);
    when(userService.findRolesUserByUser(user)).thenReturn(List.of(role));
    when(rolePermRepo.findResourcesByRolName(anyList())).thenReturn(List.of("/user/{id}/data"));

    List<String> urls = service.listAllowedUrlsForUserRole("tester");

    assertEquals("/user/10/data", urls.get(0));
  }

  @Test
  @DisplayName("Crea un permiso correctamente cuando no existe uno con el mismo nombre")
  void testCreatePermission_success() {
    PermissionDTO dto = new PermissionDTO("P1", "POST", "D1");
    dto.setDescription("permission create test");
    dto.setOperation("CREATE");

    when(permissionRepo.existsByName("P1")).thenReturn(false);
    when(permissionRepo.save(any())).thenAnswer(invoc -> {
      Permission p = invoc.getArgument(0);
      p.setId(1L);
      return p;
    });

    Long id = service.createPemission(dto, request, userDetails);
    assertEquals(1L, id);
  }

  @Test
  @DisplayName("Lanza excepción funcional cuando se intenta crear un permiso duplicado")
  void testCreatePermission_entityExists_throwsFunctional() {
    PermissionDTO dto = new PermissionDTO("P1", "R1", "D1");
    when(permissionRepo.existsByName("P1")).thenReturn(true);

    assertThrows(FunctionalException.class,
        () -> service.createPemission(dto, request, userDetails));
  }

  @Test
  @DisplayName("Actualiza un permiso correctamente cuando existe en base de datos")
  void testUpdatePermission_success() {
    Permission target = new Permission();
    target.setName("PERMISSION_UPDATE");
    target.setOperation(PermissionOperationEnum.UPDATE);

    when(permissionRepo.findById(1L)).thenReturn(Optional.of(target));
    when(permissionRepo.save(any())).thenReturn(target);

    PermissionDTO dto = new PermissionDTO("PERMISSION_UPDATE", "POST", "/api/permissions/{id}");
    dto.setDescription("permission update test");
    dto.setOperation("UPDATE");


    assertDoesNotThrow(() -> service.updatePermission(1L, dto, request, userDetails));
  }

  @Test
  @DisplayName("Elimina un permiso correctamente cuando no tiene restricciones")
  void testDeletePermission_success() {
    Permission p = new Permission();
    when(permissionRepo.findById(1L)).thenReturn(Optional.of(p));


    assertDoesNotThrow(() -> service.deletePermission(1L, request, userDetails));
  }

  @Test
  @DisplayName("Lanza excepción técnica al intentar eliminar un permiso con dependencias")
  void testDeletePermission_integrityViolation() {
    when(permissionRepo.findById(1L)).thenReturn(Optional.of(new Permission()));
    doThrow(new DataIntegrityViolationException("err")).when(permissionRepo).delete(any());

    assertThrows(TechnicalException.class,
        () -> service.deletePermission(1L, request, userDetails));
  }

  @Test
  @DisplayName("Obtiene un permiso por id correctamente")
  void testFindByIdPermission_success() {
    Permission p = new Permission();
    p.setId(1L);
    p.setName("TEST");
    p.setOperation(PermissionOperationEnum.GET);

    when(permissionRepo.findById(1L)).thenReturn(Optional.of(p));

    PermissionDTO dto = service.findByIdPermission(1L, request, userDetails);

    assertEquals("TEST", dto.getName());
  }

  @Test
  @DisplayName("Lanza excepción cuando no existe un permiso para el id consultado")
  void testFindPermissionById_notFound() {
    when(permissionRepo.findById(1L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> service.findPermissionById(1L));
  }

  @Test
  @DisplayName("Obtiene todas los permisos paginados correctamente")
  void testGetAllPermissions_success() {
    Permission p = new Permission();
    p.setId(1L);
    p.setOperation(PermissionOperationEnum.LIST);

    Page<Permission> page = new PageImpl<>(List.of(p));
    Pageable pageable = PageRequest.of(0, 10);

    when(permissionRepo.findAll(any(Pageable.class))).thenReturn(page);

    PagePermissionDTO result = service.getAllPermissions(pageable, request, userDetails);

    assertEquals(1, result.getPermissions().size());
  }

  @Test
  @DisplayName("Construye correctamente un PagePermissionDTO desde un Page de permisos")
  void testGetPagePermissionDTO_success() {
    Permission p = new Permission();
    p.setId(5L);
    p.setOperation(PermissionOperationEnum.GET);

    Page<Permission> page = new PageImpl<>(List.of(p));

    PagePermissionDTO dto = service.getPagePermissionDTO(page);

    assertEquals(1, dto.getTotalPages());
    assertEquals(1, dto.getPermissions().size());
  }

  @Test
  @DisplayName("Obtiene los permisos asociados a un rol con paginación")
  void testGetPermissionsForRole() {
    Permission p = new Permission();
    p.setId(2L);
    p.setOperation(PermissionOperationEnum.LIST);

    Page<Permission> page = new PageImpl<>(List.of(p));
    Pageable pageable = PageRequest.of(0, 10);

    when(permissionRepo.getAllPermissionsForRoleId(eq(99L), any())).thenReturn(page);

    Page<Permission> result = service.getPermissionsForRole(pageable, 99L, 0);

    assertEquals(1, result.getSize());
  }
}
