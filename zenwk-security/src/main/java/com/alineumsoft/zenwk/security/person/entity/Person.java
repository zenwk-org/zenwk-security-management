package com.alineumsoft.zenwk.security.person.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class UserState
 */
@Entity
@Table(name = "sec_person")
@Data
public class Person {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "secperid")
  private Long id;

  @Column(name = "secperfirstname")
  private String firstName;

  @Column(name = "secpermiddlename")
  private String middleName;

  @Column(name = "secperlastname")
  private String lastName;

  @Column(name = "secpermiddlelastname")
  private String middleLastName;

  @Column(name = "secperdateofbirth")
  private LocalDateTime dateOfBirth;

  @Column(name = "secperaddress")
  private String address;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "secperidpersex")
  private PersonSex personSex;

  @Column(name = "secperage")
  private Long age;

  @Column(name = "secperprofilepicture")
  private byte[] profilePicture;

  @Column(name = "secpercreationdate")
  private LocalDateTime creationDate;

  @Column(name = "secpermodificationdate")
  private LocalDateTime modificationDate;

  @Column(name = "secperusercreation")
  private String userCreation;

  @Column(name = "secperusermodification")
  private String userModification;
}
