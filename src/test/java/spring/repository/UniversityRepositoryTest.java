package spring.repository;

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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import spring.model.University;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
class UniversityRepositoryTest {

	@Autowired
	private UniversityRepository universityRepository;

	private static final String UNIVERSITY_NAME_ONE = "University One";
	private static final String UNIVERSITY_NAME_THREE = "University Three";
	private static final UUID UNIVERSITY_ID_NOT_EXISTING = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private static final UUID UNIVERSITY_ID_ONE = UUID.fromString("00000000-0000-0000-0000-000000000001");

	@Test
	void testAddUniversity() {
		University university = new University();
		university.setName(UNIVERSITY_NAME_THREE);
		assertTrue(universityRepository.save(university).getName().equals(UNIVERSITY_NAME_THREE));
	}

	@Test
	void testGetUniversityById_exists() {
		assertTrue(universityRepository.findById(UNIVERSITY_ID_ONE).get().getName().equals(UNIVERSITY_NAME_ONE));
	}

	@Test
	void testGetUniversityById_NotExists() {
		RuntimeException exception = assertThrows(RuntimeException.class, () -> universityRepository
				.findById(UNIVERSITY_ID_NOT_EXISTING).map(University::getName).orElseThrow(() -> {
					throw new RuntimeException("Такого id нет. " + UNIVERSITY_ID_NOT_EXISTING);
				}));
		assertTrue(exception.getMessage().contains("Такого id нет. " + UNIVERSITY_ID_NOT_EXISTING));
	}

	@Test
	void testGetUniversityByName() {
		assertTrue(universityRepository.findByName(UNIVERSITY_NAME_ONE).get().getName().equals(UNIVERSITY_NAME_ONE));
	}

	@Test
	void testGetAllUniversity() {
		List<University> universities = universityRepository.findAll();
		assertNotNull(universities);
		assertFalse(universities.isEmpty());
		assertTrue(2 == universities.size());
	}

	@Test
	@Transactional
	void testDeleteUniversityById() {
		University university = new University();
		university.setName(UNIVERSITY_NAME_THREE);
		university = universityRepository.save(university);
		universityRepository.deleteById(university.getId());
		universityRepository.flush();
		assertFalse(universityRepository.existsById(university.getId()));
	}
}
