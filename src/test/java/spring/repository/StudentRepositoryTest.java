package spring.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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

import spring.model.Student;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
class StudentRepositoryTest {

	@Autowired
	private StudentRepository studentRepository;

	private static final String STUDENT_FIRST_NAME_ONE = "Sergey";
	private static final String STUDENT_LAST_NAME_ONE = "Sergeev";
	private static final String STUDENT_NAME_THREE = "Student Three";
	private static final String STUDENT_USER_NAME = "student1";
	private static final UUID GROUP_ID = UUID.fromString("00000000-0000-0000-0000-000000000041");
	private static final UUID STUDENT_ID_NOT_EXISTING = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private static final UUID STUDENT_ID_ONE = UUID.fromString("00000000-0000-0000-0000-000000000121");

	@Test
	void testAddStudent() {
		Student student = studentRepository
				.findByUserFirstNameAndUserLastName(STUDENT_FIRST_NAME_ONE, STUDENT_LAST_NAME_ONE).get();
		student.getUser().setFirstName(STUDENT_NAME_THREE);
		assertTrue(studentRepository.save(student).getUser().getFirstName().equals(STUDENT_NAME_THREE));
	}

	@Test
	void testGetStudentById_exists() {
		assertTrue(studentRepository.findById(STUDENT_ID_ONE).get().getUser().getFirstName()
				.equals(STUDENT_FIRST_NAME_ONE));
	}

	@Test
	void testGetStudentById_NotExists() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> studentRepository.findById(STUDENT_ID_NOT_EXISTING).map(Student::getUser)
						.map(user -> user.getFirstName()).orElseThrow(() -> {
							throw new RuntimeException("Такого id нет. " + STUDENT_ID_NOT_EXISTING);
						}));
		assertTrue(exception.getMessage().contains("Такого id нет. " + STUDENT_ID_NOT_EXISTING));
	}

	@Test
	void testGetStudentByName() {
		assertTrue(studentRepository.findByUserFirstNameAndUserLastName(STUDENT_FIRST_NAME_ONE, STUDENT_LAST_NAME_ONE)
				.get().getUser().getFirstName().equals(STUDENT_FIRST_NAME_ONE));
	}

	@Test
	void testGetAllStudent() {
		List<Student> universities = studentRepository.findAll();
		assertNotNull(universities);
		assertFalse(universities.isEmpty());
		assertTrue(1 == universities.size());
	}

	@Test
	void testGetAllPageLesson() {
		Pageable pageable = PageRequest.of(0, 1);
		Page<Student> page = studentRepository.findAll(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getContent()).hasSize(1);
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getUser().getFirstName()).contains(STUDENT_FIRST_NAME_ONE);
	}

	@Test
	void testGetAllByGroupId() {
		Pageable pageable = PageRequest.of(0, 3);
		Page<Student> page = studentRepository.findAllByGroup_Id(pageable, GROUP_ID);

		assertNotNull(page);
		assertThat(page.getContent()).hasSize(1);
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().stream().anyMatch(result -> result.getId().equals(STUDENT_ID_ONE)));
	}

	@Test
	void testGetUserUserName() {
		Student student = studentRepository.findByUserUserName(STUDENT_USER_NAME).get();
		assertEquals(STUDENT_USER_NAME, student.getUser().getUserName());
	}

	@Test
	@Transactional
	void testDeleteStudentById() {
		Student student = studentRepository
				.findByUserFirstNameAndUserLastName(STUDENT_FIRST_NAME_ONE, STUDENT_LAST_NAME_ONE).get();
		studentRepository.deleteById(student.getId());
		assertFalse(studentRepository.existsById(student.getId()));
	}

	@Test
	void getStudentByIds() {
		List<UUID> uuids = new ArrayList<>();
		uuids.add(STUDENT_ID_ONE);
		List<Student> studnets = studentRepository.findAllById(uuids);
		assertTrue(studnets.size() == 1);
		assertTrue(studnets.get(0).getId().equals(studentRepository.findById(STUDENT_ID_ONE).get().getId()));
	}

	@Test
	void getPageStudentByIds() {
		List<UUID> uuids = new ArrayList<>();
		uuids.add(STUDENT_ID_ONE);
		Page<Student> page = studentRepository.findByIdIn(uuids, PageRequest.of(0, 2));

		assertThat(page).isNotNull();
		assertThat(page.getContent()).hasSize(1);
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getUser().getUserName()).contains(STUDENT_USER_NAME);
	}

	@Test
	void getStudentsNotInLessons() {
		UUID lessonId = UUID.fromString("00000000-0000-0000-0000-000000000071");
		List<UUID> uuids = new ArrayList<>();
		uuids.add(lessonId);
		List<Student> students = studentRepository.findStudentsNotInLessons(uuids);

		assertFalse(students.get(0).getLessons().stream().anyMatch(l -> l.getId().equals(lessonId)));
	}

}
