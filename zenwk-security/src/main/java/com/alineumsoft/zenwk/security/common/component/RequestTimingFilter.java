package com.alineumsoft.zenwk.security.common.component;

import java.io.IOException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Filtro para la medicion de la solicitud antes del filtro JwtAuthenticationFilter. Se establece
 * orden como primero.
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project security-zenwk
 * @class RequestTimingFilter
 */
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestTimingFilter extends OncePerRequestFilter {
  /**
   * Hilo local para la medicion
   */
  private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

  /**
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param response
   * @param filterChain
   * @throws ServletException
   * @throws IOException
   * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(jakarta.servlet.http.HttpServletRequest,
   *      jakarta.servlet.http.HttpServletResponse, jakarta.servlet.FilterChain)
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    startTime.set(System.currentTimeMillis());
    filterChain.doFilter(request, response);
  }

  /**
   * <p>
   * <b> General </b> Obtener timepo incial
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  public static Long getStartTime() {
    return startTime.get();
  }

  /**
   * <p>
   * <b> General </b> Remueve el hilo actual
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   */
  public static void destroyStartTime() {
    startTime.remove();
  }

}
