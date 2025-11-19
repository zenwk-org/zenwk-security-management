package com.alineumsoft.zenwk.security;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import com.alineumsoft.zenwk.security.auth.dto.PageRoleDTO;
import com.alineumsoft.zenwk.security.auth.dto.RoleDTO;
import com.alineumsoft.zenwk.security.auth.service.RoleService;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.common.exception.TechnicalException;
import com.alineumsoft.zenwk.security.common.util.ObjectUpdaterUtil;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.entity.Role;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import com.alineumsoft.zenwk.security.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

class RoleServiceTest {

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private LogSecurityRepository logSecRepo;

  @Mock
  private HttpServletRequest request;

  @Mock
  private UserDetails userDetails;

  @InjectMocks
  private RoleService roleService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    when(userDetails.getUsername()).thenReturn("tester");

    // Mock HttpServletRequest completamente
    when(request.getRequestURI()).thenReturn("/mock");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    when(request.getHeader(anyString())).thenReturn("mock-header");

    // MOCK CLAVE → getRequestURL()
    when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/mock"));

    when(logSecRepo.save(any(LogSecurity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }


  @Test
  @DisplayName("createRole: crea rol exitosamente")
  void createRole_Success() {
    RoleDTO dto = new RoleDTO();

    dto.setName("ADMIN");
    dto.setDescription("Administrador");

    when(roleRepository.existsByName("ADMIN")).thenReturn(false);
    when(roleRepository.save(any())).thenAnswer(inv -> {
      Role r = inv.getArgument(0);
      r.setId(1L);
      return r;
    });

    Long id = roleService.createRole(dto, request, userDetails);
    assertEquals(1L, id);
  }

  @Test
  @DisplayName("createRole: lanza EntityExistsException si el nombre ya existe")
  void createRole_AlreadyExists() {
    RoleDTO dto = new RoleDTO();
    dto.setName("ADMIN");
    dto.setDescription("Administrador");
    when(userDetails.getUsername()).thenReturn("tester");
    when(roleRepository.existsByName("ADMIN")).thenReturn(true);

    assertThrows(FunctionalException.class,
        () -> roleService.createRole(dto, request, userDetails));
  }

  @Test
  @DisplayName("updateRole: actualiza un rol cuando hay cambios")
  void updateRole_Success() {
    RoleDTO dto = new RoleDTO();
    dto.setName("ADMIN");
    dto.setDescription("Administrador");
    Role role = new Role("OLD_NAME", "OLD_DESC");
    role.setId(1L);

    when(userDetails.getUsername()).thenReturn("tester");
    when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
    when(roleRepository.save(any())).thenReturn(role);

    assertDoesNotThrow(() -> roleService.updateRole(1L, dto, request, userDetails));
    verify(roleRepository).save(any());
  }

  @Test
  @DisplayName("findRoleById: lanza EntityNotFoundException si no existe")
  void findRoleById_NotFound() {
    when(roleRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> roleService.findRoleById(1L));
  }

  @Test
  @DisplayName("deleteRole: falla por integridad referencial (TechnicalException)")
  void deleteRole_DataIntegrityViolation() {
    Role role = new Role("USER", "Desc");
    when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
    doThrow(DataIntegrityViolationException.class).when(roleRepository).delete(role);

    assertThrows(TechnicalException.class, () -> roleService.deleteRole(1L, request, userDetails));
  }

  @Test
  @DisplayName("findAllRoles: retorna paginado correctamente")
  void findAllRoles_Success() {
    Pageable pageable = PageRequest.of(0, 2, Sort.by("name"));
    List<Role> roles = List.of(new Role("ADMIN", "A"), new Role("USER", "B"));
    Page<Role> page = new PageImpl<>(roles, pageable, roles.size());

    when(userDetails.getUsername()).thenReturn("tester");
    when(roleRepository.findAll(any(PageRequest.class))).thenReturn(page);

    PageRoleDTO result = roleService.findAllRoles(pageable, request, userDetails);

    assertNotNull(result);
    assertEquals(2, result.getRoles().size());
    assertEquals(2, result.getTotalElements());
  }

  @Test
  @DisplayName("updateRole: NO hay cambios, no debe guardar")
  void updateRole_NoChanges() {
    RoleDTO dto = new RoleDTO();
    dto.setName("ADMIN");
    dto.setDescription("Administrador");

    Role role = new Role("ADMIN", "Administrador"); // mismos valores
    role.setId(1L);

    when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

    // forzar que no hay cambios
    try (MockedStatic<ObjectUpdaterUtil> mock = Mockito.mockStatic(ObjectUpdaterUtil.class)) {
      mock.when(() -> ObjectUpdaterUtil.updateDataEqualObject(any(), any())).thenReturn(false);

      roleService.updateRole(1L, dto, request, userDetails);
      verify(roleRepository).findById(1L); // solo debe buscar
      verify(roleRepository, Mockito.never()).save(any()); // NO guardarlo
    }
  }

  @Test
  @DisplayName("findRoleById con request: éxito")
  void findRoleById_WithRequest_Success() {
    Role role = new Role("ADMIN", "Desc");
    role.setId(1L);
    when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

    RoleDTO result = roleService.findRoleById(1L, request, userDetails);
    assertNotNull(result);
    assertEquals("ADMIN", result.getName());
  }

  @Test
  @DisplayName("findRoleByName: éxito")
  void findRoleByName_Success() {
    Role role = new Role("USER", "Desc");
    when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));

    Role result = roleService.findRoleByName("USER");
    assertEquals("USER", result.getName());
  }


  @Test
  @DisplayName("findRoleByIds: retorna lista")
  void findRoleByIds_Success() {
    List<Role> roles = List.of(new Role("R1", "D1"), new Role("R2", "D2"));
    when(roleRepository.findByIds(List.of(1L, 2L))).thenReturn(roles);

    List<Role> result = roleService.findRoleByIds(List.of(1L, 2L));
    assertEquals(2, result.size());
  }

  @Test
  @DisplayName("deleteRole: eliminación exitosa")
  void deleteRole_Success() {
    Role role = new Role("USER", "Desc");
    when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

    assertDoesNotThrow(() -> roleService.deleteRole(1L, request, userDetails));
    verify(roleRepository).delete(role); // se debe eliminar
  }

  @Test
  @DisplayName("findAllRoles: error lanza FunctionalException")
  void findAllRoles_Error() {
    Pageable pageable = PageRequest.of(0, 5);
    when(roleRepository.findAll(any(PageRequest.class)))
        .thenThrow(new RuntimeException("db error"));

    assertThrows(FunctionalException.class,
        () -> roleService.findAllRoles(pageable, request, userDetails));
  }

  @Test
  @DisplayName("getRolesForPermission: obtiene roles con permiso")
  void getRolesForPermission_Success() {
    Pageable pageable = PageRequest.of(0, 5);
    Page<Role> page = new PageImpl<>(List.of(new Role("ADMIN", "X")));
    when(roleRepository.getAllRolesForPermissionId(any(), any())).thenReturn(page);

    Page<Role> result = roleService.getRolesForPermission(pageable, 99L, 1);
    assertEquals(1, result.getTotalElements());
  }



}
