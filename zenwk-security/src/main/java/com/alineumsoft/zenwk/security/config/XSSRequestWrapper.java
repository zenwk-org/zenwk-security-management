package com.alineumsoft.zenwk.security.config;


import org.apache.commons.text.StringEscapeUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project zenwk-security
 * @class XSSRequestWrapper
 */
public class XSSRequestWrapper extends HttpServletRequestWrapper {
  /**
   * 
   * <p>
   * <b> Super constructor </b>
   * </p>
   * 
   * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   */
  public XSSRequestWrapper(HttpServletRequest request) {
    super(request);

  }

  /**
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param name
   * @return
   * @see jakarta.servlet.ServletRequestWrapper#getParameter(java.lang.String)
   */
  @Override
  public String getParameter(String name) {
    return sanitize(super.getParameter(name));
  }

  /**
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param name
   * @return
   * @see jakarta.servlet.ServletRequestWrapper#getParameterValues(java.lang.String)
   */
  @Override
  public String[] getParameterValues(String name) {
    String[] values = super.getParameterValues(name);
    if (values == null)
      return null;

    String[] sanitized = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      sanitized[i] = sanitize(values[i]);
    }
    return sanitized;
  }

  private String sanitize(String input) {
    if (input == null)
      return null;
    return StringEscapeUtils.escapeHtml4(input);
  }

}
