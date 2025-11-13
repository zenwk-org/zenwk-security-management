package com.alineumsoft.zenwk.security.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.alineumsoft.zenwk.security.entity.Role;

/**
 * <p>
 * Repositorio para entidad sec_role
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class RoleRepository
 */
@Repository
public interface RoleRepository
    extends JpaRepository<Role, Long>, PagingAndSortingRepository<Role, Long> {
  /**
   * JPQL que consulta el nombre de los roles con los id indicados
   */
  public static final String JPQL_QUERY_FIND_BY_IDS = "SELECT r FROM Role r where r.id IN :ids";
  /**
   * Query, obtiene todos los roles filtrando por el id de un permiso.
   */
  public static final String JPQL_ROLES_FILTER_PERMISSION_ID =
      "SELECT r " + "FROM RolePermission rp " + " JOIN rp.role r " + " JOIN rp.permission p "
          + "WHERE p.id =:permissionId ";

  /**
   * <p>
   * <b> CU002_Seguridad_Creacion_Usuario </b> Busca un role con su nombre
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param name
   * @return
   */
  public Optional<Role> findByName(String name);

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Busca un role por su id
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param name
   * @return
   */
  public Optional<Role> findById(Long id);

  /**
   * <p>
   * <b> CU001_Seguridad_Asignación_Roles </b> Consulta los roles con los id´s indicados
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param ids
   * @return
   */
  @Query(JPQL_QUERY_FIND_BY_IDS)
  public List<Role> findByIds(List<Long> ids);

  /**
   * <p>
   * <b> CU002_Seguridad_Asignación_Roles </b> Valida si ya existe un rol para ese nombre
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param name
   * @return
   */
  public boolean existsByName(String name);

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
  @Query(JPQL_ROLES_FILTER_PERMISSION_ID)
  Page<Role> getAllRolesForPermissionId(Long permissionId, Pageable pageable);

}
