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

import spring.model.Enrollment;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
class EnrollmentRepositoryTest {

	@Autowired
	private EnrollmentRepository enrollmentRepository;

	private static final String ENROLLMENT_NAME_ONE_ORIGINAL = "A";
	private static final String ENROLLMENT_NAME_THREE = "Enrollment Three";
	private static final UUID ENROLLMENT_ID_NOT_EXISTING = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private static final UUID ENROLLMENT_ID_ONE = UUID.fromString("00000000-0000-0000-0000-000000000081");

	@Test
	void testAddEnrollment() {
		Enrollment enrollment = enrollmentRepository.findByGrade(ENROLLMENT_NAME_ONE_ORIGINAL).get();
		enrollment.setGrade(ENROLLMENT_NAME_THREE);
		assertTrue(enrollmentRepository.save(enrollment).getGrade().equals(ENROLLMENT_NAME_THREE));
	}

	@Test
	void testGetEnrollmentById_exists() {
		assertTrue(
				enrollmentRepository.findById(ENROLLMENT_ID_ONE).get().getGrade().equals(ENROLLMENT_NAME_ONE_ORIGINAL));
	}

	@Test
	void testGetEnrollmentById_NotExists() {
		RuntimeException exception = assertThrows(RuntimeException.class, () -> enrollmentRepository
				.findById(ENROLLMENT_ID_NOT_EXISTING).map(Enrollment::getGrade).orElseThrow(() -> {
					throw new RuntimeException("Такого id нет. " + ENROLLMENT_ID_NOT_EXISTING);
				}));
		assertTrue(exception.getMessage().contains("Такого id нет. " + ENROLLMENT_ID_NOT_EXISTING));
	}

	@Test
	void testGetEnrollmentByGrade() {
		assertTrue(enrollmentRepository.findByGrade(ENROLLMENT_NAME_ONE_ORIGINAL).get().getGrade()
				.equals(ENROLLMENT_NAME_ONE_ORIGINAL));
	}

	@Test
	void testGetAllEnrollment() {
		List<Enrollment> universities = enrollmentRepository.findAll();
		assertNotNull(universities);
		assertFalse(universities.isEmpty());
		assertTrue(1 == universities.size());
	}

	@Test
	void testGetAllPageEnrollment() {
		Pageable pageable = PageRequest.of(0, 1);
		Page<Enrollment> page = enrollmentRepository.findAll(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getContent()).hasSize(1);
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getGrade()).contains(ENROLLMENT_NAME_ONE_ORIGINAL);
	}

	@Test
	@Transactional
	void testDeleteEnrollmentById() {
		Enrollment enrollment = enrollmentRepository.findByGrade(ENROLLMENT_NAME_ONE_ORIGINAL).get();
		enrollment.setGrade(ENROLLMENT_NAME_THREE);
		enrollment = enrollmentRepository.save(enrollment);
		enrollmentRepository.deleteById(enrollment.getId());
		enrollmentRepository.flush();
		assertFalse(enrollmentRepository.existsById(enrollment.getId()));
	}

}
