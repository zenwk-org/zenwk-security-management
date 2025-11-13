package com.alineumsoft.zenwk.security.auth.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.alineumsoft.zenwk.security.auth.dto.PagePermissionDTO;
import com.alineumsoft.zenwk.security.auth.dto.PagePermissionRolesDTO;
import com.alineumsoft.zenwk.security.auth.dto.PageRoleDTO;
import com.alineumsoft.zenwk.security.auth.dto.PageRolePermissionsDTO;
import com.alineumsoft.zenwk.security.auth.dto.RoleUserDTO;
import com.alineumsoft.zenwk.security.common.constants.CommonMessageConstants;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.common.exception.TechnicalException;
import com.alineumsoft.zenwk.security.common.helper.ApiRestSecurityHelper;
import com.alineumsoft.zenwk.security.dto.PaginatorDTO;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.entity.Permission;
import com.alineumsoft.zenwk.security.entity.Role;
import com.alineumsoft.zenwk.security.entity.RolePermission;
import com.alineumsoft.zenwk.security.entity.RoleUser;
import com.alineumsoft.zenwk.security.enums.RoleEnum;
import com.alineumsoft.zenwk.security.enums.SecurityActionEnum;
import com.alineumsoft.zenwk.security.enums.SecurityExceptionEnum;
import com.alineumsoft.zenwk.security.person.repository.RolePermissionRepository;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import com.alineumsoft.zenwk.security.repository.RolUserRepository;
import com.alineumsoft.zenwk.security.user.entity.User;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Servicio para las gestion de los roles
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class RoleService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleAssignmentService extends ApiRestSecurityHelper {
  /**
   * Repositorio para Role
   */
  private final RoleService roleService;
  /**
   * Repositorio para Permission
   */
  private final PermissionService permissionService;
  /**
   * Repositorio para la entidad que relaciona Role con User
   */
  private final RolUserRepository rolUserRepo;

  /**
   * Repositorio para la entidad que relaciona Role con Permission
   */
  private final RolePermissionRepository rolPermissionRepo;
  /**
   * Repositorio utilizado para el log
   */
  private final LogSecurityRepository logSecRepo;

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Creacion del registro que relaciona el rol con el
   * usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param dto
   * @return
   */
  public RoleUser createRoleUser(HttpServletRequest request, RoleUserDTO dto) {
    String notFound = CommonMessageConstants.NOT_FOUND;
    try {
      // Pendiente. implmentar metodo generico programacion defensiva a nivel de DTO's
      Role role = roleService.findRoleByName(dto.getNameRole());
      dto.setIdRole(role.getId());
      deleteRolNewUser(dto.getIdUser());
      return rolUserRepo.save(convertToEntityRoleUser(dto));
    } catch (RuntimeException e) {
      log.info(CommonMessageConstants.LOG_MSG_EXCEPTION, e);
      LogSecurity logSec = initializeLog(request, dto.getUsername(), notFound, notFound, notFound);
      throw new TechnicalException(e.getMessage(), e.getCause(), logSecRepo, logSec);
    }
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Se genera una lista con el rol temporal new_user
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  public List<Role> getRoleNewUser() {
    try {
      Role rolTemp = roleService.findRoleByName(RoleEnum.NEW_USER.name());
      return Arrays.asList(rolTemp);
    } catch (EntityNotFoundException e) {
      throw new EntityNotFoundException(
          SecurityExceptionEnum.FUNC_ROLE_USER_NOT_EXIST.getCodeMessage(RoleEnum.NEW_USER.name()));
    }
  }

  /**
   * 
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Se obtienen todos los roles asociados a los ids.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idsRoles
   * @return
   */
  public List<Role> findRoleByIds(List<Long> idsRoles) {
    return roleService.findRoleByIds(idsRoles);
  }

  /**
   * 
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Eliminacion de registros por id de usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param idUser
   * @return
   */
  public boolean deleteRoleUserByIdUser(Long idUser) {
    try {
      User user = new User();
      user.setId(idUser);
      rolUserRepo.deleteByUser(user);
      return true;
    } catch (RuntimeException e) {
      throw new EntityNotFoundException(e.getMessage());
    }
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Se obtienen todos los registros asociados al usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @return
   */
  public List<RoleUser> findRoleUserByIdUser(Long idUser) {
    User user = new User();
    user.setId(idUser);
    return rolUserRepo.findByUser(user);
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Si ya existe un rol y este es NEW_USER lo elimina.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   */
  private void deleteRolNewUser(Long idUser) {
    try {
      User user = new User();
      user.setId(idUser);
      List<RoleUser> listRoleUser = rolUserRepo.findByUser(user);
      listRoleUser = listRoleUser.stream()
          .filter(rolUser -> RoleEnum.NEW_USER.name().equals(rolUser.getRole().getName()))
          .collect(Collectors.toList());
      if (!listRoleUser.isEmpty() && listRoleUser.size() == 1) {
        rolUserRepo.delete(listRoleUser.get(0));
      }
    } catch (Exception e) {
      throw new IllegalAccessError();
    }
  }

  /**
   * 
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Obtiene una entidad RoleUser a partir de su DTO
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @return
   */
  private RoleUser convertToEntityRoleUser(RoleUserDTO dto) {
    User user = new User();
    Role role = new Role();
    user.setId(dto.getIdUser());
    role.setId(dto.getIdRole());
    return new RoleUser(null, user, role, dto.getUsername(), LocalDateTime.now());
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Asignamiento de un permiso a un rol. Si ya se
   * encuentra asignado se genera error funcional.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param roleId
   * @param permissionId
   * @param request
   * @param userDetails
   */
  public void assignPermissionsToRole(Long roleId, Long permissionId, HttpServletRequest request,
      UserDetails userDetails) {
    String username = userDetails.getUsername();
    LogSecurity logSec = initializeLog(request, username, notBody, notBody,
        SecurityActionEnum.ROLE_ASSIGNMENT_PERMISSION.getCode());
    try {
      Role role = roleService.findRoleById(roleId);
      Permission permission = permissionService.findPermissionById(permissionId);
      RolePermission assignmentPermission =
          getAssignmentPermissionToRol(role, permission).orElse(null);
      if (assignmentPermission == null) {
        assignmentPermission =
            new RolePermission(null, role, permission, LocalDateTime.now(), username);
        rolPermissionRepo.save(assignmentPermission);
      } else {
        throw new EntityExistsException(SecurityExceptionEnum.FUNC_ROLE_ASSIGNMENT_PERMISSION_EXISTS
            .getCodeMessage(assignmentPermission.getPermission().getName(),
                assignmentPermission.getRole().getName()));
      }
      // Se persiste log con solicitud exitosa.
      saveSuccessLog(HttpStatus.NO_CONTENT.value(), logSec, logSecRepo);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSec);
      throw new FunctionalException(e.getMessage(), e.getCause(), logSecRepo, logSec);
    }
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Remueve un permiso asignado a un rol si este existen
   * en el sistema.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param roleId
   * @param permissionId
   * @param request
   * @param userDetails
   */
  public void removePermissionFromRole(Long roleId, Long permissionId, HttpServletRequest request,
      UserDetails userDetails) {
    String username = userDetails.getUsername();
    LogSecurity logSec = initializeLog(request, username, notBody, notBody,
        SecurityActionEnum.ROLE_REMOVE_PERMISSION.getCode());
    try {
      Role role = roleService.findRoleById(roleId);
      Permission permission = permissionService.findPermissionById(permissionId);
      RolePermission assignmentPermission =
          getAssignmentPermissionToRol(role, permission).orElse(null);
      if (assignmentPermission != null) {
        rolPermissionRepo.delete(assignmentPermission);
      } else {
        throw new EntityExistsException(
            SecurityExceptionEnum.FUNC_ROLE_ASSIGNMENT_PERMISSION_NOT_EXISTS
                .getCodeMessage(permission.getName(), role.getName()));
      }
      // Se persiste log con solicitud exitosa.
      saveSuccessLog(HttpStatus.NO_CONTENT.value(), logSec, logSecRepo);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSec);
      throw new FunctionalException(e.getMessage(), e.getCause(), logSecRepo, logSec);
    }
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Lista todos los permisos asociados a un rol.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pageable
   * @param roleId
   * @param request
   * @param userDetails
   * @return
   */
  public PageRolePermissionsDTO listPermissionsForRole(Pageable pageable, Long roleId,
      HttpServletRequest request, UserDetails userDetails) {
    String username = userDetails.getUsername();
    LogSecurity logSec = initializeLog(request, username, notBody, notBody,
        SecurityActionEnum.ROLE_LIST_PERMISSIONS.getCode());
    try {
      Role role = roleService.findRoleById(roleId);
      int pageNumber = getPageNumber(pageable);
      // Consulta paginada y ordenamiento
      Page<Permission> pagePermission =
          permissionService.getPermissionsForRole(pageable, roleId, pageNumber);
      // Se obtiene el page de permission
      PagePermissionDTO pagePermissionDTO = permissionService.getPagePermissionDTO(pagePermission);
      // Se obtiene la paginación
      PaginatorDTO paginator = new PaginatorDTO(pagePermissionDTO.getTotalElements(),
          pagePermissionDTO.getTotalPages(), pagePermissionDTO.getCurrentPage());
      // Se persiste log
      saveSuccessLog(HttpStatus.OK.value(), logSec, logSecRepo);
      // Retorno con los datos paginados
      return new PageRolePermissionsDTO(roleId, role.getName(), pagePermissionDTO.getPermissions(),
          paginator);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSec);
      throw new FunctionalException(e.getMessage(), e.getCause(), logSecRepo, logSec);

    }
  }

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Asignación_Roles </b> Obtiene todos los roles que tiene asignado un
   * permiso.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pageable
   * @param permissionId
   * @param request
   * @param userDetails
   * @return
   */
  public PagePermissionRolesDTO listRolesForPermission(Pageable pageable, Long permissionId,
      HttpServletRequest request, UserDetails userDetails) {
    String username = userDetails.getUsername();
    LogSecurity logSec = initializeLog(request, username, notBody, notBody,
        SecurityActionEnum.PERMISSION_LIST_ROLES.getCode());
    try {
      Permission permisssion = permissionService.findPermissionById(permissionId);
      int pageNumber = getPageNumber(pageable);
      // Consulta paginada y ordenamiento
      Page<Role> pageRoles = roleService.getRolesForPermission(pageable, permissionId, pageNumber);
      // Se obtiene el page de permission
      PageRoleDTO pageRoleDTO = roleService.getPageRolesDTO(pageRoles);
      // Se obtiene la paginación
      PaginatorDTO paginator = new PaginatorDTO(pageRoleDTO.getTotalElements(),
          pageRoleDTO.getTotalPages(), pageRoleDTO.getCurrentPage());
      // Se persiste log
      saveSuccessLog(HttpStatus.OK.value(), logSec, logSecRepo);
      // Retorno con los datos paginados
      return new PagePermissionRolesDTO(permissionId, permisssion.getName(),
          permisssion.getMethod().concat(" ").concat(permisssion.getResource()),
          pageRoleDTO.getRoles(), paginator);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSec);
      throw new FunctionalException(e.getMessage(), e.getCause(), logSecRepo, logSec);

    }
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Actualiza un role y un permiso si se encuentran
   * asociados en el sistema.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param role
   * @param permission
   * @return
   */
  private Optional<RolePermission> getAssignmentPermissionToRol(Role role, Permission permission) {
    try {
      return rolPermissionRepo.findByRoleAndPermission(role, permission);
    } catch (ConstraintViolationException e) {
      return Optional.empty();
    }
  }

}
