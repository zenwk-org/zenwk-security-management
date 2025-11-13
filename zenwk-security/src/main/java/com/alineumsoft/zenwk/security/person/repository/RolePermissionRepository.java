package com.alineumsoft.zenwk.security.person.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.alineumsoft.zenwk.security.entity.Permission;
import com.alineumsoft.zenwk.security.entity.Role;
import com.alineumsoft.zenwk.security.entity.RolePermission;

/**
 * <p>
 * Repositorio para la entidad sec_role_permission
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class RolePermissionRepository
 */
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
  /**
   * Query, obtiene para un rol todos sus permisos indicando el nombre del rol y, la operación,
   * método y recurso del permiso.
   */
  public static final String JPQL_ROLE_PERMISSIONS =
      "SELECT p.operation, r.name, p.method, p.resource " + "FROM RolePermission rp "
          + " JOIN rp.role r " + " JOIN rp.permission p " + "ORDER BY r.name";
  /**
   * Query, el nombre del recuerso de un permiso filtrando por el nombre del rol.
   */
  public static final String JPQL_RESOURCES_FILTER_ROL_NAME =
      "SELECT DISTINCT p.resource " + "FROM RolePermission rp " + " JOIN rp.role r "
          + " JOIN rp.permission p " + "WHERE r.name IN (:rolName) ";

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Consulta todos lo roles configurados para el modulo
   * security
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  @Query(JPQL_ROLE_PERMISSIONS)
  public List<Object[]> findAllRolePermissions();

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Consulta todos lo roles configurados para el modulo
   * security filtrando por el nombre del rol
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param rolName
   * @return
   */
  @Query(JPQL_RESOURCES_FILTER_ROL_NAME)
  public List<String> findResourcesByRolName(List<String> rolName);

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Busca en el sistema si un permiso ya sido asignado al
   * role.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param role
   * @param permission
   * @return
   */
  Optional<RolePermission> findByRoleAndPermission(Role role, Permission permission);

}
