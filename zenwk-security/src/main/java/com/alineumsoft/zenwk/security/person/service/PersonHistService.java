package com.alineumsoft.zenwk.security.person.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.alineumsoft.zenwk.security.common.hist.enums.HistoricalOperationEnum;
import com.alineumsoft.zenwk.security.person.entity.Person;
import com.alineumsoft.zenwk.security.person.entity.PersonHist;
import com.alineumsoft.zenwk.security.person.repository.PersonHistRepository;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class PersonHistService
 */
@Service
public class PersonHistService {
  private final PersonHistRepository personHistRepository;

  /**
   * <p>
   * <b> Constructor </b>
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param personHistRepository
   */
  public PersonHistService(PersonHistRepository personHistRepository) {
    this.personHistRepository = personHistRepository;
  }

  /**
   * <p>
   * <b> Persistencia historico </b>
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param person
   * @param operationType
   */
  public void saveHistorical(Person person, HistoricalOperationEnum operationType) {
    PersonHist historical = new PersonHist();
    historical.setIdPerson(person.getId());
    historical.setFirstName(person.getFirstName());
    historical.setMiddleName(person.getMiddleName());
    historical.setLastName(person.getLastName());
    historical.setMiddleLastName(person.getMiddleLastName());
    historical.setDateOfBirth(person.getDateOfBirth());
    historical.setAddress(person.getAddress());
    historical.setCreationDate(person.getCreationDate());
    historical.setModificationDate(person.getModificationDate());
    historical.setUserCreation(person.getUserCreation());
    historical.setUserModification(person.getUserModification());
    historical.setHistCreationDate(LocalDateTime.now());
    historical.setOperation(operationType);
    historical.setAge(person.getAge());
    historical.setPersonSex(person.getPersonSex() != null ? person.getPersonSex().getId() : null);
    historical.setProfilePicture(null);
    personHistRepository.save(historical);
  }
}
