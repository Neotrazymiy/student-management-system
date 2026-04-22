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

import spring.model.Faculty;
import spring.model.University;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
class FacultyRepositoryTest {

	@Autowired
	private FacultyRepository facultyRepository;

	@Autowired
	private UniversityRepository universityRepository;

	private static final String FACULTY_NAME_ONE = "Faculty One";
	private static final String FACULTY_NAME_THREE = "Faculty Three";
	private static final UUID FACULTY_ID_NOT_EXISTING = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private static final UUID UNIVERSITY_ID_EXISTS = UUID.fromString("00000000-0000-0000-0000-000000000001");
	private static final UUID FACULTY_ID_ONE = UUID.fromString("00000000-0000-0000-0000-000000000011");

	@Test
	void testAddFaculty() {
		University university = universityRepository.findById(UNIVERSITY_ID_EXISTS)
				.orElseThrow(() -> new RuntimeException());
		universityRepository.save(university);
		Faculty faculty = new Faculty();
		faculty.setName(FACULTY_NAME_THREE);
		faculty.setUniversity(university);
		assertTrue(facultyRepository.save(faculty).getName().equals(FACULTY_NAME_THREE));
	}

	@Test
	void testGetFacultyById_exists() {
		assertTrue(facultyRepository.findById(FACULTY_ID_ONE).get().getName().equals(FACULTY_NAME_ONE));
	}

	@Test
	void testGetFacultyById_NotExists() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> facultyRepository.findById(FACULTY_ID_NOT_EXISTING).map(Faculty::getName).orElseThrow(() -> {
					throw new RuntimeException("Такого id нет. " + FACULTY_ID_NOT_EXISTING);
				}));
		assertTrue(exception.getMessage().contains("Такого id нет. " + FACULTY_ID_NOT_EXISTING));
	}

	@Test
	void testGetFacultyByName() {
		assertTrue(facultyRepository.findByName(FACULTY_NAME_ONE).get().getName().equals(FACULTY_NAME_ONE));
	}

	@Test
	void testGetAllFaculty() {
		List<Faculty> universities = facultyRepository.findAll();
		assertNotNull(universities);
		assertFalse(universities.isEmpty());
		assertTrue(2 == universities.size());
	}

	@Test
	@Transactional
	void testDeleteFacultyById() {
		Faculty faculty = facultyRepository.findById(FACULTY_ID_ONE).orElseThrow(() -> new RuntimeException());
		facultyRepository.deleteById(faculty.getId());
		assertFalse(facultyRepository.existsById(faculty.getId()));
	}
}
