package com.alineumsoft.zenwk.security.user.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.userdetails.UserDetails;
import com.alineumsoft.zenwk.security.person.entity.Person;
import com.alineumsoft.zenwk.security.user.dto.UserDTO;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class UpdateUserEvent
 */
public class UpdateUserEvent extends ApplicationEvent {
  private static final long serialVersionUID = 1L;
  private final Long idUser;
  private final UserDTO dto;
  private final UserDetails userDetails;
  private final Person person;
  private boolean isUpdate;

  /**
   * <p>
   * <b> Constructor </b>
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param source
   * @param idUser
   * @param dto
   * @param userDetails
   * @param person
   */
  public UpdateUserEvent(Object source, Long idUser, UserDTO dto, UserDetails userDetails,
      Person person) {
    super(source);
    this.idUser = idUser;
    this.dto = dto;
    this.userDetails = userDetails;
    this.person = person;
  }

  /**
   * Gets the value of isUpdate.
   * 
   * @return the value of isUpdate.
   */
  public boolean isUpdate() {
    return isUpdate;
  }

  /**
   * Sets the value of isUpdate.
   * 
   * @param isUpdate the new value of isUpdate.
   */
  public void setUpdate(boolean isUpdate) {
    this.isUpdate = isUpdate;
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
   * Gets the value of dto.
   * 
   * @return the value of dto.
   */
  public UserDTO getDto() {
    return dto;
  }

  /**
   * Gets the value of principal.
   * 
   * @return the value of principal.
   */
  public UserDetails getUserDetails() {
    return userDetails;
  }

  /**
   * Gets the value of person.
   * 
   * @return the value of person.
   */
  public Person getPerson() {
    return person;
  }

}
