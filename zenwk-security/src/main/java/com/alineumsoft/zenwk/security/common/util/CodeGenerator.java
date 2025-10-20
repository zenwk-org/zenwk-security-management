package com.alineumsoft.zenwk.security.common.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * <p>
 * Generador de códigos
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project verification-zenwk
 * @class codeGenerator
 */
public class CodeGenerator {
  /**
   * random java.util.secureRandom
   */
  private static final SecureRandom random = new SecureRandom();


  /**
   * 
   * <p>
   * <b> CU003_Gestionar token de verificación. </b> Generador de token con criptografía mas fuerte
   * que la usada en this.generateUUID().
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param size
   * @return
   */
  public static String generateCode(int size) {
    byte[] randomBytes = new byte[size];
    random.nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes).substring(0, size);
  }

  /**
   * 
   * <p>
   * <b> CU001_Gestionar token de verificación </b> Genera UUID aleatorio con criptografía debil
   * para ser utilizado en url con parmetros solo accedidas desde correo le usuario
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  public static String generateUUID() {
    UUID uuid = UUID.randomUUID();
    return uuid.toString();

  }

}
