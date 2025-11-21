package com.alineumsoft.zenwk.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import com.alineumsoft.zenwk.security.person.event.FindPersonByIdEvent;
import com.alineumsoft.zenwk.security.person.event.PersonDeleteEvent;
import com.alineumsoft.zenwk.security.person.listener.PersonEventListener;
import com.alineumsoft.zenwk.security.person.service.PersonService;

class PersonEventListenerTest {

  private PersonService personService;
  private PersonEventListener listener;

  @BeforeEach
  void setUp() {
    personService = mock(PersonService.class);
    listener = new PersonEventListener(personService);
  }

  @Test
  @DisplayName("handlePersonDeleteEvent() debe llamar a deletePerson y actualizar el evento")
  void handlePersonDeleteEvent_success() {
    Long personId = 1L;
    UserDetails userDetails = mock(UserDetails.class);

    // Creamos el evento con source arbitrario
    PersonDeleteEvent event = new PersonDeleteEvent(new Object(), personId, userDetails);

    when(personService.deletePerson(personId, null, userDetails)).thenReturn(true);

    listener.handlePersonDeleteEvent(event);

    verify(personService, times(1)).deletePerson(personId, null, userDetails);
    assertTrue(event.isDelete());
  }

  @Test
  @DisplayName("handlePersonDeleteEvent() debe actualizar evento como false si delete falla")
  void handlePersonDeleteEvent_failure() {
    Long personId = 2L;
    UserDetails userDetails = mock(UserDetails.class);
    PersonDeleteEvent event = new PersonDeleteEvent(new Object(), personId, userDetails);

    when(personService.deletePerson(personId, null, userDetails)).thenReturn(false);

    listener.handlePersonDeleteEvent(event);

    verify(personService).deletePerson(personId, null, userDetails);
    assertFalse(event.isDelete());
  }

  @Test
  @DisplayName("handleFindPersonByIdEvent() debe llamar a findEntityByIdPerson y actualizar el evento")
  void handleFindPersonByIdEvent_success() {
    Long personId = 3L;
    Person person = new Person();

    FindPersonByIdEvent event = new FindPersonByIdEvent(new Object(), personId);

    when(personService.findEntityByIdPerson(personId)).thenReturn(person);

    listener.handleFindPersonByIdEvent(event);

    verify(personService, times(1)).findEntityByIdPerson(personId);
    assertEquals(person, event.getPerson());
  }

  @Test
  @DisplayName("handleFindPersonByIdEvent() debe manejar null si persona no encontrada")
  void handleFindPersonByIdEvent_notFound() {
    Long personId = 4L;
    FindPersonByIdEvent event = new FindPersonByIdEvent(new Object(), personId);

    when(personService.findEntityByIdPerson(personId)).thenReturn(null);

    listener.handleFindPersonByIdEvent(event);

    verify(personService).findEntityByIdPerson(personId);
    assertNull(event.getPerson());
  }
}
