package com.alineumsoft.zenwk.security.user.entity;

import java.time.LocalDateTime;
import com.alineumsoft.zenwk.security.enums.UserStateEnum;
import com.alineumsoft.zenwk.security.person.entity.Person;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 */
@SuppressWarnings("java:S2975")
@Entity
@Table(name = "sec_user")
@Data
@NoArgsConstructor
public class User {
  /**
   * id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "secuseiduser")
  private Long id;
  /**
   * username
   */
  @Column(name = "secuseusername")
  private String username;
  /**
   * password
   */
  @Column(name = "secusepassword")
  private String password;
  /**
   * email
   */
  @Column(name = "secuseemail")
  private String email;
  /**
   * creationDate
   */
  @Column(name = "secusecreationdate")
  private LocalDateTime creationDate;
  /**
   * modificationDate
   */
  @Column(name = "secusemodificationdate")
  private LocalDateTime modificationDate;
  /**
   * userCreation
   */
  @Column(name = "secuseusecreation")
  private String userCreation;
  /**
   * userModification
   */
  @Column(name = "secuseusemodification")
  private String userModification;
  /**
   * state
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "secusestate")
  private UserStateEnum state;
  /**
   * person
   */
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "secuseidperson")
  private Person person;



}
