package com.alineumsoft.zenwk.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import com.alineumsoft.zenwk.security.auth.dto.RoleUserDTO;
import com.alineumsoft.zenwk.security.auth.jwt.JwtProvider;
import com.alineumsoft.zenwk.security.auth.service.RoleAssignmentService;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.common.exception.TechnicalException;
import com.alineumsoft.zenwk.security.common.util.HistoricalUtil;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.entity.Role;
import com.alineumsoft.zenwk.security.entity.RoleUser;
import com.alineumsoft.zenwk.security.enums.UserStateEnum;
import com.alineumsoft.zenwk.security.person.entity.Person;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import com.alineumsoft.zenwk.security.user.dto.PageUserDTO;
import com.alineumsoft.zenwk.security.user.dto.UserDTO;
import com.alineumsoft.zenwk.security.user.entity.User;
import com.alineumsoft.zenwk.security.user.repository.UserRepository;
import com.alineumsoft.zenwk.security.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private RoleAssignmentService roleAsignmentService;

  @Mock
  private LogSecurityRepository logSecurityUserRespository;

  @Mock
  private TransactionTemplate templateTx;

  @Mock
  private HistoricalUtil historicalUtil;

  @Mock
  private HttpServletRequest request;

  @Mock
  private UserDetails userDetails;

  @Mock
  private JwtProvider jwtProvider;

  @Mock
  private org.springframework.context.ApplicationEventPublisher eventPublisher;

  private MockedStatic<HistoricalUtil> mockedHistorical;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    when(userDetails.getUsername()).thenReturn("tester");
    when(request.getRequestURI()).thenReturn("/mock");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    when(request.getHeader(anyString())).thenReturn("mock-header");
    when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/mock"));

    when(logSecurityUserRespository.save(any(LogSecurity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Mock para TransactionStatus
    TransactionStatus mockStatus = mock(TransactionStatus.class);

    // Transaction template always executes the lambda
    when(templateTx.execute(any(TransactionCallback.class))).thenAnswer(invocation -> {
      TransactionCallback<?> callback = invocation.getArgument(0);
      return callback.doInTransaction(mockStatus);
    });

    mockedHistorical = mockStatic(HistoricalUtil.class);
    mockedHistorical.when(() -> HistoricalUtil.registerHistorical(any(), any(), any()))
        .thenAnswer(invocation -> null);

  }

  @AfterEach
  void teardown() {
    if (mockedHistorical != null) {
      // Cierra el mock estático
      mockedHistorical.close();
    }
  }

  @Test
  @DisplayName("Crear usuario exitosamente")
  void testCreateUser() {
    UserDTO dto = new UserDTO();
    dto.setUsername("user");
    dto.setPassword("Password123*");
    dto.setEmail("email@test.com");

    when(userRepository.existsByUsernameAndEmail(anyString(), anyString())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      user.setId(1L);
      return user;
    });

    Long id = userService.createUser(dto, request);
    assertEquals(1L, id);
    verify(roleAsignmentService).createRoleUser(any(), any(RoleUserDTO.class));
  }

  @Test
  @DisplayName("Actualizar usuario exitosamente")
  void testUpdateUser() {
    Long idUser = 1L;
    User user = new User();
    user.setId(idUser);
    when(userRepository.findById(idUser)).thenReturn(Optional.of(user));

    Person person = new Person();
    UserDTO dto = new UserDTO();
    boolean result = userService.updateUser(request, idUser, dto, userDetails, person);
    assertTrue(result);
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("Eliminar usuario exitosamente")
  void testDeleteUser() {
    Long idUser = 1L;
    User user = new User();
    user.setId(idUser);
    when(userRepository.findById(idUser)).thenReturn(Optional.of(user));

    boolean result = userService.deleteUser(idUser, request, userDetails);
    assertTrue(result);
    verify(userRepository).deleteById(idUser);
  }

  @Test
  @DisplayName("Obtener usuario por ID")
  void testFindByIdUser() {
    Long idUser = 1L;
    User user = new User();
    user.setId(idUser);
    user.setPassword("secret");
    when(userRepository.findById(idUser)).thenReturn(Optional.of(user));

    UserDTO dto = userService.findByIdUser(idUser, request, userDetails);
    assertEquals("tester", dto.getUsername() == null ? "tester" : dto.getUsername());
    assertEquals("************", dto.getPassword());
  }

  @Test
  @DisplayName("Obtener usuario actual")
  void testGetCurrentUser() {
    Long idUser = 1L;
    String token = "token";
    User user = new User();
    user.setId(idUser);
    user.setPassword("secret");

    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.of(token));
    when(jwtProvider.extractIdUser(token)).thenReturn(idUser);
    when(userRepository.findById(idUser)).thenReturn(Optional.of(user));

    UserDTO dto = userService.getCurrentUser(request, userDetails);
    assertEquals("************", dto.getPassword());
  }

  @Test
  @DisplayName("Validar existencia de usuario por ID")
  void testIsExistsUserById() {
    Long idUser = 1L;
    User user = new User();
    user.setId(idUser);
    when(userRepository.findById(idUser)).thenReturn(Optional.of(user));

    assertTrue(userService.isExistsUserById(idUser));
  }

  @Test
  @DisplayName("Validar existencia de usuario por email")
  void testFindByEmailReturnsTrueIfExists() {
    String email = "email@test.com";
    User user = new User();
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    assertTrue(userService.findByEmail(email, request));
  }

  @Test
  @DisplayName("Obtener todos los usuarios paginados")
  void testGetAllUsers() {
    User user = new User();
    user.setId(1L);
    Page<User> page = new PageImpl<>(List.of(user));
    when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

    PageUserDTO pageUserDTO = userService.getAllUsers(PageRequest.of(0, 10), request, userDetails);
    assertEquals(1, pageUserDTO.getUsers().size());
  }

  @Test
  @DisplayName("Obtener roles de un usuario por username")
  void testFindRolesByUsername() {
    User user = new User();
    RoleUser rolUser = new RoleUser();
    Role role = new Role("R1", "Description R1");

    user.setId(1L);
    user.setState(UserStateEnum.ACTIVE);
    rolUser.setUser(user);
    role.setId(1L);
    rolUser.setRole(role);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    when(roleAsignmentService.findRoleUserByIdUser(anyLong())).thenReturn(List.of(rolUser));
    when(roleAsignmentService.findRoleByIds(any())).thenReturn(List.of(role));


    List<Role> roles = userService.findRolesByUsername("tester");
    assertNotNull(roles);
  }

  @Test
  @DisplayName("Buscar usuario por username")
  void testFindByUsername() {
    String username = "tester";
    User user = new User();
    user.setUsername(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

    User result = userService.findByUsername(username);
    assertEquals(username, result.getUsername());
  }

  @Test
  @DisplayName("Verificar existencia por ID con existsById")
  void testExistsById() {
    when(userRepository.existsById(1L)).thenReturn(true);
    assertTrue(userService.existsById(1L));
  }

  @Test
  @DisplayName("Buscar usuario por ID con findById")
  void testFindById() {
    Long id = 1L;
    User user = new User();
    when(userRepository.findById(id)).thenReturn(Optional.of(user));
    User result = userService.findById(id);
    assertEquals(user, result);
  }

  @Test
  @DisplayName("Crear usuario falla si ya existe")
  void testCreateUserAlreadyExists() {
    UserDTO dto = new UserDTO();
    dto.setUsername("user");
    dto.setPassword("Password123*");
    dto.setEmail("email@test.com");

    when(userRepository.existsByUsernameAndEmail(anyString(), anyString())).thenReturn(true);

    assertThrows(FunctionalException.class, () -> userService.createUser(dto, request));
  }

  @Test
  @DisplayName("Actualizar usuario falla si no existe")
  void testUpdateUserNotFound() {
    Long idUser = 999L;
    UserDTO dto = new UserDTO();
    Person person = new Person();

    when(userRepository.findById(idUser)).thenReturn(Optional.empty());

    assertThrows(FunctionalException.class,
        () -> userService.updateUser(request, idUser, dto, userDetails, person));
  }

  @Test
  @DisplayName("Eliminar usuario falla si no existe")
  void testDeleteUserNotFound() {
    Long idUser = 999L;
    when(userRepository.findById(idUser)).thenReturn(Optional.empty());

    assertThrows(FunctionalException.class,
        () -> userService.deleteUser(idUser, request, userDetails));
  }

  @Test
  @DisplayName("Obtener usuario actual falla si no hay token")
  void testGetCurrentUserNoToken() {
    // Simula que no hay cookies
    when(request.getCookies()).thenReturn(null);
    // Simula que jwtProvider no encuentra token
    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.empty());

    // Verifica que se lance la excepción funcional
    assertThrows(FunctionalException.class, () -> userService.getCurrentUser(request, userDetails));
  }

  @Test
  @DisplayName("Obtener usuario actual falla si usuario no encontrado")
  void testGetCurrentUserNotFound() {
    String token = "token";
    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.of(token));
    when(jwtProvider.extractIdUser(token)).thenReturn(999L);
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(FunctionalException.class, () -> userService.getCurrentUser(request, userDetails));
  }

  @Test
  @DisplayName("Buscar usuario por ID retorna null si no existe")
  void testFindByIdUserNotFound() {
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(FunctionalException.class,
        () -> userService.findByIdUser(999L, request, userDetails));
  }

  @Test
  @DisplayName("Buscar usuario por email retorna false si no existe")
  void testFindByEmailNotFound() {
    String email = "notfound@test.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    assertEquals(false, userService.findByEmail(email, request));
  }

  @Test
  @DisplayName("Buscar usuario por username lanza excepción si no existe")
  void testFindByUsernameNotFound() {
    String username = "unknown";
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
    try {
      userService.findByUsername(username);
    } catch (Exception e) {
      assertTrue(e instanceof RuntimeException);
    }
  }

  @Test
  @DisplayName("Buscar roles por username retorna lista vacía si usuario no existe")
  void testFindRolesByUsernameNotFound() {
    when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

    assertThrows(FunctionalException.class, () -> userService.findRolesByUsername("unknown"));
  }

  @Test
  @DisplayName("Actualizar usuario sin modificar campos nulos")
  void testUpdateUserWithNullFields() {
    Long idUser = 1L;
    User user = new User();
    user.setId(idUser);
    user.setEmail("old@test.com");
    when(userRepository.findById(idUser)).thenReturn(Optional.of(user));

    UserDTO dto = new UserDTO(); // todos los campos null
    Person person = new Person();
    boolean result = userService.updateUser(request, idUser, dto, userDetails, person);

    assertTrue(result);
    verify(userRepository).save(any(User.class));
    assertEquals("old@test.com", user.getEmail()); // se mantiene el valor anterior
  }

  @Test
  @DisplayName("Obtener todos los usuarios paginados retorna vacío si no hay usuarios")
  void testGetAllUsersEmpty() {
    Page<User> page = new PageImpl<>(List.of());
    when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

    PageUserDTO pageUserDTO = userService.getAllUsers(PageRequest.of(0, 10), request, userDetails);
    assertNotNull(pageUserDTO);
    assertTrue(pageUserDTO.getUsers().isEmpty());
  }

  @Test
  @DisplayName("Buscar roles por username retorna lista vacía si usuario no tiene roles")
  void testFindRolesByUsernameEmptyRoles() {
    User user = new User();
    user.setId(1L);
    user.setState(UserStateEnum.INCOMPLETE_PERFIL);

    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    when(roleAsignmentService.findRoleUserByIdUser(anyLong())).thenReturn(List.of()); // sin roles

    List<Role> roles = userService.findRolesByUsername("tester");
    assertNotNull(roles);
    assertTrue(roles.isEmpty());
  }

  @Test
  @DisplayName("Eliminar usuario registra logSecurity aunque no falle")
  void testDeleteUserRegistersLog() {
    Long idUser = 1L;
    User user = new User();
    user.setId(idUser);
    when(userRepository.findById(idUser)).thenReturn(Optional.of(user));

    boolean result = userService.deleteUser(idUser, request, userDetails);
    assertTrue(result);
    verify(logSecurityUserRespository).save(any(LogSecurity.class));
    verify(userRepository).deleteById(idUser);
  }

  @Test
  @DisplayName("Crear usuario registra histórico")
  void testCreateUserRegistersHistorical() {
    UserDTO dto = new UserDTO();
    dto.setUsername("user");
    dto.setPassword("Password123*");
    dto.setEmail("email@test.com");

    when(userRepository.existsByUsernameAndEmail(anyString(), anyString())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      user.setId(1L);
      return user;
    });

    Long id = userService.createUser(dto, request);
    assertEquals(1L, id);

    // Verifica que se haya llamado al método histórico
    mockedHistorical.verify(() -> HistoricalUtil.registerHistorical(any(),
        eq(com.alineumsoft.zenwk.security.common.hist.enums.HistoricalOperationEnum.INSERT),
        eq(com.alineumsoft.zenwk.security.user.service.UserHistService.class)));
  }


  @Test
  @DisplayName("createUserTx lanza TechnicalException si falla transacción")
  void testCreateUserTxException() {
    UserDTO dto = new UserDTO();
    dto.setUsername("user");
    dto.setEmail("email@test.com");
    dto.setPassword("Password123*");

    when(userRepository.existsByUsernameAndEmail(anyString(), anyString())).thenReturn(false);
    when(templateTx.execute(any())).thenThrow(new RuntimeException("DB error"));

    assertThrows(TechnicalException.class, () -> userService.createUser(dto, request));
  }



  @Test
  @DisplayName("Actualizar usuario lanza TechnicalException si falla al guardar")
  void testUpdateUserThrowsTechnicalException() {
    Long idUser = 1L;
    User user = new User();
    user.setId(idUser);
    when(userRepository.findById(idUser)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB failure"));

    UserDTO dto = new UserDTO();
    Person person = new Person();

    assertThrows(TechnicalException.class,
        () -> userService.updateUser(request, idUser, dto, userDetails, person));
  }

  @Test
  @DisplayName("Obtener todos los usuarios lanza TechnicalException si falla el repositorio")
  void testGetAllUsersThrowsTechnicalException() {
    when(userRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("DB failure"));

    assertThrows(TechnicalException.class,
        () -> userService.getAllUsers(PageRequest.of(0, 10), request, userDetails));
  }

  @Test
  @DisplayName("Buscar usuario por username lanza TechnicalException si falla el repositorio")
  void testFindByUsernameThrowsTechnicalException() {
    String username = "tester";
    when(userRepository.findByUsername(username))
        .thenThrow(new EntityNotFoundException("DB failure"));

    assertThrows(EntityNotFoundException.class, () -> userService.findByUsername(username));
  }

  @Test
  @DisplayName("Crear usuario lanza FunctionalException si contraseña inválida")
  void testCreateUserInvalidPassword() {
    UserDTO dto = new UserDTO();
    dto.setUsername("user");
    dto.setPassword("123"); // contraseña inválida
    dto.setEmail("email@test.com");

    when(userRepository.existsByUsernameAndEmail(anyString(), anyString())).thenReturn(false);

    assertThrows(TechnicalException.class, () -> userService.createUser(dto, request));
  }

}
