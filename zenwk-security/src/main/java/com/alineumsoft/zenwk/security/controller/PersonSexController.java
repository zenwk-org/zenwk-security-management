package com.alineumsoft.zenwk.security.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alineumsoft.zenwk.security.person.entity.PersonSex;
import com.alineumsoft.zenwk.security.person.service.PersonSexService;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para la entity sec_person_sex
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project zenwk-security
 * @class PersonSexController
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/person-sex")
public class PersonSexController {
  /**
   * PersonSexService
   */
  private final PersonSexService service;


  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Retorna la lista de sexos parametrizados.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  @GetMapping
  public ResponseEntity<List<PersonSex>> getPersonSexOptions() {
    return ResponseEntity.ok(service.getAllForSelect());
  }
}
