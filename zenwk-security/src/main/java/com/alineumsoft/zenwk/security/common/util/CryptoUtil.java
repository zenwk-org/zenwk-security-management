package com.alineumsoft.zenwk.security.common.util;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class CryptoUtil
 */
public final class CryptoUtil {
  private final static PasswordEncoder BCRYPT_ENCODER = new BCryptPasswordEncoder();
  private final static String AES_ALGORITHM = "AES";

  /**
   * <p>
   * Constructor
   * </p>
   */
  private CryptoUtil() {

  }

  /**
   * <p>
   * <b> CryptoUtil: </b> Encriptacion con BCryptPasswordEncodera
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param rawPassword
   * @return
   */
  public static String encryptPassword(String rawPassword) {
    return rawPassword != null ? BCRYPT_ENCODER.encode(rawPassword) : null;
  }

  /**
   * <p>
   * <b> CryptoUtil: </b> Verifica si un password coincide con su hash encriptado.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param rawPassword
   * @param encodePassword
   * @return
   */
  public static boolean matchesPassword(String rawPassword, String encodePassword) {
    return BCRYPT_ENCODER.matches(rawPassword, encodePassword);
  }

  /**
   * 
   * <p>
   * <b> CryptoUtil: </b> Encripta un texto usando AES
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param rawText
   * @param secretKey
   * @return
   * @throws Exception
   */
  public static String encryptAES(String rawText, String secretKey) throws Exception {
    Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
    SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), AES_ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, keySpec);
    byte[] encryptedBytes = cipher.doFinal(rawText.getBytes());
    return Base64.getEncoder().encodeToString(encryptedBytes);
  }

  /**
   * <p>
   * <b> CryptoUtil: </b> Desencripta un texto cifrado en AES
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param encryptedText
   * @param secretKey
   * @return
   * @throws Exception
   */
  public static String decryptAES(String encryptedText, String secretKey) throws Exception {
    Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
    SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), AES_ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, keySpec);
    byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
    byte[] originalBytes = cipher.doFinal(decodedBytes);
    return new String(originalBytes);
  }

  /**
   * <p>
   * <b> CryptoUtil: </b> Genera una clave secreta AES de 16 bytes
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   * @throws Exception
   */
  public static String generateAESSecretKey() throws Exception {
    KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
    keyGen.init(128);
    SecretKey secretKey = keyGen.generateKey();
    return Base64.getEncoder().encodeToString(secretKey.getEncoded());
  }

}
