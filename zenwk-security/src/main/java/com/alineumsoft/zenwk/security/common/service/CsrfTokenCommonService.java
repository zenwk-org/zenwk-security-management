package com.alineumsoft.zenwk.security.common.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.alineumsoft.zenwk.security.common.constants.GeneralConstants;
import com.alineumsoft.zenwk.security.common.dto.TokenDTO;
import com.alineumsoft.zenwk.security.common.entity.CsrfToken;
import com.alineumsoft.zenwk.security.common.exception.enums.CoreExceptionEnum;
import com.alineumsoft.zenwk.security.common.helper.ApiRestSecurityHelper;
import com.alineumsoft.zenwk.security.common.repository.CsrfTokenRepository;
import com.alineumsoft.zenwk.security.common.util.CodeGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Service común para gestión del token CSRF
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project zenwk-verification
 * @class CsrfTokenService
 */
@Service
@RequiredArgsConstructor
public class CsrfTokenCommonService extends ApiRestSecurityHelper {

  /**
   * Repositorio para los tokens csrf
   */
  private final CsrfTokenRepository csrfTokenRepo;

  /**
   * 
   * <p>
   * <b> CU00X_Gestionar protección contra ataques CSRF </b> Genera el token csrf
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @return
   */
  public TokenDTO generateCsrfToken(TokenDTO dto, HttpServletRequest request, String username) {
    TokenDTO outDTO = generateTokenDTO(dto.getEmail());
    CsrfToken csrfToken = new CsrfToken(outDTO, username, getUserAgent(request));
    csrfTokenRepo.save(csrfToken);
    return outDTO;
  }

  /**
   * 
   * <p>
   * <b> CU00X_Gestionar protección contra ataques CSRF </b> Valida el token csrf
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @return
   */
  public CsrfToken validateCsrfToken(TokenDTO dto) {
    CsrfToken csrfToken = csrfTokenRepo.findByEmailAndCode(dto.getEmail(), dto.getCode())
        .orElseThrow(() -> new EntityNotFoundException(
            CoreExceptionEnum.FUNC_VERIFICATION_TOKEN_CSRF_NOT_FOUND.getCodeMessage(dto.getEmail(),
                "[" + dto.getCode() + "]")));

    String messageError = validateToken(csrfToken);
    if (!messageError.isEmpty()) {
      throw new IllegalArgumentException(messageError);
    }

    return csrfToken;
  }


  /**
   * 
   * <p>
   * <b> CU00X_Gestionar protección contra ataques CSRF </b> Validacion del token
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param dto
   * @param token
   * @return
   */
  private String validateToken(CsrfToken token) {
    String messageError = "";
    if (token.getExpirationDate().isBefore(LocalDateTime.now())) {
      messageError = CoreExceptionEnum.FUNC_VERIFICATION_TOKEN_CSRF_EXPIRATION.getCodeMessage();
    } else if (token.isRevoked()) {
      messageError = CoreExceptionEnum.FUNC_VERIFICATION_TOKEN_CSRF_REVOKED.getCodeMessage();
    }
    return messageError;
  }


  /**
   * <p>
   * <b> CU00X_Gestionar protección contra ataques CSRF </b> Genera el dto con los datos del token
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param email
   * @return
   */
  private TokenDTO generateTokenDTO(String email) {
    TokenDTO dto = new TokenDTO();
    dto.setEmail(email);
    dto.setCode(CodeGenerator.generateCode(GeneralConstants.TOKEN_CSRF_CODE_ZISE));
    dto.setExpirationDate(
        LocalDateTime.now().plusMinutes(GeneralConstants.TOKEN_CSRF_CODE_MINUTES));
    return dto;
  }


}
