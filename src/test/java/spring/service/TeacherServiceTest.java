package spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import spring.auxiliaryObjects.CreateObjects;
import spring.dto.TeacherAddEditDto;
import spring.dto.TeacherReadDto;
import spring.mapper.TeacherAddEditMapper;
import spring.mapper.TeacherReadMapper;
import spring.model.Teacher;
import spring.repository.CourseRepository;
import spring.repository.DepartmentRepository;
import spring.repository.LessonRepository;
import spring.repository.RoleRepository;
import spring.repository.TeacherRepository;

@SpringBootTest
class TeacherServiceTest {

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private TeacherAddEditMapper teacherAddEditMapper;

	@Autowired
	private TeacherReadMapper teacherReadMapper;

	@MockBean
	private TeacherRepository teacherRepository;

	@MockBean
	private DepartmentRepository departmentRepository;

	@MockBean
	private CourseRepository courseRepository;

	@MockBean
	private LessonRepository lessonRepository;

	@MockBean
	private RoleRepository roleRepository;

	private CreateObjects createObjects = new CreateObjects();
	private static final String USER_NAME = "namenamename";

	@Test
	void testAddTeacher() {
		Teacher teacher = createObjects.createTeacher();
		TeacherAddEditDto teacherEditDto = createObjects.createTeacherEditDto();

		when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

		TeacherReadDto result = teacherService.addTeacher(teacherEditDto);

		assertThat(result).isNotNull();
		assertTrue(USER_NAME.equals(result.getUser().getUserName()));
		assertThat(result.getUser().getUserName()).isEqualTo(USER_NAME);

		verify(teacherRepository).save(any(Teacher.class));
	}

	@Test
	void testGetTeacherById_exists() {
		Teacher teacher = createObjects.createTeacher();

		when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

		TeacherReadDto result = teacherService.getTeacherById(teacher.getId()).get();

		assertTrue(USER_NAME.equals(result.getUser().getUserName()));

		verify(teacherRepository).findById(teacher.getId());
	}

	@Test
	void testGetTeacherById_NotExists() {
		UUID random = UUID.randomUUID();

		when(teacherRepository.findById(random)).thenReturn(Optional.empty());
		RuntimeException exception = assertThrows(RuntimeException.class, () -> teacherService.getTeacherById(random));
		assertTrue(exception.getMessage().equals("Такого id нет. " + random));
	}

	@Test
	void testGetTeacherByUser() {
		Teacher teacher = createObjects.createTeacher();
		TeacherAddEditDto teacherEditDto = createObjects.createTeacherEditDto();

		when(teacherRepository.findByUserFirstNameAndUserLastName(teacherEditDto.getUser().getFirstName(),
				teacherEditDto.getUser().getLastName())).thenReturn(Optional.of(teacher));

		TeacherReadDto result = teacherService
				.getTeacherByName(teacherEditDto.getUser().getFirstName(), teacherEditDto.getUser().getLastName())
				.get();

		assertTrue(USER_NAME.equals(result.getUser().getUserName()));

		verify(teacherRepository).findByUserFirstNameAndUserLastName(teacherEditDto.getUser().getFirstName(),
				teacherEditDto.getUser().getLastName());
	}

	@Test
	void testGetAllTeacher() {
		Teacher teacher = createObjects.createTeacher();

		when(teacherRepository.findAll()).thenReturn(Arrays.asList(teacher));

		List<TeacherReadDto> result = teacherService.getAllTeachers();

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(1 == result.size());
		assertTrue(USER_NAME.equals(result.get(0).getUser().getUserName()));

		verify(teacherRepository).findAll();
	}

	@Test
	void testGetAllPageTeachers() {
		Teacher teacher = createObjects.createTeacher();

		Pageable pageable = PageRequest.of(0, 1);
		Page<Teacher> mockPage = new PageImpl<>(Arrays.asList(teacher), pageable, 1);

		when(teacherRepository.findAll(pageable)).thenReturn(mockPage);

		Page<TeacherReadDto> page = teacherService.getAllPageTeachers(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getUser().getUserName()).isEqualTo(USER_NAME);

		verify(teacherRepository).findAll(pageable);
	}

	@Test
	void testUpdateTeacher() {
		Teacher teacher = createObjects.createTeacher();
		TeacherAddEditDto teacherEditDto = createObjects.createTeacherEditDto();
		UUID departmnetId = teacherEditDto.getDepartmentId();

		when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
		when(departmentRepository.findById(teacherEditDto.getDepartmentId()))
				.thenReturn(Optional.of(teacher.getDepartment()));
		when(courseRepository.findAllById(teacherEditDto.getCourseIds())).thenReturn(teacher.getCourses());
		when(lessonRepository.findAllById(teacherEditDto.getCourseIds())).thenReturn(teacher.getLessons());
		when(roleRepository.findAllById(teacherEditDto.getUser().getRoleIds()))
				.thenReturn(teacher.getUser().getRoles());
		when(teacherRepository.saveAndFlush(teacher)).thenReturn(teacher);

		TeacherReadDto result = teacherService.updateTeacher(teacher.getId(), teacherEditDto).get();

		assertEquals(USER_NAME, (result.getUser().getUserName()));
		assertTrue(USER_NAME.equals(result.getUser().getUserName()));
		assertTrue(departmnetId.equals(result.getDepartment().getId()));

		verify(teacherRepository).findById(teacher.getId());
		verify(departmentRepository).findById(teacherEditDto.getDepartmentId());
		verify(courseRepository).findAllById(teacherEditDto.getCourseIds());
		verify(lessonRepository).findAllById(teacherEditDto.getLessonIds());
		verify(roleRepository).findAllById(teacherEditDto.getUser().getRoleIds());
		verify(teacherRepository).saveAndFlush(teacher);
	}

	@Test
	void testDeleteTeacherById() {
		Teacher teacher = createObjects.createTeacher();

		when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

		assertTrue(teacherService.deleteTeacherById(teacher.getId()));

		verify(teacherRepository).findById(teacher.getId());
		verify(teacherRepository).delete(teacher);
	}

}
