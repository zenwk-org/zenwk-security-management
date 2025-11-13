package com.alineumsoft.zenwk.security.person.dto;

import java.io.Serializable;
import java.util.List;
import com.alineumsoft.zenwk.security.dto.PaginatorDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <p>
 * DTO usado para la lista de resultados en la api de usuarios
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class ResponseUser
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"persons"})
public class PagePersonDTO extends PaginatorDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * Lista de personas
   */
  private List<CreatePersonDTO> persons;

  /**
   * <p>
   * Constructor
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param users
   * @param dto
   */
  public PagePersonDTO(List<CreatePersonDTO> persons, PaginatorDTO dto) {
    super(dto.getTotalElements(), dto.getTotalPages(), dto.getCurrentPage());
    this.persons = persons;
  }

  /**
   * Gets the value of persons.
   * 
   * @return the value of persons.
   */
  public List<CreatePersonDTO> getPersons() {
    return persons;
  }

  /**
   * Sets the value of persons.
   * 
   * @param persons the new value of persons.
   */
  public void setPersons(List<CreatePersonDTO> persons) {
    this.persons = persons;
  }

}
