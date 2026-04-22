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
import spring.dto.CourseAddEditDto;
import spring.dto.CourseReadDto;
import spring.mapper.CourseAddEditMapper;
import spring.mapper.CourseReadMapper;
import spring.model.Course;
import spring.repository.CourseRepository;
import spring.repository.DepartmentRepository;
import spring.repository.GroupRepository;

@SpringBootTest
class CourseServiceTest {

	@Autowired
	private CourseService courseService;

	@Autowired
	private CourseAddEditMapper courseAddEditMapper;

	@Autowired
	private CourseReadMapper courseReadMapper;

	@MockBean
	private CourseRepository courseRepository;

	@MockBean
	private GroupRepository groupRepository;

	@MockBean
	private DepartmentRepository departmentRepository;

	private static final String NAME = "namenamename";
	private CreateObjects createObjects = new CreateObjects();

	@Test
	void testAddCourse() {
		Course course = createObjects.createCourse();
		CourseAddEditDto courseEditDto = createObjects.createCourseEditDto();

		UUID departmentId = courseEditDto.getDepartmentId();
		UUID groupId = courseEditDto.getGroupId();

		when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(course.getDepartment()));
		when(groupRepository.findById(groupId)).thenReturn(Optional.of(course.getGroups().get(0)));
		when(courseRepository.save(any(Course.class))).thenReturn(course);

		CourseReadDto result = courseService.addCourse(courseEditDto);

		assertThat(result).isNotNull();
		assertTrue(NAME.equals(result.getCourseName()));
		assertTrue(departmentId.equals(result.getDepartment().getId()));
		assertTrue(groupId.equals(result.getGroups().get(0).getId()));
		assertThat(result.getCourseName()).isEqualTo(NAME);

		verify(courseRepository).save(any(Course.class));
		verify(departmentRepository).findById(courseEditDto.getDepartmentId());
		verify(groupRepository).findById(courseEditDto.getGroupId());
	}

	@Test
	void testGetCourseById_exists() {
		Course course = createObjects.createCourse();

		when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));

		CourseReadDto result = courseService.getCourseById(course.getId()).get();

		assertTrue(NAME.equals(result.getCourseName()));

		verify(courseRepository).findById(course.getId());
	}

	@Test
	void testGetCourseById_NotExists() {
		UUID random = UUID.randomUUID();

		when(courseRepository.findById(random)).thenReturn(Optional.empty());
		RuntimeException exception = assertThrows(RuntimeException.class, () -> courseService.getCourseById(random));
		assertTrue(exception.getMessage().equals("Такого id нет. " + random));
	}

	@Test
	void testGetCourseByName() {
		Course course = createObjects.createCourse();
		CourseAddEditDto courseEditDto = createObjects.createCourseEditDto();

		when(courseRepository.findByCourseName(course.getCourseName())).thenReturn(Optional.of(course));

		CourseReadDto result = courseService.getCourseByName(courseEditDto.getCourseName()).get();

		assertTrue(NAME.equals(result.getCourseName()));

		verify(courseRepository).findByCourseName(NAME);
	}

	@Test
	void testGetAllCourse() {
		Course course = createObjects.createCourse();

		when(courseRepository.findAll()).thenReturn(Arrays.asList(course));

		List<CourseReadDto> result = courseService.getAllCourses();

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(1 == result.size());
		assertTrue(NAME.equals(result.get(0).getCourseName()));

		verify(courseRepository).findAll();
	}

	@Test
	void testGetAllPageCourses() {
		Course course = createObjects.createCourse();

		Pageable pageable = PageRequest.of(0, 1);
		Page<Course> mockPage = new PageImpl<>(Arrays.asList(course), pageable, 1);

		when(courseRepository.findAll(pageable)).thenReturn(mockPage);

		Page<CourseReadDto> page = courseService.getAllPageCourses(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getCourseName()).isEqualTo(NAME);

		verify(courseRepository).findAll(pageable);
	}

	@Test
	void testUpdateCourse() {
		Course course = createObjects.createCourse();
		CourseAddEditDto courseEditDto = createObjects.createCourseEditDto();
		courseEditDto.setCourseName(NAME + NAME);
		courseEditDto.setDepartmentId(UUID.randomUUID());
		courseEditDto.setGroupId(UUID.randomUUID());

		when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
		when(departmentRepository.findById(courseEditDto.getDepartmentId()))
				.thenReturn(Optional.of(course.getDepartment()));
		when(groupRepository.findById(courseEditDto.getGroupId())).thenReturn(Optional.of(course.getGroups().get(0)));
		when(courseRepository.saveAndFlush(course)).thenReturn(course);

		CourseReadDto result = courseService.updateCourse(course.getId(), courseEditDto).get();

		assertEquals((NAME + NAME), (result.getCourseName()));
		assertTrue((NAME + NAME).equals(result.getCourseName()));

		verify(courseRepository).findById(course.getId());
		verify(departmentRepository).findById(courseEditDto.getDepartmentId());
		verify(groupRepository).findById(courseEditDto.getGroupId());
		verify(courseRepository).saveAndFlush(course);
	}

	@Test
	void testDeleteCourseById() {
		Course course = createObjects.createCourse();
		when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));

		assertTrue(courseService.deleteCourseById(course.getId()));

		verify(courseRepository).findById(course.getId());
		verify(courseRepository).delete(course);
	}

}
