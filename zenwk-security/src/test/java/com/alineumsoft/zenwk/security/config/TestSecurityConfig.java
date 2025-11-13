package com.alineumsoft.zenwk.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.alineumsoft.zenwk.security.enums.RoleEnum;

@Configuration
@Profile("test")
@ConditionalOnProperty(name = "security.test-config.enabled", havingValue = "true")
public class TestSecurityConfig {
	private final static String USER_NAME_PROPERTY = "${spring.security.user.name}";
	private final static String USER_PASSWORD_PROPERTY = "${spring.security.user.password}";

	@Value(USER_NAME_PROPERTY)
	private String userName;

	@Value(USER_PASSWORD_PROPERTY)
	private String userPassword;

	/**
	 * <p>
	 * <b>Security: </b> usuario ambiente de pruebas desarrollo
	 * </p>
	 * 
	 * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
	 * @param passwordEncoder
	 * @return
	 */
	@Bean
	public UserDetailsService userDetailService(PasswordEncoder passwordEncoder) {
		User.UserBuilder users = User.builder();
		UserDetails userTemp = users.username(userName).password(passwordEncoder.encode(userPassword))
				.authorities(RoleEnum.USER.name()).build();
		return new InMemoryUserDetailsManager(userTemp);
	}

}
