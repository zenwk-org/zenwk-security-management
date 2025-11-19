package com.alineumsoft.zenwk.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.alineumsoft.zenwk.security.auth.service.CustomUserDetailsService;
import com.alineumsoft.zenwk.security.entity.Role;
import com.alineumsoft.zenwk.security.user.entity.User;
import com.alineumsoft.zenwk.security.user.service.UserService;

class CustomUserDetailsServiceTest {

  @Mock
  private UserService userService;

  @InjectMocks
  private CustomUserDetailsService customUserDetailsService;

  private User userEntity;
  private Role role;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    userEntity = new User();
    userEntity.setUsername("tester");
    userEntity.setPassword("123");

    role = new Role();
    role.setName("ADMIN");
  }

  @Test
  @DisplayName("loadUserByUsername: usuario por email correcto")
  void loadUserByEmail_Success() {
    when(userService.findByEmail("t@e.com")).thenReturn(userEntity);
    when(userService.findRolesByUsername("tester")).thenReturn(List.of(role));

    UserDetails result = customUserDetailsService.loadUserByUsername("t@e.com");

    assertEquals("tester", result.getUsername());
    assertEquals("123", result.getPassword());
    assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN")));
  }

  @Test
  @DisplayName("loadUserByUsername: usuario por username (no email)")
  void loadUserByUsername_Success() {
    when(userService.findByUsername("tester")).thenReturn(userEntity);
    when(userService.findRolesByUsername("tester")).thenReturn(List.of(role));

    UserDetails result = customUserDetailsService.loadUserByUsername("tester");

    assertEquals("tester", result.getUsername());
    assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN")));
  }

  @Test
  @DisplayName("loadUserByUsername: lanza UsernameNotFoundException")
  void loadUserByUsername_NotFound() {
    when(userService.findByUsername("badUser"))
        .thenThrow(new UsernameNotFoundException("not found"));

    assertThrows(UsernameNotFoundException.class,
        () -> customUserDetailsService.loadUserByUsername("badUser"));
  }

  @Test
  @DisplayName("isEmail: valida expresiones correctas e incorrectas")
  void isEmail_Validation() throws Exception {
    var method = CustomUserDetailsService.class.getDeclaredMethod("isEmail", String.class);
    method.setAccessible(true);

    assertTrue((boolean) method.invoke(customUserDetailsService, "test@mail.com"));
    assertFalse((boolean) method.invoke(customUserDetailsService, "user123"));
  }
}
