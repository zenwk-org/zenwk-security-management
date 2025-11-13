package com.alineumsoft.zenwk.security.controller;

import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import com.alineumsoft.zenwk.security.enums.HttpMethodResourceEnum;
import com.alineumsoft.zenwk.security.person.dto.CreatePersonDTO;
import com.alineumsoft.zenwk.security.person.dto.PagePersonDTO;
import com.alineumsoft.zenwk.security.person.dto.PersonDTO;
import com.alineumsoft.zenwk.security.person.service.PersonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class PersonController
 */
@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {
  /**
   * Servicio de controlador
   */
  private final PersonService personService;

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Servicio REST para la creacion de una persona en el
   * sistema
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @param request
   * @param uri
   * @param userDetails
   * @return
   * @throws JsonProcessingException
   */
  @PostMapping
  public ResponseEntity<Void> createPerson(@Validated @RequestBody CreatePersonDTO dto,
      HttpServletRequest request, UriComponentsBuilder uriCB,
      @AuthenticationPrincipal UserDetails userDetails) {
    Long idPerson = personService.createPerson(dto, request, userDetails);
    URI location = uriCB.path(HttpMethodResourceEnum.PERSON_GET.getResource())
        .buildAndExpand(idPerson).toUri();
    return ResponseEntity.created(location).build();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Servcio REST para la actualizacion de una persona
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @param dto
   * @param request
   * @param userDetails
   * @return
   */
  @PutMapping("/{idPerson}")
  public ResponseEntity<Void> updatePerson(@PathVariable Long idPerson,
      @Validated @RequestBody PersonDTO dto, HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    personService.updatePerson(idPerson, dto, request, userDetails);
    return ResponseEntity.noContent().build();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Servicio REST para la eliminación de una persona
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @param request
   * @param userDetails
   * @return
   */
  @DeleteMapping("/{idPerson}")
  public ResponseEntity<Void> deletePerson(@PathVariable Long idPerson, HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {

    personService.deletePerson(idPerson, request, userDetails);
    return ResponseEntity.noContent().build();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Servicio REST retorna la busqueda de una persona por
   * id
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idPerson
   * @param request
   * @param userDetails
   * @return
   */
  @GetMapping("/{idPerson}")
  public ResponseEntity<CreatePersonDTO> findById(@PathVariable Long idPerson,
      HttpServletRequest request, @AuthenticationPrincipal UserDetails userDetails) {
    return ResponseEntity.ok(personService.findByIdPerson(idPerson, request, userDetails));
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Servicio REST retorna la busqueda de todas las
   * personas guardadas en el sistema
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param idUser
   * @param request
   * @param userDetails
   * @return
   */
  @GetMapping
  public ResponseEntity<PagePersonDTO> getAllPersons(HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails, Pageable pegeable) {
    return ResponseEntity.ok(personService.getAllPersons(pegeable, request, userDetails));
  }


  /**
   * 
   * <p>
   * <b>CU001_Seguridad_Creacion_Usuario </b> Servicio para cargar la foto de perfil de usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param userDetails
   * @param file
   * @return
   */
  @PostMapping("/profile/upload-photo")
  public ResponseEntity<Void> uploadPhotoProfile(HttpServletRequest request,
      @AuthenticationPrincipal UserDetails userDetails, @RequestParam MultipartFile file) {
    personService.uploadPhotoProfile(request, userDetails, file);
    return ResponseEntity.noContent().build();
  }

}
