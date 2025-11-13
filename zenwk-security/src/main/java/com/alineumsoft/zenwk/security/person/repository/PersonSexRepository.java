package com.alineumsoft.zenwk.security.person.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.alineumsoft.zenwk.security.person.entity.PersonSex;

/**
 * Repositorio para tabla person
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project zenwk-security
 * @class PersonSexRepository
 */
public interface PersonSexRepository extends JpaRepository<PersonSex, Long> {
  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Búsquedad por código.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param code
   * @return
   */
  Optional<PersonSex> findOneByCode(String code);

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Retorna todos los registros persistidos en la
   * entidad.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param code
   * @return
   */
  List<PersonSex> findAllByOrderByDescriptionAsc();
}
