package com.alineumsoft.zenwk.security;

import static org.mockito.Mockito.verify;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.alineumsoft.zenwk.security.common.hist.enums.HistoricalOperationEnum;
import com.alineumsoft.zenwk.security.enums.UserStateEnum;
import com.alineumsoft.zenwk.security.person.entity.Person;
import com.alineumsoft.zenwk.security.user.entity.User;
import com.alineumsoft.zenwk.security.user.entity.UserHist;
import com.alineumsoft.zenwk.security.user.repository.UserHistRepository;
import com.alineumsoft.zenwk.security.user.service.UserHistService;

class UserHistServiceTest {

  @Mock
  private UserHistRepository userHistRepository;

  private UserHistService userHistService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    userHistService = new UserHistService(userHistRepository);
  }

  @Test
  @DisplayName("Guardar histórico correctamente cuando User tiene Person")
  void testSaveHistoricalWithPerson() {
    // Preparar usuario con persona
    Person person = new Person();
    person.setId(10L);

    User user = new User();
    user.setId(1L);
    user.setUsername("tester");
    user.setPassword("Password123*");
    user.setEmail("email@test.com");
    user.setState(UserStateEnum.ACTIVE);
    user.setPerson(person);
    user.setUserCreation("creator");
    user.setUserModification("modifier");
    user.setCreationDate(LocalDateTime.now().minusDays(1));
    user.setModificationDate(LocalDateTime.now());

    userHistService.saveHistorical(user, HistoricalOperationEnum.INSERT);

    ArgumentCaptor<UserHist> captor = ArgumentCaptor.forClass(UserHist.class);
    verify(userHistRepository).save(captor.capture());

    UserHist saved = captor.getValue();
    assert saved.getIdUser().equals(user.getId());
    assert saved.getUsername().equals(user.getUsername());
    assert saved.getEmail().equals(user.getEmail());
    assert saved.getPassword().equals(user.getPassword());
    assert saved.getState().equals(user.getState());
    assert saved.getIdPerson().equals(person.getId());
    assert saved.getUserCreation().equals(user.getUserCreation());
    assert saved.getUserModification().equals(user.getUserModification());
    assert saved.getCreationDate().equals(user.getCreationDate());
    assert saved.getModificationDate().equals(user.getModificationDate());
    assert saved.getOperation() == HistoricalOperationEnum.INSERT;
    assert saved.getHistCreationDate() != null;
  }

  @Test
  @DisplayName("Guardar histórico correctamente cuando User no tiene Person")
  void testSaveHistoricalWithoutPerson() {
    User user = new User();
    user.setId(2L);
    user.setUsername("tester2");
    user.setPassword("Password123*");
    user.setEmail("email2@test.com");
    user.setState(UserStateEnum.ACTIVE);

    userHistService.saveHistorical(user, HistoricalOperationEnum.UPDATE);

    ArgumentCaptor<UserHist> captor = ArgumentCaptor.forClass(UserHist.class);
    verify(userHistRepository).save(captor.capture());

    UserHist saved = captor.getValue();
    assert saved.getIdUser().equals(user.getId());
    assert saved.getIdPerson() == null;
    assert saved.getOperation() == HistoricalOperationEnum.UPDATE;
  }
}
