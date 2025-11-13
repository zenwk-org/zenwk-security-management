package com.alineumsoft.zenwk.security.person.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "sec_person_sex")
@Data
@NoArgsConstructor
public class PersonSex {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "secpersexid", nullable = false, updatable = false)
  private Long id;

  @Column(name = "secpersexcode", length = 10, nullable = false, unique = true)
  private String code;

  @Column(name = "secpersexdesc", length = 50, nullable = false)
  private String description;
}
