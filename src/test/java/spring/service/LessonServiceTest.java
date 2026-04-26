package spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import spring.auxiliaryObjects.CreateObjects;
import spring.auxiliaryObjects.DateRange;
import spring.dto.LessonAddEditDto;
import spring.dto.LessonReadDto;
import spring.mapper.LessonAddEditMapper;
import spring.mapper.LessonReadMapper;
import spring.model.Course;
import spring.model.DateFilter;
import spring.model.Enrollment;
import spring.model.Lesson;
import spring.model.Student;
import spring.model.Teacher;
import spring.repository.CourseRepository;
import spring.repository.LessonRepository;
import spring.repository.RoomRepository;
import spring.repository.StudentRepository;
import spring.repository.TeacherRepository;

@SpringBootTest
class LessonServiceTest {

	@Autowired
	private LessonService lessonService;

	@Autowired
	private LessonAddEditMapper lessonAddEditMapper;

	@Autowired
	private LessonReadMapper lessonReadMapper;

	@MockBean
	private StudentRepository studentRepository;

	@MockBean
	private TeacherRepository teacherRepository;

	@MockBean
	private LessonRepository lessonRepository;

	@MockBean
	private CourseRepository courseRepository;

	@MockBean
	private RoomRepository roomRepository;

	@MockBean
	private ApplicationEventPublisher applicationEventPublisher;

	private static final String NAME = "namenamename";
	private CreateObjects createObjects = new CreateObjects();

	@Test
	void testAddLessonForStudents() {
		Lesson lesson = createObjects.createLesson();
		LessonAddEditDto lessonEditDto = createObjects.createLessonEditDto();
		lessonEditDto.getGroupIds().clear();

		when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);

		LessonReadDto result = lessonService.addLessonForStudents(lessonEditDto);

		assertThat(result).isNotNull();
		assertTrue(NAME.equals(result.getCourse().getCourseName()));
		assertThat(result.getCourse().getCourseName()).isEqualTo(NAME);

		verify(lessonRepository).save(any(Lesson.class));
	}

	@Test
	void testAddLessonForGroups() {
		Lesson lesson = createObjects.createLesson();
		LessonAddEditDto lessonEditDto = createObjects.createLessonEditDto();
		lessonEditDto.getStudentIds().clear();

		when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);

		LessonReadDto result = lessonService.addLessonForGroups(lessonEditDto);

		assertThat(result).isNotNull();
		assertTrue(NAME.equals(result.getCourse().getCourseName()));
		assertThat(result.getCourse().getCourseName()).isEqualTo(NAME);

		verify(lessonRepository).save(any(Lesson.class));
	}

	@Test
	void testGetLessonById_exists() {
		Lesson lesson = createObjects.createLesson();

		when(lessonRepository.findById(lesson.getId())).thenReturn(Optional.of(lesson));

		LessonReadDto result = lessonService.getLessonById(lesson.getId()).get();

		assertTrue(NAME.equals(result.getCourse().getCourseName()));

		verify(lessonRepository).findById(lesson.getId());
	}

	@Test
	void testGetLessonById_NotExists() {
		UUID random = UUID.randomUUID();

		when(lessonRepository.findById(random)).thenReturn(Optional.empty());
		RuntimeException exception = assertThrows(RuntimeException.class, () -> lessonService.getLessonById(random));
		assertTrue(exception.getMessage().equals("Такого id нет. " + random));
	}

	@Test
	void testGetLessonByNameAndTime() {
		Lesson lesson = createObjects.createLesson();

		when(lessonRepository.findByTeachersUserUserNameAndDateAndStartTime(eq(NAME), any(LocalDate.class),
				any(LocalTime.class))).thenReturn(Optional.of(lesson));

		LessonReadDto result = lessonService.getLessonByNameAndDate(NAME, LocalDate.now(), LocalTime.now()).get();

		assertTrue(NAME.equals(result.getCourse().getCourseName()));

		verify(lessonRepository).findByTeachersUserUserNameAndDateAndStartTime(eq(NAME), any(LocalDate.class),
				any(LocalTime.class));
	}

	@Test
	void testGetAllLesson() {
		Lesson lesson = createObjects.createLesson();

		when(lessonRepository.findAll()).thenReturn(Arrays.asList(lesson));

		List<LessonReadDto> result = lessonService.getAllLessons();

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(1 == result.size());
		assertTrue(NAME.equals(result.get(0).getCourse().getCourseName()));

		verify(lessonRepository).findAll();
	}

	@Test
	void testGetAllPageLesson() {
		Pageable pageable = PageRequest.of(0, 10);
		Lesson lesson = createObjects.createLesson();
		List<Lesson> lessons = new ArrayList<>();
		lessons.add(lesson);
		Page<Lesson> pageLesson = new PageImpl<>(lessons);

		when(lessonRepository.findAll(pageable)).thenReturn(pageLesson);

		Page<LessonReadDto> pageDto = lessonService.getAllPageLessons(pageable);

		assertThat(pageDto).isNotNull();
		assertThat(pageDto.getTotalElements()).isEqualTo(1);
		assertThat(pageDto.getContent().get(0).getCourse().getCourseName()).isEqualTo(NAME);

		verify(lessonRepository).findAll(pageable);
	}

	@Test
	void testGetAllMapPageLesson() {
		Pageable pageable = PageRequest.of(0, 10);
		Lesson lesson = createObjects.createLesson();
		List<Lesson> lessons = new ArrayList<>();
		lessons.add(lesson);
		Page<Lesson> pageLesson = new PageImpl<>(lessons);

		when(lessonRepository.findAll(pageable)).thenReturn(pageLesson);

		Map<UUID, LessonReadDto> result = lessonService.getAllMapLessons(pageable);

		LessonReadDto expectedDto = lessonReadMapper.toDto(lesson);

		assertThat(result).isNotNull();
		assertEquals(1, result.size());
		assertEquals(expectedDto, result.get(expectedDto.getId()));

		verify(lessonRepository).findAll(pageable);
	}

	@Test
	void testGetPageLessonsFilter() {
		Pageable pageable = PageRequest.of(0, 10);
		Lesson lesson = createObjects.createLesson();
		List<Lesson> lessons = new ArrayList<>();
		lessons.add(lesson);
		Page<Lesson> page = new PageImpl<>(lessons);

		LessonAddEditDto lessonAddEditDto = createObjects.createLessonEditDto();

		when(lessonRepository.findAll(ArgumentMatchers.<Specification<Lesson>>any(), eq(pageable))).thenReturn(page);

		Page<LessonReadDto> result = lessonService.getPageLessonsfilter(pageable, lessonAddEditDto);

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals(result.getContent().get(0).getStatus(), lessonAddEditDto.getStatus());

		verify(lessonRepository).findAll(ArgumentMatchers.<Specification<Lesson>>any(), eq(pageable));
	}

	@Test
	void testGetPageStudentSchedule() {
		Student student = createObjects.createStudent();

		Pageable pageable = PageRequest.of(0, 10);
		Lesson lesson = createObjects.createLesson();
		List<Lesson> lessons = new ArrayList<>();
		lessons.add(lesson);
		Page<Lesson> page = new PageImpl<>(lessons);

		LessonAddEditDto lessonAddEditDto = createObjects.createLessonEditDto();

		when(studentRepository.findByUserUserName(NAME)).thenReturn(Optional.of(student));
		when(lessonRepository.findAll(ArgumentMatchers.<Specification<Lesson>>any(), eq(pageable))).thenReturn(page);

		Page<LessonReadDto> result = lessonService.getPageStudentSchedule(pageable, NAME, lessonAddEditDto);

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals(result.getContent().get(0).getStatus(), lessonAddEditDto.getStatus());
		assertTrue(result.getContent().get(0).getGroups().get(0).getName().equals(student.getGroup().getName()));

		verify(lessonRepository).findAll(ArgumentMatchers.<Specification<Lesson>>any(), eq(pageable));
	}

	@Test
	void testGetStudentScheduleForExport() {
		Student student = createObjects.createStudent();

		Lesson lesson = createObjects.createLesson();
		List<Lesson> lessons = new ArrayList<>();
		lessons.add(lesson);

		LessonAddEditDto lessonAddEditDto = createObjects.createLessonEditDto();

		when(studentRepository.findByUserUserName(NAME)).thenReturn(Optional.of(student));
		when(lessonRepository.findAll(ArgumentMatchers.<Specification<Lesson>>any())).thenReturn(lessons);

		List<LessonReadDto> result = lessonService.getStudentScheduleForExport(NAME, lessonAddEditDto);

		assertNotNull(result);
		assertEquals(result.get(0).getStatus(), lessonAddEditDto.getStatus());
		assertTrue(result.get(0).getGroups().get(0).getName().equals(student.getGroup().getName()));

		verify(studentRepository).findByUserUserName(NAME);
		verify(lessonRepository).findAll(ArgumentMatchers.<Specification<Lesson>>any());
	}

	@Test
	void testGetPageStudentScheduleException() {
		Student student = createObjects.createStudent();
		student.setGroup(null);

		LessonAddEditDto lessonAddEditDto = createObjects.createLessonEditDto();

		when(studentRepository.findByUserUserName(NAME)).thenReturn(Optional.of(student));

		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> lessonService.getPageStudentSchedule(PageRequest.of(0, 10), NAME, lessonAddEditDto));

		assertEquals(exception.getMessage(), "Вы без группы, обратитесь к администратору.");

		verify(studentRepository).findByUserUserName(NAME);
		verify(lessonRepository, never()).findAll(ArgumentMatchers.<Specification<Lesson>>any(),
				eq(PageRequest.of(0, 10)));
	}

	@Test
	void testGetPageTeacherSchedule() {
		Teacher teacher = createObjects.createTeacher();

		Pageable pageable = PageRequest.of(0, 10);
		Lesson lesson = createObjects.createLesson();
		List<Lesson> lessons = new ArrayList<>();
		lessons.add(lesson);
		Page<Lesson> page = new PageImpl<>(lessons);

		LessonAddEditDto lessonAddEditDto = createObjects.createLessonEditDto();

		when(teacherRepository.findByUserUserName(NAME)).thenReturn(Optional.of(teacher));
		when(lessonRepository.findAll(ArgumentMatchers.<Specification<Lesson>>any(), eq(pageable))).thenReturn(page);

		Page<LessonReadDto> result = lessonService.getPageTeacherSchedule(pageable, NAME, lessonAddEditDto);

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals(result.getContent().get(0).getStatus(), lessonAddEditDto.getStatus());
		assertTrue(result.getContent().get(0).getId().equals(teacher.getId()));

		verify(lessonRepository).findAll(ArgumentMatchers.<Specification<Lesson>>any(), eq(pageable));
	}

	@Test
	void testGetTeacherScheduleForExport() {
		Teacher teacher = createObjects.createTeacher();

		Lesson lesson = createObjects.createLesson();
		List<Lesson> lessons = new ArrayList<>();
		lessons.add(lesson);

		LessonAddEditDto lessonAddEditDto = createObjects.createLessonEditDto();

		when(teacherRepository.findByUserUserName(NAME)).thenReturn(Optional.of(teacher));
		when(lessonRepository.findAll(ArgumentMatchers.<Specification<Lesson>>any())).thenReturn(lessons);

		List<LessonReadDto> result = lessonService.getTeacherScheduleForExport(NAME, lessonAddEditDto);

		assertNotNull(result);
		assertEquals(result.get(0).getStatus(), lessonAddEditDto.getStatus());
		assertTrue(result.get(0).getId().equals(teacher.getId()));

		verify(lessonRepository).findAll(ArgumentMatchers.<Specification<Lesson>>any());
	}

	@Test
	void testGetRangeDateAllNull() {
		assertEquals(null, lessonService.getRangeTime(null, null, null));
	}

	@Test
	void testGetRangeDateWhenFromNull() {
		assertEquals(new DateRange(LocalDate.now(), LocalDate.now()),
				lessonService.getRangeTime(null, LocalDate.now(), DateFilter.DAY));
	}

	@Test
	void testGetRangeDateWhenFromAndFilterNull() {
		assertEquals(new DateRange(LocalDate.now(), LocalDate.now()),
				lessonService.getRangeTime(null, LocalDate.now(), null));
	}

	@Test
	void testGetRangeDateDAY() {
		assertEquals(new DateRange(LocalDate.now(), LocalDate.now()),
				lessonService.getRangeTime(LocalDate.now(), null, DateFilter.DAY));
	}

	@Test
	void testGetRangeDateWEEK() {
		assertEquals(new DateRange(LocalDate.now(), LocalDate.now().plusDays(6)),
				lessonService.getRangeTime(LocalDate.now(), null, DateFilter.WEEK));
	}

	@Test
	void testGetRangeDateMONTH() {
		assertEquals(new DateRange(LocalDate.now(), LocalDate.now().plusMonths(1).minusDays(1)),
				lessonService.getRangeTime(LocalDate.now(), null, DateFilter.MONTH));
	}

	@Test
	void testGetRangeDateCURRENT_MONTH() {
		assertEquals(
				new DateRange(LocalDate.now().withDayOfMonth(1),
						LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())),
				lessonService.getRangeTime(LocalDate.now(), null, DateFilter.CURRENT_MONTH));
	}

	@Test
	void testGetRangeDateCASTOM_andToNotNull() {
		assertEquals(new DateRange(LocalDate.now(), LocalDate.now().plusMonths(2)),
				lessonService.getRangeTime(LocalDate.now(), LocalDate.now().plusMonths(2), DateFilter.CASTOM));
	}

	@Test
	void testGetAllPageLessons() {
		Lesson lesson = createObjects.createLesson();

		Pageable pageable = PageRequest.of(0, 1);
		Page<Lesson> mockPage = new PageImpl<>(Arrays.asList(lesson), pageable, 1);

		when(lessonRepository.findAll(pageable)).thenReturn(mockPage);

		Page<LessonReadDto> page = lessonService.getAllPageLessons(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getCourse().getCourseName()).isEqualTo(NAME);

		verify(lessonRepository).findAll(pageable);
	}

	@Test
	void testUpdateLesson() {
		Lesson lesson = createObjects.createLesson();
		lesson.getCourse().setId(UUID.randomUUID());
		lesson.getRoom().setId(UUID.randomUUID());
		lesson.getStudents().get(0).setId(UUID.randomUUID());
		lesson.getRoom().setNumber(NAME + NAME);
		Enrollment enrollment = new Enrollment();
		enrollment.setId(UUID.randomUUID());
		List<Enrollment> enrollments = new ArrayList<>();
		enrollments.add(enrollment);

		LessonAddEditDto lessonEditDto = createObjects.createLessonEditDto();
		lessonEditDto.setFrom(LocalDate.of(2030, 9, 10));

		when(lessonRepository.findById(lesson.getId())).thenReturn(Optional.of(lesson));
		when(courseRepository.findById(lessonEditDto.getCourseId())).thenReturn(Optional
				.of(new Course(lesson.getCourse().getId(), NAME + NAME, NAME, lesson.getCourse().getDepartment(),
						enrollments, new ArrayList<>(), lesson.getTeachers(), lesson.getGroups())));
		when(roomRepository.findById(lessonEditDto.getRoomId())).thenReturn(Optional.of(lesson.getRoom()));
		when(studentRepository.findByIdIn(lessonEditDto.getStudentIds())).thenReturn(lesson.getStudents());
		when(lessonRepository.saveAndFlush(lesson)).thenReturn(lesson);

		LessonReadDto result = lessonService.updateLesson(lesson.getId(), lessonEditDto).get();

		assertEquals(NAME + NAME, (result.getCourse().getCourseName()));
		assertTrue((NAME + NAME).equals(result.getCourse().getCourseName()));

		verify(lessonRepository).findById(lesson.getId());
		verify(courseRepository).findById(lessonEditDto.getCourseId());
		verify(roomRepository).findById(lessonEditDto.getRoomId());
		verify(studentRepository).findByIdIn(lessonEditDto.getStudentIds());
		verify(lessonRepository).saveAndFlush(lesson);
	}

	@Test
	void testDeleteLessonById() {
		Lesson lesson = createObjects.createLesson();

		when(lessonRepository.findById(lesson.getId())).thenReturn(Optional.of(lesson));

		assertTrue(lessonService.deleteLessonById(lesson.getId()));

		verify(lessonRepository).findById(lesson.getId());
		verify(lessonRepository).delete(lesson);
	}

	@Test
	void testGetLessonIdsByDate() {
		List<Lesson> lessons = new ArrayList<>();
		lessons.add(createObjects.createLesson());
		LocalDate date = LocalDate.now();
		LocalTime time = LocalTime.now();

		when(lessonRepository.findByDateAndStartTime(date, time)).thenReturn(lessons);

		List<UUID> result = lessonService.getLessonIdsByDate(date, time);

		assertEquals(lessons.get(0).getId(), result.get(0));

		verify(lessonRepository).findByDateAndStartTime(date, time);
	}

}
