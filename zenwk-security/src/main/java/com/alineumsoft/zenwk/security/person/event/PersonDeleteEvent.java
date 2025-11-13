package com.alineumsoft.zenwk.security.person.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class PersonCreatedEvent
 */
public class PersonDeleteEvent extends ApplicationEvent {
  private static final long serialVersionUID = 1L;
  private final Long idPerson;
  private final UserDetails userDetails;
  private boolean isDelete;

  /**
   * <p>
   * <b> Constructor </b>
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param source
   * @param personDTO
   * @param userDetails
   */
  public PersonDeleteEvent(Object source, Long personDTO, UserDetails userDetails) {
    super(source);
    this.idPerson = personDTO;
    this.userDetails = userDetails;
  }

  /**
   * Gets the value of isDelete.
   * 
   * @return the value of isDelete.
   */
  public boolean isDelete() {
    return isDelete;
  }

  /**
   * Sets the value of isDelete.
   * 
   * @param isDelete the new value of isDelete.
   */
  public void setDelete(boolean isDelete) {
    this.isDelete = isDelete;
  }

  /**
   * Gets the value of idPerson.
   * 
   * @return the value of idPerson.
   */
  public Long getIdPerson() {
    return idPerson;
  }

  /**
   * Gets the value of userDetails.
   * 
   * @return the value of userDetails.
   */
  public UserDetails getUserDetails() {
    return userDetails;
  }

}
