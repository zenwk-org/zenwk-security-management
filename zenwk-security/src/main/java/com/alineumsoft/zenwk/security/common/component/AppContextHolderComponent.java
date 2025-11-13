package com.alineumsoft.zenwk.security.common.component;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * <b> Clase que proporciona acceso estatico al ApplicationContext de Spring en cualquier parte de
 * la aplicacion</b>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class AppContextHolderComponent
 */
@Component
public class AppContextHolderComponent implements ApplicationContextAware {
  private static ApplicationContext context;

  /**
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param applicationContext
   * @throws BeansException
   * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
   */
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    context = applicationContext;
  }

  /**
   * <p>
   * <b> Historical: </b> Obtiene el bean si esta en el contexto
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param <T>
   * @param clazz
   * @return
   */
  public static <T> T getBean(Class<T> clazz) {
    return context.getBean(clazz);
  }
}
