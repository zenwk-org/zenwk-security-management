package com.alineumsoft.zenwk.security.person.event;

import org.springframework.context.ApplicationEvent;
import com.alineumsoft.zenwk.security.person.entity.Person;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class FindPersonByIdEvent
 */
public class FindPersonByIdEvent extends ApplicationEvent {
  private static final long serialVersionUID = 1L;
  private final Long idPerson;
  private Person person;

  /**
   * <p>
   * <b> Constructor </b>
   * </p>
   * 
   * @param source
   * @param idPerson
   */
  public FindPersonByIdEvent(Object source, Long idPerson) {
    super(source);
    this.idPerson = idPerson;
  }

  /**
   * Gets the value of person.
   * 
   * @return the value of person.
   */
  public Person getPerson() {
    return person;
  }

  /**
   * Sets the value of person.
   * 
   * @param person the new value of person.
   */
  public void setPerson(Person person) {
    this.person = person;
  }

  /**
   * Gets the value of idPerson.
   * 
   * @return the value of idPerson.
   */
  public Long getIdPerson() {
    return idPerson;
  }

}
