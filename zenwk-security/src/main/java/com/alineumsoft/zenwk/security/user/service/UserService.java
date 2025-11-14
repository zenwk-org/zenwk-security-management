package com.alineumsoft.zenwk.security.user.service;

import java.awt.FontFormatException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import com.alineumsoft.zenwk.security.auth.dto.RoleUserDTO;
import com.alineumsoft.zenwk.security.auth.jwt.JwtProvider;
import com.alineumsoft.zenwk.security.auth.service.RoleAssignmentService;
import com.alineumsoft.zenwk.security.common.constants.CommonMessageConstants;
import com.alineumsoft.zenwk.security.common.constants.GeneralConstants;
import com.alineumsoft.zenwk.security.common.constants.RegexConstants;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.common.exception.TechnicalException;
import com.alineumsoft.zenwk.security.common.exception.enums.CoreExceptionEnum;
import com.alineumsoft.zenwk.security.common.helper.ApiRestSecurityHelper;
import com.alineumsoft.zenwk.security.common.hist.enums.HistoricalOperationEnum;
import com.alineumsoft.zenwk.security.common.util.CryptoUtil;
import com.alineumsoft.zenwk.security.common.util.HistoricalUtil;
import com.alineumsoft.zenwk.security.common.util.ObjectUpdaterUtil;
import com.alineumsoft.zenwk.security.constants.ServiceControllerConstants;
import com.alineumsoft.zenwk.security.dto.PaginatorDTO;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.entity.Role;
import com.alineumsoft.zenwk.security.entity.RoleUser;
import com.alineumsoft.zenwk.security.enums.RoleEnum;
import com.alineumsoft.zenwk.security.enums.SecurityActionEnum;
import com.alineumsoft.zenwk.security.enums.SecurityExceptionEnum;
import com.alineumsoft.zenwk.security.enums.UserPersonEnum;
import com.alineumsoft.zenwk.security.enums.UserStateEnum;
import com.alineumsoft.zenwk.security.person.entity.Person;
import com.alineumsoft.zenwk.security.person.event.PersonDeleteEvent;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import com.alineumsoft.zenwk.security.user.dto.PageUserDTO;
import com.alineumsoft.zenwk.security.user.dto.UserDTO;
import com.alineumsoft.zenwk.security.user.entity.User;
import com.alineumsoft.zenwk.security.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class UserService
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService extends ApiRestSecurityHelper {
  /**
   * Repositorio para user
   */
  private final UserRepository userRepository;
  /**
   * Repositorio para log persistible de modulo
   */
  private final LogSecurityRepository logSecurityUserRespository;
  /**
   * Template para transaccion
   */
  private final TransactionTemplate templateTx;
  /**
   * Interfaz para publicacion de evento
   */
  private final ApplicationEventPublisher eventPublisher;
  /**
   * Servicio la gestión en el asignamiento de roles
   */
  private final RoleAssignmentService roleAsignmentService;

  /**
   * Manejador de jwt
   */
  private final JwtProvider jwtProvider;


  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Metodo de servicio que crea un nuevo usuario si
   * cumple con las validaciones
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @param userDetails
   * 
   * @return
   * @throws JsonProcessingException
   */
  public Long createUser(UserDTO dto, HttpServletRequest request) {
    // inicializacion log transaccional
    LogSecurity logSecurity = initializeLog(request, dto.getUsername(), getJson(dto), notBody,
        SecurityActionEnum.USER_CREATE.getCode());
    try {
      return createUserTx(dto, logSecurity);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSecurity);
      if (isFunctionalException(e)) {
        throw new FunctionalException(e.getMessage(), e.getCause(), logSecurityUserRespository,
            logSecurity);
      }
      throw new TechnicalException(e.getMessage(), e.getCause(), logSecurityUserRespository,
          logSecurity);
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Actualizacion general de usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param idUser
   * @param dto
   * @param userDetails
   * @oaram person
   */
  public boolean updateUser(HttpServletRequest request, Long idUser, UserDTO dto,
      UserDetails userDetails, Person person) {
    String username = userDetails != null ? userDetails.getUsername() : dto.getUsername();
    // Inicializacion log transaccional
    LogSecurity logSecurity = initializeLog(request, username, getJson(dto), notBody,
        SecurityActionEnum.USER_UPDATE.getCode());
    try {
      return updateUserTx(idUser, dto, person, logSecurity);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSecurity);
      if (isFunctionalException(e)) {
        throw new FunctionalException(e.getMessage(), e.getCause(), logSecurityUserRespository,
            logSecurity);
      }
      throw new TechnicalException(e.getMessage(), e.getCause(), logSecurityUserRespository,
          logSecurity);
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Se realiza un borrado fisico del usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @param request
   * @param userDetails
   * @return
   * @throws JsonProcessingException
   */
  public boolean deleteUser(Long idUser, HttpServletRequest request, UserDetails userDetails) {
    LogSecurity logSecurity = initializeLog(request, userDetails.getUsername(), notBody, notBody,
        SecurityActionEnum.USER_DELETE.getCode());
    try {
      // request = null, se carga en PersonEventListener.handlePersonDeleteEvent()
      return deleteUserTx(idUser, logSecurity, request != null, userDetails);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSecurity);
      if (isFunctionalException(e)) {
        throw new FunctionalException(e.getMessage(), e.getCause(), logSecurityUserRespository,
            logSecurity);
      }
      throw new TechnicalException(e.getMessage(), e.getCause(), logSecurityUserRespository,
          logSecurity);
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Realiza la transaccion para la eliminacion de un
   * usuario en caso de error hace rollback y actualiza el log del modulo de seguridad
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @param logSecurity
   * @param userDetails
   * @param invokedByListener
   * @return
   */
  private boolean deleteUserTx(Long idUser, LogSecurity logSecurity, boolean invokedByListener,
      UserDetails userDetails) {
    // Se valida si es necesario eliminar la relacion con Person
    if (invokedByListener) {
      deletePersonEvent(userDetails, idUser);
    }
    // Se recupera el usuario.
    User user = userRepository.findById(idUser).orElseThrow(() -> new EntityNotFoundException(
        SecurityExceptionEnum.FUNC_USER_NOT_FOUND_ID.getCodeMessage(idUser.toString())));
    // Se ejecuta la transaccion
    Boolean result = templateTx.execute(transaction -> {
      try {
        return deleteUserRecord(idUser, logSecurity, user);
      } catch (RuntimeException e) {
        transaction.setRollbackOnly();
        throw new EntityNotFoundException(getMessageSQLException(e));
      }
    });

    if (result == null) {
      throw new IllegalStateException("Transaction result is null for deleteUserTx");
    }
    return result;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Elimina el registro del usuario en cascada en la BD
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @param logSecurity
   * @param user
   * @return
   */
  private Boolean deleteUserRecord(Long idUser, LogSecurity logSecurity, User user) {
    // Eliminacion de los permisos del usuario
    roleAsignmentService.deleteRoleUserByIdUser(idUser);
    userRepository.deleteById(idUser);
    // Persistencia de historico
    HistoricalUtil.registerHistorical(user, HistoricalOperationEnum.DELETE, UserHistService.class);
    // Pesistencia de log
    saveSuccessLog(HttpStatus.NO_CONTENT.value(), logSecurity, logSecurityUserRespository);
    return true;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Realiza la transaccion para la creacion de un usuario
   * en caso de error hace rollback y actualiza el log del modulo de seguridad
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @param logSecurity
   * 
   * @param person
   * @return
   */
  private Long createUserTx(UserDTO dto, LogSecurity logSecurity) {
    return templateTx.execute(transaction -> {
      try {
        return createUserRecord(dto, logSecurity).getId();
      } catch (RuntimeException e) {
        transaction.setRollbackOnly();
        throw new IllegalArgumentException(getMessageSQLException(e));
      }
    });
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Realiza la transaccion para la actualizacion de un
   * usuario en caso de error hace rollback y actualiza el log del modulo de seguridad
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @param dto
   * @param person
   * @param logSecurity
   * @return
   * @throws SQLException
   */
  private boolean updateUserTx(Long idUser, UserDTO dto, Person person, LogSecurity logSecurity) {
    User userTarget = userRepository.findById(idUser).orElseThrow(() -> new EntityNotFoundException(
        SecurityExceptionEnum.FUNC_USER_NOT_FOUND_ID.getCodeMessage(idUser.toString())));
    // Se da inicio a la transccion de actualizacion

    Boolean result = templateTx.execute(transaction -> {
      try {
        updateUserRecord(userTarget, idUser, dto, person, logSecurity);
        return true;
      } catch (RuntimeException e) {
        transaction.setRollbackOnly();
        throw new EntityNotFoundException(e);
      }
    });

    if (result == null) {
      throw new IllegalStateException("Transaction result is null for updateUserTx");
    }
    return result;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Realiza la actualizacion de la persona y el usuario
   * en la BD.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param userTarget
   * @param idUser
   * @param dto
   * @param person
   * @param logSecurity
   */
  private void updateUserRecord(User userTarget, Long idUser, UserDTO dto, Person person,
      LogSecurity logSecurity) {
    // actualizacion de la persona, el estado y el rol en entidad usuario por primerva vez.
    // la persona es dirente de null
    updateFromPerson(userTarget, idUser, dto, person, logSecurity);

    // Se realiza la validación si updateUser no es invocado desde evento en personController
    if (person == null
        && ObjectUpdaterUtil.updateDataEqualObject(getUserSource(dto, userTarget), userTarget)) {
      updateEntityUser(userTarget, logSecurity);
    }
    // Pesistencia de log
    saveSuccessLog(HttpStatus.NO_CONTENT.value(), logSecurity, logSecurityUserRespository);
  }

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Actualización de la persona desde create api/person
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param userTarget
   * @param idUser
   * @param dto
   * @param person
   * @param logSecurity
   */
  private void updateFromPerson(User userTarget, Long idUser, UserDTO dto, Person person,
      LogSecurity logSecurity) {
    if (person != null) {
      RoleUserDTO roleUserDTO =
          new RoleUserDTO(null, RoleEnum.USER.name(), idUser, userTarget.getUsername());
      userTarget.setPerson(person);
      userTarget.setState(UserStateEnum.ACTIVE);
      // Rol por defecto
      roleAsignmentService.createRoleUser(null, roleUserDTO);
      dto.setIdPerson(person.getId());
      dto.setPerson(person);
      updateEntityUser(userTarget, logSecurity);
    }
  }



  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Método que actualiza la entidad del usuario, compara
   * a nivel de campos si hay diferencias realiza el update
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param userTarget
   * @param logSecurity
   */
  private void updateEntityUser(User userTarget, LogSecurity logSecurity) {
    userTarget.setUserModification(logSecurity.getUserCreation());
    userTarget.setModificationDate(LocalDateTime.now());
    userRepository.save(userTarget);
    HistoricalUtil.registerHistorical(userTarget, HistoricalOperationEnum.UPDATE,
        UserHistService.class);
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Obtiene un User desde su dto
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @param userTarget
   * @return
   */
  private User getUserSource(UserDTO dto, User userTarget) {
    User user = new User();
    if (CryptoUtil.matchesPassword(dto.getPassword(), userTarget.getPassword())) {
      user.setPassword(userTarget.getPassword());
    } else {
      user.setPassword(CryptoUtil.encryptPassword(dto.getPassword()));
    }
    user.setUsername(dto.getUsername());
    user.setEmail(dto.getEmail());
    user.setState(dto.getState() != null ? dto.getState() : userTarget.getState());
    user.setPerson(userTarget.getPerson());
    return user;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Recuperacion del usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @param request
   * @param userDetails
   * @return
   */
  public UserDTO findByIdUser(Long idUser, HttpServletRequest request, UserDetails userDetails) {
    LogSecurity logSecurity = initializeLog(request, userDetails.getUsername(), notBody, notBody,
        SecurityActionEnum.USER_GET.getCode());
    try {
      User user = userRepository.findById(idUser).orElseThrow(() -> new EntityNotFoundException(
          SecurityExceptionEnum.FUNC_USER_NOT_FOUND_ID.getCodeMessage(idUser.toString())));
      // Pesistencia de log
      saveSuccessLog(HttpStatus.OK.value(), logSecurity, logSecurityUserRespository);
      // se oculta el password
      user.setPassword(GeneralConstants.VALUE_SENSITY_MASK);
      return new UserDTO(user);
    } catch (EntityNotFoundException e) {
      setLogSecurityError(e, logSecurity);
      throw new FunctionalException(e.getMessage(), e.getCause(), logSecurityUserRespository,
          logSecurity);
    }

  }



  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Recuperacion del usuario autenticado desde el jwt
   * almacenado en la cookie
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @param request
   * @param userDetails
   * @return
   */
  public UserDTO getCurrentUser(HttpServletRequest request, UserDetails userDetails) {
    LogSecurity logSecurity = initializeLog(request, userDetails.getUsername(), notBody, notBody,
        SecurityActionEnum.USER_ME_JWT.getCode());
    try {
      String token = jwtProvider.extractJwtFromCookie(request).orElseThrow();
      Long idUser = jwtProvider.extractIdUser(token);
      User user = userRepository.findById(idUser).orElseThrow(() -> new EntityNotFoundException(
          SecurityExceptionEnum.FUNC_USER_NOT_FOUND_ID.getCodeMessage(idUser.toString())));
      // Pesistencia de log
      saveSuccessLog(HttpStatus.OK.value(), logSecurity, logSecurityUserRespository);
      // se oculta el password
      user.setPassword(GeneralConstants.VALUE_SENSITY_MASK);
      return new UserDTO(user);
    } catch (EntityNotFoundException e) {
      setLogSecurityError(e, logSecurity);
      throw new FunctionalException(e.getMessage(), e.getCause(), logSecurityUserRespository,
          logSecurity);
    }

  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Recupera la entidad user con su id en caso de que no
   * exista retorna null
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idPerson
   * @return
   */
  public User findUserByIdPerson(Long idPerson) {
    return userRepository.finByIdPerson(idPerson);
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Recuperación de todos los usuarios paginados, esto
   * solo debe ser accedido por rol admin
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pageable
   * @param request
   * @param userDetails
   * @return
   */
  public PageUserDTO getAllUsers(Pageable pageable, HttpServletRequest request,
      UserDetails userDetails) {
    LogSecurity logSecurity = initializeLog(request, userDetails.getUsername(), notBody, notBody,
        SecurityActionEnum.USER_LIST.getCode());
    try {
      // Conteo paginacion en 1
      int pageNumber = pageable.getPageNumber() > 0 ? pageable.getPageNumber() - 1 : 0;
      // Consulta paginada y ordenamiento
      Page<User> pageUser = userRepository.findAll(PageRequest.of(pageNumber,
          pageable.getPageSize(),
          pageable.getSortOr(Sort.by(Sort.Direction.ASC, UserPersonEnum.USER_EMAIL.getMessageKey())
              .and(Sort.by(Sort.Direction.ASC, UserPersonEnum.USER_USERNAME.getMessageKey())))));
      return getFindAll(pageUser, logSecurity);
    } catch (RuntimeException e) {
      log.error(CommonMessageConstants.FORMAT_EXCEPTION, e);
      setLogSecurityError(e, logSecurity);
      throw new TechnicalException(e.getMessage(), e.getCause(), logSecurityUserRespository,
          logSecurity);
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Obtiene la lista de usuarios en caso de ser vacia
   * excepciona
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pagePerson
   * @return
   */
  private PageUserDTO getFindAll(Page<User> pageUser, LogSecurity logSecurity) {
    List<UserDTO> listUser = pageUser.stream().map(user -> {
      UserDTO dto = new UserDTO(user);
      dto.setPassword(GeneralConstants.VALUE_SENSITY_MASK);
      return dto;
    }).toList();

    // Pesistencia de log
    saveSuccessLog(HttpStatus.OK.value(), logSecurity, logSecurityUserRespository);
    return new PageUserDTO(listUser, new PaginatorDTO(pageUser.getTotalElements(),
        pageUser.getTotalPages(), pageUser.getNumber() + 1));
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
    if (e.getMessage().contains(ServiceControllerConstants.SQL_MESSAGE_EMAIL_EXISTS)) {
      return SecurityExceptionEnum.FUNC_USER_MAIL_EXISTS.getCodeMessage();
    }
    return e.getMessage();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Persistencia del registro user
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @param logSecurity
   * @return
   */
  private User createUserRecord(UserDTO dto, LogSecurity logSecurity) {
    User user = new User();
    user.setUsername(dto.getUsername());
    user.setPassword(CryptoUtil.encryptPassword(dto.getPassword()));
    user.setEmail(dto.getEmail());
    user.setUserCreation(logSecurity.getUserCreation());
    user.setState(UserStateEnum.INCOMPLETE_PERFIL);
    user.setCreationDate(LocalDateTime.now());
    // validacion si el usuario ya existe
    if (!isExistUser(user)) {
      user = userRepository.save(user);
      // Persistencia en log
      HistoricalUtil.registerHistorical(user, HistoricalOperationEnum.INSERT,
          UserHistService.class);
      // Pesistencia de log
      saveSuccessLog(HttpStatus.CREATED.value(), logSecurity, logSecurityUserRespository);
      // Se crea rol por defecto cuando apenas se crea el usuario
      RoleUserDTO roleUserDTO =
          new RoleUserDTO(null, RoleEnum.NEW_USER.name(), user.getId(), dto.getUsername());
      roleAsignmentService.createRoleUser(null, roleUserDTO);
    }
    return user;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Evento que invoca la operacion elminar persona
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param id
   * @param userDetails
   */
  private void deletePersonEvent(UserDetails userDetails, Long idUser) {
    Object idPerson = userRepository.findIdPersonByIdUser(idUser);
    if (idPerson != null) {
      PersonDeleteEvent event =
          new PersonDeleteEvent(this, Long.parseLong(idPerson.toString()), userDetails);
      eventPublisher.publishEvent(event);
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creación_Usuario </b> Valida si existe el usuario por id
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @return
   */
  public boolean isExistsUserById(Long idUser) {
    User user = userRepository.findById(idUser).orElse(null);
    return user != null && user.getId() > 0;
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
  private boolean isExistUser(User user) {
    if (userRepository.existsByUsernameAndEmail(user.getUsername(), user.getEmail())) {
      throw new IllegalArgumentException(SecurityExceptionEnum.FUNC_USER_EXIST.getCodeMessage());
    }
    return false;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Recupera la entidad rol del usuario si tiene un rol
   * asignado asociada
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param httpRequest
   * @return
   */
  public List<Role> findRolesByUsername(String username) {
    try {
      // se obtienen los roles del usuario
      return getRolesForUser(username);
    } catch (EntityNotFoundException e) {
      // inicializacion log transaccional
      LogSecurity logSecurity = initializeLog(null, username, notBody, notBody,
          SecurityActionEnum.USER_LIST_ROLES.getCode());
      logSecurity.setStatusCode(HttpStatus.FORBIDDEN.value());
      logSecurity.setExecutionTime(getExecutionTime());
      // Se lanza excepcion funcional
      throw new FunctionalException(e.getMessage(), e.getCause(), logSecurityUserRespository,
          logSecurity);
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Obtien los roles del usuario si existe en caso
   * contrario guarda un log con la excepcion generada
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param username
   * @return
   */
  private List<Role> getRolesForUser(String username) {
    // Consulta del usuario desde el campo username
    User user = findByUsername(username);
    // Se valida el estado del usuario.
    if (UserStateEnum.INCOMPLETE_PERFIL.equals(user.getState())) {
      // Se retorna el rol temporal new user
      return roleAsignmentService.getRoleNewUser();
    } else {
      // Consulta que roles estan asignados al usuario
      List<RoleUser> rolesUser = findRolesUserByUser(user);
      // Consulta los roles del usuario
      return findRolesByRoleUser(rolesUser);
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Consulta los roles del usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param rolesUser
   * @return
   */
  private List<Role> findRolesByRoleUser(List<RoleUser> rolesUser) {
    List<Long> idsRoles = rolesUser.stream().map(roleUser -> roleUser.getRole().getId()).toList();
    List<Role> roles = roleAsignmentService.findRoleByIds(idsRoles);
    if (roles == null || roles.isEmpty()) {
      throw new EntityNotFoundException(
          CoreExceptionEnum.FUNC_COMMON_ROLE_NOT_EXIST.getCodeMessage());
    }
    return roles;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Consulta los registros de la tabla sec_role_user
   * asociados al del usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param user
   * @return
   */
  public List<RoleUser> findRolesUserByUser(User user) {
    List<RoleUser> rolesUser = roleAsignmentService.findRoleUserByIdUser(user.getId());
    if (rolesUser == null || rolesUser.isEmpty()) {
      throw new EntityNotFoundException(
          SecurityExceptionEnum.FUNC_ROLE_USER_NOT_EXIST.getCodeMessage(user.getUsername()));
    }
    return rolesUser;
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Recupera la entidad user con el username si tiene un
   * rol asignado asociada
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param username
   * @return
   */
  public User findByUsername(String username) {
    return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(
        SecurityExceptionEnum.FUNC_USER_NOT_FOUND_USERNAME.getCodeMessage()));
  }



  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Busca un usuario por email, si existe retorna genera
   * una excepcíon en caso contrario false.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param email
   * @param request
   * @return
   * @throws FontFormatException
   */
  public boolean findByEmail(String email, HttpServletRequest request) {
    LogSecurity logSecurity = initializeLog(request, email, notBody, notBody,
        SecurityActionEnum.USER_GET_EMAIL.getCode());

    try {
      if (!email.matches(RegexConstants.EMAIL)) {
        throw new IllegalArgumentException(
            SecurityExceptionEnum.FUNC_USER_EMAIL_INVALID.getCodeMessage());
      }
      if (findByEmail(email) != null) {
        setLogSecurityError(
            (new RuntimeException(
                SecurityExceptionEnum.FUNC_USER_CREATE_EMAIL_UNIQUE.getCodeMessage())),
            logSecurity);
        return true;
      }
    } catch (EntityNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      log.error("UserService.findByEmail - ", e.getMessage());
      setLogSecurityError((RuntimeException) e, logSecurity);
      throw new FunctionalException(e.getMessage(), e.getCause(), logSecurityUserRespository,
          logSecurity);
    }

    // Pesistencia de log
    saveSuccessLog(HttpStatus.OK.value(), logSecurity, logSecurityUserRespository);
    return false;
  }


  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Recupera la entidad user con el email si tiene un rol
   * asignado asociada
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param username
   * @return
   */
  public User findByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(
        SecurityExceptionEnum.FUNC_USER_NOT_FOUND_USERNAME.getCodeMessage()));
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> A partir del id valida si un usuario existe
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param id
   * @return
   */
  public boolean existsById(Long id) {
    return userRepository.existsById(id);
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Búsqueda del usuario a partir del id.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param id
   * @return
   */
  public User findById(Long userId) {
    return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
        SecurityExceptionEnum.FUNC_USER_NOT_FOUND_ID.getCodeMessage()));
  }

}
