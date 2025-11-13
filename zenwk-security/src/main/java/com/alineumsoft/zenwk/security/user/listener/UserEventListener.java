package com.alineumsoft.zenwk.security.user.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.alineumsoft.zenwk.security.user.event.DeleteUserEvent;
import com.alineumsoft.zenwk.security.user.event.UpdateUserEvent;
import com.alineumsoft.zenwk.security.user.service.UserService;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class UserEventListener
 */
@Component
public class UserEventListener {
  private final UserService userService;

  /**
   * <p>
   * <b> Constructor </b>
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param userService
   */
  public UserEventListener(UserService userService) {
    this.userService = userService;
  }


  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Evento que elimina un usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param event
   */
  @EventListener
  public void handleDeleteUserEvent(DeleteUserEvent event) {
    boolean isDelete = userService.deleteUser(event.getIdUser(), null, event.getUserDetails());
    event.setDelete(isDelete);
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Evento que actualiza un usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param event
   */
  @EventListener
  public void handleUpdateUserEvent(UpdateUserEvent event) {
    boolean isUpdate = userService.updateUser(null, event.getIdUser(), event.getDto(),
        event.getUserDetails(), event.getPerson());
    event.setUpdate(isUpdate);
  }

}
