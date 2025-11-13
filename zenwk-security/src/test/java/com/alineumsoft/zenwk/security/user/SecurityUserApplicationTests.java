//package com.alineumsoft.zenwk.security.user;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.time.LocalDateTime;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.annotation.DirtiesContext;
//
//import com.alineumsoft.zenwk.security.user.model.User;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class SecurityUserApplicationTests {
//
//	@Autowired
//	TestRestTemplate testRestTemplate;
//
//	@Test
//	@DirtiesContext
//	void createNewUser() {
//		ResponseEntity<Void> createResponse = testRestTemplate.postForEntity("/user", getUser1(), Void.class);
//		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//	}
//
//	/**
//	 * 
//	 * <p>
//	 * <b> Metodo utilitario que genera un usuario generico </b>
//	 * </p>
//	 * 
//	 * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
//	 * @return
//	 */
//	private User getUser1() {
//		User userTemp = new User();
//		userTemp.setIdUsuario(1L);
//		userTemp.setNombre("Carlos");
//		userTemp.setApellido("Gómez");
//		userTemp.setEmail("carlos.gomez@example.com");
//		userTemp.setUsername("cgomez");
//		userTemp.setPassword("cgomez123");
//		userTemp.setEstado("ACTIVO");
//		userTemp.setFechaCreacion(LocalDateTime.parse("2024-12-01T10:15:30"));
//		userTemp.setFechaModificacion(LocalDateTime.parse("2024-12-05T14:20:45"));
//		userTemp.setUsuarioCreacion("admin");
//		userTemp.setUsuarioModificacion("admin");
//		return userTemp;
//	}
//
//}
