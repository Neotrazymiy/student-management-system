package spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import spring.dto.StudentAddEditDto;
import spring.dto.StudentReadDto;
import spring.mapper.StudentAddEditMapper;
import spring.mapper.StudentReadMapper;
import spring.model.Student;
import spring.repository.GroupRepository;
import spring.repository.RoleRepository;
import spring.repository.StudentRepository;

@SpringBootTest
class StudentServiceTest {

	@Autowired
	private StudentService studentService;

	@Autowired
	private StudentAddEditMapper studentAddEditMapper;

	@Autowired
	private StudentReadMapper studentReadMapper;

	@MockBean
	private StudentRepository studentRepository;

	@MockBean
	private GroupRepository groupRepository;

	@MockBean
	private RoleRepository roleRepository;

	private CreateObjects createObjects = new CreateObjects();
	private static final String USER_NAME = "namenamename";

	@Test
	void testAddStudent() {
		Student student = createObjects.createStudent();
		StudentAddEditDto studentEditDto = createObjects.createStudentEditDto();

		when(studentRepository.save(any(Student.class))).thenReturn(student);

		StudentReadDto result = studentService.addStudent(studentEditDto);

		assertThat(result).isNotNull();
		assertTrue(USER_NAME.equals(result.getUser().getUserName()));
		assertThat(result.getUser().getUserName()).isEqualTo(USER_NAME);

		verify(studentRepository).save(any(Student.class));
	}

	@Test
	void testGetStudentById_exists() {
		Student student = createObjects.createStudent();

		when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));

		StudentReadDto result = studentService.getStudentById(student.getId()).get();

		assertTrue(USER_NAME.equals(result.getUser().getUserName()));

		verify(studentRepository).findById(student.getId());
	}

	@Test
	void testGetStudentById_NotExists() {
		UUID random = UUID.randomUUID();

		when(studentRepository.findById(random)).thenReturn(Optional.empty());
		RuntimeException exception = assertThrows(RuntimeException.class, () -> studentService.getStudentById(random));
		assertTrue(exception.getMessage().equals("Такого id нет. " + random));
	}

	@Test
	void testGetStudentByUser() {
		Student student = createObjects.createStudent();
		StudentAddEditDto studentEditDto = createObjects.createStudentEditDto();

		when(studentRepository.findByUserFirstNameAndUserLastName(studentEditDto.getUser().getFirstName(),
				studentEditDto.getUser().getLastName())).thenReturn(Optional.of(student));

		StudentReadDto result = studentService
				.getStudentByName(studentEditDto.getUser().getFirstName(), studentEditDto.getUser().getLastName())
				.get();

		assertTrue(USER_NAME.equals(result.getUser().getUserName()));

		verify(studentRepository).findByUserFirstNameAndUserLastName(studentEditDto.getUser().getFirstName(),
				studentEditDto.getUser().getLastName());
	}

	@Test
	void testGetStudentByIds() {
		Student student = createObjects.createStudent();
		List<Student> students = new ArrayList<>();
		students.add(student);
		List<UUID> uuids = new ArrayList<>();
		uuids.add(UUID.randomUUID());

		when(studentRepository.findByIdIn(uuids)).thenReturn(students);

		List<StudentReadDto> studentReadDtos = studentService.getStudentByIds(uuids);

		assertEquals(student.getId(), studentReadDtos.get(0).getId());

		verify(studentRepository).findByIdIn(uuids);
	}

	@Test
	void testGetPageStudentByIds() {
		Pageable pageable = PageRequest.of(0, 10);
		Student student = createObjects.createStudent();
		List<Student> students = new ArrayList<>();
		students.add(student);
		List<UUID> uuids = new ArrayList<>();
		uuids.add(UUID.randomUUID());
		Page<Student> page = new PageImpl<>(students);

		when(studentRepository.findByIdIn(uuids, pageable)).thenReturn(page);

		Page<StudentReadDto> studentReadDtos = studentService.getPageStudentByIds(uuids, pageable);

		assertEquals(student.getId(), studentReadDtos.getContent().get(0).getId());

		verify(studentRepository).findByIdIn(uuids, pageable);
	}

	@Test
	void testGetPageStudentNullByIds() {
		Pageable pageable = PageRequest.of(0, 10);
		List<UUID> uuids = new ArrayList<>();

		Page<StudentReadDto> studentReadDtos = studentService.getPageStudentByIds(uuids, pageable);

		assertTrue(studentReadDtos.isEmpty());

		verify(studentRepository, never()).findByIdIn(uuids, pageable);
	}

	@Test
	void testGetAllStudent() {
		Student student = createObjects.createStudent();

		when(studentRepository.findAll()).thenReturn(Arrays.asList(student));

		List<StudentReadDto> result = studentService.getAllStudents();

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(1 == result.size());
		assertTrue(USER_NAME.equals(result.get(0).getUser().getUserName()));

		verify(studentRepository).findAll();
	}

	@Test
	void testGetAllPageStudents() {
		Student student = createObjects.createStudent();
		List<Student> students = new ArrayList<>();
		students.add(student);

		Pageable pageable = PageRequest.of(0, 10);
		Page<Student> mockPage = new PageImpl<>(students);

		when(studentRepository.findAllByGroup_Id(pageable, student.getGroup().getId())).thenReturn(mockPage);

		Page<StudentReadDto> page = studentService.getAllPageStudents(pageable, student.getId());

		assertThat(page).isNotNull();
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getUser().getUserName()).isEqualTo(USER_NAME);

		verify(studentRepository).findAllByGroup_Id(pageable, student.getId());
	}

	@Test
	void testGetAllPageStudentsGroupNull() {
		Student student = createObjects.createStudent();
		List<Student> students = new ArrayList<>();
		students.add(student);

		Pageable pageable = PageRequest.of(0, 10);
		Page<Student> mockPage = new PageImpl<>(students);

		when(studentRepository.findAll(pageable)).thenReturn(mockPage);

		Page<StudentReadDto> page = studentService.getAllPageStudents(pageable, null);

		assertThat(page).isNotNull();
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getUser().getUserName()).isEqualTo(USER_NAME);

		verify(studentRepository).findAll(pageable);
	}

	@Test
	void testUpdateStudent() {
		Student studentFirst = createObjects.createStudent();
		StudentAddEditDto studentEditDto = createObjects.createStudentEditDto();
		studentEditDto.setGroupId(UUID.randomUUID());
		UUID groupId = studentEditDto.getGroupId();

		Student studentLast = createObjects.createStudent();
		studentLast.getGroup().setId(studentEditDto.getGroupId());

		when(studentRepository.findById(studentFirst.getId())).thenReturn(Optional.of(studentFirst));
		when(groupRepository.findById(studentEditDto.getGroupId())).thenReturn(Optional.of(studentLast.getGroup()));
		when(roleRepository.findAllById(studentEditDto.getUser().getRoleIds()))
				.thenReturn(studentFirst.getUser().getRoles());
		when(studentRepository.saveAndFlush(studentFirst)).thenReturn(studentFirst);

		StudentReadDto result = studentService.updateStudent(studentFirst.getId(), studentEditDto).get();

		assertEquals(USER_NAME, (result.getUser().getUserName()));
		assertTrue(USER_NAME.equals(result.getUser().getUserName()));
		assertTrue(groupId.equals(result.getGroup().getId()));

		verify(studentRepository).findById(studentFirst.getId());
		verify(groupRepository).findById(studentEditDto.getGroupId());
		verify(roleRepository).findAllById(studentEditDto.getUser().getRoleIds());
		verify(studentRepository).saveAndFlush(studentFirst);
	}

	@Test
	void testDeleteStudentById() {
		Student student = createObjects.createStudent();

		when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));

		assertTrue(studentService.deleteStudentById(student.getId()));

		verify(studentRepository).findById(student.getId());
		verify(studentRepository).delete(student);
	}

	@Test
	void testGetStudentsWithoutLesson() {
		List<UUID> uuids = new ArrayList<>();
		uuids.add(UUID.randomUUID());
		Student student = createObjects.createStudent();
		List<Student> students = new ArrayList<>();
		students.add(student);

		when(studentRepository.findStudentsNotInLessons(uuids)).thenReturn(students);

		List<StudentReadDto> dtos = studentService.getStudentsWithoutLesson(uuids);

		assertEquals(student.getId(), dtos.get(0).getId());

		verify(studentRepository).findStudentsNotInLessons(uuids);
	}

	@Test
	void testGetStudentsWithoutLessonNull() {
		List<UUID> uuids = new ArrayList<>();
		Student student = createObjects.createStudent();
		List<Student> students = new ArrayList<>();
		students.add(student);

		when(studentRepository.findAll()).thenReturn(students);

		List<StudentReadDto> dtos = studentService.getStudentsWithoutLesson(uuids);

		assertEquals(student.getId(), dtos.get(0).getId());

		verify(studentRepository).findAll();
	}

}
