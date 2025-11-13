package com.alineumsoft.zenwk.security.user.entity;

import java.time.LocalDateTime;
import com.alineumsoft.zenwk.security.common.hist.enums.HistoricalOperationEnum;
import com.alineumsoft.zenwk.security.enums.UserStateEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 */
@Entity
@Table(name = "sec_user_hist")
@Data
public class UserHist {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "hsecuseidhist")
  private Long id;

  @Column(name = "hsecuseiduser")
  private Long idUser;

  @Column(name = "hsecuseusername")
  private String username;

  @Column(name = "hsecusepassword")
  private String password;

  @Column(name = "hsecuseemail")
  private String email;

  @Column(name = "hsecusucreationdate")
  private LocalDateTime creationDate;

  @Column(name = "hsecusumodificationdate")
  private LocalDateTime modificationDate;

  @Column(name = "hsecuseusercreation")
  private String userCreation;

  @Column(name = "hsecuseusermodification")
  private String userModification;

  @Enumerated(EnumType.STRING)
  @Column(name = "hsecusestate")
  private UserStateEnum state;

  @Column(name = "hsecuseidperson")
  private Long idPerson;

  // Campo de auditoria propio de la entidad historica
  @Column(name = "hsecusehistcreationdate")
  private LocalDateTime histCreationDate;

  // Campo de auditoria propio de la entidad historica
  @Enumerated(EnumType.STRING)
  @Column(name = "hsecuseoperation")
  private HistoricalOperationEnum operation;

}
