package spring.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
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
import spring.model.Lesson;
import spring.model.LessonStatus;
import spring.model.Room;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
class LessonRepositoryTest {

	@Autowired
	private LessonRepository lessonRepository;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private RoomRepository roomRepository;

	private static final String LESSON_NAME_ONE_ORIGINAL = "Math";
	private static final String LESSON_NAME_TWO_ORIGINAL = "Physics";
	private static final String LESSON_NAME_THREE = "Lesson Three";
	private static final String USER_NAME = "teacher1";
	private static final LocalDate DATE = LocalDate.of(2026, 12, 30);
	private static final LocalTime TIME = LocalTime.of(9, 00);
	private static final UUID GROUP_ID = UUID.fromString("00000000-0000-0000-0000-000000000041");
	private static final UUID LESSON_ID_NOT_EXISTING = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private static final UUID LESSON_ID_ONE = UUID.fromString("00000000-0000-0000-0000-000000000071");

	@Test
	void testAddLesson() {
		Course course = courseRepository.findByCourseName("Math").get();
		course.setCourseName(LESSON_NAME_THREE);
		Room room = new Room();
		room.setNumber("101");
		room = roomRepository.save(room);
		Lesson lesson = new Lesson();
		lesson.setCourse(course);
		lesson.setRoom(room);
		lesson.setDate(LocalDate.now());
		lesson.setStartTime(LocalTime.of(10, 0));
		lesson.setEndTime(LocalTime.of(11, 0));
		lesson.setStatus(LessonStatus.PLANNED);

		Lesson saved = lessonRepository.save(lesson);

		assertTrue(course.getCourseName().equals(saved.getCourse().getCourseName()));
		assertNotNull(saved.getId());
	}

	@Test
	void testGetLessonById_exists() {
		Lesson lesson = lessonRepository.findById(LESSON_ID_ONE).orElseThrow(() -> new RuntimeException());
		assertTrue(LESSON_NAME_ONE_ORIGINAL.equals(lesson.getCourse().getCourseName()));
	}

	@Test
	void testGetLessonById_NotExists() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> lessonRepository.findById(LESSON_ID_NOT_EXISTING).map(Lesson::getCourse)
						.orElseThrow(() -> new RuntimeException("Такого id нет. " + LESSON_ID_NOT_EXISTING)));
		assertTrue(exception.getMessage().contains("Такого id нет. " + LESSON_ID_NOT_EXISTING));
	}

	@Test
	void testGetLessonByCourseName() {
		Optional<Lesson> lessons = lessonRepository.findByTeachersUserUserNameAndDateAndStartTime(USER_NAME, DATE,
				TIME);
		assertEquals(LESSON_NAME_ONE_ORIGINAL, lessons.get().getCourse().getCourseName());
	}

	@Test
	void testGetAllLessons() {
		List<Lesson> lessons = lessonRepository.findAll();
		assertNotNull(lessons);
		assertFalse(lessons.isEmpty());
		assertEquals(6, lessons.size());
	}

	@Test
	void testGetLessonsByGroupId() {
		List<Lesson> lessons = lessonRepository.findAllByGroups_Id(GROUP_ID);
		assertNotNull(lessons);
		assertFalse(lessons.isEmpty());
		assertEquals(LESSON_ID_ONE, lessons.get(0).getId());
	}

	@Test
	void testGetAllPageLesson() {
		Pageable pageable = PageRequest.of(0, 2);
		Page<Lesson> page = lessonRepository.findAll(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getContent()).hasSize(2);
		assertThat(page.getTotalElements()).isEqualTo(6);
		assertThat(page.getContent().get(0).getCourse().getCourseName()).contains(LESSON_NAME_ONE_ORIGINAL);
		assertThat(page.getContent().get(1).getCourse().getCourseName()).contains(LESSON_NAME_TWO_ORIGINAL);
	}

	@Test
	@Transactional
	void testDeleteLessonById() {
		Course course = courseRepository.findByCourseName("Math").get();

		Room room = new Room();
		room.setNumber("101");
		roomRepository.save(room);

		Lesson lesson = new Lesson();
		lesson.setCourse(course);
		lesson.setDate(LocalDate.now());
		lesson.setStartTime(LocalTime.of(9, 0));
		lesson.setEndTime(LocalTime.of(10, 0));
		lesson.setStatus(LessonStatus.PLANNED);
		lesson.setRoom(room);

		Lesson saved = lessonRepository.save(lesson);
		lessonRepository.deleteById(saved.getId());

		assertFalse(lessonRepository.existsById(saved.getId()));
	}

}
