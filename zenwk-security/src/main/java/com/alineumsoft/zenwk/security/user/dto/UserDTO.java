package com.alineumsoft.zenwk.security.user.dto;

import java.io.Serializable;
import java.util.Optional;
import com.alineumsoft.zenwk.security.common.constants.RegexConstants;
import com.alineumsoft.zenwk.security.constants.DtoValidationKeys;
import com.alineumsoft.zenwk.security.enums.UserStateEnum;
import com.alineumsoft.zenwk.security.person.entity.Person;
import com.alineumsoft.zenwk.security.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * DTO usado para la gestion usuarios usado en el api de users
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class UserInDTO
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * Id generado del usuario.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Long id;
  /**
   * Id de la pesona asociado.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Long idPerson;
  /**
   * Nombre de usuario.
   */
  @Pattern(regexp = RegexConstants.USERNAME, message = DtoValidationKeys.USER_USERNAME_INVALID)
  @Size(max = 30, message = DtoValidationKeys.USER_USERNAME_MAX_LENGTH)
  @NotNull(message = DtoValidationKeys.USER_USERNAME_NOT_NULL)
  private String username;
  /**
   * Password (max 64 RFC 5321).
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Pattern(regexp = RegexConstants.PASSWORD, message = DtoValidationKeys.USER_PASSWORD_INVALID)
  @Size(max = 64, message = DtoValidationKeys.USER_PASSWORD_MAX_LENGTH)
  @NotNull(message = DtoValidationKeys.USER_PASSWORD_NOT_NULL)
  private String password;
  /**
   * Email (max 254).
   */
  @Pattern(regexp = RegexConstants.EMAIL, message = DtoValidationKeys.USER_EMAIL_INVALID)
  @NotNull(message = DtoValidationKeys.USER_EMAIL_NOT_NULL)
  @Size(max = 254, message = DtoValidationKeys.USER_EMAIL_MAX_LENGTH)
  private String email;

  /**
   * Estado, valor autogenerado
   */
  private UserStateEnum state;
  /**
   * Persona asociada
   */
  @JsonIgnore
  private Person person;

  /**
   * <p>
   * <b> Constructor </b>
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param user
   */
  public UserDTO(User user) {
    Long idpersonTemp = Optional.ofNullable(user.getPerson()).map(Person::getId).orElse(null);
    this.id = user.getId();
    this.idPerson = idpersonTemp;
    this.username = user.getUsername();
    this.password = user.getPassword();
    this.email = user.getEmail();
    this.state = user.getState();

  }
}
