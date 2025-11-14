
package com.alineumsoft.zenwk.security.user.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.alineumsoft.zenwk.security.user.entity.UserHist;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class UserHistRepository
 */
public interface UserHistRepository extends JpaRepository<UserHist, Long> {
  /**
   * JPQL que consulta el id del usuario, recibe el id de la persona
   */
  public static final String JPQL_FIND_HIST_USER_BY_ID =
      "SELECT u FROM UserHist u WHERE u.idUser = :idUser ORDER BY u.id DESC ";

  /**
   * 
   * <p>
   * <b> CU004_Reestablecer contraseña </b> Recupera los registros historicos asociados al id
   * usuario.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @return
   */
  @Query(JPQL_FIND_HIST_USER_BY_ID)
  public List<UserHist> findLast20ByIdUser(Long idUser);

}
