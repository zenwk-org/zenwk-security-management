package com.alineumsoft.zenwk.security.person.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.alineumsoft.zenwk.security.enums.SecurityExceptionEnum;
import com.alineumsoft.zenwk.security.person.entity.PersonSex;
import com.alineumsoft.zenwk.security.person.repository.PersonSexRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Servicio para la entidad sec_person_sex
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project zenwk-security
 * @class PersonSexService
 */
@Service
@RequiredArgsConstructor
public class PersonSexService {

  /**
   * Referencia al respositorio de la entidad.
   */
  private final PersonSexRepository repository;


  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Retorna la lista sex a desplegar en el front.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  public List<PersonSex> getAllForSelect() {
    return repository.findAllByOrderByDescriptionAsc();
  }

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Busca un sexo por su id.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param id
   * @return
   */
  public PersonSex findPersonSexById(Long id) {
    return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(
        SecurityExceptionEnum.FUNC_PERSON_SEX_NO_FOUND.getCodeMessage(id.toString())));
  }
}
