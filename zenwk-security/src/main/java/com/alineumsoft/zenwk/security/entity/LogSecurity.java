package com.alineumsoft.zenwk.security.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class LogSecurityUser
 */
@Entity(name = "log_sec")
@Data
public class LogSecurity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "logsecuseid")
  private Long idLogUser;

  @Column(name = "logsecusemethod")
  private String method;

  @Column(name = "logsecusestatuscode")
  private Integer statusCode;

  @Column(name = "logsecuseurl")
  private String url;

  @Column(name = "logsecuserequest")
  private String request;

  @Column(name = "logsecuseresponse")
  private String response;

  @Column(columnDefinition = "TEXT", name = "logsecuseerrormessage")
  private String errorMessage;

  @Column(name = "logsecusecreationdate")
  private LocalDateTime creationDate;

  @Column(name = "logsecuseusercreation")
  private String userCreation;


  @Column(name = "logsecuseipaddress")
  private String ipAddress;

  @Column(name = "logsecuseuseragent")
  private String userAgent;

  @Column(name = "logsecuseexecutiontime")
  private String executionTime;

  @Column(name = "logsecuseservicename")
  private String serviceName;
}
