package spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import spring.auxiliaryObjects.CreateObjects;
import spring.dto.CourseReadDto;
import spring.dto.GroupReadDto;
import spring.dto.LessonAddEditDto;
import spring.dto.StudentReadDto;
import spring.dto.TeacherReadDto;
import spring.model.RoleElement;
import spring.model.Teacher;
import spring.repository.CourseRepository;
import spring.repository.TeacherRepository;

@SpringBootTest
class MethodistServiceTest {

	@Autowired
	private MethodistService methodistService;

	@MockBean
	private StudentService studentService;

	@MockBean
	private LessonService lessonService;

	@MockBean
	private GroupService groupService;

	@MockBean
	private TeacherRepository teacherRepository;

	@MockBean
	private CourseRepository courseRepository;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	private CreateObjects createObjects = new CreateObjects();

	@Test
	void updateTeacherCourses() {
		TeacherReadDto teacherReadDto = createObjects
				.createTeacherDto(createObjects.createUserDto(createObjects.createRoleDto(RoleElement.ROLE_QUEST)));
		CourseReadDto courseReadDto = createObjects.createCourseDto();
		Teacher teacher = createObjects.createTeacher();

		when(teacherRepository.findById(teacherReadDto.getId())).thenReturn(Optional.of(teacher));
		when(courseRepository.findById(courseReadDto.getId())).thenReturn(Optional.of(createObjects.createCourse()));
		when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

		methodistService.updateTeacherCourses(teacherReadDto.getId(), courseReadDto.getId());

		verify(teacherRepository).findById(teacherReadDto.getId());
		verify(courseRepository).findById(courseReadDto.getId());
		verify(teacherRepository).save(any(Teacher.class));
	}

	@Test
	void deleteTeacherCourses() {
		TeacherReadDto teacherReadDto = createObjects
				.createTeacherDto(createObjects.createUserDto(createObjects.createRoleDto(RoleElement.ROLE_QUEST)));
		CourseReadDto courseReadDto = createObjects.createCourseDto();
		Teacher teacher = createObjects.createTeacher();

		when(teacherRepository.findById(teacherReadDto.getId())).thenReturn(Optional.of(teacher));
		when(courseRepository.findById(courseReadDto.getId())).thenReturn(Optional.of(createObjects.createCourse()));
		when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

		methodistService.deleteTeacherCourses(teacherReadDto.getId(), courseReadDto.getId());

		verify(teacherRepository).findById(teacherReadDto.getId());
		verify(courseRepository).findById(courseReadDto.getId());
		verify(teacherRepository).save(any(Teacher.class));
	}

	@Test
	void testAvailableStudentsUpdate() {
		List<UUID> uuids = new ArrayList<>();
		uuids.add(UUID.randomUUID());
		uuids.add(UUID.randomUUID());
		LessonAddEditDto lessonAddEditDto = createObjects.createLessonEditDto();
		List<StudentReadDto> dtos1 = new ArrayList<>();
		dtos1.add(createObjects
				.createStudentDto(createObjects.createUserDto(createObjects.createRoleDto(RoleElement.ROLE_STUDENT))));
		List<StudentReadDto> dtos2 = new ArrayList<>();
		dtos2.add(createObjects
				.createStudentDto(createObjects.createUserDto(createObjects.createRoleDto(RoleElement.ROLE_STUDENT))));

		when(lessonService.getLessonIdsByDate(lessonAddEditDto.getFrom(), lessonAddEditDto.getStartTime()))
				.thenReturn(uuids);
		when(studentService.getStudentByIds(lessonAddEditDto.getStudentIds())).thenReturn(dtos1);
		when(studentService.getStudentsWithoutLesson(uuids)).thenReturn(dtos2);

		List<UUID> result = methodistService.availableStudentsUpdate(lessonAddEditDto);

		assertTrue(result.isEmpty());

		verify(lessonService).getLessonIdsByDate(lessonAddEditDto.getFrom(), lessonAddEditDto.getStartTime());
		verify(studentService).getStudentByIds(lessonAddEditDto.getStudentIds());
		verify(studentService).getStudentsWithoutLesson(uuids);
	}

	@Test
	void testAvailableStudentsCreate() {
		List<UUID> uuids = new ArrayList<>();
		uuids.add(UUID.randomUUID());
		uuids.add(UUID.randomUUID());
		LessonAddEditDto lessonAddEditDto = createObjects.createLessonEditDto();
		List<StudentReadDto> dtos1 = new ArrayList<>();
		dtos1.add(createObjects
				.createStudentDto(createObjects.createUserDto(createObjects.createRoleDto(RoleElement.ROLE_STUDENT))));

		when(lessonService.getLessonIdsByDate(lessonAddEditDto.getFrom(), lessonAddEditDto.getStartTime()))
				.thenReturn(uuids);
		when(studentService.getStudentsWithoutLesson(uuids)).thenReturn(dtos1);

		List<UUID> result = methodistService.availableStudentsUpdate(lessonAddEditDto);

		assertEquals(dtos1.get(0).getId(), result.get(0));

		verify(lessonService).getLessonIdsByDate(lessonAddEditDto.getFrom(), lessonAddEditDto.getStartTime());
		verify(studentService).getStudentsWithoutLesson(uuids);
	}

	@Test
	void testAvailableGroupsUpdate() {
		List<UUID> uuids = new ArrayList<>();
		uuids.add(UUID.randomUUID());
		uuids.add(UUID.randomUUID());
		LessonAddEditDto lessonAddEditDto = createObjects.createLessonEditDto();
		List<GroupReadDto> dtos1 = new ArrayList<>();
		dtos1.add(createObjects.createGroupDto());
		List<GroupReadDto> dtos2 = new ArrayList<>();
		dtos2.add(createObjects.createGroupDto());

		when(lessonService.getLessonIdsByDate(lessonAddEditDto.getFrom(), lessonAddEditDto.getStartTime()))
				.thenReturn(uuids);
		when(groupService.getGroupsByIds(lessonAddEditDto.getGroupIds())).thenReturn(dtos1);
		when(groupService.getGroupsWithoutLesson(uuids)).thenReturn(dtos2);

		List<UUID> result = methodistService.availableGroupsUpdate(lessonAddEditDto);

		assertTrue(result.isEmpty());

		verify(lessonService).getLessonIdsByDate(lessonAddEditDto.getFrom(), lessonAddEditDto.getStartTime());
		verify(groupService).getGroupsByIds(lessonAddEditDto.getGroupIds());
		verify(groupService).getGroupsWithoutLesson(uuids);
	}

	@Test
	void testAvailableGroupsCreate() {
		List<UUID> uuids = new ArrayList<>();
		uuids.add(UUID.randomUUID());
		uuids.add(UUID.randomUUID());
		LessonAddEditDto lessonAddEditDto = createObjects.createLessonEditDto();
		List<GroupReadDto> dtos1 = new ArrayList<>();
		dtos1.add(createObjects.createGroupDto());

		when(lessonService.getLessonIdsByDate(lessonAddEditDto.getFrom(), lessonAddEditDto.getStartTime()))
				.thenReturn(uuids);
		when(groupService.getGroupsWithoutLesson(uuids)).thenReturn(dtos1);

		List<UUID> result = methodistService.availableGroupsUpdate(lessonAddEditDto);

		assertEquals(dtos1.get(0).getId(), result.get(0));

		verify(lessonService).getLessonIdsByDate(lessonAddEditDto.getFrom(), lessonAddEditDto.getStartTime());
		verify(groupService).getGroupsWithoutLesson(uuids);
	}

}
