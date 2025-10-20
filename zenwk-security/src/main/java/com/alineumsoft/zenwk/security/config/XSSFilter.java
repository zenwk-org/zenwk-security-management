package com.alineumsoft.zenwk.security.config;

import java.io.IOException;
import org.springframework.stereotype.Component;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project zenwk-security
 * @class XSSFilter
 */
@Component
public class XSSFilter implements Filter {

  /**
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param response
   * @param chain
   * @throws IOException
   * @throws ServletException
   * @see jakarta.servlet.Filter#doFilter(jakarta.servlet.ServletRequest,
   *      jakarta.servlet.ServletResponse, jakarta.servlet.FilterChain)
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    XSSRequestWrapper sanitizedRequest = new XSSRequestWrapper(httpRequest);
    chain.doFilter(sanitizedRequest, response);
  }

}
