package com.alineumsoft.zenwk.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.alineumsoft.zenwk.security.enums.SecurityExceptionEnum;
import com.alineumsoft.zenwk.security.person.entity.PersonSex;
import com.alineumsoft.zenwk.security.person.repository.PersonSexRepository;
import com.alineumsoft.zenwk.security.person.service.PersonSexService;
import jakarta.persistence.EntityNotFoundException;

class PersonSexServiceTest {

  private PersonSexRepository repository;
  private PersonSexService personSexService;

  @BeforeEach
  void setUp() {
    repository = mock(PersonSexRepository.class);
    personSexService = new PersonSexService(repository);
  }

  @Test
  @DisplayName("getAllForSelect() debe retornar la lista ordenada por descripción")
  void getAllForSelect_shouldReturnOrderedList() {
    PersonSex sex1 = new PersonSex();
    sex1.setId(1L);
    sex1.setDescription("Femenino");

    PersonSex sex2 = new PersonSex();
    sex2.setId(2L);
    sex2.setDescription("Masculino");

    when(repository.findAllByOrderByDescriptionAsc()).thenReturn(Arrays.asList(sex1, sex2));

    List<PersonSex> result = personSexService.getAllForSelect();

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Femenino", result.get(0).getDescription());
    verify(repository, times(1)).findAllByOrderByDescriptionAsc();
  }

  @Test
  @DisplayName("findPersonSexById() debe retornar PersonSex si el ID existe")
  void findPersonSexById_shouldReturnSexWhenExists() {
    PersonSex sex = new PersonSex();
    sex.setId(5L);
    sex.setDescription("Otro");

    when(repository.findById(5L)).thenReturn(Optional.of(sex));

    PersonSex result = personSexService.findPersonSexById(5L);

    assertNotNull(result);
    assertEquals(5L, result.getId());
    assertEquals("Otro", result.getDescription());
    verify(repository, times(1)).findById(5L);
  }

  @Test
  @DisplayName("findPersonSexById() debe lanzar EntityNotFoundException si ID no existe")
  void findPersonSexById_shouldThrowExceptionWhenNotFound() {
    Long id = 99L;
    when(repository.findById(id)).thenReturn(Optional.empty());

    EntityNotFoundException exception =
        assertThrows(EntityNotFoundException.class, () -> personSexService.findPersonSexById(id));

    assertTrue(exception.getMessage()
        .contains(SecurityExceptionEnum.FUNC_PERSON_SEX_NO_FOUND.getCodeMessage(id.toString())));

    verify(repository, times(1)).findById(id);
  }
}
