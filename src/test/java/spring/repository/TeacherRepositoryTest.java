package spring.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

import spring.model.Teacher;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
class TeacherRepositoryTest {

	@Autowired
	private TeacherRepository teacherRepository;

	private static final String TEACHER_FIRST_NAME_ONE = "Ivan";
	private static final String TEACHER_LAST_NAME_ONE = "Ivanov";
	private static final String TEACHER_NAME_THREE = "Teacher Three";
	private static final String TEACHER_USER_NAME = "teacher1";
	private static final UUID TEACHER_ID_NOT_EXISTING = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private static final UUID TEACHER_ID_ONE = UUID.fromString("00000000-0000-0000-0000-000000000111");

	void testAddTeacher() {
		Teacher teacher = teacherRepository
				.findByUserFirstNameAndUserLastName(TEACHER_FIRST_NAME_ONE, TEACHER_LAST_NAME_ONE).get();
		teacher.getUser().setFirstName(TEACHER_NAME_THREE);
		assertTrue(teacherRepository.save(teacher).getUser().getFirstName().equals(TEACHER_FIRST_NAME_ONE));
	}

	@Test
	void testGetTeacherById_exists() {
		assertTrue(teacherRepository.findById(TEACHER_ID_ONE).get().getUser().getFirstName()
				.equals(TEACHER_FIRST_NAME_ONE));
	}

	@Test
	void testGetTeacherById_NotExists() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> teacherRepository.findById(TEACHER_ID_NOT_EXISTING).map(Teacher::getUser)
						.map(user -> user.getFirstName()).orElseThrow(() -> {
							throw new RuntimeException("Такого id нет. " + TEACHER_ID_NOT_EXISTING);
						}));
		assertTrue(exception.getMessage().contains("Такого id нет. " + TEACHER_ID_NOT_EXISTING));
	}

	@Test
	void testGetTeacherByName() {
		assertTrue(teacherRepository.findByUserFirstNameAndUserLastName(TEACHER_FIRST_NAME_ONE, TEACHER_LAST_NAME_ONE)
				.get().getUser().getFirstName().equals(TEACHER_FIRST_NAME_ONE));
	}

	@Test
	void testGetAllTeacher() {
		List<Teacher> universities = teacherRepository.findAll();
		assertNotNull(universities);
		assertFalse(universities.isEmpty());
		assertTrue(1 == universities.size());
	}

	@Test
	void testGetAllPageTeacher() {
		Pageable pageable = PageRequest.of(0, 1);
		Page<Teacher> page = teacherRepository.findAll(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getContent()).hasSize(1);
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getUser().getFirstName()).contains(TEACHER_FIRST_NAME_ONE);
	}

	@Test
	void testGetUserUserName() {
		Teacher teacher = teacherRepository.findByUserUserName(TEACHER_USER_NAME).get();
		assertEquals(TEACHER_USER_NAME, teacher.getUser().getUserName());
	}

	@Test
	@Transactional
	void testDeleteTeacherById() {
		Teacher teacher = teacherRepository
				.findByUserFirstNameAndUserLastName(TEACHER_FIRST_NAME_ONE, TEACHER_LAST_NAME_ONE).get();
		teacherRepository.deleteById(teacher.getId());
		assertFalse(teacherRepository.existsById(teacher.getId()));
	}
}
