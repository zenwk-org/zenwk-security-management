package com.alineumsoft.zenwk.security.auth.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.alineumsoft.zenwk.security.auth.dto.PageRoleDTO;
import com.alineumsoft.zenwk.security.auth.dto.RoleDTO;
import com.alineumsoft.zenwk.security.common.exception.FunctionalException;
import com.alineumsoft.zenwk.security.common.exception.TechnicalException;
import com.alineumsoft.zenwk.security.common.exception.enums.CoreExceptionEnum;
import com.alineumsoft.zenwk.security.common.helper.ApiRestSecurityHelper;
import com.alineumsoft.zenwk.security.common.util.ObjectUpdaterUtil;
import com.alineumsoft.zenwk.security.dto.PaginatorDTO;
import com.alineumsoft.zenwk.security.entity.LogSecurity;
import com.alineumsoft.zenwk.security.entity.Role;
import com.alineumsoft.zenwk.security.enums.SecurityActionEnum;
import com.alineumsoft.zenwk.security.enums.SecurityExceptionEnum;
import com.alineumsoft.zenwk.security.repository.LogSecurityRepository;
import com.alineumsoft.zenwk.security.repository.RoleRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
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
public class RoleService extends ApiRestSecurityHelper {
  /**
   * campo name del dto
   */
  protected static final String ROlE_NAME = "name";
  /**
   * Repositorio para Role
   */
  private final RoleRepository roleRepository;
  /**
   * Repositorio utilizado para el log
   */
  private final LogSecurityRepository logSecRepo;

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Crea un nuevo rol en el sistema.
   * </p>
   * 
   * @param dto
   * @param request
   * @param userDetails
   * @return
   */
  public Long createRole(RoleDTO dto, HttpServletRequest request, UserDetails userDetails) {
    String username = userDetails.getUsername();
    LogSecurity logSec = initializeLog(request, username, getJson(dto), notBody,
        SecurityActionEnum.ROLE_CREATE.name());
    try {
      if (roleRepository.existsByName(dto.getName())) {
        throw new EntityExistsException(
            SecurityExceptionEnum.FUNC_ROLE_EXISTS.getCodeMessage(dto.getName()));
      }
      // Se crea el rol asociado
      Role role = new Role(dto.getName(), dto.getDescription());
      role.setCreationUser(username);
      role.setCreationDate(LocalDateTime.now());
      role = roleRepository.save(role);
      saveSuccessLog(HttpStatus.OK.value(), logSec, logSecRepo);
      // HistoricalUtil.registerHistorical(role, HistoricalOperationEnum.INSERT,
      // RoleHistService.class);
      return role.getId();
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
   * @param roleId
   * @param dto
   * @param request
   * @param userDetails
   */
  public void updateRole(Long roleId, RoleDTO dto, HttpServletRequest request,
      UserDetails userDetails) {
    String username = userDetails.getUsername();
    LogSecurity logSec = initializeLog(request, username, getJson(dto), notBody,
        SecurityActionEnum.ROLE_UPDATE.name());
    try {
      Role roleTarget = findRoleById(roleId);
      if (ObjectUpdaterUtil.updateDataEqualObject(new Role(dto.getName(), dto.getDescription()),
          roleTarget)) {
        roleTarget.setModificationUser(username);
        roleTarget.setModificationDate(LocalDateTime.now());
        roleRepository.save(roleTarget);
        // HistoricalUtil.registerHistorical(role, HistoricalOperationEnum.INSERT,
        // RoleHistService.class);
      }
      saveSuccessLog(HttpStatus.NO_CONTENT.value(), logSec, logSecRepo);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSec);
      throw new FunctionalException(e.getMessage(), e.getCause(), logSecRepo, logSec);
    }
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Busca un rol por su id.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param roleId
   * @param request
   * @param userDetails
   * @return
   */
  public RoleDTO findRoleById(Long roleId, HttpServletRequest request, UserDetails userDetails) {
    String username = userDetails.getUsername();
    LogSecurity logSec =
        initializeLog(request, username, notBody, notBody, SecurityActionEnum.ROLE_GET.name());
    try {
      Role role = findRoleById(roleId);
      saveSuccessLog(HttpStatus.OK.value(), logSec, logSecRepo);
      return new RoleDTO(role);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSec);
      throw new FunctionalException(e.getMessage(), e.getCause(), logSecRepo, logSec);
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Asignación_Roles </b> Busca todos los roles guardados en el sistema.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pageable
   * @param request
   * @param userDetails
   * @return
   */
  public PageRoleDTO findAllRoles(Pageable pageable, HttpServletRequest request,
      UserDetails userDetails) {
    String username = userDetails.getUsername();
    LogSecurity logSec =
        initializeLog(request, username, notBody, notBody, SecurityActionEnum.ROLE_LIST.name());
    try {
      int pageNumber = getPageNumber(pageable);
      // Consulta paginada y ordenamiento
      Page<Role> pageRoles = roleRepository.findAll(PageRequest.of(pageNumber,
          pageable.getPageSize(), pageable.getSortOr(Sort.by(Sort.Direction.ASC, ROlE_NAME))));
      // Se persiste log
      saveSuccessLog(HttpStatus.OK.value(), logSec, logSecRepo);
      // Retorno con los datos paginados
      return getPageRolesDTO(pageRoles);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSec);
      throw new FunctionalException(e.getMessage(), e.getCause(), logSecRepo, logSec);
    }
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Genera el DTO para listar los roles.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pageRoles
   * @return
   */
  public PageRoleDTO getPageRolesDTO(Page<Role> pageRoles) {
    List<RoleDTO> roles =
        pageRoles.stream().map(role -> new RoleDTO(role)).collect(Collectors.toList());
    PaginatorDTO paginator = new PaginatorDTO(pageRoles.getTotalElements(),
        pageRoles.getTotalPages(), pageRoles.getNumber() + 1);
    return new PageRoleDTO(roles, paginator);
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Asignación_Roles </b> ELmina un rol almacenado en el sistema.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param roleId
   * @param request
   * @param userDetails
   */
  public void deleteRole(Long roleId, HttpServletRequest request, UserDetails userDetails) {
    LogSecurity logSec = initializeLog(request, userDetails.getUsername(), notBody, notBody,
        SecurityActionEnum.ROLE_DELETE.name());
    try {
      Role role = findRoleById(roleId);
      roleRepository.delete(role);
      // HistoricalUtil.registerHistorical(role, HistoricalOperationEnum.INSERT,
      // RoleHistService.class);
      saveSuccessLog(HttpStatus.NO_CONTENT.value(), logSec, logSecRepo);
    } catch (RuntimeException e) {
      setLogSecurityError(e, logSec);
      if (e instanceof DataIntegrityViolationException) {
        throw new TechnicalException(SecurityExceptionEnum.TECH_ROLE_NOT_DELETE.getCodeMessage(),
            e.getCause(), logSecRepo, logSec);
      } else {
        throw new FunctionalException(e.getMessage(), e.getCause(), logSecRepo, logSec);
      }
    }
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Util. Busca un rol por su id.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param roleId
   * @return
   */
  public Role findRoleById(Long roleId) {
    return roleRepository.findById(roleId).orElseThrow(() -> new EntityNotFoundException(
        SecurityExceptionEnum.FUNC_ROLE_NOT_EXISTS.getCodeMessage(roleId.toString())));
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Util. Busca un rol por su nombre.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param name
   * @return
   */
  public Role findRoleByName(String name) {
    return roleRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException(
        CoreExceptionEnum.FUNC_COMMON_ROLE_NOT_EXIST.getCodeMessage(name)));
  }

  /**
   * 
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Se obtienen todos los roles asociados a los ids
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idsRoles
   * @return
   */
  public List<Role> findRoleByIds(List<Long> idsRoles) {
    return roleRepository.findByIds(idsRoles);
  }

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Obtien los roles asociados a un permiso persistido en
   * el sistema.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pageable
   * @param roleId
   * @param pageNumber
   * @return
   */
  public Page<Role> getRolesForPermission(Pageable pageable, Long permissionId, int pageNumber) {
    return roleRepository.getAllRolesForPermissionId(permissionId, PageRequest.of(pageNumber,
        pageable.getPageSize(), pageable.getSortOr(Sort.by(Sort.Direction.ASC, ID))));
  }

}
