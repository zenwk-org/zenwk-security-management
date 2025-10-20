package com.alineumsoft.zenwk.security.person.dto;

import java.io.Serializable;
import com.alineumsoft.zenwk.security.common.constants.RegexConstants;
import com.alineumsoft.zenwk.security.constants.DtoValidationKeys;
import com.alineumsoft.zenwk.security.person.entity.Person;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * DTO base para solicitud de creacion de persona
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class CreatePersonDTO
 */
@Data
@NoArgsConstructor
public class PersonDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * Id de person.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Long id;
  /**
   * Primer nombre.
   */
  @Pattern(regexp = RegexConstants.NAME, message = DtoValidationKeys.PERSON_NAME_INVALID)
  @NotNull(message = DtoValidationKeys.PERSON_FIRST_NAME_NOT_NULL)
  @Size(max = 30, message = DtoValidationKeys.PERSON_NAME_INVALID)
  private String firstName;
  /**
   * Segundo nombre.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Pattern(regexp = RegexConstants.NAME, message = DtoValidationKeys.PERSON_NAME_INVALID)
  @Size(max = 30, message = DtoValidationKeys.PERSON_NAME_INVALID)
  private String middleName;
  /**
   * Primer apellido.
   */
  @NotNull(message = DtoValidationKeys.PERSON_LAST_NAME_NOT_NULL)
  @Pattern(regexp = RegexConstants.NAME, message = DtoValidationKeys.PERSON_LAST_NAME_INVALID)
  @Size(max = 30, message = DtoValidationKeys.PERSON_LAST_NAME_INVALID)
  private String lastName;
  /**
   * Segundo apellido.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Pattern(regexp = RegexConstants.NAME, message = DtoValidationKeys.PERSON_LAST_NAME_INVALID)
  @Size(max = 30, message = DtoValidationKeys.PERSON_LAST_NAME_INVALID)
  private String middleLastName;
  /**
   * Fecha de cumpleanios
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Pattern(regexp = RegexConstants.DATE_ISO_8601, message = DtoValidationKeys.PERSON_DATE_INVALID)
  private String dateOfBirth;
  /**
   * Direccion.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Pattern(regexp = RegexConstants.COLOMBIA_ADDRESS,
      message = DtoValidationKeys.PERSON_COLOMBIA_ADDRESS)
  private String address;
  /**
   * Edad
   */
  @NotNull(message = DtoValidationKeys.PERSON_AGE_NOT_NULL)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Long age;
  /**
   * Sexo
   */
  @NotNull(message = DtoValidationKeys.PERSON_ID_SEX_NOT_NULL)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Long idSex;
  /**
   * Foto de perfil
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private byte[] profilePicture;



  /**
   * <b>Constructor </b> Genera una instancia a partir de la entidad JPA Person
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param person
   */
  public PersonDTO(Person person) {
    this.id = person.getId();
    this.firstName = person.getFirstName();
    this.middleName = person.getMiddleName();
    this.lastName = person.getLastName();
    this.middleLastName = person.getMiddleLastName();
    this.dateOfBirth = person.getDateOfBirth() != null ? person.getDateOfBirth().toString() : null;
    this.address = person.getAddress();
    this.age = person.getAge();
    this.idSex = person.getPersonSex() != null ? person.getPersonSex().getId() : null;
    this.profilePicture = person.getProfilePicture();

  }
}
