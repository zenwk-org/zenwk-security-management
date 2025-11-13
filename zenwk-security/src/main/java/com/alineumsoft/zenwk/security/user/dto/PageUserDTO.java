package com.alineumsoft.zenwk.security.user.dto;

import java.io.Serializable;
import java.util.List;
import com.alineumsoft.zenwk.security.dto.PaginatorDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <p>
 * DTO para listar usado en el api de user
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class ResponseUser
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"users"})
public class PageUserDTO extends PaginatorDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * users
   */
  private List<UserDTO> users;

  /**
   * <p>
   * Constructor
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param users
   * @param dto
   */
  public PageUserDTO(List<UserDTO> users, PaginatorDTO dto) {
    super(dto.getTotalElements(), dto.getTotalPages(), dto.getCurrentPage());
    this.users = users;
  }

  /**
   * Gets the value of users.
   * 
   * @return the value of users.
   */
  public List<UserDTO> getUsers() {
    return users;
  }


  /**
   * Sets the value of users.
   * 
   * @param users the new value of users.
   */
  public void setUsers(List<UserDTO> users) {
    this.users = users;
  }

}
