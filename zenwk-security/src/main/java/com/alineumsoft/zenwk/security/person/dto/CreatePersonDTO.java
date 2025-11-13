package com.alineumsoft.zenwk.security.person.dto;

import java.io.Serializable;
import com.alineumsoft.zenwk.security.common.validation.EntityExists;
import com.alineumsoft.zenwk.security.constants.DtoValidationKeys;
import com.alineumsoft.zenwk.security.person.entity.Person;
import com.alineumsoft.zenwk.security.user.service.UserService;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

/**
 * <p>
 * DTO para la gestion de personas
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class PersonDTO
 */
public class CreatePersonDTO extends PersonDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * Id del usuario asociado.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @NotNull(message = DtoValidationKeys.PERSON_ID_USER_NOT_NULL)
  @EntityExists(service = UserService.class, message = DtoValidationKeys.PERSON_ID_USER_NOT_FOUND)
  private Long idUser;

  /**
   * <p>
   * <b> Constructor </b>
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   */
  public CreatePersonDTO() {}

  /**
   * 
   * <p>
   * <b> Constructor que extiende de PersonDTO</b>
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param person
   */
  public CreatePersonDTO(Person person) {
    super(person);
  }

  /**
   * Gets the value of idUser.
   * 
   * @return the value of idUser.
   */
  public Long getIdUser() {
    return idUser;
  }

  /**
   * Sets the value of idUser.
   * 
   * @param idUser the new value of idUser.
   */
  public void setIdUser(Long idUser) {
    this.idUser = idUser;
  }

}
