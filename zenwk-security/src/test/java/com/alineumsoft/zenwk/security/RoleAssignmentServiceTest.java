package com.alineumsoft.zenwk.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import com.alineumsoft.zenwk.security.auth.service.PermissionService;
import com.alineumsoft.zenwk.security.auth.service.RoleAssignmentService;
import com.alineumsoft.zenwk.security.auth.service.RoleService;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.entity.Permission;
import com.alineumsoft.zenwk.security.entity.Role;
import com.alineumsoft.zenwk.security.entity.RolePermission;
import com.alineumsoft.zenwk.security.person.repository.RolePermissionRepository;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import jakarta.servlet.http.HttpServletRequest;

class RoleAssignmentServiceTest {

  @Mock
  private RoleService roleService;
  @Mock
  private PermissionService permissionService;
  @Mock
  private RolePermissionRepository rolPermissionRepo;
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

    when(logSecRepo.save(any(LogSecurity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
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

    roleAssignmentService.assignPermissionsToRole(roleId, permissionId, request, userDetails);

    verify(rolPermissionRepo, times(1)).save(any(RolePermission.class));
    verify(logSecRepo, times(1)).save(any(LogSecurity.class));
  }

  @Test
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

    assertThrows(FunctionalException.class, () -> roleAssignmentService
        .assignPermissionsToRole(roleId, permissionId, request, userDetails));

    verify(rolPermissionRepo, never()).save(any(RolePermission.class));
  }
}
