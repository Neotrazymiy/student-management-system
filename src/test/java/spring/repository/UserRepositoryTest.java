package spring.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import spring.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	private static final String USER_NAME_ONE = "teacher1";
	private static final String USER_NAME_THREE = "User Three";
	private static final UUID USER_ID_NOT_EXISTING = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private static final UUID USER_ID_ONE = UUID.fromString("00000000-0000-0000-0000-000000000091");

	@Test
	void testAddUser() {
		User user = userRepository.findById(USER_ID_ONE).orElseThrow(() -> new RuntimeException());
		user.setUserName(USER_NAME_THREE);
		user = userRepository.save(user);
		assertTrue(USER_NAME_THREE.equals(user.getUserName()));
	}

	@Test
	void testGetUserById_exists() {
		assertTrue(userRepository.findById(USER_ID_ONE).get().getUserName().equals(USER_NAME_ONE));
	}

	@Test
	void testGetUserById_NotExists() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userRepository.findById(USER_ID_NOT_EXISTING).map(User::getUserName).orElseThrow(() -> {
					throw new RuntimeException("Такого id нет. " + USER_ID_NOT_EXISTING);
				}));
		assertTrue(exception.getMessage().contains("Такого id нет. " + USER_ID_NOT_EXISTING));
	}

	@Test
	void testGetUserByName() {
		assertTrue(userRepository.findByUserName(USER_NAME_ONE).get().getUserName().equals(USER_NAME_ONE));
	}

	@Test
	void testGetAllUser() {
		List<User> universities = userRepository.findAll();
		assertNotNull(universities);
		assertFalse(universities.isEmpty());
		assertTrue(5 == universities.size());
	}

	@Test
	void testGetAllPageUser() {
		Pageable pageable = PageRequest.of(0, 2);
		Page<User> page = userRepository.findAll(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getContent()).hasSize(2);
		assertThat(page.getTotalElements()).isEqualTo(5);
		assertThat(page.getContent().get(0).getUserName()).contains(USER_NAME_ONE);
	}

	@Test
	@Transactional
	void testDeleteUserById() {
		User user = new User();
		user.setUserName(USER_NAME_ONE + USER_NAME_ONE);
		user.setEmail(USER_NAME_ONE);
		user.setFirstName(USER_NAME_ONE);
		user.setLastName(USER_NAME_ONE);
		user.setEnabled(true);
		user.setPasswordHash(USER_NAME_ONE);
		user.setFailedAttempts(0);
		userRepository.save(user);
		userRepository.deleteById(user.getId());
		userRepository.flush();
		assertFalse(userRepository.existsById(user.getId()));
	}
}
