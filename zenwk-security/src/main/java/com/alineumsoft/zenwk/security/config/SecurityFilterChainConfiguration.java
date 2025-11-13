package com.alineumsoft.zenwk.security.config;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import com.alineumsoft.zenwk.security.auth.jwt.JwtAuthenticationFilter;
import com.alineumsoft.zenwk.security.auth.service.PermissionService;
import com.alineumsoft.zenwk.security.common.enums.PermissionOperationEnum;
import com.alineumsoft.zenwk.security.dto.PermissionDTO;
import com.alineumsoft.zenwk.security.enums.HttpMethodResourceEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Configuración de seguridad para el control de acceso basado en permisos y roles. Define la
 * configuración de Spring Security y la asignación de permisos a las rutas.
 * </p>
 * 
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class SecurityConfiguration
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityFilterChainConfiguration {
  public static final String CORS_AUTH_ALLOWED_ORIGINS = "${cors.allowed-origins}";
  /**
   * Componenete para jwt
   */
  private final JwtAuthenticationFilter jwtAuthFilter;
  /**
   * AuthenticationProvider para la autenticacion del usuario
   */
  private final AuthenticationProvider authenticationProvider;
  /**
   * Servicio para la gestion de permisos
   */
  private final PermissionService permService;
  /**
   * Manejador para los errores de acceso denegado
   */
  private final AccessDeniedHandler customAccessDeniedHandler;
  /**
   * Filtro protección CSRF
   */
  private final CsrfValidationFilter csrfValidationFilter;
  /**
   * Configuracion CORS
   */
  @Value(CORS_AUTH_ALLOWED_ORIGINS)
  private String allowedOrigins;


  /**
   * <p>
   * Configuración CORS para el consumo dese del front
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @return
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    log.info("Allowed origins: {}", allowedOrigins);
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
    config.setAllowedMethods(List.of("POST", "GET", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(
        List.of("Authorization", "content-type", "X-XSRF-TOKEN", "X-USER-EMAIL"));
    config.setAllowCredentials(true);
    config.addExposedHeader("Location");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  /**
   * <p>
   * <b> CU001_XX_Filtro_XSS </b> Registra el filtro de protección contra ataques XSS (Cross Site
   * Scripting). Este filtro intercepta las peticiones HTTP y limpia los parámetros de entrada
   * potencialmente peligrosos antes de ser procesados por los controladores.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param xssFilter
   * @return
   */
  @Bean
  public FilterRegistrationBean<XSSFilter> xssFilterRegistration(XSSFilter xssFilter) {
    FilterRegistrationBean<XSSFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(xssFilter);
    registration.setName("xssFilter");
    registration.addUrlPatterns("/*");
    // Se ejecuta antes del CSRF y JWT (menor número = mayor prioridad)
    registration.setOrder(1);
    return registration;
  }

  /**
   * 
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Configura la seguridad de la aplicación utilizando
   * Spring Security. Define las reglas de autorización de acceso a los endpoints agrupando por
   * operación.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param http
   * @return
   * @throws Exception
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    final Map<PermissionOperationEnum, List<PermissionDTO>> maRolPermissions =
        permService.getOperationPermission();
    // Usa la configuraciópn por defecto: @Bean corsConfigurationSource()
    http.headers(headers -> headers
        // (protección XSS spring.security )
        // Política moderna: evita ejecución de JS inyectado o inline
        .contentSecurityPolicy(
            csp -> csp.policyDirectives("default-src 'self'; " + "script-src 'self'; "
                + "object-src 'none'; " + "style-src 'self' 'unsafe-inline'; " + "base-uri 'self'; "
                + "frame-ancestors 'none'; " + "form-action 'self'; " + "img-src 'self' data:;"))
        // Evita que se envíen referrers a otros dominios
        .referrerPolicy(ref -> ref.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)))
        // csrf automatico deshabilitado para hacer uso del custom
        .csrf(AbstractHttpConfigurer::disable)
        // CORS (usa el bean @Bean corsConfigurationSource())
        .cors(Customizer.withDefaults())
        // Configurar las reglas de autorización
        .authorizeHttpRequests(request -> configureAuthorizationRules(request, maRolPermissions))
        // CSRF con repositorio basado en cookie (o usa tu filtro personalizado si aplica)
        .addFilterBefore(csrfValidationFilter, CorsFilter.class)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        // Política de sesión sin estado
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Autenticador principal
        .authenticationProvider(authenticationProvider)
        // Manejador de errores de acceso personalizado
        .exceptionHandling(exception -> exception.accessDeniedHandler(customAccessDeniedHandler));

    return http.build();
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Configura las reglas de autorización por path y
   * método HTTP
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param maRolPermissions
   */
  private void configureAuthorizationRules(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry request,
      Map<PermissionOperationEnum, List<PermissionDTO>> maRolPermissions) {

    // Endpoints públicos
    request
        .requestMatchers(HttpMethodResourceEnum.USER_CREATE.getMethod(),
            HttpMethodResourceEnum.USER_CREATE.getResource())
        .permitAll()
        .requestMatchers(HttpMethodResourceEnum.USER_JWT_ME.getMethod(),
            HttpMethodResourceEnum.USER_JWT_ME.getResource())
        .permitAll()
        .requestMatchers(HttpMethodResourceEnum.AUTH_LOGIN.getMethod(),
            HttpMethodResourceEnum.AUTH_LOGIN.getResource())
        .permitAll().requestMatchers(HttpMethodResourceEnum.VERIFICATION_TOKEN.getResource())
        .permitAll()
        .requestMatchers(HttpMethodResourceEnum.USER_GET_EMAIL.getMethod(),
            HttpMethodResourceEnum.USER_GET_EMAIL.getResource())
        .permitAll()
        .requestMatchers(HttpMethodResourceEnum.SEX_LIST_OPTIONS.getMethod(),
            HttpMethodResourceEnum.SEX_LIST_OPTIONS.getResource())
        .permitAll()
        // .requestMatchers(HttpMethodResourceEnum.AUTH_REFRESH_JWT.getMethod(),
        // HttpMethodResourceEnum.AUTH_REFRESH_JWT.getResource())
        // .permitAll()
        .requestMatchers(HttpMethodResourceEnum.AUTH_RESET_PASSWORD.getMethod(),
            HttpMethodResourceEnum.AUTH_RESET_PASSWORD.getResource())
        .permitAll().requestMatchers(HttpMethodResourceEnum.ACTUATOR.getResource()).permitAll();

    // Aplicar permisos basados en roles y operaciones
    addAuthorizationForOperation(request, maRolPermissions);

    // Todo lo demás requiere autenticación
    request.anyRequest().authenticated();
  }


  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Agrega restricciones solo si hay permisos en el mapa
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param request
   * @param maRolPermissions
   */
  private void addAuthorizationForOperation(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry request,
      Map<PermissionOperationEnum, List<PermissionDTO>> maRolPermissions) {
    if (!maRolPermissions.isEmpty()) {
      request
          .requestMatchers(
              getRequestMatchersForOperation(PermissionOperationEnum.CREATE, maRolPermissions))
          .hasAnyAuthority(getRoles(maRolPermissions.get(PermissionOperationEnum.CREATE)))
          .requestMatchers(
              getRequestMatchersForOperation(PermissionOperationEnum.UPDATE, maRolPermissions))
          .hasAnyAuthority(getRoles(maRolPermissions.get(PermissionOperationEnum.UPDATE)))
          .requestMatchers(
              getRequestMatchersForOperation(PermissionOperationEnum.DELETE, maRolPermissions))
          .hasAnyAuthority(getRoles(maRolPermissions.get(PermissionOperationEnum.DELETE)))
          .requestMatchers(
              getRequestMatchersForOperation(PermissionOperationEnum.LIST, maRolPermissions))
          .hasAnyAuthority(getRoles(maRolPermissions.get(PermissionOperationEnum.LIST)))
          .requestMatchers(
              getRequestMatchersForOperation(PermissionOperationEnum.GET, maRolPermissions))
          .hasAnyAuthority(getRoles(maRolPermissions.get(PermissionOperationEnum.GET)));
    }
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Obtiene los `RequestMatcher` correspondientes a una
   * operación especifica, eliminando rutas duplicadas.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param operation
   * @param mapPermission
   * @return
   */
  private RequestMatcher[] getRequestMatchersForOperation(PermissionOperationEnum operation,
      Map<PermissionOperationEnum, List<PermissionDTO>> mapPermission) {
    // Obtener los permisos asociados al rol
    List<PermissionDTO> listPermissions = mapPermission.get(operation);

    // Si no hay permisos, devolver un array vacio
    if (listPermissions == null || listPermissions.isEmpty()) {
      return new RequestMatcher[0];
    }

    // Elminar permisos duplicados por la consulta.
    // Convertir los permisos en RequestMatchers[].
    return listPermissions.stream()
        .map(perm -> new AntPathRequestMatcher(perm.getResource(), perm.getMethod().toUpperCase()))
        .collect(Collectors.toCollection(LinkedHashSet::new)).toArray(RequestMatcher[]::new);
  }

  /**
   * <p>
   * <b> CU001_Seguridad_Creacion_Usuario </b> Obtiene los roles asociados a una lista de permisos
   * eliminando duplicados.
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param listPermission
   * @return
   */
  private String[] getRoles(List<PermissionDTO> listPermission) {
    // Elimina roles duplicados por la consulta original.
    // Convierte una lista de roles en un String[]
    return listPermission.stream().map(PermissionDTO::getName)
        .collect(Collectors.toCollection(LinkedHashSet::new)).toArray(String[]::new);

  }

}
