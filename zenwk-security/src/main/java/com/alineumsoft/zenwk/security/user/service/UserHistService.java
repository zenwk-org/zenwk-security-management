package com.alineumsoft.zenwk.security.user.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.alineumsoft.zenwk.security.common.hist.enums.HistoricalOperationEnum;
import com.alineumsoft.zenwk.security.person.entity.Person;
import com.alineumsoft.zenwk.security.user.entity.User;
import com.alineumsoft.zenwk.security.user.entity.UserHist;
import com.alineumsoft.zenwk.security.user.repository.UserHistRepository;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class UserServiceHist
 */
@Service
public class UserHistService {
  private final UserHistRepository userHistRepository;

  /**
   * <p>
   * <b> Constructor </b>
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param userHistRepository
   */
  public UserHistService(UserHistRepository userHistRepository) {
    this.userHistRepository = userHistRepository;
  }

  /**
   * <p>
   * <b> Persistencia historico
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param user
   * @param userHist
   */
  public void saveHistorical(User user, HistoricalOperationEnum OperationType) {
    Long idPerson = Optional.ofNullable(user.getPerson()).map(Person::getId).orElse(null);
    UserHist historical = new UserHist();
    historical.setIdUser(user.getId());
    historical.setUsername(user.getUsername());
    historical.setPassword(user.getPassword());
    historical.setEmail(user.getEmail());
    historical.setState(user.getState());
    historical.setIdPerson(idPerson);
    historical.setUserCreation(user.getUserCreation());
    historical.setUserModification(user.getUserModification());
    historical.setCreationDate(user.getCreationDate());
    historical.setModificationDate(user.getModificationDate());
    historical.setOperation(OperationType);
    historical.setHistCreationDate(LocalDateTime.now());
    userHistRepository.save(historical);
  }

}
