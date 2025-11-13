//package com.alineumsoft.zenwk.security.user;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.json.JsonTest;
//import org.springframework.boot.test.json.JacksonTester;
//
//import com.alineumsoft.zenwk.security.user.model.User;
//
//@JsonTest
//public class UserTest01 {
//	@Autowired
//	JacksonTester<User> userJson;
//
//	@Autowired
//	JacksonTester<User[]> userListJson;
//
//	@Test
//	void userSerializacionTest() throws IOException {
//		User userTemp = getUser1();
//
//		assertThat(userJson.write(userTemp)).isStrictlyEqualToJson("userSingle.json");
//
//		assertThat(userJson.write(userTemp)).hasJsonPathNumberValue("@.idUsuario");
//		assertThat(userJson.write(userTemp)).extractingJsonPathNumberValue("@.idUsuario").isEqualTo(1);
//		assertThat(userJson.write(userTemp)).extractingJsonPathStringValue("@.fechaCreacion")
//				.isEqualTo("2024-12-01T10:15:30");
//		assertThat(userJson.write(userTemp)).doesNotHaveJsonPath("@.password");
//
//	}
//
//	@Test
//	void userDeserializacion() throws IOException {
//		String expected = """
//				{"idUsuario":1,"nombre":"Carlos","apellido":"Gómez","email":"carlos.gomez@example.com",
//				"username":"cgomez","estado":"ACTIVO","fechaCreacion":"2024-12-01T10:15:30",
//				"fechaModificacion":"2024-12-05T14:20:45","usuarioCreacion":"admin","usuarioModificacion":"admin"}
//				""";
//		assertThat(userJson.parse(expected)).isEqualTo(getUser1());
//		assertThat(userJson.parseObject(expected).getIdUsuario()).isEqualTo(1);
//	}
//
//	@Test
//	void userListSerializationTest() throws IOException {
//		assertThat(userListJson.write(getUsers())).isStrictlyEqualToJson("usersList.json");
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
//		userTemp.setEstado("ACTIVO");
//		userTemp.setFechaCreacion(LocalDateTime.parse("2024-12-01T10:15:30"));
//		userTemp.setFechaModificacion(LocalDateTime.parse("2024-12-05T14:20:45"));
//		userTemp.setUsuarioCreacion("admin");
//		userTemp.setUsuarioModificacion("admin");
//		return userTemp;
//	}
//
//	/**
//	 * <p>
//	 * <b> Metodo utilitario que genera un array de usuarios generico</b>
//	 * </p>
//	 * 
//	 * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
//	 * @return
//	 */
//	private User[] getUsers() {
//		User[] users = new User[8];
//
//		// Usuario 1
//		User user1 = new User();
//		user1.setIdUsuario(1L);
//		user1.setNombre("Carlos");
//		user1.setApellido("Gómez");
//		user1.setEmail("carlos.gomez@example.com");
//		user1.setUsername("cgomez");
//		user1.setEstado("activo");
//		user1.setFechaCreacion(LocalDateTime.parse("2024-12-01T10:15:30"));
//		user1.setFechaModificacion(LocalDateTime.parse("2024-12-05T14:20:45"));
//		user1.setUsuarioCreacion("admin");
//		user1.setUsuarioModificacion("admin");
//		users[0] = user1;
//
//		// Usuario 2
//		User user2 = new User();
//		user2.setIdUsuario(2L);
//		user2.setNombre("Ana");
//		user2.setApellido("Pérez");
//		user2.setEmail("ana.perez@example.com");
//		user2.setUsername("aperez");
//		user2.setEstado("inactivo");
//		user2.setFechaCreacion(LocalDateTime.parse("2024-11-20T09:30:00"));
//		user2.setFechaModificacion(LocalDateTime.parse("2024-12-01T12:00:00"));
//		user2.setUsuarioCreacion("supervisor");
//		user2.setUsuarioModificacion("admin");
//		users[1] = user2;
//
//		// Usuario 3
//		User user3 = new User();
//		user3.setIdUsuario(3L);
//		user3.setNombre("Luis");
//		user3.setApellido("Martínez");
//		user3.setEmail("luis.martinez@example.com");
//		user3.setUsername("lmartinez");
//		user3.setEstado("activo");
//		user3.setFechaCreacion(LocalDateTime.parse("2024-11-15T08:00:00"));
//		user3.setFechaModificacion(LocalDateTime.parse("2024-11-20T10:45:00"));
//		user3.setUsuarioCreacion("admin");
//		user3.setUsuarioModificacion("supervisor");
//		users[2] = user3;
//
//		// Usuario 4
//		User user4 = new User();
//		user4.setIdUsuario(4L);
//		user4.setNombre("María");
//		user4.setApellido("Rodríguez");
//		user4.setEmail("maria.rodriguez@example.com");
//		user4.setUsername("mrodriguez");
//		user4.setEstado("activo");
//		user4.setFechaCreacion(LocalDateTime.parse("2024-11-10T11:00:00"));
//		user4.setFechaModificacion(LocalDateTime.parse("2024-12-10T16:30:00"));
//		user4.setUsuarioCreacion("user");
//		user4.setUsuarioModificacion("admin");
//		users[3] = user4;
//
//		// Usuario 5
//		User user5 = new User();
//		user5.setIdUsuario(5L);
//		user5.setNombre("Javier");
//		user5.setApellido("Fernández");
//		user5.setEmail("javier.fernandez@example.com");
//		user5.setUsername("jfernandez");
//		user5.setEstado("activo");
//		user5.setFechaCreacion(LocalDateTime.parse("2024-12-01T08:15:00"));
//		user5.setFechaModificacion(LocalDateTime.parse("2024-12-07T09:45:00"));
//		user5.setUsuarioCreacion("admin");
//		user5.setUsuarioModificacion("manager");
//		users[4] = user5;
//
//		// Usuario 6
//		User user6 = new User();
//		user6.setIdUsuario(6L);
//		user6.setNombre("Sofía");
//		user6.setApellido("López");
//		user6.setEmail("sofia.lopez@example.com");
//		user6.setUsername("slopez");
//		user6.setEstado("inactivo");
//		user6.setFechaCreacion(LocalDateTime.parse("2024-10-20T07:20:00"));
//		user6.setFechaModificacion(LocalDateTime.parse("2024-11-15T14:50:00"));
//		user6.setUsuarioCreacion("supervisor");
//		user6.setUsuarioModificacion("supervisor");
//		users[5] = user6;
//
//		// Usuario 7
//		User user7 = new User();
//		user7.setIdUsuario(7L);
//		user7.setNombre("Fernando");
//		user7.setApellido("Castro");
//		user7.setEmail("fernando.castro@example.com");
//		user7.setUsername("fcastro");
//		user7.setEstado("activo");
//		user7.setFechaCreacion(LocalDateTime.parse("2024-09-15T10:00:00"));
//		user7.setFechaModificacion(LocalDateTime.parse("2024-12-01T13:45:00"));
//		user7.setUsuarioCreacion("manager");
//		user7.setUsuarioModificacion("manager");
//		users[6] = user7;
//
//		// Usuario 8
//		User user8 = new User();
//		user8.setIdUsuario(8L);
//		user8.setNombre("Lucía");
//		user8.setApellido("Torres");
//		user8.setEmail("lucia.torres@example.com");
//		user8.setUsername("ltorres");
//		user8.setEstado("activo");
//		user8.setFechaCreacion(LocalDateTime.parse("2024-11-01T06:30:00"));
//		user8.setFechaModificacion(LocalDateTime.parse("2024-12-15T15:00:00"));
//		user8.setUsuarioCreacion("admin");
//		user8.setUsuarioModificacion("supervisor");
//		users[7] = user8;
//
//		return users;
//	}
//
//}
