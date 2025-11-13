package com.alineumsoft.zenwk.security.user.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class DeletePersonEvent
 */
public class DeleteUserEvent extends ApplicationEvent {
  private static final long serialVersionUID = 1L;
  private final Long idUser;
  private final UserDetails userDetails;
  private boolean isDelete;

  /**
   * <p>
   * <b>Constructor </b>
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param source
   * @param idUser
   * @param userDetails
   */
  public DeleteUserEvent(Object source, Long idUser, UserDetails userDetails) {
    super(source);
    this.idUser = idUser;
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
   * Gets the value of idUser.
   * 
   * @return the value of idUser.
   */
  public Long getIdUser() {
    return idUser;
  }

  /**
   * Gets the value of principal.
   * 
   * @return the value of principal.
   */
  public UserDetails getUserDetails() {
    return userDetails;
  }
}
