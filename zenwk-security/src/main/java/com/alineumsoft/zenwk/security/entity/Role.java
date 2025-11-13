package com.alineumsoft.zenwk.security.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sec_role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
  /**
   * id (Pk)
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "secrolidrole")
  private Long id;

  /**
   * name
   */
  @Column(name = "secrolname")
  private String name;

  /**
   * description
   */
  @Column(name = "secroldescription")
  private String description;

  /**
   * creationDate
   */
  @Column(name = "secrolcreationdate")
  private LocalDateTime creationDate;

  /**
   * creationUser
   */
  @Column(name = "secrolmodificationdate")
  private LocalDateTime modificationDate;

  @Column(name = "secrolcreationuser")
  private String creationUser;

  /**
   * modificationUser
   */
  @Column(name = "secrolmodificationuser")
  private String modificationUser;

  /**
   * <p>
   * Constructor
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param name
   * @param description
   */
  public Role(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
