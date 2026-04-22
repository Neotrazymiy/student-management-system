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

import spring.model.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
class RoleRepositoryTest {

	@Autowired
	private RoleRepository roleRepository;

	private static final String ROLE_NAME_ONE_ORIGINAL = "ROLE_STUDENT";
	private static final String ROLE_NAME_TWO_ORIGINAL = "ROLE_TEACHER";
	private static final String ROLE_NAME_THREE = "Role Three";
	private static final UUID ROLE_ID_NOT_EXISTING = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private static final UUID ROLE_ID_ONE = UUID.fromString("00000000-0000-0000-0000-000000000061");

	@Test
	void testAddRole() {
		Role role = roleRepository.findByName(ROLE_NAME_ONE_ORIGINAL).get();
		role.setName(ROLE_NAME_THREE);
		assertTrue(roleRepository.save(role).getName().equals(ROLE_NAME_THREE));
	}

	@Test
	void testGetRoleById_exists() {
		assertTrue(roleRepository.findById(ROLE_ID_ONE).get().getName().equals(ROLE_NAME_ONE_ORIGINAL));
	}

	@Test
	void testGetRoleById_NotExists() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> roleRepository.findById(ROLE_ID_NOT_EXISTING).map(Role::getName).orElseThrow(() -> {
					throw new RuntimeException("Такого id нет. " + ROLE_ID_NOT_EXISTING);
				}));
		assertTrue(exception.getMessage().contains("Такого id нет. " + ROLE_ID_NOT_EXISTING));
	}

	@Test
	void testGetRoleByName() {
		assertTrue(roleRepository.findByName(ROLE_NAME_ONE_ORIGINAL).get().getName().equals(ROLE_NAME_ONE_ORIGINAL));
	}

	@Test
	void testGetAllRole() {
		List<Role> universities = roleRepository.findAll();
		assertNotNull(universities);
		assertFalse(universities.isEmpty());
		assertTrue(5 == universities.size());
	}

	@Test
	void testGetAllPageRole() {
		Pageable pageable = PageRequest.of(0, 2);
		Page<Role> page = roleRepository.findAll(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getContent()).hasSize(2);
		assertThat(page.getTotalElements()).isEqualTo(5);
		assertThat(page.getContent().get(0).getName()).contains(ROLE_NAME_ONE_ORIGINAL);
		assertThat(page.getContent().get(1).getName()).contains(ROLE_NAME_TWO_ORIGINAL);
	}

	@Test
	@Transactional
	void testDeleteRoleById() {
		Role role = roleRepository.findByName(ROLE_NAME_ONE_ORIGINAL).get();
		role.setName(ROLE_NAME_THREE);
		role = roleRepository.save(role);
		roleRepository.deleteById(role.getId());
		roleRepository.flush();
		assertFalse(roleRepository.existsById(role.getId()));
	}

}
