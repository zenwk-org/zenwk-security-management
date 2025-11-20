package com.alineumsoft.zenwk.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.alineumsoft.zenwk.security.common.hist.enums.HistoricalOperationEnum;
import com.alineumsoft.zenwk.security.person.entity.Person;
import com.alineumsoft.zenwk.security.person.entity.PersonHist;
import com.alineumsoft.zenwk.security.person.entity.PersonSex;
import com.alineumsoft.zenwk.security.person.repository.PersonHistRepository;
import com.alineumsoft.zenwk.security.person.service.PersonHistService;

class PersonHistServiceTest {

  private PersonHistRepository personHistRepository;
  private PersonHistService personHistService;

  @BeforeEach
  void setUp() {
    personHistRepository = mock(PersonHistRepository.class);
    personHistService = new PersonHistService(personHistRepository);
  }

  @Test
  @DisplayName("saveHistorical() debe persistir PersonHist correctamente cuando PersonSex no es nulo")
  void saveHistorical_withPersonSex_shouldPersistCorrectly() {
    Person person = buildPersonWithSex();

    personHistService.saveHistorical(person, HistoricalOperationEnum.UPDATE);

    ArgumentCaptor<PersonHist> captor = ArgumentCaptor.forClass(PersonHist.class);
    verify(personHistRepository, times(1)).save(captor.capture());

    PersonHist savedHist = captor.getValue();
    assertEquals(person.getId(), savedHist.getIdPerson());
    assertEquals(person.getFirstName(), savedHist.getFirstName());
    assertEquals(person.getPersonSex().getId(), savedHist.getPersonSex());
    assertNotNull(savedHist.getHistCreationDate());
    assertEquals(HistoricalOperationEnum.UPDATE, savedHist.getOperation());
    assertNull(savedHist.getProfilePicture());
  }

  @Test
  @DisplayName("saveHistorical() debe persistir PersonHist con personSex nulo")
  void saveHistorical_withoutPersonSex_shouldPersistWithNullSex() {
    Person person = buildPersonWithoutSex();

    personHistService.saveHistorical(person, HistoricalOperationEnum.INSERT);

    ArgumentCaptor<PersonHist> captor = ArgumentCaptor.forClass(PersonHist.class);
    verify(personHistRepository, times(1)).save(captor.capture());

    PersonHist savedHist = captor.getValue();
    assertNull(savedHist.getPersonSex());
    assertEquals(HistoricalOperationEnum.INSERT, savedHist.getOperation());
  }

  // ----------------------------------------------------
  // Helpers
  // ----------------------------------------------------
  private Person buildPersonWithSex() {
    PersonSex sex = new PersonSex();
    sex.setId(3L);

    Person person = new Person();
    person.setId(1L);
    person.setFirstName("John");
    person.setMiddleName("A.");
    person.setLastName("Doe");
    person.setMiddleLastName("Smith");
    person.setAddress("Street 123");
    person.setUserCreation("admin");
    person.setUserModification("editor");
    person.setCreationDate(LocalDateTime.now().minusDays(2));
    person.setModificationDate(LocalDateTime.now().minusHours(5));
    person.setDateOfBirth(LocalDateTime.now());
    person.setPersonSex(sex);
    person.setAge(34L);
    return person;
  }

  private Person buildPersonWithoutSex() {
    Person person = buildPersonWithSex();
    person.setPersonSex(null);
    return person;
  }
}
