package com.alineumsoft.zenwk.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.alineumsoft.zenwk.security.auth.jwt.JwtProvider;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.common.exception.TechnicalException;
import com.alineumsoft.zenwk.security.common.util.HistoricalUtil;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.enums.RoleEnum;
import com.alineumsoft.zenwk.security.enums.SecurityExceptionEnum;
import com.alineumsoft.zenwk.security.enums.UserStateEnum;
import com.alineumsoft.zenwk.security.person.dto.CreatePersonDTO;
import com.alineumsoft.zenwk.security.person.dto.PagePersonDTO;
import com.alineumsoft.zenwk.security.person.dto.PersonDTO;
import com.alineumsoft.zenwk.security.person.entity.Person;
import com.alineumsoft.zenwk.security.person.entity.PersonSex;
import com.alineumsoft.zenwk.security.person.repository.PersonRepository;
import com.alineumsoft.zenwk.security.person.service.PersonService;
import com.alineumsoft.zenwk.security.person.service.PersonSexService;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import com.alineumsoft.zenwk.security.user.event.DeleteUserEvent;
import com.alineumsoft.zenwk.security.user.event.UpdateUserEvent;
import com.alineumsoft.zenwk.security.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

  // ============================================================
  // MOCKS
  // ============================================================

  @InjectMocks
  private PersonService personService;
  @Mock
  private PersonRepository personRepo;
  @Mock
  private LogSecurityRepository logSecurityRepo;
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private JwtProvider jwtProvider;
  @Mock
  private PersonSexService personSexService;
  @Mock
  private HttpServletRequest request;
  @Mock
  private UserDetails userDetails;
  @Mock
  private TransactionTemplate templateTx;
  @Mock
  private LogSecurityRepository logSecurityUserRespository;
  @Mock
  private UserService userService;
  @Mock
  private MultipartFile multipartFile;

  private MockedStatic<HistoricalUtil> mockedHistorical;


  // ============================================================
  // SETUP / TEARDOWN
  // ============================================================

  @BeforeEach
  void setup() {
    lenient().when(userDetails.getUsername()).thenReturn("tester");
    lenient().when(request.getRequestURI()).thenReturn("/mock");
    lenient().when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    lenient().when(request.getHeader(anyString())).thenReturn("mock-header");
    lenient().when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/mock"));

    lenient().when(logSecurityUserRespository.save(any(LogSecurity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    TransactionStatus mockStatus = mock(TransactionStatus.class);

    lenient().when(templateTx.execute(any(TransactionCallback.class))).thenAnswer(invocation -> {
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
      mockedHistorical.close();
    }
  }


  // ============================================================
  // createPerson()
  // ============================================================

  @Test
  @DisplayName("createPerson() debe retornar ID al crear persona correctamente")
  void createPerson_ok() {
    CreatePersonDTO dto = new CreatePersonDTO();

    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.of("token"));
    when(jwtProvider.extractIdUser("token")).thenReturn(10L);

    Person savedPerson = new Person();
    savedPerson.setId(99L);
    when(templateTx.execute(any())).thenReturn(savedPerson);

    Long result = personService.createPerson(dto, request, userDetails);
    assertEquals(99L, result);
  }


  @Test
  @DisplayName("createPerson() debe ejecutar createPersonRecord() realmente")
  void createPerson_ok_real_inside() {
    PersonService spyService = Mockito.spy(personService);
    CreatePersonDTO dto = new CreatePersonDTO();

    // Mock JWT
    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.of("token"));
    when(jwtProvider.extractIdUser("token")).thenReturn(10L);

    // Mock getPersonSource()
    Person personMock = new Person();
    doReturn(personMock).when(spyService).getPersonSource(dto);

    // Mock exist → evita excepción
    doReturn(true).when(spyService).isNotExistPerson(personMock);

    // Simular persistencia real
    Person savedPerson = new Person();
    savedPerson.setId(99L);
    when(personRepo.save(personMock)).thenReturn(savedPerson);

    when(templateTx.execute(any())).thenAnswer(invocation -> {
      return ((TransactionCallback<?>) invocation.getArgument(0)).doInTransaction(null);
    });

    Long result = spyService.createPerson(dto, request, userDetails);

    assertEquals(99L, result);
    verify(personRepo).save(personMock); // YA CUBRE createPersonRecord()
  }

  @Test
  @DisplayName("createPerson() debe lanzar excepción si no hay token en cookies")
  void createPerson_sinToken() {
    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.empty());
    CreatePersonDTO dto = new CreatePersonDTO();

    assertThrows(RuntimeException.class,
        () -> personService.createPerson(dto, request, userDetails));
  }


  @Test
  @DisplayName("createPerson() debe lanzar TechnicalException si ocurre error inesperado")
  void createPerson_technicalException() {
    // Spy del servicio
    PersonService spyService = Mockito.spy(personService);
    CreatePersonDTO dto = new CreatePersonDTO();
    // 1. Mock del token OK
    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.of("token"));
    when(jwtProvider.extractIdUser("token")).thenReturn(10L);

    // 2. Simular EXCEPCIÓN antes de métodos private
    when(jwtProvider.extractAuthorities("token"))
        .thenThrow(new RuntimeException("Error inesperado en JWT"));

    // 3. decir que NO es functional => debe caer en TechnicalException
    doReturn(false).when(spyService).isFunctionalException(any(RuntimeException.class));

    // 4. Validación final
    assertThrows(TechnicalException.class,
        () -> spyService.createPerson(dto, request, userDetails));
    verify(jwtProvider).extractAuthorities("token");
  }


  @DisplayName("validatePerson() debe lanzar FUNC_PERSON_ID_USER_NOT_ASSOCIATE si el usuario tiene rol NEW_USER y estado INCOMPLETE_PERFIL y el id no coincide")
  void validatePerson_userNotAssociate() {
    String token = "token";
    CreatePersonDTO dto = new CreatePersonDTO();

    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.of(token));
    when(jwtProvider.extractIdUser(token)).thenReturn(10L);

    // Mock roles y estado del usuario
    when(jwtProvider.extractAuthorities(token)).thenReturn(List.of(RoleEnum.NEW_USER.name()));
    when(jwtProvider.extractUserState(token)).thenReturn(UserStateEnum.INCOMPLETE_PERFIL);

    doReturn(true).when(personService).isFunctionalException(any(RuntimeException.class));

    assertThrows(FunctionalException.class,
        () -> personService.createPerson(dto, request, userDetails));
  }

  @Test
  @DisplayName("validatePerson() debe lanzar FUNC_PERSON_ID_USER_IS_ASSOCIATE si el usuario ya está asociado")
  void validatePerson_userAlreadyAssociated() {
    // Spy LOCAL → permite ejecutar lógica real y mockear solo isFunctionalException
    PersonService spyService = Mockito.spy(personService);

    String token = "token";
    CreatePersonDTO dto = new CreatePersonDTO();
    dto.setIdUser(999L);

    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.of(token));
    when(jwtProvider.extractIdUser(token)).thenReturn(999L);
    when(jwtProvider.extractAuthorities(token)).thenReturn(List.of(RoleEnum.USER.name())); // No
                                                                                           // NEW_USER
    when(jwtProvider.extractUserState(token)).thenReturn(UserStateEnum.ACTIVE); // Estado activo =
                                                                                // ya está asociado

    // Simula que es una excepción funcional
    doReturn(true).when(spyService).isFunctionalException(any(RuntimeException.class));

    assertThrows(FunctionalException.class,
        () -> spyService.createPerson(dto, request, userDetails));

    verify(spyService, times(1)).isFunctionalException(any(RuntimeException.class));
  }


  // ============================================================
  // private validateUser()
  // ============================================================

  @Test
  @DisplayName("validateUser() debe lanzar FUNC_PERSON_ID_USER_NOT_ASSOCIATE si el usuario NO está asociado")
  void validateUser_userNotAssociate() {
    String token = "token";
    CreatePersonDTO dto = new CreatePersonDTO();
    dto.setIdUser(20L);

    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.of(token));
    when(jwtProvider.extractIdUser(token)).thenReturn(10L); // otro id
    when(jwtProvider.extractAuthorities(token)).thenReturn(List.of(RoleEnum.NEW_USER.name()));
    when(jwtProvider.extractUserState(token)).thenReturn(UserStateEnum.INCOMPLETE_PERFIL);

    assertThrows(IllegalArgumentException.class,
        () -> ReflectionTestUtils.invokeMethod(personService, "validateUser", dto, request));
  }

  // ============================================================
  // findByIdPerson()
  // ============================================================

  @Test
  @DisplayName("findByIdPerson() debe retornar DTO cuando persona existe")
  void findByIdPerson_ok() {
    Person person = new Person();
    person.setId(5L);
    when(personRepo.findById(5L)).thenReturn(Optional.of(person));

    CreatePersonDTO dto = personService.findByIdPerson(5L, request, userDetails);

    assertNotNull(dto);
    assertEquals(5L, dto.getId());
  }

  @Test
  @DisplayName("findByIdPerson() debe lanzar excepción si no existe persona")
  void findByIdPerson_notFound() {
    when(personRepo.findById(99L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class,
        () -> personService.findByIdPerson(99L, request, userDetails));
  }


  // ============================================================
  // updatePerson()
  // ============================================================

  @Test
  @DisplayName("updatePerson() debe retornar true cuando la transacción es exitosa")
  void updatePerson_ok() {
    PersonDTO dto = new PersonDTO();
    Person personTarget = new Person();
    personTarget.setId(7L);

    lenient().when(personRepo.findById(7L)).thenReturn(Optional.of(personTarget));

    boolean result = personService.updatePerson(7L, dto, request, userDetails);
    assertTrue(result);
  }

  @Test
  @DisplayName("updatePerson() debe lanzar excepción si persona no existe")
  void updatePerson_notFound() {
    PersonDTO dto = new PersonDTO();
    when(personRepo.findById(9L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class,
        () -> personService.updatePerson(9L, dto, request, userDetails));
  }


  // ============================================================
  // deletePerson()
  // ============================================================

  @Test
  @DisplayName("deletePerson() debe retornar true cuando se elimina la persona")
  void deletePerson_ok() {
    Long idPerson = 7L;
    Person person = new Person();
    person.setId(idPerson);
    when(personRepo.findById(idPerson)).thenReturn(Optional.of(person));

    assertTrue(personService.deletePerson(idPerson, request, userDetails));
    verify(personRepo).deleteById(idPerson);
  }

  @Test
  @DisplayName("deletePerson() debe lanzar excepción si persona no existe")
  void deletePerson_notFound() {
    when(personRepo.findById(200L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class,
        () -> personService.deletePerson(200L, request, userDetails));
  }


  // ============================================================
  // getAllPersons()
  // ============================================================

  @Test
  @DisplayName("getAllPersons() debe retornar PagePersonDTO con la lista paginada")
  void getAllPersons_ok() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Person> pagePerson = new PageImpl<>(List.of(new Person()));

    lenient().when(personRepo.findAll(any(Pageable.class))).thenReturn(pagePerson);

    PagePersonDTO dto = personService.getAllPersons(pageable, request, userDetails);
    assertNotNull(dto);
    assertEquals(1, dto.getTotalElements());
  }

  @Test
  @DisplayName("getAllPersons() debe lanzar FunctionalException cuando ocurre RuntimeException y es funcional")
  void getAllPersons_functionalException() {
    PersonService spyService = Mockito.spy(personService);
    Pageable pageable = PageRequest.of(1, 5);

    when(personRepo.findAll(any(Pageable.class))).thenThrow(new RuntimeException("Error DB"));
    doReturn(true).when(spyService).isFunctionalException(any(RuntimeException.class));

    assertThrows(FunctionalException.class,
        () -> spyService.getAllPersons(pageable, request, userDetails));

    verify(spyService).setLogSecurityError(any(RuntimeException.class), any(LogSecurity.class));
  }


  @Test
  @DisplayName("getAllPersons() debe lanzar TechnicalException cuando ocurre RuntimeException NO funcional")
  void getAllPersons_technicalException() {
    PersonService spyService = Mockito.spy(personService);

    Pageable pageable = PageRequest.of(1, 5);

    when(personRepo.findAll(any(Pageable.class))).thenThrow(new RuntimeException("Error DB"));

    // Simulamos que NO es funcional
    doReturn(false).when(spyService).isFunctionalException(any(RuntimeException.class));

    assertThrows(TechnicalException.class,
        () -> spyService.getAllPersons(pageable, request, userDetails));

    verify(spyService).setLogSecurityError(any(RuntimeException.class), any(LogSecurity.class));
  }


  // ============================================================
  // uploadPhotoProfile()
  // ============================================================

  @Test
  @DisplayName("uploadPhotoProfile() debe procesar correctamente sin lanzar excepciones")
  void uploadPhotoProfile_ok() {
    Person person = new Person();
    person.setId(10L);

    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.of("token"));
    when(jwtProvider.extractIdUser("token")).thenReturn(10L);
    when(personRepo.findPersonFromUserId(10L)).thenReturn(Optional.of(person));

    assertDoesNotThrow(() -> personService.uploadPhotoProfile(request, userDetails, multipartFile));
  }

  @Test
  @DisplayName("uploadPhotoProfile() debe lanzar FunctionalException si no se encuentra la persona")
  void uploadPhotoProfile_personNotFound() {
    PersonService spyService = Mockito.spy(personService);

    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.of("token"));
    when(jwtProvider.extractIdUser("token")).thenReturn(99L);
    when(personRepo.findPersonFromUserId(99L)).thenReturn(Optional.empty());

    doReturn(true).when(spyService).isFunctionalException(any(RuntimeException.class));

    assertThrows(FunctionalException.class,
        () -> spyService.uploadPhotoProfile(request, userDetails, multipartFile));

    verify(spyService, times(1)).isFunctionalException(any(RuntimeException.class));
  }

  @Test
  @DisplayName("uploadPhotoProfile() debe lanzar TechnicalException ante error inesperado")
  void uploadPhotoProfile_unexpectedException() {
    when(jwtProvider.extractJwtFromCookie(request)).thenReturn(Optional.of("token"));
    when(jwtProvider.extractIdUser("token")).thenReturn(10L);
    when(personRepo.findPersonFromUserId(10L)).thenThrow(new RuntimeException("DB error"));

    assertThrows(TechnicalException.class,
        () -> personService.uploadPhotoProfile(request, userDetails, multipartFile));
  }


  // ============================================================
  // private isNotExistPerson()
  // ============================================================
  @Test
  @DisplayName("isNotExistPerson() debe lanzar excepción si la persona ya existe")
  void isNotExistPerson_alreadyExist() {
    Person person = new Person();
    person.setFirstName("John");
    person.setMiddleName("A");
    person.setLastName("Doe");
    person.setMiddleLastName("Smith");
    person.setDateOfBirth(LocalDateTime.now());

    when(personRepo.existsByFirstNameAndMiddleNameAndLastNameAndMiddleLastNameAndDateOfBirth(any(),
        any(), any(), any(), any())).thenReturn(true);

    assertThrows(IllegalArgumentException.class,
        () -> ReflectionTestUtils.invokeMethod(personService, "isNotExistPerson", person));
  }

  @Test
  @DisplayName("isNotExistPerson() debe retornar true cuando la persona NO existe")
  void isNotExistPerson_notExist_ok() {
    Person person = new Person();
    when(personRepo.existsByFirstNameAndMiddleNameAndLastNameAndMiddleLastNameAndDateOfBirth(any(),
        any(), any(), any(), any())).thenReturn(false);

    boolean result = ReflectionTestUtils.invokeMethod(personService, "isNotExistPerson", person);

    assertTrue(result);
  }


  // ============================================================
  // private updatePersonRecord()
  // ============================================================
  @Test
  @DisplayName("updatePersonRecord() debe actualizar persona correctamente")
  void updatePersonRecord_ok() {
    Person person = new Person();
    person.setId(50L);

    ReflectionTestUtils.invokeMethod(personService, "updatePersonRecord", person, "adminUser");

    verify(personRepo).save(person);
    assertEquals("adminUser", person.getUserModification());
    assertNotNull(person.getModificationDate());
  }

  // ============================================================
  // private updateUserEvent()
  // ============================================================

  @Test
  @DisplayName("updateUserEvent() debe publicar el evento correctamente")
  void updateUserEvent_ok() {
    Person person = new Person();
    ReflectionTestUtils.invokeMethod(personService, "updateUserEvent", 100L, person, userDetails);

    verify(eventPublisher).publishEvent(any(UpdateUserEvent.class));
  }

  // ============================================================
  // findEntityByIdPerson()
  // ============================================================
  @Test
  @DisplayName("findEntityByIdPerson() debe retornar la entidad si existe")
  void findEntityByIdPerson_ok() {
    Long id = 1L;
    Person person = new Person();
    person.setId(id);

    when(personRepo.findById(id)).thenReturn(Optional.of(person));

    Person result = personService.findEntityByIdPerson(id);

    assertNotNull(result);
    assertEquals(id, result.getId());
    verify(personRepo).findById(id);
  }

  @Test
  @DisplayName("findEntityByIdPerson() debe lanzar EntityNotFoundException si no existe")
  void findEntityByIdPerson_notFound() {
    Long id = 99L;
    when(personRepo.findById(id)).thenReturn(Optional.empty());

    EntityNotFoundException exception =
        assertThrows(EntityNotFoundException.class, () -> personService.findEntityByIdPerson(id));

    String expectedMessage =
        SecurityExceptionEnum.FUNC_PERSON_NOT_FOUND.getCodeMessage(id.toString());
    assertEquals(expectedMessage, exception.getMessage());
    verify(personRepo).findById(id);
  }


  // ============================================================
  // deleteUserEvent()
  // ============================================================
  @Test
  @DisplayName("deleteUserEvent() debe publicar evento cuando idUser existe")
  void deleteUserEvent_publicaEvento() {
    Long idPerson = 5L;
    when(userDetails.getUsername()).thenReturn("testerUser");

    when(personRepo.findIdPersonByIdUser(idPerson)).thenReturn("123");
    ReflectionTestUtils.invokeMethod(personService, "deleteUserEvent", idPerson, userDetails);
    verify(eventPublisher, times(1)).publishEvent(any(DeleteUserEvent.class));
  }


  // ============================================================
  // getPersonSource()
  // ============================================================
  @Test
  @DisplayName("getPersonSource() debe asignar PersonSex si idSex no es null")
  void getPersonSource_assignPersonSex() {
    // Arrange
    PersonDTO dto = new PersonDTO();
    dto.setIdSex(5L);

    PersonSex mockSex = new PersonSex();
    mockSex.setId(5L);

    when(personSexService.findPersonSexById(5L)).thenReturn(mockSex);

    Person person = personService.getPersonSource(dto);
    assertNotNull(person.getPersonSex());
    assertEquals(5L, person.getPersonSex().getId());
    verify(personSexService).findPersonSexById(5L);
  }
}
