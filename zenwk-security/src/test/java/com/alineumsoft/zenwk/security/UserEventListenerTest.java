package com.alineumsoft.zenwk.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import com.alineumsoft.zenwk.security.person.entity.Person;
import com.alineumsoft.zenwk.security.user.dto.UserDTO;
import com.alineumsoft.zenwk.security.user.event.DeleteUserEvent;
import com.alineumsoft.zenwk.security.user.event.UpdateUserEvent;
import com.alineumsoft.zenwk.security.user.listener.UserEventListener;
import com.alineumsoft.zenwk.security.user.service.UserService;

class UserEventListenerTest {

  private UserService userService;
  private UserEventListener listener;

  @BeforeEach
  void setUp() {
    userService = mock(UserService.class);
    listener = new UserEventListener(userService);
  }

  @Test
  @DisplayName("handleDeleteUserEvent() debe llamar a deleteUser y actualizar el evento como true")
  void handleDeleteUserEvent_success() {
    Long userId = 1L;
    UserDetails userDetails = mock(UserDetails.class);
    DeleteUserEvent event = new DeleteUserEvent(new Object(), userId, userDetails);

    when(userService.deleteUser(userId, null, userDetails)).thenReturn(true);

    listener.handleDeleteUserEvent(event);

    verify(userService, times(1)).deleteUser(userId, null, userDetails);
    assertTrue(event.isDelete());
  }

  @Test
  @DisplayName("handleDeleteUserEvent() debe actualizar evento como false si delete falla")
  void handleDeleteUserEvent_failure() {
    Long userId = 2L;
    UserDetails userDetails = mock(UserDetails.class);
    DeleteUserEvent event = new DeleteUserEvent(new Object(), userId, userDetails);

    when(userService.deleteUser(userId, null, userDetails)).thenReturn(false);

    listener.handleDeleteUserEvent(event);

    verify(userService).deleteUser(userId, null, userDetails);
    assertFalse(event.isDelete());
  }

  @Test
  @DisplayName("handleUpdateUserEvent() debe llamar a updateUser y actualizar el evento como true")
  void handleUpdateUserEvent_success() {
    Long userId = 3L;
    UserDTO dto = new UserDTO();
    Person person = new Person();
    UserDetails userDetails = mock(UserDetails.class);
    UpdateUserEvent event = new UpdateUserEvent(new Object(), userId, dto, userDetails, person);

    when(userService.updateUser(null, userId, dto, userDetails, person)).thenReturn(true);

    listener.handleUpdateUserEvent(event);

    verify(userService, times(1)).updateUser(null, userId, dto, userDetails, person);
    assertTrue(event.isUpdate());
  }

  @Test
  @DisplayName("handleUpdateUserEvent() debe actualizar evento como false si update falla")
  void handleUpdateUserEvent_failure() {
    Long userId = 4L;
    UserDTO dto = new UserDTO();
    Person person = new Person();
    UserDetails userDetails = mock(UserDetails.class);
    UpdateUserEvent event = new UpdateUserEvent(new Object(), userId, dto, userDetails, person);

    when(userService.updateUser(null, userId, dto, userDetails, person)).thenReturn(false);

    listener.handleUpdateUserEvent(event);

    verify(userService).updateUser(null, userId, dto, userDetails, person);
    assertFalse(event.isUpdate());
  }
}
