package com.alineumsoft.zenwk.security.person.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.alineumsoft.zenwk.security.person.entity.Person;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class PersonRepository
 */
public interface PersonRepository
    extends JpaRepository<Person, Long>, PagingAndSortingRepository<Person, Long> {

  /**
   * JPQL que consulta el id del usuario, recibe el id de la persona
   */
  public static final String JPQL_FIND_USER_ID =
      "SELECT u.id FROM User u  LEFT JOIN u.person p WHERE p.id = :idPerson ";

  /**
   * JPQL que consulta la persona por medio del userId
   */
  public static final String JPQL_FIND_PERSON_FROM_USER_ID =
      "SELECT p FROM User u  LEFT JOIN u.person p WHERE u.id = :userId ";

  /**
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param id
   * @return
   * @see org.springframework.data.repository.CrudRepository#findById(java.lang.Object)
   */
  public Optional<Person> findById(Long id);

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> JPQL para la busqueda del id del usuario con el id de
   * la persona
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idPerson
   * @return
   */
  @Query(JPQL_FIND_USER_ID)
  public Object findIdPersonByIdUser(Long idPerson);

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> valida si la persona ya existen en la base de datos
   * con las siguientes campos:
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param firstName
   * @param middleName
   * @param lastName
   * @param middleLastName
   * @param dateOfBirth
   * @return
   */
  public boolean existsByFirstNameAndMiddleNameAndLastNameAndMiddleLastNameAndDateOfBirth(
      String firstName, String middleName, String lastName, String middleLastName,
      LocalDateTime dateOfBirth);


  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Consulta para validar si un persona ya existe
   * descartando por ID.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param firstName
   * @param middleName
   * @param lastName
   * @param middleLastName
   * @param dateOfBirth
   * @param id
   * @return
   */
  public boolean existsByFirstNameAndMiddleNameAndLastNameAndMiddleLastNameAndDateOfBirthAndIdNot(
      String firstName, String middleName, String lastName, String middleLastName,
      LocalDateTime dateOfBirth, Long id);



  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> JPQL para la busqueda de la persona a través del
   * userId
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idPerson
   * @return
   */
  @Query(JPQL_FIND_PERSON_FROM_USER_ID)
  public Optional<Person> findPersonFromUserId(Long userId);


}
