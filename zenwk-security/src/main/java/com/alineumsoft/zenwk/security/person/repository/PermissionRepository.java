package com.alineumsoft.zenwk.security.person.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.alineumsoft.zenwk.security.entity.Permission;

/**
 * <p>
 * Representa el repositorio de la clase sed_permission
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class PermissionRepository
 */
public interface PermissionRepository extends JpaRepository<Permission, Long> {
  /**
   * Query, obtiene todos los permisos filtrando por el id del rol.
   */
  public static final String JPQL_PERMISSIONS_FILTER_ROLE_ID =
      "SELECT p " + "FROM RolePermission rp " + " JOIN rp.role r " + " JOIN rp.permission p "
          + "WHERE r.id =:roleId ";

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Valida si un rol ya existe por su nombre
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param name
   * @return
   */
  public boolean existsByName(String name);

  /**
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pageable
   * @return
   * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Pageable)
   */
  Page<Permission> findAll(Pageable pageable);

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Filtra el resultado por el rol indicado.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param role
   * @param pageable
   * @return
   */
  @Query(JPQL_PERMISSIONS_FILTER_ROLE_ID)
  Page<Permission> getAllPermissionsForRoleId(Long roleId, Pageable pageable);
}
