package com.alineumsoft.zenwk.security.person.entity;

import java.time.LocalDateTime;
import com.alineumsoft.zenwk.security.common.hist.enums.HistoricalOperationEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "sec_person_hist")
@NoArgsConstructor
public class PersonHist {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "hsecperid")
  private Long id;

  @Column(name = "hsecperidperson")
  private Long idPerson;

  @Column(name = "hsecperfirstname")
  private String firstName;

  @Column(name = "hsecpermiddlename")
  private String middleName;

  @Column(name = "hsecperlastname")
  private String lastName;

  @Column(name = "hsecpermiddleLastname")
  private String middleLastName;

  @Column(name = "hsecperdateofbirth")
  private LocalDateTime dateOfBirth;

  @Column(name = "hsecperaddress")
  private String address;

  @Column(name = "hsecperidpersex")
  private Long personSex;

  @Column(name = "hsecperage")
  private Long age;

  @Column(name = "hsecperprofilepicture")
  private byte[] profilePicture;

  @Column(name = "hsecpercreationdate")
  private LocalDateTime creationDate;

  @Column(name = "hsecpermodificationdate")
  private LocalDateTime modificationDate;

  @Column(name = "hsecperusercreation")
  private String userCreation;

  @Column(name = "hsecperusermodification")
  private String userModification;

  // Campo de auditoria propio de la entidad historica
  @Column(name = "hsecperhistcreationdate")
  private LocalDateTime histCreationDate;

  // Campo de auditoria propio de la entidad historica
  @Column(name = "hsecperoperation")
  @Enumerated(EnumType.STRING)
  private HistoricalOperationEnum operation;

}
