package com.alineumsoft.zenwk.security.auth.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.alineumsoft.zenwk.security.auth.dto.PagePermissionDTO;
import com.alineumsoft.zenwk.security.common.constants.AuthConfigConstants;
import com.alineumsoft.zenwk.security.common.constants.CommonMessageConstants;
import com.alineumsoft.zenwk.security.common.enums.PermissionOperationEnum;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.common.exception.TechnicalException;
import com.alineumsoft.zenwk.security.common.helper.ApiRestSecurityHelper;
import com.alineumsoft.zenwk.security.common.util.ObjectUpdaterUtil;
import com.alineumsoft.zenwk.security.dto.PaginatorDTO;
import com.alineumsoft.zenwk.security.dto.PermissionDTO;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.entity.Permission;
import com.alineumsoft.zenwk.security.entity.RoleUser;
import com.alineumsoft.zenwk.security.enums.RoleEnum;
import com.alineumsoft.zenwk.security.enums.SecurityActionEnum;
import com.alineumsoft.zenwk.security.enums.SecurityExceptionEnum;
import com.alineumsoft.zenwk.security.person.repository.PermissionRepository;
import com.alineumsoft.zenwk.security.person.repository.RolePermissionRepository;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import com.alineumsoft.zenwk.security.user.entity.User;
import com.alineumsoft.zenwk.security.user.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Consulta los roles y permisos del sistema
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class PermissionService
 */
@Service
@Slf4j
public class PermissionService extends ApiRestSecurityHelper {
  /**
   * Nombre atributo que indica el nombre del permiso.
   */
  private static final String PERMISSION_NAME = "name";
  /**
   * Repositorio para la consulta de permisos y roles.
   */
  private final RolePermissionRepository rolePermRepo;

  /**
   * Servicio para la gestion de user
   */
  private final UserService userService;
  /**
   * Repositorio utilizado para el log
   */
  private final LogSecurityRepository logSecRepo;

  /**
   * PermissionRepository
   */
  private final PermissionRepository permissionRepo;

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Constructor
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param rolePermRepo
   * @param userService
   * @param logSecRepo
   * @param permissionRepo
   */
  public PermissionService(RolePermissionRepository rolePermRepo, @Lazy UserService userService,
      LogSecurityRepository logSecRepo, PermissionRepository permissionRepo) {
    this.rolePermRepo = rolePermRepo;
    this.userService = userService;
    this.logSecRepo = logSecRepo;
    this.permissionRepo = permissionRepo;
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> RecRecupera y organiza los permisos de los roles en
   * el sistema. Agrupa los permisos por operación (CREATE, UPDATE, DELETE, FIND_ALL, FIND_BY_ID).
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  public Map<PermissionOperationEnum, List<PermissionDTO>> getOperationPermission() {
    try {
      // Consulta de roles y permisos
      List<Object[]> listRolPermssions = rolePermRepo.findAllRolePermissions();
      return getMapPermissions(listRolPermssions);

    } catch (Exception e) {
      log.error(CommonMessageConstants.FORMAT_EXCEPTION, e.getMessage(), e);
      return Collections.emptyMap();
    }
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Retorna los recursos rest permitidos para el usuario
   * para el rol user
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  public List<String> listAllowedUrlsForUserRole(String username) {
    User user = userService.findByUsername(username);
    List<RoleUser> rolesUser = userService.findRolesUserByUser(user);
    List<String> namesRole =
        rolesUser.stream().map(role -> role.getRole().getName()).collect(Collectors.toList());
    List<String> permissionResources = rolePermRepo.findResourcesByRolName(namesRole);

    if (namesRole.contains(RoleEnum.USER.name()) || namesRole.contains(RoleEnum.NEW_USER.name())) {
      permissionResources = permissionResources.stream().map(url -> {
        if (url.contains(AuthConfigConstants.ID)) {
          url = generatedUrlFromId(user, url);
        }
        return url;
      }).collect(Collectors.toList());
    }

    return permissionResources.stream().filter(Objects::nonNull).collect(Collectors.toList());
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Actualiza las rutas de los recursos que tengan como
   * parametro el valor {id}
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param user
   * @param url
   * @return
   */
  private String generatedUrlFromId(User user, String url) {
    String updateUrl = null;
    if (url.contains(AuthConfigConstants.URL_USER) && user.getId() != null) {
      updateUrl = url.replace(AuthConfigConstants.ID, user.getId().toString());
    }
    if (url.contains(AuthConfigConstants.URL_PERSON) && url.contains(AuthConfigConstants.ID)
        && user.getPerson() != null && user.getPerson().getId() != null) {
      updateUrl = url.replace(AuthConfigConstants.ID, user.getPerson().getId().toString());
    }
    return updateUrl;
  }

  /**
   * 
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Util. Mapeo del resultado desde bd a un mapa
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param listRolPermssions
   * @return
   */
  private Map<PermissionOperationEnum, List<PermissionDTO>> getMapPermissions(
      List<Object[]> listRolPermssions) {
    Map<PermissionOperationEnum, List<PermissionDTO>> mapRolPermissions = new HashMap<>();
    // Mapa con cache de roles
    listRolPermssions.forEach(rolPerm -> {
      PermissionOperationEnum keyMap = PermissionOperationEnum.valueOf(rolPerm[0].toString());
      // Agregar o actualizar la lista de permisos en el mapa
      mapRolPermissions.computeIfAbsent(keyMap, k -> new ArrayList<>()).add(new PermissionDTO(
          String.valueOf(rolPerm[1]), String.valueOf(rolPerm[2]), String.valueOf(rolPerm[3])));
    });
    return mapRolPermissions;
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Crea un nuevo permiso en el sistema.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @param request
   * @param userDetails
   * @return
   */
  public Long createPemission(PermissionDTO dto, HttpServletRequest request,
      UserDetails userDetails) {
    String username = userDetails.getUsername();
    LogSecurity logSec = initializeLog(request, username, getJson(dto), notBody,
        SecurityActionEnum.PERMISSION_CREATE.name());
    try {
      if (!permissionRepo.existsByName(dto.getName())) {
        Permission permission = new Permission(dto);
        permission.setCreationDate(LocalDateTime.now());
        permission.setCreationUser(username);
        permission = permissionRepo.save(permission);
        saveSuccessLog(HttpStatus.OK.value(), logSec, logSecRepo);
        // HistoricalUtil.registerHistorical(role, HistoricalOperationEnum.INSERT,
        // RoleHistService.class);
        return permission.getId();
      } else {
        throw new EntityExistsException(
            SecurityExceptionEnum.FUNC_PERMISSION_EXIST.getCodeMessage(dto.getName()));
      }
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSec);
      throw new FunctionalException(e.getMessage(), e.getCause(), logSecRepo, logSec);
    }
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Actualiza un rol existente en el sistema.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param id
   * @param dto
   * @param request
   * @param userDetails
   */
  public void updatePermission(Long id, PermissionDTO dto, HttpServletRequest request,
      UserDetails userDetails) {
    String username = userDetails.getUsername();
    LogSecurity logSec = initializeLog(request, username, getJson(dto), notBody,
        SecurityActionEnum.PERMISSION_UPDATE.name());
    try {
      Permission permissionTarget = findPermissionById(id);
      if (ObjectUpdaterUtil.updateDataEqualObject(new Permission(dto), permissionTarget)) {
        permissionTarget.setModificationDate(LocalDateTime.now());
        permissionTarget.setModificationUser(username);
        permissionRepo.save(permissionTarget);
        // HistoricalUtil.registerHistorical(role, HistoricalOperationEnum.INSERT,
        // RoleHistService.class);
        saveSuccessLog(HttpStatus.NO_CONTENT.value(), logSec, logSecRepo);
      }
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSec);
      throw new FunctionalException(e.getMessage(), e.getCause(), logSecRepo, logSec);
    }

  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Elmina un un permiso existen en el sistema
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param id
   * @param request
   * @param userDetails
   */
  public void deletePermission(Long id, HttpServletRequest request, UserDetails userDetails) {
    String username = userDetails.getUsername();
    LogSecurity logSec = initializeLog(request, username, notBody, notBody,
        SecurityActionEnum.PERMISSION_DELETE.name());
    try {
      // Si el permiso ya esta asignado a un rol no se puede elminar, primero se debe
      // desasignar.
      permissionRepo.delete(findPermissionById(id));
      // HistoricalUtil.registerHistorical(role, HistoricalOperationEnum.INSERT,
      // RoleHistService.class);
      saveSuccessLog(HttpStatus.NO_CONTENT.value(), logSec, logSecRepo);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSec);
      if (e instanceof DataIntegrityViolationException) {
        throw new TechnicalException(
            SecurityExceptionEnum.TECH_PERMISSION_NOT_DELETE.getCodeMessage(), e.getCause(),
            logSecRepo, logSec);
      } else {
        throw new FunctionalException(e.getMessage(), e.getCause(), logSecRepo, logSec);
      }
    }
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Se recupera recupera un permiso por su id si este
   * existen en el sistema.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param id
   * @param request
   * @param userDetails
   * @return
   */
  public PermissionDTO findByIdPermission(Long id, HttpServletRequest request,
      UserDetails userDetails) {
    String username = userDetails.getUsername();
    LogSecurity logSec = initializeLog(request, username, notBody, notBody,
        SecurityActionEnum.PERMISSION_GET.name());
    try {
      Permission permission = findPermissionById(id);
      return new PermissionDTO(permission);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSec);
      throw new FunctionalException(e.getMessage(), e.getCause(), logSecRepo, logSec);
    }
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Recupera la lista de permisos existentes en el
   * sistema.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pageable
   * @param request
   * @param userDetails
   * @return
   */
  public PagePermissionDTO getAllPermissions(Pageable pageable, HttpServletRequest request,
      UserDetails userDetails) {
    String username = userDetails.getUsername();
    LogSecurity logSec = initializeLog(request, username, notBody, notBody,
        SecurityActionEnum.PERMISSION_LIST.name());
    try {
      int pageNumber = getPageNumber(pageable);
      // Consulta paginada y ordenamiento
      Page<Permission> pagePermission = getAllPermission(pageable, pageNumber);
      // Se persiste log
      saveSuccessLog(HttpStatus.OK.value(), logSec, logSecRepo);
      // Retorno con los datos paginados
      return getPagePermissionDTO(pagePermission);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSec);
      throw new FunctionalException(e.getMessage(), e.getCause(), logSecRepo, logSec);
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Asignación_Roles </b> Método general que obtiene un permiso por us id.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param id
   * @return
   */
  public Permission findPermissionById(Long id) {
    return permissionRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(
        SecurityExceptionEnum.FUNC_PERMISSION_NOT_EXISTS.getCodeMessage()));
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Obtiene todos los permisos persistidos en el sistema.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pageable
   * @param pageNumber
   * @return
   */
  private Page<Permission> getAllPermission(Pageable pageable, int pageNumber) {
    return permissionRepo.findAll(PageRequest.of(pageNumber, pageable.getPageSize(),
        pageable.getSortOr(Sort.by(Sort.Direction.ASC, PERMISSION_NAME))));
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Genera el DTO para listar los permisos.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pagePermission
   * @return
   */
  public PagePermissionDTO getPagePermissionDTO(Page<Permission> pagePermission) {
    List<PermissionDTO> permissions = pagePermission.stream()
        .map(permission -> new PermissionDTO(permission)).collect(Collectors.toList());
    PaginatorDTO paginator = new PaginatorDTO(pagePermission.getTotalElements(),
        pagePermission.getTotalPages(), pagePermission.getNumber() + 1);
    return new PagePermissionDTO(permissions, paginator);
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Obtien los permisos asociados a un rol persistido en
   * el sistema.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pageable
   * @param roleId
   * @param pageNumber
   * @return
   */
  public Page<Permission> getPermissionsForRole(Pageable pageable, Long roleId, int pageNumber) {
    return permissionRepo.getAllPermissionsForRoleId(roleId, PageRequest.of(pageNumber,
        pageable.getPageSize(), pageable.getSortOr(Sort.by(Sort.Direction.ASC, ID))));
  }

}
