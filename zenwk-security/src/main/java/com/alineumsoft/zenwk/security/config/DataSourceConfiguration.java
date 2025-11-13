package com.alineumsoft.zenwk.security.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import com.zaxxer.hikari.HikariDataSource;

@Configuration(proxyBeanMethods = false)
@Profile({"default", "dev", "local"})
public class DataSourceConfiguration {
  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Clase configuracion de datasource
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  @Bean
  @Primary
  @ConfigurationProperties("app.datasource")
  public DataSourceProperties dataSourceProperties() {
    return new DataSourceProperties();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Configuracion pool de conexiones
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  @Bean
  @ConfigurationProperties("app.datasource.hikari")
  public HikariDataSource dataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

}
