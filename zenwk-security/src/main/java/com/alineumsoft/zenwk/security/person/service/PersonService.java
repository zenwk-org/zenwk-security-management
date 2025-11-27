package com.alineumsoft.zenwk.security.person.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.alineumsoft.zenwk.security.auth.jwt.JwtProvider;
import com.alineumsoft.zenwk.security.common.constants.CommonMessageConstants;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.common.exception.TechnicalException;
import com.alineumsoft.zenwk.security.common.helper.ApiRestSecurityHelper;
import com.alineumsoft.zenwk.security.common.hist.enums.HistoricalOperationEnum;
import com.alineumsoft.zenwk.security.common.util.HistoricalUtil;
import com.alineumsoft.zenwk.security.common.util.LocalDateTimeUtil;
import com.alineumsoft.zenwk.security.common.util.ObjectUpdaterUtil;
import com.alineumsoft.zenwk.security.constants.ServiceControllerConstants;
import com.alineumsoft.zenwk.security.dto.PaginatorDTO;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.enums.RoleEnum;
import com.alineumsoft.zenwk.security.enums.SecurityActionEnum;
import com.alineumsoft.zenwk.security.enums.SecurityExceptionEnum;
import com.alineumsoft.zenwk.security.enums.UserPersonEnum;
import com.alineumsoft.zenwk.security.enums.UserStateEnum;
import com.alineumsoft.zenwk.security.person.dto.CreatePersonDTO;
import com.alineumsoft.zenwk.security.person.dto.PagePersonDTO;
import com.alineumsoft.zenwk.security.person.dto.PersonDTO;
import com.alineumsoft.zenwk.security.person.entity.Person;
import com.alineumsoft.zenwk.security.person.entity.PersonSex;
import com.alineumsoft.zenwk.security.person.repository.PersonRepository;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import com.alineumsoft.zenwk.security.user.dto.UserDTO;
import com.alineumsoft.zenwk.security.user.event.DeleteUserEvent;
import com.alineumsoft.zenwk.security.user.event.UpdateUserEvent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonService extends ApiRestSecurityHelper {
  /**
   * Repositorio de la persona
   */
  private final PersonRepository personRepo;
  /**
   * Repositorio para log persistible de modulo
   */
  private final LogSecurityRepository logSecUserRespo;
  /**
   * Template para transaccion
   */
  private final TransactionTemplate templateTx;
  /**
   * Interfaz para publicacion de evento
   */
  private final ApplicationEventPublisher eventPublisher;
  /**
   * Manejador de jwt
   */
  private final JwtProvider jwtProvider;
  /*
   * Servicio para el sexo de la persona.
   */
  private final PersonSexService personSexService;


  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Metodo de servicio que crea una persona si cumple con
   * las validaciones
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param inDTOE
   * @param request
   * @param userDetails
   * 
   * @return
   */
  public Long createPerson(CreatePersonDTO dto, HttpServletRequest request,
      UserDetails userDetails) {
    LogSecurity logSecurity = initializeLog(request, userDetails.getUsername(), getJson(dto),
        CommonMessageConstants.NOT_APPLICABLE_BODY, SecurityActionEnum.PERSON_CREATE.getCode());
    try {
      // Validacion funcional de la pesona a registrar
      validateUser(dto, request);
      Person person = createPersonTx(dto, logSecurity, userDetails);

      return Objects.requireNonNull(person).getId();

    } catch (RuntimeException e) {
      setLogSecurityError(e, logSecurity);
      if (isFunctionalException(e)) {
        throw new FunctionalException(e.getMessage(), e.getCause(), logSecUserRespo, logSecurity);
      }
      throw new TechnicalException(e.getMessage(), e.getCause(), logSecUserRespo, logSecurity);
    }
  }

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Valida los datos antes de registrar la persona
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @param request
   */
  private void validateUser(CreatePersonDTO dto, HttpServletRequest request) {
    String token =
        jwtProvider.extractJwtFromCookie(request).orElseThrow(EntityNotFoundException::new);
    Long idUser = jwtProvider.extractIdUser(token);
    List<String> roles = jwtProvider.extractAuthorities(token);
    UserStateEnum userState = jwtProvider.extractUserState(token);
    // Si el contiene el rol NEW_USER, el estado es incompleto y el id del usuario
    // no corresponde con la sesion
    // genera excepcion funcional
    if (!idUser.equals(dto.getIdUser()) && roles.contains(RoleEnum.NEW_USER.name())
        && UserStateEnum.INCOMPLETE_PERFIL.equals(userState)) {
      throw new IllegalArgumentException(
          SecurityExceptionEnum.FUNC_PERSON_ID_USER_NOT_ASSOCIATE.getCodeMessage());
    }

    // Si el usuario ya tiene una persona asociada no se puede crear esa persona es
    // neceario indicar otro id, se generar excepcion funcional
    if (UserStateEnum.ACTIVE.equals(userState)) {
      throw new IllegalArgumentException(
          SecurityExceptionEnum.FUNC_PERSON_ID_USER_IS_ASSOCIATE.getCodeMessage());
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Metodo se servicio que actualiza una persona
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idPerson
   * @param dto
   * @param request
   * @param userDetails
   * @return
   */
  public boolean updatePerson(Long idPerson, PersonDTO dto, HttpServletRequest request,
      UserDetails userDetails) {
    LogSecurity logSecurity = initializeLog(request, userDetails.getUsername(), getJson(dto),
        CommonMessageConstants.NOT_APPLICABLE_BODY, SecurityActionEnum.PERSON_UPDATE.getCode());
    try {
      return updatePersonTx(idPerson, dto, userDetails.getUsername(), logSecurity);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSecurity);
      if (isFunctionalException(e)) {
        throw new FunctionalException(e.getMessage(), e.getCause(), logSecUserRespo, logSecurity);
      }
      throw new TechnicalException(e.getMessage(), e.getCause(), logSecUserRespo, logSecurity);
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Metodo de servicio que elmina una persona
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idPerson
   * @param request
   * @param userDetails
   * 
   * @return
   */
  public boolean deletePerson(Long idPerson, HttpServletRequest request, UserDetails userDetails) {
    LogSecurity logSecurity = initializeLog(request, userDetails.getUsername(),
        CommonMessageConstants.NOT_APPLICABLE_BODY, CommonMessageConstants.NOT_APPLICABLE_BODY,
        SecurityActionEnum.PERSON_DELETE.getCode());
    try {
      // request = null, se carga en UserEventListener.handleUserDeleteEvent()
      return deletePersonTx(idPerson, logSecurity, userDetails, request != null);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSecurity);
      if (isFunctionalException(e)) {
        throw new FunctionalException(e.getMessage(), e.getCause(), logSecUserRespo, logSecurity);
      }
      throw new TechnicalException(e.getMessage(), e.getCause(), logSecUserRespo, logSecurity);
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Realiza la transaccion para la creacion de la persona
   * en caso de error hace rollback y actualiza el log del modulo de seguridad
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @param logSecurity
   * 
   * @param userDetails
   * @return
   */
  private Person createPersonTx(CreatePersonDTO dto, LogSecurity logSecurity,
      UserDetails userDetails) {
    return templateTx.execute(transaction -> {
      try {
        Person person = createPersonRecord(dto, logSecurity);
        // Se asocia la persona la usuario
        updateUserEvent(dto.getIdUser(), person, userDetails);
        return person;
      } catch (ConstraintViolationException e) {
        throw new IllegalArgumentException(e.getMessage());
      }
    });
  }

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Realiza la transaccion para la actualizacion de la
   * persona en caso de error hace rollback y actualiza el log del modulo
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idPerson
   * @param dto
   * @param name
   * @param logSecurity
   * 
   * @return
   */
  private boolean updatePersonTx(Long idPerson, PersonDTO dto, String name,
      LogSecurity logSecurity) {
    Person personTarget =
        personRepo.findById(idPerson).orElseThrow(() -> new EntityNotFoundException(
            SecurityExceptionEnum.FUNC_PERSON_NOT_FOUND.getCodeMessage(idPerson.toString())));
    Boolean result = templateTx.execute(transaction -> {
      try {
        if (ObjectUpdaterUtil.updateDataEqualObject(getPersonSource(dto), personTarget)) {
          updatePersonRecord(personTarget, name);
        }
        saveSuccessLog(HttpStatus.NO_CONTENT.value(), logSecurity, logSecUserRespo);
        return true;
      } catch (RuntimeException e) {
        transaction.setRollbackOnly();
        throw new IllegalArgumentException(getMessageSQLException(e));
      }
    });

    if (result == null) {
      throw new IllegalStateException("Transaction result is null for updatePersonTx");
    }
    return result;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> SRealiza la transaccion para la eliminacion de la
   * persona en caso de error hace rollback y actualiza el log del modulo
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idPerson
   * @param logSecurity
   * @param userDetails
   * @param invokedByListener
   * @return
   */
  private boolean deletePersonTx(Long idPerson, LogSecurity logSecurity, UserDetails userDetails,
      boolean invokedByListener) {
    // Se ejecuta la transaccion
    Boolean isDelete = templateTx.execute(transaction -> {
      try {
        return deletePersonRecord(idPerson, logSecurity);
      } catch (RuntimeException e) {
        transaction.setRollbackOnly();
        throw new IllegalArgumentException(getMessageSQLException(e));
      }
    });
    // ELminacion del usuario a travez del evento, si aplica
    if (invokedByListener) {
      deleteUserEvent(idPerson, userDetails);
    }
    if (isDelete == null) {
      throw new IllegalStateException("Transaction result is null for deletePersonTx");
    }
    return isDelete;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Realiza la creacioo de la persona en la BD
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @param logSecurity
   * 
   * @return
   */
  private Person createPersonRecord(CreatePersonDTO dto, LogSecurity logSecurity) {
    Person person = getPersonSource(dto);
    person.setUserCreation(logSecurity.getUserCreation());
    person.setCreationDate(LocalDateTime.now());
    // Si la person no existe se persiste
    isNotExistPerson(person);
    person = personRepo.save(person);
    // Persistencia de logs
    HistoricalUtil.registerHistorical(person, HistoricalOperationEnum.INSERT,
        PersonHistService.class);
    saveSuccessLog(HttpStatus.CREATED.value(), logSecurity, logSecUserRespo);

    return person;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> ealiza la actualizacion de la persona en la BD
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param personTarget
   * @param userModification
   * @param logSecurity
   * 
   * @return
   */
  private void updatePersonRecord(Person personTarget, String userModification) {
    personTarget.setUserModification(userModification);
    personTarget.setModificationDate(LocalDateTime.now());
    isNotExistPersonForEdit(personTarget);
    personRepo.save(personTarget);
    HistoricalUtil.registerHistorical(personTarget, HistoricalOperationEnum.UPDATE,
        PersonHistService.class);
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Realiza la eliminacion de la persona en la BD
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idPerson
   * @param logSecurity
   * 
   * @return
   */
  private Boolean deletePersonRecord(Long idPerson, LogSecurity logSecurity) {
    Person person = personRepo.findById(idPerson).orElseThrow(() -> new EntityNotFoundException(
        SecurityExceptionEnum.FUNC_PERSON_NOT_FOUND.getCodeMessage(idPerson.toString())));
    personRepo.deleteById(idPerson);
    HistoricalUtil.registerHistorical(person, HistoricalOperationEnum.DELETE,
        PersonHistService.class);
    saveSuccessLog(HttpStatus.NO_CONTENT.value(), logSecurity, logSecUserRespo);
    return true;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Gener un entity persona a partir de un inDTO
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @return
   */
  public Person getPersonSource(PersonDTO dto) {
    Person person = new Person();

    // Recuperamos el sexo.
    if (dto.getIdSex() != null) {
      PersonSex personSex = personSexService.findPersonSexById(dto.getIdSex());
      person.setPersonSex(personSex);
    }

    // procesamos la imagen del perfil del usuario
    if (dto.getProfilePicture() != null) {
      try {
        person.setProfilePicture(dto.getProfilePicture());
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    }
    person.setFirstName(dto.getFirstName());
    person.setMiddleName(dto.getMiddleName());
    person.setLastName(dto.getLastName());
    person.setMiddleLastName(dto.getMiddleLastName());
    person.setDateOfBirth(person.getDateOfBirth() != null
        ? LocalDateTimeUtil.getLocalDateTimeIso8601(dto.getDateOfBirth())
        : null);
    person.setAddress(dto.getAddress());
    person.setAge(dto.getAge());

    return person;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Recupera el mensaje predefinido para para el error
   * datos a nivel de BD
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param e
   * @return
   */
  private String getMessageSQLException(Exception e) {
    if (e.getLocalizedMessage().contains(ServiceControllerConstants.SQL_MESSAGE_EMAIL_EXISTS)) {
      return SecurityExceptionEnum.FUNC_USER_MAIL_EXISTS.getCodeMessage();
    }

    return e.getMessage();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Busqueda de persona por id en caso de error se
   * persiste en log de modulo
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idPerson
   * @param request
   * @param userDetails
   * 
   * @return
   */
  public CreatePersonDTO findByIdPerson(Long idPerson, HttpServletRequest request,
      UserDetails userDetails) {
    LogSecurity logSecurity = initializeLog(request, userDetails.getUsername(),
        CommonMessageConstants.NOT_APPLICABLE_BODY, CommonMessageConstants.NOT_APPLICABLE_BODY,
        SecurityActionEnum.PERSON_GET.getCode());
    try {
      Person person = personRepo.findById(idPerson).orElseThrow(() -> new EntityNotFoundException(
          SecurityExceptionEnum.FUNC_PERSON_NOT_FOUND.getCodeMessage(idPerson.toString())));
      saveSuccessLog(HttpStatus.OK.value(), logSecurity, logSecUserRespo);
      return new CreatePersonDTO(person);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSecurity);
      if (isFunctionalException(e)) {
        throw new FunctionalException(e.getMessage(), e.getCause(), logSecUserRespo, logSecurity);
      }
      throw new TechnicalException(e.getMessage(), e.getCause(), logSecUserRespo, logSecurity);
    }

  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Recupera la entidad person a partir de su id en caso
   * contrario glanza excepcion
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idPerson
   * @return
   */
  public Person findEntityByIdPerson(Long id) {
    return personRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(
        SecurityExceptionEnum.FUNC_PERSON_NOT_FOUND.getCodeMessage(id.toString())));
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Recuperación de todos las personas almacenas en el
   * sistema paginados, esto solo debe ser accedido por rol admin
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pageable
   * @param request
   * @param userDetails
   * @return
   */
  public PagePersonDTO getAllPersons(Pageable pageable, HttpServletRequest request,
      UserDetails userDetails) {
    LogSecurity logSecurity = initializeLog(request, userDetails.getUsername(),
        CommonMessageConstants.NOT_APPLICABLE_BODY, CommonMessageConstants.NOT_APPLICABLE_BODY,
        SecurityActionEnum.PERSON_LIST.getCode());
    try {
      int pageNumber = pageable.getPageNumber() > 0 ? pageable.getPageNumber() - 1 : 0;
      Page<Person> pagePerson = personRepo.findAll(PageRequest.of(pageNumber,
          pageable.getPageSize(),
          pageable.getSortOr(
              Sort.by(Sort.Direction.ASC, UserPersonEnum.PERSON_LAST_NAME.getMessageKey()).and(
                  Sort.by(Sort.Direction.ASC, UserPersonEnum.PERSON_FIRST_NAME.getMessageKey())))));

      return getFindAll(pagePerson, logSecurity);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSecurity);
      if (isFunctionalException(e)) {
        throw new FunctionalException(e.getMessage(), e.getCause(), logSecUserRespo, logSecurity);
      }
      throw new TechnicalException(e.getMessage(), e.getCause(), logSecUserRespo, logSecurity);
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Obtiene la lista de personas en caso de ser vacia
   * excepciona
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pagePerson
   * @param logSecurity
   * @return
   */
  private PagePersonDTO getFindAll(Page<Person> pagePerson, LogSecurity logSecurity) {
    List<CreatePersonDTO> listPeople = pagePerson.stream().map(CreatePersonDTO::new).toList();
    saveSuccessLog(HttpStatus.OK.value(), logSecurity, logSecUserRespo);
    return new PagePersonDTO(listPeople, new PaginatorDTO(pagePerson.getTotalElements(),
        pagePerson.getTotalPages(), pagePerson.getNumber() + 1));
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Publicacion de los eventos buscar y eliminar un
   * usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idPerson
   * @param userDetails
   */
  private void deleteUserEvent(Long idPerson, UserDetails userDetails) {
    Object idUser = personRepo.findIdPersonByIdUser(idPerson);
    if (idUser != null) {
      // Evento para la eliminacion del usuario
      DeleteUserEvent eventDelete = new DeleteUserEvent(this,
          Long.parseLong(sanitize(idUser.toString())), sanitizeUserDetails(userDetails));
      eventPublisher.publishEvent(eventDelete);
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Evento para la actualizacion del usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @param person
   * @param userDetails
   */
  private void updateUserEvent(Long idUser, Person person, UserDetails userDetails) {
    UserDTO dto = new UserDTO();
    dto.setPerson(person);
    UpdateUserEvent event = new UpdateUserEvent(this, idUser, dto, userDetails, person);
    eventPublisher.publishEvent(event);
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Valida si el request usado para la creacion de la
   * persona ya existe
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param person
   * @return
   */
  public boolean isNotExistPerson(Person person) {
    if (personRepo.existsByFirstNameAndMiddleNameAndLastNameAndMiddleLastNameAndDateOfBirth(
        person.getFirstName(), person.getMiddleName(), person.getLastName(),
        person.getMiddleLastName(), person.getDateOfBirth())) {
      throw new IllegalArgumentException(SecurityExceptionEnum.FUNC_PERSON_EXIST.getCodeMessage());
    }
    return true;
  }



  /**
   * <p>
   * <b>CU001_Seguridad_Creacion_Usuario </b> Gestiona la carga de la foto de perfil de usuario.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param userDetails
   * @param file
   */
  public void uploadPhotoProfile(HttpServletRequest request, UserDetails userDetails,
      MultipartFile file) {
    LogSecurity logSecurity = initializeLog(request, userDetails.getUsername(),
        CommonMessageConstants.NOT_APPLICABLE_BODY, CommonMessageConstants.NOT_APPLICABLE_BODY,
        SecurityActionEnum.PERSON_UPLOAD_PHOTO_PROFILE.getCode());
    try {
      String token = jwtProvider.extractJwtFromCookie(request).orElseThrow();
      Long idUser = jwtProvider.extractIdUser(token);
      Person person =
          personRepo.findPersonFromUserId(idUser).orElseThrow(() -> new EntityNotFoundException(
              SecurityExceptionEnum.FUNC_PERSON_NOT_FOUND.getMessage()));

      savePhotoProfile(file, person);
      saveSuccessLog(HttpStatus.NO_CONTENT.value(), logSecurity, logSecUserRespo);

    } catch (RuntimeException e) {
      if (isFunctionalException(e)) {
        throw new FunctionalException(e.getMessage(), e.getCause(), logSecUserRespo, logSecurity);
      }
      throw new TechnicalException(e.getMessage(), e.getCause(), logSecUserRespo, logSecurity);
    }
  }


  /**
   * 
   * <p>
   * <b>CU001_Seguridad_Creacion_Usuario </b> Actualiza la información en la entidad persona.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param file
   * @param person
   */
  private void savePhotoProfile(MultipartFile file, Person person) {
    try {
      person.setProfilePicture(file.getBytes());
      personRepo.save(person);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);

    }

  }


  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Valida si el request usado para la creacion de la
   * persona ya existe, se usa solo en la edición.Excluye el mismo registro.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param person
   * @return
   */
  public boolean isNotExistPersonForEdit(Person person) {
    if (personRepo.existsByFirstNameAndMiddleNameAndLastNameAndMiddleLastNameAndDateOfBirthAndIdNot(
        person.getFirstName(), person.getMiddleName(), person.getLastName(),
        person.getMiddleLastName(), person.getDateOfBirth(), person.getId())) {
      throw new IllegalArgumentException(SecurityExceptionEnum.FUNC_PERSON_EXIST.getCodeMessage());
    }
    return true;
  }
}
