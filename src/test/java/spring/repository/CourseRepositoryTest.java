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

import spring.model.Course;
import spring.model.Department;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
class CourseRepositoryTest {

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	private static final String COURSE_NAME_ONE = "Math";
	private static final String COURSE_NAME_THREE = "Physics";
	private static final UUID COURSE_ID_NOT_EXISTING = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private static final UUID COURSE_ID_ONE = UUID.fromString("00000000-0000-0000-0000-000000000031");

	@Test
	void testAddCourse() {
		Department department = departmentRepository.findByName("Department One").get();
		Course course = new Course();
		course.setCourseName(COURSE_NAME_THREE);
		course.setDepartment(department);
		assertTrue(courseRepository.save(course).getCourseName().equals(COURSE_NAME_THREE));
	}

	@Test
	void testGetCourseById_exists() {
		assertTrue(courseRepository.findById(COURSE_ID_ONE).get().getCourseName().equals(COURSE_NAME_ONE));
	}

	@Test
	void testGetCourseById_NotExists() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> courseRepository.findById(COURSE_ID_NOT_EXISTING).map(Course::getCourseName).orElseThrow(() -> {
					throw new RuntimeException("Такого id нет. " + COURSE_ID_NOT_EXISTING);
				}));
		assertTrue(exception.getMessage().contains("Такого id нет. " + COURSE_ID_NOT_EXISTING));
	}

	@Test
	void testGetCourseByName() {
		assertTrue(courseRepository.findByCourseName(COURSE_NAME_ONE).get().getCourseName().equals(COURSE_NAME_ONE));
	}

	@Test
	void testGetAllCourse() {
		List<Course> universities = courseRepository.findAll();
		assertNotNull(universities);
		assertFalse(universities.isEmpty());
		assertTrue(2 == universities.size());
	}

	@Test
	void testGetAllPageCourse() {
		Pageable pageable = PageRequest.of(0, 2);
		Page<Course> page = courseRepository.findAll(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getContent()).hasSize(2);
		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent().get(0).getCourseName()).contains(COURSE_NAME_ONE);
		assertThat(page.getContent().get(1).getCourseName()).contains(COURSE_NAME_THREE);
	}

	@Test
	@Transactional
	void testDeleteCourseById() {
		Course course = new Course();
		course.setCourseName(COURSE_NAME_THREE + COURSE_NAME_THREE);
		course = courseRepository.save(course);
		courseRepository.deleteById(course.getId());
		courseRepository.flush();
		assertFalse(courseRepository.existsById(course.getId()));
	}
}
